package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbauth;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUserIdentity;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAuthorizer implements Authorizer<AJEntityUserIdentity> {

    private final ProcessorContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthorizer.class);

    public UserAuthorizer(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> authorize(AJEntityUserIdentity model) {
        LazyList<AJEntityUserIdentity> user =
            AJEntityUserIdentity.findBySQL(AJEntityUserIdentity.SELECT_USER_TO_VALIDATE, context.userIdFromURL());
        if (user.isEmpty()) {
            LOGGER.warn("user not found in database");
            return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse(), ExecutionStatus.FAILED);
        }

        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

}
