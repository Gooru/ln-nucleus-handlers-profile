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
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityContent;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.formatter.JsonFormatter;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.handlers.profiles.processors.utils.HelperUtility;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListQuestionsHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(ListQuestionsHandler.class);
  private boolean isPublic;
  private String sortOn;
  private String order;
  private int limit;
  private int offset;
  private StringBuilder query;
  private List<Object> params;
  private Map<String, AJEntityCollection> assessmentInfo;

  ListQuestionsHandler(ProcessorContext context) {
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
    sortOn = sortOnFromRequest != null ? sortOnFromRequest : AJEntityContent.DEFAULT_SORTON;
    if (!AJEntityContent.VALID_SORTON_FIELDS.contains(sortOn)) {
      LOGGER.warn("Invalid value provided for sort");
      return new ExecutionResult<>(
          MessageResponseFactory.createInvalidRequestResponse("Invalid value for sort"),
          ExecutionStatus.FAILED);
    }

    String orderFromRequest = HelperUtility
        .readRequestParam(HelperConstants.REQ_PARAM_ORDER, context);
    order = orderFromRequest != null ? orderFromRequest : AJEntityContent.DEFAULT_ORDER;
    if (!AJEntityContent.VALID_ORDER_FIELDS.contains(order)) {
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

    LazyList<AJEntityContent> questionList = AJEntityContent
        .findBySQL(query.toString(), params.toArray());
    Set<String> ownerIdList = new HashSet<>();
    if (questionList.isEmpty()) {
      sendEmptyResponse();
    }

    return processNonEmptyResponse(questionList, ownerIdList);
  }

  private ExecutionResult<MessageResponse> processNonEmptyResponse(
      LazyList<AJEntityContent> questionList, Set<String> ownerIdList) {
    Set<String> assessmentIdList = new HashSet<>();
    for (AJEntityContent question : questionList) {
      ownerIdList.add(question.getString(AJEntityContent.CREATOR_ID));
      if (question.getString(AJEntityContent.COLLECTION_ID) != null) {
        assessmentIdList.add(question.getString(AJEntityContent.COLLECTION_ID));
      }
    }
    LOGGER.debug("number of assessment found {}", assessmentIdList.size());

    initializeAssessmentInfo(assessmentIdList);

    return createAndSendResponse(questionList, ownerIdList);
  }

  private ExecutionResult<MessageResponse> createAndSendResponse(
      LazyList<AJEntityContent> questionList, Set<String> ownerIdList) {
    JsonArray questionArray = new JsonArray();
    JsonFormatter questionFieldsFormatter =
        JsonFormatterBuilder.buildSimpleJsonFormatter(false, AJEntityContent.QUESTION_LIST);
    JsonFormatter assessmentFieldsFormatter =
        JsonFormatterBuilder
            .buildSimpleJsonFormatter(false, AJEntityCollection.ASSESSMENT_FIELDS_FOR_QUESTION);

    for (AJEntityContent question : questionList) {
      JsonObject result = new JsonObject(questionFieldsFormatter.toJson(question));
      String assessmentId = question.getString(AJEntityContent.COLLECTION_ID);
      if (assessmentId != null && !assessmentId.isEmpty()) {
        AJEntityCollection assessment = assessmentInfo.get(assessmentId);
        result.put(HelperConstants.RESP_JSON_KEY_ASSESSMENT,
            new JsonObject(assessmentFieldsFormatter.toJson(assessment)));
      }
      questionArray.add(result);
    }

    JsonObject responseBody = new JsonObject();
    responseBody.put(HelperConstants.RESP_JSON_KEY_QUESTIONS, questionArray);
    responseBody.put(HelperConstants.RESP_JSON_KEY_OWNER_DETAILS,
        DBHelperUtility.getOwnerDemographics(ownerIdList));
    responseBody.put(HelperConstants.RESP_JSON_KEY_FILTERS, getFiltersJson());
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
        ExecutionStatus.SUCCESSFUL);
  }

  private ExecutionResult<MessageResponse> sendEmptyResponse() {
    JsonObject responseBody = new JsonObject();
    responseBody.put(HelperConstants.RESP_JSON_KEY_QUESTIONS, new JsonArray());
    responseBody.put(HelperConstants.RESP_JSON_KEY_OWNER_DETAILS, new JsonArray());
    responseBody.put(HelperConstants.RESP_JSON_KEY_FILTERS, getFiltersJson());
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
        ExecutionStatus.SUCCESSFUL);
  }

  private void initializeAssessmentInfo(Set<String> assessmentIdList) {
    assessmentInfo = new HashMap<>();
    LazyList<AJEntityCollection> assessmentList =
        AJEntityCollection.findBySQL(AJEntityCollection.SELECT_ASSESSMENT_FOR_QUESTION,
            HelperUtility.toPostgresArrayString(assessmentIdList));
    for (AJEntityCollection assessment : assessmentList) {
      assessmentInfo.put(assessment.getString(AJEntityCollection.ID), assessment);
    }
    LOGGER.debug("assessment fetched from DB are {}", assessmentInfo.size());
  }

  private void initializeQueryAndParams() {
    params = new ArrayList<>();

    // Parameters to be added in list should be in same way as below
    params.add(context.userIdFromURL());
    query = new StringBuilder(AJEntityContent.SELECT_QUESTIONS);

    if (isPublic) {
      query.append(HelperConstants.SPACE).append(AJEntityContent.OP_AND)
          .append(HelperConstants.SPACE)
          .append(AJEntityContent.CRITERIA_PUBLIC);
    }

    query.append(HelperConstants.SPACE).append(AJEntityContent.CLAUSE_ORDERBY)
        .append(HelperConstants.SPACE)
        .append(sortOn).append(HelperConstants.SPACE).append(order).append(HelperConstants.SPACE)
        .append(AJEntityContent.CLAUSE_LIMIT_OFFSET);
    params.add(limit);
    params.add(offset);

    LOGGER.debug(
        "SelectQuery:{}, paramSize:{}, sortOn: {}, order: {}, limit:{}, offset:{}",
        query, params.size(), sortOn, order, limit, offset);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

  private JsonObject getFiltersJson() {
    return new JsonObject()
        .put(HelperConstants.RESP_JSON_KEY_SORTON, sortOn)
        .put(HelperConstants.RESP_JSON_KEY_ORDER, order)
        .put(HelperConstants.RESP_JSON_KEY_LIMIT, limit)
        .put(HelperConstants.RESP_JSON_KEY_OFFSET, offset);
  }
}
