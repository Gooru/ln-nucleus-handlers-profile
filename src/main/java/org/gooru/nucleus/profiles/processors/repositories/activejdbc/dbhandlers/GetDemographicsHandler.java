package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.profiles.constants.HelperConstants;
import org.gooru.nucleus.profiles.constants.MessageConstants;
import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityUserDemographic;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityUserIdentity;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityUserNetwork;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.profiles.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class GetDemographicsHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(GetDemographicsHandler.class);

  public GetDemographicsHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    if (context.userIdFromURL() == null || context.userIdFromURL().isEmpty()) {
      LOGGER.warn("Invalid user id");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid user id"), ExecutionStatus.FAILED);
    }

    LOGGER.debug("checkSanity() OK");
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    LOGGER.debug("request to get demographics");
    LazyList<AJEntityUserDemographic> demographics = AJEntityUserDemographic.findBySQL(AJEntityUserDemographic.SELECT_DEMOGRAPHICS, context.userIdFromURL());
    JsonObject responseBody = new JsonObject(new JsonFormatterBuilder().buildSimpleJsonFormatter(false, AJEntityUserDemographic.ALL_FIELDS).toJson(demographics.get(0)));

    Long followers = Base.count(AJEntityUserNetwork.TABLE, AJEntityUserNetwork.SELECT_FOLLOWERS_COUNT, context.userIdFromURL());
    Long followings = Base.count(AJEntityUserNetwork.TABLE, AJEntityUserNetwork.SELECT_FOLLOWINGS_COUNT , context.userIdFromURL());

    responseBody.put(HelperConstants.RESP_JSON_KEY_FOLLOWERS, followers);
    responseBody.put(HelperConstants.RESP_JSON_KEY_FOLLOWINGS, followings);

    //Check whether user is following other user
    //In case own profile it should be false
    boolean isFollowing = false;
    if (!context.userId().equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
      LazyList<AJEntityUserNetwork> userNetwork = AJEntityUserNetwork.where(AJEntityUserNetwork.CHECK_IF_FOLLOWER, context.userId(), context.userIdFromURL());
      if (!userNetwork.isEmpty()) {
        isFollowing = true;
      }
    }
    responseBody.put(HelperConstants.RESP_JSON_KEY_ISFOLLOWING, isFollowing);
    
    //Get username from user_identity as not all user have first and last name
    String username = null;
    LazyList<AJEntityUserIdentity> userIdentities = AJEntityUserIdentity.findBySQL(AJEntityUserIdentity.SELECT_USERNAME, context.userIdFromURL());
    if (!userIdentities.isEmpty()) {
      username = userIdentities.get(0).getString(AJEntityUserIdentity.USERNAME);
    }
    responseBody.put(AJEntityUserIdentity.USERNAME, username);
    
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody), ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

}
