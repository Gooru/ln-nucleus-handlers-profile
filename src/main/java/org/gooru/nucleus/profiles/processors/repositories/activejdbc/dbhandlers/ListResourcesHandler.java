package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.profiles.constants.HelperConstants;
import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityContent;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityCourse;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityUserDemographic;
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
  private String standard;
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

    limit = getLimit();
    offset = getOffset();
    
    standard = readRequestParam(HelperConstants.REQ_PARAM_STANDARD);

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

    if (standard != null) {
      query = new StringBuilder(AJEntityContent.SELECT_RESOURCES_BY_TAXONOMY);
      params.add(standard + HelperConstants.PERCENTAGE);
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

    LOGGER.debug("SelectQuery:{}, paramSize:{}, standard:{}, searchText:{}, sortOn: {}, order: {}, limit:{}, offset:{}", query,
            params.size(), standard, searchText, sortOn, order, limit, offset);

    LazyList<AJEntityContent> resourceList = AJEntityContent.findBySQL(query.toString(), params.toArray());
    JsonArray resourceArray = new JsonArray();
    if (!resourceList.isEmpty()) {
      List<String> creatorIdList = new ArrayList<>();
      resourceList.stream().forEach(resource -> creatorIdList.add(resource.getString(AJEntityContent.CREATOR_ID)));
      
      LazyList<AJEntityUserDemographic> userDemographics =
              AJEntityUserDemographic.findBySQL(AJEntityUserDemographic.SELECT_DEMOGRAPHICS_MULTIPLE, listToPostgresArrayString(creatorIdList));
      Map<String, AJEntityUserDemographic> userDemographicsMap = new HashMap<>();
      userDemographics.forEach(user -> userDemographicsMap.put(user.getId().toString(), user));
      
      resourceList.stream().forEach(resource -> resourceArray
              .add(new JsonObject(new JsonFormatterBuilder().buildSimpleJsonFormatter(false, AJEntityContent.RESOURCE_LIST).toJson(resource))
                      .put(AJEntityContent.OWNER_INFO, new JsonObject(new JsonFormatterBuilder()
                                                      .buildSimpleJsonFormatter(false, AJEntityUserDemographic.DEMOGRAPHIC_FIELDS)
                                                      .toJson(userDemographicsMap.get(resource.getString(AJEntityContent.CREATOR_ID)))))));
    }
    JsonObject responseBody = new JsonObject();
    responseBody.put(HelperConstants.RESP_JSON_KEY_RESOURCES, resourceArray);
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
      .put(HelperConstants.RESP_JSON_KEY_STANDARD, standard)
      .put(HelperConstants.RESP_JSON_KEY_SORTON, sortOn)
      .put(HelperConstants.RESP_JSON_KEY_ORDER, order)
      .put(HelperConstants.RESP_JSON_KEY_LIMIT, limit)
      .put(HelperConstants.RESP_JSON_KEY_OFFSET, offset);
    return filters;
  }
  
  private String listToPostgresArrayString(List<String> input) {
    int approxSize = ((input.size() + 1) * 36); // Length of UUID is around 36
                                                // chars
    Iterator<String> it = input.iterator();
    if (!it.hasNext()) {
      return "{}";
    }

    StringBuilder sb = new StringBuilder(approxSize);
    sb.append('{');
    for (;;) {
      String s = it.next();
      sb.append('"').append(s).append('"');
      if (!it.hasNext()) {
        return sb.append('}').toString();
      }
      sb.append(',');
    }
  }
  
  private int getLimit() {
    try {
      String strLimit = readRequestParam(HelperConstants.REQ_PARAM_LIMIT);
      int limitFromRequest = strLimit != null ? Integer.valueOf(strLimit) : AJEntityCourse.DEFAULT_LIMIT; 
      return limitFromRequest > AJEntityCourse.DEFAULT_LIMIT ? AJEntityCourse.DEFAULT_LIMIT : limitFromRequest;
    } catch (NumberFormatException nfe) {
      return AJEntityCourse.DEFAULT_LIMIT;
    }
  }
  
  private int getOffset() {
    try {
      String offsetFromRequest = readRequestParam(HelperConstants.REQ_PARAM_OFFSET);
      return offsetFromRequest != null ? Integer.valueOf(offsetFromRequest) : AJEntityCourse.DEFAULT_OFFSET; 
    } catch (NumberFormatException nfe) {
      return AJEntityCourse.DEFAULT_OFFSET;
    }
  }


}
