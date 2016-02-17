package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonObject;

public class ListResourcesHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(ListResourcesHandler.class);

  public ListResourcesHandler(ProcessorContext context) {
    this.context = context;
  }
  
  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    LOGGER.debug("request to get list of resources");
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(new JsonObject().put("test", "OK")), ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return false;
  }

}
