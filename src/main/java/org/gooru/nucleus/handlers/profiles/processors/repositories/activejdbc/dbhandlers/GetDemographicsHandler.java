package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

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
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class GetDemographicsHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(GetDemographicsHandler.class);

  private AJEntityUsers user;

  GetDemographicsHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    if (context.userId() == null || context.userId().isEmpty() || context.userId()
        .equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
      LOGGER.warn("Anonymous or invalid user attempting to access demographics");
      return new ExecutionResult<>(
          MessageResponseFactory.createForbiddenResponse("Not allowed"),
          ExecutionResult.ExecutionStatus.FAILED);
    }
    LOGGER.debug("checkSanity() OK");
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    LazyList<AJEntityUsers> users = AJEntityUsers
        .findBySQL(AJEntityUsers.SELECT_USER, context.userId());
    if (users == null || users.isEmpty()) {
      LOGGER.warn("user not found in database");
      return new ExecutionResult<>(
          MessageResponseFactory.createNotFoundResponse("user not found in database"),
          ExecutionStatus.FAILED);
    }

    this.user = users.get(0);
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    JsonObject responseBody =
        new JsonObject(
            JsonFormatterBuilder.buildSimpleJsonFormatter(false, AJEntityUsers.ALL_FIELDS)
                .toJson(user));

    Long followers =
        Base.count(AJEntityUserNetwork.TABLE, AJEntityUserNetwork.SELECT_FOLLOWERS_COUNT,
            context.userId());
    Long followings =
        Base.count(AJEntityUserNetwork.TABLE, AJEntityUserNetwork.SELECT_FOLLOWINGS_COUNT,
            context.userId());

    responseBody.put(HelperConstants.RESP_JSON_KEY_FOLLOWERS, followers);
    responseBody.put(HelperConstants.RESP_JSON_KEY_FOLLOWINGS, followings);
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
        ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

}
