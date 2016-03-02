package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.ArrayList;
import java.util.List;

import org.gooru.nucleus.profiles.constants.HelperConstants;
import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityCollection;
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

public class ListAssessmentsHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(ListAssessmentsHandler.class);
  private boolean isPublic;
  private String searchText;
  private String taxonomyCode;

  public ListAssessmentsHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    if (context.userIdFromURL() == null || context.userIdFromURL().isEmpty()) {
      LOGGER.warn("Invalid user id");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid user id"), ExecutionStatus.FAILED);
    }

    isPublic = checkPublic();
    searchText = readRequestParam(HelperConstants.REQ_PARAM_SEARCH_TEXT);

    // If standard is available in request we do not care about subject/level
    // for filter. Similarly if we find subject in request we will not care
    // about level
    String standard = readRequestParam(HelperConstants.REQ_PARAM_STANDARD);
    if (standard != null) {
      taxonomyCode = standard;
      return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    String subject = readRequestParam(HelperConstants.REQ_PARAM_SUBJECT);
    if (subject != null) {
      taxonomyCode = subject;
      return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    String level = readRequestParam(HelperConstants.REQ_PARAM_LEVEL);
    if (level != null) {
      taxonomyCode = level;
      return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    StringBuffer query = null;
    List<Object> params = new ArrayList<>();

    // Parameters to be added in list should be in same way as below
    params.add(context.userIdFromURL());

    if (taxonomyCode != null) {
      query = new StringBuffer(AJEntityCollection.SELECT_ASSESSMENTS_BY_TAXONOMY);
      params.add(taxonomyCode + HelperConstants.PERCENTAGE);
    } else {
      query = new StringBuffer(AJEntityCollection.SELECT_ASSESSMENTS);
    }

    if (searchText != null) {
      query.append(AJEntityCollection.OP_AND).append(AJEntityCollection.CRITERIA_TITLE);
      params.add(HelperConstants.PERCENTAGE + searchText + HelperConstants.PERCENTAGE);
    }

    if (isPublic) {
      query.append(AJEntityCollection.OP_AND).append(AJEntityCollection.CRITERIA_PUBLIC);
    }

    LOGGER.debug("SelectQuery:{}, paramSize:{}, txCode:{}, searchText:{}", query, params.size(), taxonomyCode, searchText);
    LazyList<AJEntityCollection> assessmentList = AJEntityCollection.findBySQL(query.toString(), params.toArray());
    JsonObject responseBody = new JsonObject();
    responseBody.put(HelperConstants.RESP_JSON_KEY_ASSESSMENTS,
            new JsonArray(new JsonFormatterBuilder().buildSimpleJsonFormatter(false, AJEntityCollection.ASSESSMENT_LIST).toJson(assessmentList)));
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody), ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

  private String readRequestParam(String param) {
    JsonArray requestParams = context.request().getJsonArray(param);
    if (requestParams == null || requestParams.isEmpty()) {
      return null;
    }

    String value = requestParams.getString(0);
    return (value != null && !value.isEmpty()) ? value : null;
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
    return Boolean.parseBoolean(preview);
  }

}
