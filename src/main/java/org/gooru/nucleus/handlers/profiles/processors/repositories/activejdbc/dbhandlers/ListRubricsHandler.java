package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.gooru.nucleus.handlers.profiles.constants.HelperConstants;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbutils.DBHelperUtility;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityRubric;
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

/**
 * @author szgooru Created On: 01-Mar-2017
 */
public class ListRubricsHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(ListRubricsHandler.class);

  private boolean isPublic;
  private String sortOn;
  private String order;
  private int limit;
  private int offset;
  private StringBuilder query;
  private List<Object> params;

  ListRubricsHandler(ProcessorContext context) {
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
    sortOn = sortOnFromRequest != null ? sortOnFromRequest : AJEntityRubric.DEFAULT_SORTON;
    if (!AJEntityRubric.VALID_SORTON_FIELDS.contains(sortOn)) {
      LOGGER.warn("Invalid value provided for sort");
      return new ExecutionResult<>(
          MessageResponseFactory.createInvalidRequestResponse("Invalid value for sort"),
          ExecutionStatus.FAILED);
    }

    String orderFromRequest = HelperUtility
        .readRequestParam(HelperConstants.REQ_PARAM_ORDER, context);
    order = orderFromRequest != null ? orderFromRequest : AJEntityRubric.DEFAULT_ORDER;
    if (!AJEntityRubric.VALID_ORDER_FIELDS.contains(order)) {
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

    LazyList<AJEntityRubric> rubricList = AJEntityRubric
        .findBySQL(query.toString(), params.toArray());
    JsonArray rubricArray = new JsonArray();
    Set<String> ownerIdList = new HashSet<>();
    if (!rubricList.isEmpty()) {
      JsonFormatter rubricFieldsFormatter =
          JsonFormatterBuilder.buildSimpleJsonFormatter(false, AJEntityRubric.RUBRIC_LIST);

      for (AJEntityRubric rubric : rubricList) {
        ownerIdList.add(rubric.getString(AJEntityRubric.CREATOR_ID));
        JsonObject result = new JsonObject(rubricFieldsFormatter.toJson(rubric));
        rubricArray.add(result);
      }
    }

    JsonObject responseBody = new JsonObject();
    responseBody.put(HelperConstants.RESP_JSON_KEY_RUBRICS, rubricArray);
    responseBody.put(HelperConstants.RESP_JSON_KEY_OWNER_DETAILS,
        DBHelperUtility.getOwnerDemographics(ownerIdList));
    responseBody.put(HelperConstants.RESP_JSON_KEY_FILTERS, getFiltersJson());
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
        ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

  private void initializeQueryAndParams() {
    params = new ArrayList<>();

    // Parameters to be added in list should be in same way as below
    params.add(context.userIdFromURL());
    query = new StringBuilder(AJEntityRubric.SELECT_RUBRICS);

    if (isPublic) {
      query.append(HelperConstants.SPACE).append(AJEntityRubric.OP_AND)
          .append(HelperConstants.SPACE)
          .append(AJEntityRubric.CRITERIA_PUBLIC);
    }
    query.append(HelperConstants.SPACE).append(AJEntityRubric.OP_AND).append(HelperConstants.SPACE)
        .append(AJEntityRubric.CRITERIA_STANDALONE);

    query.append(HelperConstants.SPACE).append(AJEntityRubric.CLAUSE_ORDERBY)
        .append(HelperConstants.SPACE)
        .append(sortOn).append(HelperConstants.SPACE).append(order).append(HelperConstants.SPACE)
        .append(AJEntityRubric.CLAUSE_LIMIT_OFFSET);
    params.add(limit);
    params.add(offset);

    LOGGER.debug(
        "SelectQuery:{}, paramSize:{}, sortOn: {}, order: {}, limit:{}, offset:{}",
        query, params.size(), sortOn, order, limit, offset);
  }

  private JsonObject getFiltersJson() {
    return new JsonObject()
        .put(HelperConstants.RESP_JSON_KEY_SORTON, sortOn)
        .put(HelperConstants.RESP_JSON_KEY_ORDER, order)
        .put(HelperConstants.RESP_JSON_KEY_LIMIT, limit)
        .put(HelperConstants.RESP_JSON_KEY_OFFSET, offset);
  }

}
