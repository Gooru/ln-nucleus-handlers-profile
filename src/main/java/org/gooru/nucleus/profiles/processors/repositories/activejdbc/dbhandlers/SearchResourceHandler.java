package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.ArrayList;
import java.util.List;

import org.gooru.nucleus.profiles.constants.HelperConstants;
import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityContent;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityTaxonomyCode;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJTaxonomySubject;
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

public class SearchResourceHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(SearchResourceHandler.class);
  private boolean isPublic;
  private String searchText;
  private String level;
  private String subject;
  private String standard;
  private String standardFramework;
  
  public SearchResourceHandler(ProcessorContext context) {
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
    
    // TODO: Not sure how to handle the use case when standard framework is null
    // or not exists in prefs. May need to revisit later
    standardFramework = context.prefs().getString(HelperConstants.PREFS_SFCODE);
    
    searchText = readRequestParam(HelperConstants.REQ_PARAM_SEARCH_TEXT);
    level = readRequestParam(HelperConstants.REQ_PARAM_LEVEL);
    subject = readRequestParam(HelperConstants.REQ_PARAM_SUBJECT);
    standard = readRequestParam(HelperConstants.REQ_PARAM_STANDARD);
    LOGGER.debug("IsPublic:{}, SearchText:{}, level:{}, subject:{}, standard:{}", isPublic, searchText, level, subject, standard);
    
    LOGGER.debug("checkSanity() OK");
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    // Purposefully initiating to empty string so if there is no filter criteria
    // provided it should not treat as null and fail
    String txCode = "";
    String selectQuery;
    
    if (isPublic) {
      selectQuery = AJEntityContent.SEARCH_RESOURCES_FOR_PUBLIC;
    } else {
      selectQuery = AJEntityContent.SEARCH_RESOURCES_FOR_OWNER;
    }
    
    // If standard is available in request we do not care about subject/level
    // for filter. Similarly if we find subject in request we will not care about level
    if (standard != null) {
      AJEntityTaxonomyCode ajEntityTaxonomyCode = AJEntityTaxonomyCode.first(AJEntityTaxonomyCode.SELECT_DEFAULT_CODE_FOR_SEARCH, standard, standardFramework);
      if (ajEntityTaxonomyCode == null) {
        LOGGER.warn("no matching default code found in database for standard {}", standard);
        return new ExecutionResult<>(MessageResponseFactory.createInternalErrorResponse("No matching code found for standard"), ExecutionStatus.FAILED);
      }
      txCode = ajEntityTaxonomyCode.getString(AJEntityTaxonomyCode.DEFAULT_CODE_ID);
    } else if (subject != null) {
      AJTaxonomySubject ajTaxonomySubject = AJTaxonomySubject.first(AJTaxonomySubject.SELECT_DEFAULT_CODE_FOR_SEARCH, subject, standardFramework);
      if (ajTaxonomySubject == null) {
        LOGGER.warn("no matching default code found in database for subject {}", subject);
        return new ExecutionResult<>(MessageResponseFactory.createInternalErrorResponse("No matching code found for subject"), ExecutionStatus.FAILED);
      }
      txCode = ajTaxonomySubject.getString(AJTaxonomySubject.DEFAULT_SUBJECT_ID);
    } else if (level != null) {
      txCode = level;
    }
    
    //Order of the parameters to be added in list should be in same way as below
    List<Object> params = new ArrayList<>();
    params.add(context.userIdFromURL());
    params.add(txCode + HelperConstants.PERCENTAGE);
    
    if (searchText != null ) {
      selectQuery = selectQuery + AJEntityContent.OP_AND + AJEntityContent.CRITERIA_TITLE;
      params.add(HelperConstants.PERCENTAGE + searchText + HelperConstants.PERCENTAGE);
    }
    
    LOGGER.debug("SelectQuery:{}, paramSize:{}, txCode:{}, searchText:{}", selectQuery, params.size(), txCode, searchText);
    LazyList<AJEntityContent> resourceList = AJEntityContent.findBySQL(selectQuery, params.toArray());
    JsonObject responseBody = new JsonObject();
    responseBody.put("resources", new JsonArray(new JsonFormatterBuilder().buildSimpleJsonFormatter(false, AJEntityContent.RESOURCE_LIST).toJson(resourceList)));
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
    return (value != null && !value.isEmpty()) ? value: null;
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
