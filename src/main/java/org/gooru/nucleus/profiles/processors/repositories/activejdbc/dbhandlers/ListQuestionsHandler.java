package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.profiles.constants.HelperConstants;
import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityContent;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.profiles.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ListQuestionsHandler implements DBHandler {
  
  private final ProcessorContext context;
  private boolean isPublic = false;
  private static final Logger LOGGER = LoggerFactory.getLogger(ListQuestionsHandler.class);

  public ListQuestionsHandler(ProcessorContext context) {
    this.context = context;
  }
  
  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    if (context.userIdFromURL() == null || context.userIdFromURL().isEmpty()) {
      LOGGER.warn("Invalid user id");
      return new ExecutionResult<MessageResponse>(MessageResponseFactory.createInvalidRequestResponse("Invalid user id"), ExecutionStatus.FAILED);
    }

    // identify whether the request is for public or owner
    isPublic = checkPublic();
    LOGGER.debug("isPublic:{}", isPublic);
    LOGGER.debug("checkSanity() OK");
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    LazyList<AJEntityContent> questionList;
    if(isPublic) {
      LOGGER.debug("getting list of questions for public view");  
      questionList = AJEntityContent.findBySQL(AJEntityContent.SELECT_QUESTIONS_FOR_PUBLIC, context.userIdFromURL());
    } else {
      LOGGER.debug("getting list of questions for owner view"); 
      questionList = AJEntityContent.findBySQL(AJEntityContent.SELECT_QUESTIONS_FOR_OWNER, context.userIdFromURL());
    }
    
    JsonObject responseBody = new JsonObject();
    responseBody.put("questions", new JsonArray(new JsonFormatterBuilder().buildSimpleJsonFormatter(false, AJEntityContent.QUESTION_LIST).toJson(questionList)));
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody), ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }
  
  private boolean checkPublic() {
    if (!context.userId().equalsIgnoreCase(context.userIdFromURL())) {
      return true;
    } 
    
    JsonArray previewArray = context.request().getJsonArray(HelperConstants.REQ_PARAM_PREVIEW);
    if (previewArray == null || previewArray.isEmpty()) {
      return false;
    }

    String preview = (String) previewArray.getValue(0);
    // Assuming that preview parameter only exists when user want to view his
    // profile as public
    if (Boolean.parseBoolean(preview)) {
      return true;
    } else {
      return false;
    }
  }

}
