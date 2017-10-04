package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gooru.nucleus.handlers.profiles.constants.HelperConstants;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbutils.DBHelperUtility;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityOriginalResource;
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
        if (context.userIdFromURL() == null || context.userIdFromURL().isEmpty()
            || !(HelperUtility.validateUUID(context.userIdFromURL()))) {
            LOGGER.warn("Invalid user id");
            return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid user id"),
                ExecutionStatus.FAILED);
        }

        isPublic = HelperUtility.checkPublic(context);
        searchText = HelperUtility.readRequestParam(HelperConstants.REQ_PARAM_SEARCH_TEXT, context);

        String sortOnFromRequest = HelperUtility.readRequestParam(HelperConstants.REQ_PARAM_SORTON, context);
        sortOn = sortOnFromRequest != null ? sortOnFromRequest : AJEntityOriginalResource.DEFAULT_SORTON;
        if (!AJEntityOriginalResource.VALID_SORTON_FIELDS.contains(sortOn)) {
            LOGGER.warn("Invalid value provided for sort");
            return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid value for sort"),
                ExecutionStatus.FAILED);
        }

        String orderFromRequest = HelperUtility.readRequestParam(HelperConstants.REQ_PARAM_ORDER, context);
        order = orderFromRequest != null ? orderFromRequest : AJEntityOriginalResource.DEFAULT_ORDER;
        if (!AJEntityOriginalResource.VALID_ORDER_FIELDS.contains(order)) {
            LOGGER.warn("Invalid value provided for order");
            return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid value for order"),
                ExecutionStatus.FAILED);
        }

        limit = HelperUtility.getLimitFromRequest(context);
        offset = HelperUtility.getOffsetFromRequest(context);

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
            query = new StringBuilder(AJEntityOriginalResource.SELECT_RESOURCES_BY_TAXONOMY);
            params.add(standard);
        } else {
            query = new StringBuilder(AJEntityOriginalResource.SELECT_RESOURCES);
        }

        if (searchText != null) {
            query.append(HelperConstants.SPACE).append(AJEntityOriginalResource.OP_AND).append(HelperConstants.SPACE)
                .append(AJEntityOriginalResource.CRITERIA_TITLE);
            // Purposefully adding same search text twice to fulfill the
            // criteria of
            // title and description search
            params.add(HelperConstants.PERCENTAGE + searchText + HelperConstants.PERCENTAGE);
            params.add(HelperConstants.PERCENTAGE + searchText + HelperConstants.PERCENTAGE);
        }

        if (isPublic) {
            query.append(HelperConstants.SPACE).append(AJEntityOriginalResource.OP_AND).append(HelperConstants.SPACE)
                .append(AJEntityOriginalResource.CRITERIA_PUBLIC);
        }

        query.append(HelperConstants.SPACE).append(AJEntityOriginalResource.CLAUSE_ORDERBY)
            .append(HelperConstants.SPACE).append(sortOn).append(HelperConstants.SPACE).append(order)
            .append(HelperConstants.SPACE).append(AJEntityOriginalResource.CLAUSE_LIMIT_OFFSET);
        params.add(limit);
        params.add(offset);

        LOGGER.debug(
            "SelectQuery:{}, paramSize:{}, standard:{}, searchText:{}, sortOn: {}, order: {}, limit:{}, offset:{}",
            query, params.size(), standard, searchText, sortOn, order, limit, offset);

        LazyList<AJEntityOriginalResource> resourceList =
            AJEntityOriginalResource.findBySQL(query.toString(), params.toArray());
        JsonArray resourceArray = new JsonArray();
        Set<String> ownerIdList = new HashSet<>();
        if (!resourceList.isEmpty()) {
            resourceList.forEach(resource -> ownerIdList.add(resource.getString(AJEntityOriginalResource.CREATOR_ID)));
            String strNull = null;
            resourceList.forEach(resource -> {
                JsonObject resourceJson = new JsonObject(JsonFormatterBuilder
                    .buildSimpleJsonFormatter(false, AJEntityOriginalResource.RESOURCE_LIST).toJson(resource));
                resourceJson.put(AJEntityOriginalResource.CONTENT_FORMAT,
                    AJEntityOriginalResource.RESOURCE_CONTENT_FORMAT);
                resourceJson.put(AJEntityOriginalResource.ORIGINAL_CREATOR_ID, strNull);
                resourceArray.add(resourceJson);
            });
        }

        JsonObject responseBody = new JsonObject();
        responseBody.put(HelperConstants.RESP_JSON_KEY_RESOURCES, resourceArray);
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

    private JsonObject getFiltersJson() {
        return new JsonObject().put(HelperConstants.RESP_JSON_KEY_STANDARD, standard)
            .put(HelperConstants.RESP_JSON_KEY_SORTON, sortOn).put(HelperConstants.RESP_JSON_KEY_ORDER, order)
            .put(HelperConstants.RESP_JSON_KEY_LIMIT, limit).put(HelperConstants.RESP_JSON_KEY_OFFSET, offset);
    }
}
