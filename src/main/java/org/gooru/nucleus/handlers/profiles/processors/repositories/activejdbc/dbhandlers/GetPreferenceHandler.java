package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.handlers.profiles.constants.MessageConstants;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUserPreference;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUsers;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.handlers.profiles.processors.utils.PreferenceSettingsUtil;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 01-Feb-2017
 */
public class GetPreferenceHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(GetPreferenceHandler.class);

  GetPreferenceHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    // The user should not be anonymous
    if (context.userId() == null || context.userId().isEmpty() || context.userId()
        .equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
      LOGGER.warn("Anonymous or invalid user attempting to get preference");
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
        .findBySQL(AJEntityUsers.VALIDATE_USER, context.userId());
    if (users == null || users.isEmpty()) {
      LOGGER.warn("user not found in database");
      return new ExecutionResult<>(
          MessageResponseFactory.createNotFoundResponse("user not found in database"),
          ExecutionStatus.FAILED);
    }

    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    AJEntityUserPreference userPreference =
        AJEntityUserPreference.findFirst(AJEntityUserPreference.SELECT_BY_USERID, context.userId());
    JsonObject userPreferenceJson;
    if (userPreference == null) {
      LOGGER.warn("user preferences not found, returning default");
      userPreferenceJson = PreferenceSettingsUtil.getDefaultPreference();
    } else {
      userPreferenceJson = new JsonObject(
          userPreference.getString(AJEntityUserPreference.PREFERENCE_SETTINGS));
    }

    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(userPreferenceJson),
        ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

}
