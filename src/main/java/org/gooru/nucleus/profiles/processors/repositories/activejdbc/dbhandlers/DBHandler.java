package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;

public interface DBHandler {
        ExecutionResult<MessageResponse> checkSanity();

    ExecutionResult<MessageResponse> validateRequest();

    ExecutionResult<MessageResponse> executeRequest();

    boolean handlerReadOnly();
}
