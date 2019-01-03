package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.handlers.profiles.constants.MessageConstants;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.events.EventBuilderFactory;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUserNetwork;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUsers;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.handlers.profiles.processors.utils.HelperUtility;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FollowHandler implements DBHandler {

  private final ProcessorContext context;
  private AJEntityUserNetwork userNetwork;
  private static final Logger LOGGER = LoggerFactory.getLogger(FollowHandler.class);
  private String followOnUserId;

  FollowHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {

    JsonObject validateErrors = validatePayload();
    if (validateErrors != null && !validateErrors.isEmpty()) {
      return new ExecutionResult<>(
          MessageResponseFactory.createValidationErrorResponse(validateErrors),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    JsonObject notNullErrors = validateNullFields();
    if (notNullErrors != null && !notNullErrors.isEmpty()) {
      return new ExecutionResult<>(
          MessageResponseFactory.createValidationErrorResponse(notNullErrors),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    followOnUserId = context.request().getString(AJEntityUserNetwork.USER_ID);
    if (!(HelperUtility.validateUUID(followOnUserId))) {
      LOGGER.error("Invalid user id passed in request json");
      return new ExecutionResult<>(
          MessageResponseFactory.createValidationErrorResponse(
              new JsonObject()
                  .put(MessageConstants.MSG_MESSAGE, "Invalid user id passed in request JSON")),
          ExecutionResult.ExecutionStatus.FAILED);
    }

    if (context.userId().equalsIgnoreCase(followOnUserId)) {
      LOGGER.error("user trying to follow him self");
      return new ExecutionResult<>(
          MessageResponseFactory.createValidationErrorResponse(
              new JsonObject()
                  .put(MessageConstants.MSG_MESSAGE, "User is trying to follow him self")),
          ExecutionStatus.FAILED);
    }

    LOGGER.debug("checkSanity() OK");
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    LazyList<AJEntityUsers> users = AJEntityUsers
        .findBySQL(AJEntityUsers.VALIDATE_USER, this.followOnUserId);
    if (users == null || users.isEmpty()) {
      LOGGER.warn("user not found in database to which you are trying to follow");
      return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse(
          "user not found in database to which you are trying to follow"), ExecutionStatus.FAILED);
    }

    AJEntityUsers user = users.get(0);

    AJEntityUserNetwork userNetwork =
        AJEntityUserNetwork
            .findFirst(AJEntityUserNetwork.VERIFY_FOLLOW, context.userId(), followOnUserId);
    if (userNetwork != null) {
      LOGGER.warn("user is already following the user in request payload");
      return new ExecutionResult<>(
          MessageResponseFactory.createValidationErrorResponse(new JsonObject()
              .put(MessageConstants.MSG_MESSAGE,
                  "user is already following the user in request payload")),
          ExecutionStatus.FAILED);
    }

    LOGGER.debug("validateRequest() OK");
    return AuthorizerBuilder.buildFollowUserAuthorizer(context).authorize(user);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    userNetwork = new AJEntityUserNetwork();
    userNetwork.setUserId(context.userId());
    userNetwork.setFollowOnUserId(followOnUserId);

    if (userNetwork.hasErrors()) {
      LOGGER.warn("adding follower has errors");
      return new ExecutionResult<>(
          MessageResponseFactory.createValidationErrorResponse(getModelErrors()),
          ExecutionStatus.FAILED);
    }

    if (userNetwork.insert()) {
      LOGGER.info("user {} is now following {}", context.userId(), followOnUserId);
      return new ExecutionResult<>(
          MessageResponseFactory.createPostResponse(
              EventBuilderFactory.getFollowProfileEventBuilder(context.userId(), followOnUserId)),
          ExecutionStatus.SUCCESSFUL);
    } else {
      LOGGER.error("error while adding follower");
      return new ExecutionResult<>(
          MessageResponseFactory.createValidationErrorResponse(getModelErrors()),
          ExecutionStatus.FAILED);
    }
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }

  private JsonObject validatePayload() {
    JsonObject input = context.request();
    JsonObject output = new JsonObject();

    input.fieldNames().stream().filter(key -> !AJEntityUserNetwork.REQUIRED_FIELDS.contains(key))
        .forEach(key -> output.put(key, "Field not allowed"));
    return output.isEmpty() ? null : output;
  }

  private JsonObject validateNullFields() {
    JsonObject input = context.request();
    JsonObject output = new JsonObject();
    AJEntityUserNetwork.REQUIRED_FIELDS.stream()
        .filter(notNullField -> (input.getValue(notNullField) == null
            || input.getValue(notNullField).toString().isEmpty()))
        .forEach(notNullField -> output.put(notNullField, "Field should not be empty or null"));
    return output.isEmpty() ? null : output;
  }

  private JsonObject getModelErrors() {
    JsonObject errors = new JsonObject();
    this.userNetwork.errors().forEach(errors::put);
    return errors;
  }

}
