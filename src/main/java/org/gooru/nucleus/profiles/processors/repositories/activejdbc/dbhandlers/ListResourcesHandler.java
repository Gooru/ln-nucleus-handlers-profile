package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.ArrayList;
import java.util.List;

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

public class ListResourcesHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(ListResourcesHandler.class);
  private boolean isPublic;
  private String searchText;
  private String taxonomyCode;
  private String sortOn;
  private String order;
  private int limit;
  private int offset;
  
  public ListResourcesHandler(ProcessorContext context) {
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

    String sortOnFromRequest = readRequestParam(HelperConstants.REQ_PARAM_SORTON);
    sortOn = sortOnFromRequest != null ? sortOnFromRequest : AJEntityContent.DEFAULT_SORTON;
    if (!AJEntityContent.VALID_SORTON_FIELDS.contains(sortOn)) {
      LOGGER.warn("Invalid value provided for sort");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid value for sort"), ExecutionStatus.FAILED);
    }

    String orderFromRequest = readRequestParam(HelperConstants.REQ_PARAM_ORDER);
    order = orderFromRequest != null ? orderFromRequest : AJEntityContent.DEFAULT_ORDER;
    if (!AJEntityContent.VALID_ORDER_FIELDS.contains(order)) {
      LOGGER.warn("Invalid value provided for order");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid value for order"), ExecutionStatus.FAILED);
    }

    String strLimit = readRequestParam(HelperConstants.REQ_PARAM_LIMIT);
    int limitFromRequest = strLimit != null ? Integer.valueOf(strLimit) : AJEntityContent.DEFAULT_LIMIT;
    limit = limitFromRequest > AJEntityContent.DEFAULT_LIMIT ? AJEntityContent.DEFAULT_LIMIT : limitFromRequest;

    String offsetFromRequest = readRequestParam(HelperConstants.REQ_PARAM_OFFSET);
    offset = offsetFromRequest != null ? Integer.valueOf(offsetFromRequest) : AJEntityContent.DEFAULT_OFFSET;
    
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
    StringBuilder query = null;
    List<Object> params = new ArrayList<>();

    // Parameters to be added in list should be in same way as below
    params.add(context.userIdFromURL());

    if (taxonomyCode != null) {
      query = new StringBuilder(AJEntityContent.SELECT_RESOURCES_BY_TAXONOMY);
      params.add(taxonomyCode + HelperConstants.PERCENTAGE);
    } else {
      query = new StringBuilder(AJEntityContent.SELECT_RESOURCES);
    }

    if (searchText != null) {
      query.append(HelperConstants.SPACE)
           .append(AJEntityContent.OP_AND)
           .append(HelperConstants.SPACE)
           .append(AJEntityContent.CRITERIA_TITLE);
      // Purposefully adding same search text twice to fulfill the criteria of
      // title and description search
      params.add(HelperConstants.PERCENTAGE + searchText + HelperConstants.PERCENTAGE);
      params.add(HelperConstants.PERCENTAGE + searchText + HelperConstants.PERCENTAGE);
    }

    if (isPublic) {
      query.append(HelperConstants.SPACE)
           .append(AJEntityContent.OP_AND)
           .append(HelperConstants.SPACE)
           .append(AJEntityContent.CRITERIA_PUBLIC);
    }

    query.append(HelperConstants.SPACE)
         .append(AJEntityContent.CLAUSE_ORDERBY)
         .append(HelperConstants.SPACE)
         .append(sortOn)
         .append(HelperConstants.SPACE)
         .append(order)
         .append(HelperConstants.SPACE)
         .append(AJEntityContent.CLAUSE_LIMIT_OFFSET);
    params.add(limit);
    params.add(offset);

    LOGGER.debug("SelectQuery:{}, paramSize:{}, txCode:{}, searchText:{}, sortOn: {}, order: {}, limit:{}, offset:{}", query,
            params.size(), taxonomyCode, searchText, sortOn, order, limit, offset);

    LazyList<AJEntityContent> resourceList = AJEntityContent.findBySQL(query.toString(), params.toArray());
    JsonObject responseBody = new JsonObject();
    responseBody.put(HelperConstants.RESP_JSON_KEY_RESOURCES,
            new JsonArray(new JsonFormatterBuilder().buildSimpleJsonFormatter(false, AJEntityContent.RESOURCE_LIST).toJson(resourceList)));
    responseBody.put(HelperConstants.RESP_JSON_KEY_FILTERS, getFiltersJson());
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
  
  private JsonObject getFiltersJson() {
    JsonObject filters = new JsonObject()
      .put(HelperConstants.RESP_JSON_KEY_TAXONOMY, taxonomyCode)
      .put(HelperConstants.RESP_JSON_KEY_SORTON, sortOn)
      .put(HelperConstants.RESP_JSON_KEY_ORDER, order)
      .put(HelperConstants.RESP_JSON_KEY_LIMIT, limit)
      .put(HelperConstants.RESP_JSON_KEY_OFFSET, offset);
    return filters;
  }

}
