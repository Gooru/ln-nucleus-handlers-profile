package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbauth;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUsers;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserAuthorizer implements Authorizer<AJEntityUsers> {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthorizer.class);

  public UserAuthorizer(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> authorize(AJEntityUsers model) {
    LazyList<AJEntityUsers> users = AJEntityUsers
        .findBySQL(AJEntityUsers.VALIDATE_USER, context.userIdFromURL());
    if (users == null || users.isEmpty()) {
      LOGGER.warn("user not found in database");
      return new ExecutionResult<>(
          MessageResponseFactory.createNotFoundResponse("user not found in database"),
          ExecutionStatus.FAILED);
    }

    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

}
