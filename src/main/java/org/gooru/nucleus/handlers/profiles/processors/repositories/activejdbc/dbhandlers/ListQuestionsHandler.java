package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

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
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.handlers.profiles.processors.utils.HelperUtility;
import org.javalite.activejdbc.LazyList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ListQuestionsHandler implements DBHandler {

    private final ProcessorContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(ListQuestionsHandler.class);
    private boolean isPublic;
    private String searchText;
    private String standard;
    private String sortOn;
    private String order;
    private int limit;
    private int offset;
    private String filterBy;

    public ListQuestionsHandler(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> checkSanity() {
        if (context.userIdFromURL() == null || context.userIdFromURL().isEmpty()
            || !(HelperUtility.validateUUID(context.userIdFromURL()))) {
            LOGGER.warn("Invalid user id");
            return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid user id"),
                ExecutionStatus.FAILED);
        }

        isPublic = HelperUtility.checkPublic(context);
        searchText = HelperUtility.readRequestParam(HelperConstants.REQ_PARAM_SEARCH_TEXT, context);

        String sortOnFromRequest = HelperUtility.readRequestParam(HelperConstants.REQ_PARAM_SORTON, context);
        sortOn = sortOnFromRequest != null ? sortOnFromRequest : AJEntityContent.DEFAULT_SORTON;
        if (!AJEntityContent.VALID_SORTON_FIELDS.contains(sortOn)) {
            LOGGER.warn("Invalid value provided for sort");
            return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid value for sort"),
                ExecutionStatus.FAILED);
        }

        String orderFromRequest = HelperUtility.readRequestParam(HelperConstants.REQ_PARAM_ORDER, context);
        order = orderFromRequest != null ? orderFromRequest : AJEntityContent.DEFAULT_ORDER;
        if (!AJEntityContent.VALID_ORDER_FIELDS.contains(order)) {
            LOGGER.warn("Invalid value provided for order");
            return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid value for order"),
                ExecutionStatus.FAILED);
        }

        limit = HelperUtility.getLimitFromRequest(context);
        offset = HelperUtility.getOffsetFromRequest(context);
        filterBy = HelperUtility.readRequestParam(HelperConstants.REQ_PARAM_FILTERBY, context);
        standard = HelperUtility.readRequestParam(HelperConstants.REQ_PARAM_STANDARD, context);

        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        StringBuilder query;
        List<Object> params = new ArrayList<>();

        // Parameters to be added in list should be in same way as below
        params.add(context.userIdFromURL());

        if (standard != null) {
            query = new StringBuilder(AJEntityContent.SELECT_QUESTIONS_BY_TAXONOMY);
            params.add(standard);
        } else {
            query = new StringBuilder(AJEntityContent.SELECT_QUESTIONS);
        }

        if (searchText != null) {
            query.append(HelperConstants.SPACE).append(AJEntityContent.OP_AND).append(HelperConstants.SPACE)
                .append(AJEntityContent.CRITERIA_TITLE);
            // Purposefully adding same search text twice to fulfill the
            // criteria of
            // title and description search
            params.add(HelperConstants.PERCENTAGE + searchText + HelperConstants.PERCENTAGE);
            params.add(HelperConstants.PERCENTAGE + searchText + HelperConstants.PERCENTAGE);
        }

        if (isPublic) {
            query.append(HelperConstants.SPACE).append(AJEntityContent.OP_AND).append(HelperConstants.SPACE)
                .append(AJEntityContent.CRITERIA_PUBLIC);
        }
        
        boolean inCollectionFilter = false;
        if (filterBy != null) {
            if (filterBy.equalsIgnoreCase(HelperConstants.FILTERBY_INCOLLECTION)) {
                query.append(HelperConstants.SPACE).append(AJEntityContent.OP_AND).append(HelperConstants.SPACE)
                    .append(AJEntityContent.CRITERIA_INCOLLECTION);
                inCollectionFilter = true;
            } else if (filterBy.equalsIgnoreCase(HelperConstants.FILTERBY_NOT_INCOLLECTION)) {
                query.append(HelperConstants.SPACE).append(AJEntityContent.OP_AND).append(HelperConstants.SPACE)
                    .append(AJEntityContent.CRITERIA_NOT_INCOLLECTION);
            }
        } else {
            query.append(HelperConstants.SPACE).append(AJEntityContent.OP_AND).append(HelperConstants.SPACE)
                .append(AJEntityContent.CRITERIA_NOT_INCOLLECTION);
        }

        query.append(HelperConstants.SPACE).append(AJEntityContent.CLAUSE_ORDERBY).append(HelperConstants.SPACE)
            .append(sortOn).append(HelperConstants.SPACE).append(order).append(HelperConstants.SPACE)
            .append(AJEntityContent.CLAUSE_LIMIT_OFFSET);
        params.add(limit);
        params.add(offset);

        LOGGER.debug(
            "SelectQuery:{}, paramSize:{}, standard:{}, searchText:{}, sortOn: {}, order: {}, limit:{}, offset:{}",
            query, params.size(), standard, searchText, sortOn, order, limit, offset);

        LazyList<AJEntityContent> questionList = AJEntityContent.findBySQL(query.toString(), params.toArray());
        JsonArray questionArray = new JsonArray();
        Set<String> ownerIdList = new HashSet<>();
        if (!questionList.isEmpty()) {
            List<String> creatorIdList = new ArrayList<>();
            questionList.stream()
                .forEach(question -> creatorIdList.add(question.getString(AJEntityContent.CREATOR_ID)));

            Map<String, AJEntityCollection> assessmentMap = new HashMap<>();
            if (inCollectionFilter) {
                LOGGER.debug("in collection filter is ON, fetching collections/assessments");
                Set<String> assessmentIdList = new HashSet<>();
                questionList.stream().filter(question -> question.getString(AJEntityContent.COLLECTION_ID) != null)
                    .forEach(question -> assessmentIdList.add(question.getString(AJEntityContent.COLLECTION_ID)));
                LOGGER.debug("number of assessment found {}", assessmentIdList.size());
                
                LazyList<AJEntityCollection> assessmentList =
                    AJEntityCollection.findBySQL(AJEntityCollection.SELECT_ASSESSMENT_FOR_QUESTION,
                        HelperUtility.toPostgresArrayString(assessmentIdList));
                assessmentList.stream()
                    .forEach(assessment -> assessmentMap.put(assessment.getString(AJEntityCollection.ID), assessment));
                LOGGER.debug("assessment fetched from DB are {}", assessmentMap.size());
            }
            
            questionList.stream().forEach(question -> {
                JsonObject result = new JsonObject(JsonFormatterBuilder
                    .buildSimpleJsonFormatter(false, AJEntityContent.QUESTION_LIST).toJson(question));
                String assessmentId = question.getString(AJEntityContent.COLLECTION_ID);
                if (assessmentId != null && !assessmentId.isEmpty()) {
                    AJEntityCollection assessment = assessmentMap.get(assessmentId);
                    result.put(HelperConstants.RESP_JSON_KEY_ASSESSMENT,
                        new JsonObject(JsonFormatterBuilder
                            .buildSimpleJsonFormatter(false, AJEntityCollection.ASSESSMENT_FIELDS_FOR_QUESTION)
                            .toJson(assessment)));
                }
                questionArray.add(result);
            });
            
            questionList.stream().forEach(question -> ownerIdList.add(question.getString(AJEntityContent.CREATOR_ID)));
        }

        JsonObject responseBody = new JsonObject();
        responseBody.put(HelperConstants.RESP_JSON_KEY_QUESTIONS, questionArray);
        responseBody.put(HelperConstants.RESP_JSON_KEY_OWNER_DETAILS, DBHelperUtility.getOwnerDemographics(ownerIdList));
        responseBody.put(HelperConstants.RESP_JSON_KEY_FILTERS, getFiltersJson());
        return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
            ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public boolean handlerReadOnly() {
        return true;
    }

    private JsonObject getFiltersJson() {
        return new JsonObject().put(HelperConstants.RESP_JSON_KEY_STANDARD, standard)
            .put(HelperConstants.RESP_JSON_KEY_SORTON, sortOn).put(HelperConstants.RESP_JSON_KEY_ORDER, order)
            .put(HelperConstants.RESP_JSON_KEY_LIMIT, limit).put(HelperConstants.RESP_JSON_KEY_OFFSET, offset);
    }
}
