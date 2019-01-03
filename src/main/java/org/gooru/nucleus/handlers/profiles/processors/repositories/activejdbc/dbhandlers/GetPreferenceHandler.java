package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonObject;
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

/**
 * @author szgooru Created On: 01-Feb-2017
 */
public class GetPreferenceHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(GetPreferenceHandler.class);

  public GetPreferenceHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
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

    JsonObject result = new JsonObject()
        .put(AJEntityUserPreference.PREFERENCE_SETTINGS, userPreferenceJson);
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(result),
        ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

}
