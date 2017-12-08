package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.handlers.profiles.constants.MessageConstants;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUserState;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

/**
 * @author szgooru Created On: 08-Dec-2017
 */
public class UpdateUserStateHandler implements DBHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateUserStateHandler.class);

    private final ProcessorContext context;
    private JsonObject clientState = null;
    private AJEntityUserState userState = null;

    public UpdateUserStateHandler(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> checkSanity() {
        if (this.context.userId() == null
            || this.context.userId().equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
            LOGGER.warn("Invalid user id or anonymous access");
            return new ExecutionResult<>(
                MessageResponseFactory.createForbiddenResponse("Invalid user id or anonymous access"),
                ExecutionStatus.FAILED);
        }   

        if (this.context.request() == null || this.context.request().isEmpty()) {
            LOGGER.warn("Invalid request body to update user client state");
            return new ExecutionResult<>(
                MessageResponseFactory.createInvalidRequestResponse("Invalid request body to update user client state"),
                ExecutionStatus.FAILED);
        }

        this.clientState = this.context.request().getJsonObject(AJEntityUserState.CLIENT_STATE);

        LOGGER.debug("checkSanity() OK");
        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        this.userState = new AJEntityUserState();
        this.userState.setUserId(this.context.userId());
        this.userState.setClientState(this.clientState.toString());

        LOGGER.debug("validateRequest() OK");
        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        if (!this.userState.save()) {
            LOGGER.error("unable to save client state for user '{}'", this.context.userId());
            return new ExecutionResult<>(
                MessageResponseFactory.createInternalErrorResponse("Unable to save client state"),
                ExecutionStatus.FAILED);
        }

        return new ExecutionResult<>(MessageResponseFactory.createNoContentResponse(), ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public boolean handlerReadOnly() {
        return false;
    }

}
