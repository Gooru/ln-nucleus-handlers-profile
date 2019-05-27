package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gooru.nucleus.handlers.profiles.constants.HelperConstants;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbutils.DBHelperUtility;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityCollection;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityCourse;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.formatter.JsonFormatter;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.handlers.profiles.processors.utils.HelperUtility;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListOfflineActivitiesHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(ListOfflineActivitiesHandler.class);
  private boolean isPublic = false;
  private String sortOn;
  private String order;
  private int limit;
  private int offset;
  private StringBuilder query;
  private List<Object> params;
  private Map<String, Integer> taskCountByOfflineActivity;
  private Map<String, AJEntityCourse> courseInfo;

  ListOfflineActivitiesHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    if (context.userIdFromURL() == null || context.userIdFromURL().isEmpty()
        || !(HelperUtility.validateUUID(context.userIdFromURL()))) {
      LOGGER.warn("Invalid user id");
      return new ExecutionResult<>(
          MessageResponseFactory.createInvalidRequestResponse("Invalid user id"),
          ExecutionStatus.FAILED);
    }

    isPublic = HelperUtility.checkPublic(context);

    String sortOnFromRequest = HelperUtility
        .readRequestParam(HelperConstants.REQ_PARAM_SORTON, context);
    sortOn = sortOnFromRequest != null ? sortOnFromRequest : AJEntityCollection.DEFAULT_SORTON;
    if (!AJEntityCollection.VALID_SORTON_FIELDS.contains(sortOn)) {
      LOGGER.warn("Invalid value provided for sort");
      return new ExecutionResult<>(
          MessageResponseFactory.createInvalidRequestResponse("Invalid value for sort"),
          ExecutionStatus.FAILED);
    }

    String orderFromRequest = HelperUtility
        .readRequestParam(HelperConstants.REQ_PARAM_ORDER, context);
    order = orderFromRequest != null ? orderFromRequest : AJEntityCollection.DEFAULT_ORDER;
    if (!AJEntityCollection.VALID_ORDER_FIELDS.contains(order)) {
      LOGGER.warn("Invalid value provided for order");
      return new ExecutionResult<>(
          MessageResponseFactory.createInvalidRequestResponse("Invalid value for order"),
          ExecutionStatus.FAILED);
    }

    limit = HelperUtility.getLimitFromRequest(context);
    offset = HelperUtility.getOffsetFromRequest(context);

    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    initializeQueryAndParams();

    LazyList<AJEntityCollection> offlineActivityList = AJEntityCollection
        .findBySQL(query.toString(), params.toArray());
    if (offlineActivityList.isEmpty()) {
      return sendEmptyResponse();
    }
    LOGGER.debug("# Offline Activities found: {}", offlineActivityList.size());
    return processNonEmptyResponse(offlineActivityList);
  }

  private ExecutionResult<MessageResponse> processNonEmptyResponse(
      LazyList<AJEntityCollection> offlineActivityList) {

    Set<String> ownerIdList = new HashSet<>();
    List<String> offlineActivityIdList = new ArrayList<>();
    Set<String> courseIdList = new HashSet<>();

    for (AJEntityCollection offlineActivity : offlineActivityList) {
      offlineActivityIdList.add(offlineActivity.getString(AJEntityCollection.ID));
      ownerIdList.add(offlineActivity.getString(AJEntityCollection.OWNER_ID));
      if (offlineActivity.getString(AJEntityCollection.COURSE_ID) != null
          && !offlineActivity.getString(AJEntityCollection.COURSE_ID).isEmpty()) {
        courseIdList.add(offlineActivity.getString(AJEntityCollection.COURSE_ID));
      }
    }
    LOGGER.debug("# Courses are associated with offline activities: {}", courseIdList.size());

    initializeOATaskCount(offlineActivityIdList);

    initializeCourseInfo(courseIdList);

    return createAndSendResponse(offlineActivityList, ownerIdList);
  }

  private ExecutionResult<MessageResponse> createAndSendResponse(
      LazyList<AJEntityCollection> offlineActivitiesList, Set<String> ownerIdList) {
    JsonFormatter offlineActivityFieldsFormatter =
        JsonFormatterBuilder.buildSimpleJsonFormatter(false, AJEntityCollection.OFFLINE_ACTIVITY_LIST);
    JsonFormatter courseFieldsFormatter =
        JsonFormatterBuilder
            .buildSimpleJsonFormatter(false, AJEntityCourse.COURSE_FIELDS_FOR_COLLECTION);
    JsonArray offlineActivityArray = new JsonArray();

    for (AJEntityCollection offlineActivity : offlineActivitiesList) {
      JsonObject result = new JsonObject(offlineActivityFieldsFormatter.toJson(offlineActivity));
      String courseId = offlineActivity.getString(AJEntityCollection.COURSE_ID);
      if (courseId != null && !courseId.isEmpty()) {
        AJEntityCourse course = courseInfo.get(courseId);
        if (course != null) {
          result.put(HelperConstants.RESP_JSON_KEY_COURSE,
              new JsonObject(courseFieldsFormatter.toJson(course)));
        } else {
          result.putNull(HelperConstants.RESP_JSON_KEY_COURSE);
        }
      }

      String oaId = offlineActivity.getString(AJEntityCollection.ID);
      Integer taskCount = taskCountByOfflineActivity.get(oaId);
      result.put(AJEntityCollection.TASK_COUNT, taskCount != null ? taskCount : 0);
      offlineActivityArray.add(result);
    }

    JsonObject responseBody = new JsonObject();
    responseBody.put(HelperConstants.RESP_JSON_KEY_OFFLINE_ACTIVITIES, offlineActivityArray);
    responseBody.put(HelperConstants.RESP_JSON_KEY_OWNER_DETAILS,
        DBHelperUtility.getOwnerDemographics(ownerIdList));
    responseBody.put(HelperConstants.RESP_JSON_KEY_FILTERS, getFiltersJson());
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
        ExecutionStatus.SUCCESSFUL);
  }

  private void initializeCourseInfo(Set<String> courseIdList) {
    courseInfo = new HashMap<>();
    LazyList<AJEntityCourse> courseList = AJEntityCourse.findBySQL(
        AJEntityCourse.SELECT_COURSE_FOR_COLLECTION,
        HelperUtility.toPostgresArrayString(courseIdList));
    courseList.forEach(course -> courseInfo.put(course.getString(AJEntityCourse.ID), course));
    LOGGER.debug("# Courses returned from database: {}", courseInfo.size());
  }

  private void initializeOATaskCount(List<String> offlineActivitiesIdList) {
    List<Map> taskCounts = Base
        .findAll(AJEntityCollection.SELECT_TASK_COUNTS_FOR_OA,
            HelperUtility.toPostgresArrayString(offlineActivitiesIdList));
    taskCountByOfflineActivity = new HashMap<>();
    taskCounts.forEach(entry -> {
      String offlineActivityId = entry.get(AJEntityCollection.OA_ID).toString();
      taskCountByOfflineActivity.put(offlineActivityId,
          Integer.valueOf(entry.get(AJEntityCollection.TASK_COUNT).toString()));
    });
  }
 

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

  private ExecutionResult<MessageResponse> sendEmptyResponse() {
    JsonObject responseBody = new JsonObject()
        .put(HelperConstants.RESP_JSON_KEY_OFFLINE_ACTIVITIES, new JsonArray())
        .put(HelperConstants.RESP_JSON_KEY_OWNER_DETAILS, new JsonArray())
        .put(HelperConstants.RESP_JSON_KEY_FILTERS, getFiltersJson());

    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
        ExecutionStatus.SUCCESSFUL);
  }


  private void initializeQueryAndParams() {
    params = new ArrayList<>();

    query = new StringBuilder(AJEntityCollection.SELECT_OFFLINE_ACTIVITES);

    if (isPublic) {
      query.append(HelperConstants.SPACE).append(AJEntityCollection.OP_AND)
          .append(HelperConstants.SPACE)
          .append(AJEntityCollection.CRITERIA_PUBLIC);
      params.add(context.userIdFromURL());
    } else {
      query.append(HelperConstants.SPACE).append(AJEntityCollection.OP_AND)
          .append(HelperConstants.SPACE)
          .append(AJEntityCollection.CRITERIA_MYPROFILE);
      params.add(context.userIdFromURL()); // for owner
      params.add(context.userIdFromURL()); // for collaborator
    }

    query.append(HelperConstants.SPACE).append(AJEntityCollection.CLAUSE_ORDERBY)
        .append(HelperConstants.SPACE)
        .append(sortOn).append(HelperConstants.SPACE).append(order).append(HelperConstants.SPACE)
        .append(AJEntityCollection.CLAUSE_LIMIT_OFFSET);
    params.add(limit);
    params.add(offset);

    LOGGER.debug(
        "SelectQuery:{}, paramSize:{}, sortOn: {}, order: {}, limit:{}, offset:{}",
        query, params.size(), sortOn, order, limit, offset);
  }

  private JsonObject getFiltersJson() {
    return new JsonObject().put(HelperConstants.RESP_JSON_KEY_SORTON, sortOn)
        .put(HelperConstants.RESP_JSON_KEY_ORDER, order)
        .put(HelperConstants.RESP_JSON_KEY_LIMIT, limit)
        .put(HelperConstants.RESP_JSON_KEY_OFFSET, offset);
  }
}
