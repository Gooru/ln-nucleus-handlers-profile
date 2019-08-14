package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.Arrays;
import java.util.List;
import org.gooru.nucleus.handlers.profiles.constants.HelperConstants;
import org.gooru.nucleus.handlers.profiles.constants.MessageConstants;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUserNetwork;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUsers;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.handlers.profiles.processors.utils.HelperUtility;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 20-Jan-2017
 */
public class SearchProfileHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(SearchProfileHandler.class);

  private String searchValue;
  private SearchType searchType = SearchType.NONE;
  private List<String> userIds;
  private boolean isPartial;

  SearchProfileHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    JsonArray inputArray = context.request().getJsonArray("userids");
    searchValue = inputArray != null ? inputArray.getString(0) : null;
    if (searchValue == null || searchValue.isEmpty()) {
      inputArray = context.request().getJsonArray("username");
      searchValue = inputArray != null ? inputArray.getString(0) : null;
      if (searchValue == null || searchValue.isEmpty()) {
        inputArray = context.request().getJsonArray("email");
        searchValue = inputArray != null ? inputArray.getString(0) : null;
        if (searchValue != null && !searchValue.isEmpty()) {
          searchType = SearchType.EMAIL;
        }
      } else {
        searchType = SearchType.USERNAME;
      }
    } else {
      searchType = SearchType.USERIDS;
    }

    if (searchType == SearchType.NONE || searchValue == null || searchValue.isEmpty()) {
      LOGGER.warn("Invalid search criteria");
      return new ExecutionResult<>(
          MessageResponseFactory.createInvalidRequestResponse("Invalid search criteria"),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    if (searchValue.length() < 3) {
      LOGGER.warn("search string should be at least 3 chars long");
      return new ExecutionResult<>(
          MessageResponseFactory
              .createInvalidRequestResponse("Search string should be at least 3 chars long"),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    JsonArray partialArray = context.request().getJsonArray("partial");
    String strPartial = partialArray != null ? partialArray.getString(0) : null;
    this.isPartial =
        (strPartial != null && !strPartial.isEmpty()) ? Boolean.valueOf(strPartial) : false;

    LOGGER.debug("checkSanity: OK");
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    LOGGER.debug("Search BY: {}, SearchValue: {}, Partial: {}", searchType, searchValue,
        this.isPartial);
    if (searchType == SearchType.USERIDS) {
      userIds = Arrays.asList(searchValue.split(","));
      if (userIds.size() > 50) {
        return new ExecutionResult<>(
            MessageResponseFactory.createInvalidRequestResponse("50 userids accepted per request."),
            ExecutionResult.ExecutionStatus.FAILED);
      }

      for (String userId : userIds) {
        if (!HelperUtility.validateUUID(userId)) {
          return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(
              "Invalid user id passed in the URL"), ExecutionResult.ExecutionStatus.FAILED);
        }
      }
    }
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    switch (searchType) {
      case USERNAME:
        return searchByUsername(searchValue.toLowerCase());
      case EMAIL:
        return searchByEmail(searchValue.toLowerCase());
      case USERIDS:
        return searchByUserids(userIds);
      default:
        LOGGER.warn("Search criteria did not match the specified ones");
    }
    JsonObject responseBody = new JsonObject();
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
        ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

  private enum SearchType {
    NONE, USERIDS, USERNAME, EMAIL
  }

  private ExecutionResult<MessageResponse> searchByUsername(String username) {
    if (this.isPartial) {
      return searchByPartialUsername(username);
    }

    LazyList<AJEntityUsers> users =
        AJEntityUsers.findBySQL(AJEntityUsers.SELECT_BY_USERNAME, username, context.tenant());
    if (users.isEmpty()) {
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    AJEntityUsers user = users.get(0);
    JsonObject result = new JsonObject(JsonFormatterBuilder
        .buildSimpleJsonFormatter(false, AJEntityUsers.ALL_FIELDS).toJson(user));
    result.mergeIn(getNetworkDetails(user.getString(AJEntityUsers.ID)));
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(result),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }

  private ExecutionResult<MessageResponse> searchByPartialUsername(String username) {
    StringBuilder searchString = new StringBuilder(username).append("%");
    LazyList<AJEntityUsers> users = AJEntityUsers
        .findBySQL(AJEntityUsers.SELECT_BY_USERNAME_PARTIAL, searchString.toString(), context.tenant());
    if (users.isEmpty()) {
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    JsonArray resultArray = new JsonArray(JsonFormatterBuilder
        .buildSimpleJsonFormatter(false, AJEntityUsers.ALL_FIELDS).toJson(users));
    JsonObject result = new JsonObject();
    result.put(HelperConstants.RESP_JSON_KEY_USERS, resultArray);
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(result),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }


  private ExecutionResult<MessageResponse> searchByEmail(String email) {
    if (this.isPartial) {
      return searchByPartialEmail(email);
    }

    LazyList<AJEntityUsers> users =
        AJEntityUsers.findBySQL(AJEntityUsers.SELECT_BY_EMAIL, email, context.tenant());
    if (users.isEmpty()) {
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    JsonObject result = new JsonObject(JsonFormatterBuilder
        .buildSimpleJsonFormatter(false, AJEntityUsers.ALL_FIELDS).toJson(users.get(0)));
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(result),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }

  private ExecutionResult<MessageResponse> searchByPartialEmail(String email) {
    StringBuilder searchString = new StringBuilder(email).append("%");
    LazyList<AJEntityUsers> users = AJEntityUsers.findBySQL(AJEntityUsers.SELECT_BY_EMAIL_PARTIAL,
        searchString.toString(), context.tenant());
    if (users.isEmpty()) {
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    JsonArray resultArray = new JsonArray(JsonFormatterBuilder
        .buildSimpleJsonFormatter(false, AJEntityUsers.ALL_FIELDS).toJson(users));
    JsonObject result = new JsonObject();
    result.put(HelperConstants.RESP_JSON_KEY_USERS, resultArray);
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(result),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }

  private ExecutionResult<MessageResponse> searchByUserids(List<String> userIds) {
    LazyList<AJEntityUsers> users = AJEntityUsers.findBySQL(AJEntityUsers.SELECT_BY_IDS,
        HelperUtility.toPostgresArrayString(userIds));
    if (users.isEmpty()) {
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse(),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    // Assuming that if only 1 id for search then probably user is looking
    // at other users profile and we need to populate network details.
    // This is not applicable for request where more that one user exists in
    // request to search
    JsonArray resultArray;
    if (userIds.size() == 1) {
      JsonObject userJson = new JsonObject(JsonFormatterBuilder
          .buildSimpleJsonFormatter(false, AJEntityUsers.ALL_FIELDS).toJson(users.get(0)));
      userJson.mergeIn(getNetworkDetails(userIds.get(0)));
      resultArray = new JsonArray().add(userJson);
    } else {
      resultArray = new JsonArray(JsonFormatterBuilder
          .buildSimpleJsonFormatter(false, AJEntityUsers.ALL_FIELDS).toJson(users));
    }

    JsonObject result = new JsonObject();
    result.put(HelperConstants.RESP_JSON_KEY_USERS, resultArray);
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(result),
        ExecutionResult.ExecutionStatus.SUCCESSFUL);
  }

  private JsonObject getNetworkDetails(String userId) {
    JsonObject network = new JsonObject();
    Long followers =
        Base.count(AJEntityUserNetwork.TABLE, AJEntityUserNetwork.SELECT_FOLLOWERS_COUNT, userId);
    Long followings =
        Base.count(AJEntityUserNetwork.TABLE, AJEntityUserNetwork.SELECT_FOLLOWINGS_COUNT, userId);

    network.put(HelperConstants.RESP_JSON_KEY_FOLLOWERS, followers);
    network.put(HelperConstants.RESP_JSON_KEY_FOLLOWINGS, followings);

    // Check whether user is following other user
    // In case own profile it should be false
    boolean isFollowing = false;
    LOGGER.debug("current logged in user: " + context.userId());
    if (!context.userId().equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)
        && !context.userId().equalsIgnoreCase(userId)) {
      LazyList<AJEntityUserNetwork> userNetwork = AJEntityUserNetwork
          .where(AJEntityUserNetwork.CHECK_IF_FOLLOWER, context.userId(), userId);
      if (!userNetwork.isEmpty()) {
        isFollowing = true;
      }
    }
    network.put(HelperConstants.RESP_JSON_KEY_ISFOLLOWING, isFollowing);
    return network;
  }

}
