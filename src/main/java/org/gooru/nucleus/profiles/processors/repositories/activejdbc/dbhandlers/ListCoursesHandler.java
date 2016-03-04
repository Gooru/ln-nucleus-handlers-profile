package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.profiles.constants.HelperConstants;
import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityCourse;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.profiles.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ListCoursesHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(ListCoursesHandler.class);
  private boolean isPublic;
  private String searchText;
  private String subjectCode;
  private String sortOn;
  private String order;
  private int limit;
  private int offset;

  public ListCoursesHandler(ProcessorContext context) {
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
    subjectCode = readRequestParam(HelperConstants.REQ_PARAM_SUBJECT);
    
    String sortOnFromRequest = readRequestParam(HelperConstants.REQ_PARAM_SORTON);
    sortOn = sortOnFromRequest != null ? sortOnFromRequest : AJEntityCourse.DEFAULT_SORTON;
    if (!AJEntityCourse.VALID_SORTON_FIELDS.contains(sortOn)) {
      LOGGER.warn("Invalid value provided for sort");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid value for sort"), ExecutionStatus.FAILED);
    }
    
    String orderFromRequest = readRequestParam(HelperConstants.REQ_PARAM_ORDER);
    order = orderFromRequest != null ? orderFromRequest : AJEntityCourse.DEFAULT_ORDER;
    if (!AJEntityCourse.VALID_ORDER_FIELDS.contains(order)) {
      LOGGER.warn("Invalid value provided for order");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid value for order"), ExecutionStatus.FAILED);
    }
    
    String strLimit = readRequestParam(HelperConstants.REQ_PARAM_LIMIT);
    int limitFromRequest = strLimit != null ? Integer.valueOf(strLimit) : AJEntityCourse.DEFAULT_LIMIT; 
    limit = limitFromRequest > AJEntityCourse.DEFAULT_LIMIT ? AJEntityCourse.DEFAULT_LIMIT : limitFromRequest;
    
    String offsetFromRequest = readRequestParam(HelperConstants.REQ_PARAM_OFFSET);
    offset = offsetFromRequest != null ? Integer.valueOf(offsetFromRequest) : AJEntityCourse.DEFAULT_OFFSET;

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

    if (subjectCode != null) {
      query = new StringBuffer(AJEntityCourse.SELECT_COURSES_BY_SUBJECT);
      params.add(subjectCode);
    } else {
      query = new StringBuffer(AJEntityCourse.SELECT_COURSES);
    }

    if (searchText != null) {
      query.append(HelperConstants.SPACE).append(AJEntityCourse.OP_AND).append(HelperConstants.SPACE).append(AJEntityCourse.CRITERIA_TITLE);
      params.add(HelperConstants.PERCENTAGE + searchText + HelperConstants.PERCENTAGE);
    }

    if (isPublic) {
      query.append(HelperConstants.SPACE).append(AJEntityCourse.OP_AND).append(HelperConstants.SPACE).append(AJEntityCourse.CRITERIA_PUBLIC);
    }
    query.append(HelperConstants.SPACE).append(AJEntityCourse.CLAUSE_ORDERBY).append(HelperConstants.SPACE).append(sortOn).append(HelperConstants.SPACE).append(order);
    query.append(HelperConstants.SPACE).append(AJEntityCourse.CLAUSE_LIMIT_OFFSET);
    params.add(limit);
    params.add(offset);
    
    LOGGER.debug("SelectQuery:{}, paramSize:{}, txCode:{}, searchText:{}, sortOn: {}, order: {}, limit:{}, offset:{}", query, params.size(), subjectCode, searchText, 
      sortOn, order, limit, offset);
    LazyList<AJEntityCourse> courseList = AJEntityCourse.findBySQL(query.toString(), params.toArray());
    
    JsonArray courseArray = new JsonArray();
    if (!courseList.isEmpty()) {
      List<String> courseIdList = new ArrayList<>();
      courseList.stream().forEach(course -> courseIdList.add(course.getString(AJEntityCourse.ID)));
  
      List<Map> unitCounts = Base.findAll(AJEntityCourse.SELECT_UNIT_COUNT_FOR_COURSES, listToPostgresArrayString(courseIdList));
      Map<String, Integer> unitCountByCourse = new HashMap<>();
      unitCounts.stream().forEach(map -> unitCountByCourse.put(map.get(AJEntityCourse.COURSE_ID).toString(),
              Integer.valueOf(map.get(AJEntityCourse.UNIT_COUNT).toString())));
  
      courseList.stream()
              .forEach(course -> courseArray
                      .add(new JsonObject(new JsonFormatterBuilder().buildSimpleJsonFormatter(false, AJEntityCourse.COURSE_LIST).toJson(course))
                              .put(AJEntityCourse.UNIT_COUNT, unitCountByCourse.get(course.getString(AJEntityCourse.ID)))));
    }
    
    JsonObject responseBody = new JsonObject();
    responseBody.put(HelperConstants.RESP_JSON_KEY_COURSES, courseArray);
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
  
  private JsonObject getFiltersJson() {
    JsonObject filters = new JsonObject().put(HelperConstants.RESP_JSON_KEY_SUBJECT , subjectCode).put(HelperConstants.RESP_JSON_KEY_SORTON, sortOn)
            .put(HelperConstants.RESP_JSON_KEY_ORDER, order).put(HelperConstants.RESP_JSON_KEY_LIMIT, limit).put(HelperConstants.RESP_JSON_KEY_OFFSET, offset);
    return filters;
  }
}
