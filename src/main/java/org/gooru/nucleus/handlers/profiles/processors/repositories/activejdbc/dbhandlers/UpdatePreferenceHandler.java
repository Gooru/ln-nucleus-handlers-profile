package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.UUID;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUserPreference;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUsers;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author szgooru Created On: 01-Feb-2017
 */
public class UpdatePreferenceHandler implements DBHandler {

    private final ProcessorContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatePreferenceHandler.class);

    private AJEntityUsers user;

    public UpdatePreferenceHandler(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> checkSanity() {
        // TODO: validation of the request JSON

        LOGGER.debug("checkSanity() OK");
        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        user = AJEntityUsers.findById(UUID.fromString(context.userId()));
        if (user == null) {
            LOGGER.warn("user not found in database");
            return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse(), ExecutionStatus.FAILED);
        }

        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        AJEntityUserPreference userPreference = new AJEntityUserPreference();
        userPreference.setUserId(context.userId());
        userPreference.setPreferenceSettings(context.request().toString());

        if (userPreference.save()) {
            LOGGER.debug("user preference settings stored successfully");
            return new ExecutionResult<>(MessageResponseFactory.createNoContentResponse(),
                ExecutionResult.ExecutionStatus.SUCCESSFUL);
        }

        return new ExecutionResult<>(
            MessageResponseFactory.createInternalErrorResponse("Error while saving user preference settings"),
            ExecutionResult.ExecutionStatus.FAILED);
    }

    @Override
    public boolean handlerReadOnly() {
        return false;
    }

}
