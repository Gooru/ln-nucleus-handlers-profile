package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbauth;

import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.javalite.activejdbc.Model;

public interface Authorizer<T extends Model> {

    ExecutionResult<MessageResponse> authorize(T model);

}
