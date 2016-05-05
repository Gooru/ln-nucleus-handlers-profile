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
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityCollection;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityCourse;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUserDemographic;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUserIdentity;
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

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ListCollectionsHandler implements DBHandler {

    private final ProcessorContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(ListCollectionsHandler.class);
    private boolean isPublic = false;
    private String searchText;
    private String standard;
    private String sortOn;
    private String order;
    private int limit;
    private int offset;
    private String filterBy;

    public ListCollectionsHandler(ProcessorContext context) {
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

        isPublic = checkPublic();
        searchText = readRequestParam(HelperConstants.REQ_PARAM_SEARCH_TEXT);

        String sortOnFromRequest = readRequestParam(HelperConstants.REQ_PARAM_SORTON);
        sortOn = sortOnFromRequest != null ? sortOnFromRequest : AJEntityCollection.DEFAULT_SORTON;
        if (!AJEntityCollection.VALID_SORTON_FIELDS.contains(sortOn)) {
            LOGGER.warn("Invalid value provided for sort");
            return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid value for sort"),
                ExecutionStatus.FAILED);
        }

        String orderFromRequest = readRequestParam(HelperConstants.REQ_PARAM_ORDER);
        order = orderFromRequest != null ? orderFromRequest : AJEntityCollection.DEFAULT_ORDER;
        if (!AJEntityCollection.VALID_ORDER_FIELDS.contains(order)) {
            LOGGER.warn("Invalid value provided for order");
            return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid value for order"),
                ExecutionStatus.FAILED);
        }

        limit = getLimit();
        offset = getOffset();

        filterBy = readRequestParam(HelperConstants.REQ_PARAM_FILTERBY);
        standard = readRequestParam(HelperConstants.REQ_PARAM_STANDARD);

        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        StringBuilder query;
        List<Object> params = new ArrayList<>();

        if (standard != null) {
            query = new StringBuilder(AJEntityCollection.SELECT_COLLECTIONS_BY_TAXONOMY);
            params.add(standard + HelperConstants.PERCENTAGE);
        } else {
            query = new StringBuilder(AJEntityCollection.SELECT_COLLECTIONS);
        }

        if (searchText != null) {
            query.append(HelperConstants.SPACE).append(AJEntityCollection.OP_AND).append(HelperConstants.SPACE)
                .append(AJEntityCollection.CRITERIA_TITLE);
            params.add(HelperConstants.PERCENTAGE + searchText + HelperConstants.PERCENTAGE);
        }

        if (isPublic) {
            query.append(HelperConstants.SPACE).append(AJEntityCollection.OP_AND).append(HelperConstants.SPACE)
                .append(AJEntityCollection.CRITERIA_PUBLIC);
            params.add(context.userIdFromURL());
        } else {
            query.append(HelperConstants.SPACE).append(AJEntityCollection.OP_AND).append(HelperConstants.SPACE)
                .append(AJEntityCollection.CRITERIA_MYPROFILE);
            params.add(context.userIdFromURL()); // for owner
            params.add(context.userIdFromURL()); // for collaborator
        }

        if (filterBy != null) {
            if (filterBy.equalsIgnoreCase(HelperConstants.FILTERBY_INCOURSE)) {
                query.append(HelperConstants.SPACE).append(AJEntityCollection.OP_AND).append(HelperConstants.SPACE)
                    .append(AJEntityCollection.CRITERIA_INCOURSE);
            } else if (filterBy.equalsIgnoreCase(HelperConstants.FILTERBY_NOT_INCOURSE)) {
                query.append(HelperConstants.SPACE).append(AJEntityCollection.OP_AND).append(HelperConstants.SPACE)
                    .append(AJEntityCollection.CRITERIA_NOT_INCOURSE);
            }
        }

        query.append(HelperConstants.SPACE).append(AJEntityCollection.CLAUSE_ORDERBY).append(HelperConstants.SPACE)
            .append(sortOn).append(HelperConstants.SPACE).append(order).append(HelperConstants.SPACE)
            .append(AJEntityCollection.CLAUSE_LIMIT_OFFSET);
        params.add(limit);
        params.add(offset);

        LOGGER.debug(
            "SelectQuery:{}, paramSize:{}, standard:{}, searchText:{}, filterBy:{}, sortOn: {}, order: {}, limit:{}, offset:{}",
            query, params.size(), standard, searchText, filterBy, sortOn, order, limit, offset);

        LazyList<AJEntityCollection> collectionList = AJEntityCollection.findBySQL(query.toString(), params.toArray());
        JsonArray collectionArray = new JsonArray();
        if (!collectionList.isEmpty()) {
            List<String> collectionIdList = new ArrayList<>();
            collectionList.stream()
                .forEach(collection -> collectionIdList.add(collection.getString(AJEntityCollection.ID)));

            List<Map> resourceCounts = Base.findAll(AJEntityCollection.SELECT_RESOURCES_COUNT_FOR_COLLECTION,
                HelperUtility.toPostgresArrayString(collectionIdList));
            Map<String, Integer> resourceCountByCollection = new HashMap<>();
            resourceCounts.stream()
                .forEach(map -> resourceCountByCollection.put(map.get(AJEntityCollection.COLLECTION_ID).toString(),
                    Integer.valueOf(map.get(AJEntityCollection.RESOURCE_COUNT).toString())));

            List<Map> questionCounts = Base.findAll(AJEntityCollection.SELECT_QUESTIONS_COUNT_FOR_COLLECTION,
                HelperUtility.toPostgresArrayString(collectionIdList));
            Map<String, Integer> questionCountByCollection = new HashMap<>();
            questionCounts.stream()
                .forEach(map -> questionCountByCollection.put(map.get(AJEntityCollection.COLLECTION_ID).toString(),
                    Integer.valueOf(map.get(AJEntityCollection.QUESTION_COUNT).toString())));

            List<String> courseIdList = new ArrayList<>();
            collectionList.stream()
                .filter(collection -> collection.getString(AJEntityCollection.COURSE_ID) != null
                    && !collection.getString(AJEntityCollection.COURSE_ID).isEmpty())
                .forEach(collection -> courseIdList.add(collection.getString(AJEntityCollection.COURSE_ID)));
            LazyList<AJEntityCourse> courseList =
                AJEntityCourse.findBySQL(AJEntityCourse.SELECT_COURSE_FOR_COLLECTION,
                    HelperUtility.toPostgresArrayString(courseIdList));
            Map<String, AJEntityCourse> courseMap = new HashMap<>();
            courseList.stream().forEach(course -> courseMap.put(course.getString(AJEntityCourse.ID), course));

            collectionList.forEach(collection -> {
                JsonObject result = new JsonObject(JsonFormatterBuilder
                    .buildSimpleJsonFormatter(false, AJEntityCollection.COLLECTION_LIST).toJson(collection));
                String courseId = collection.getString(AJEntityCollection.COURSE_ID);
                if (courseId != null && !courseId.isEmpty()) {
                    AJEntityCourse course = courseMap.get(courseId);
                    result.put(HelperConstants.RESP_JSON_KEY_COURSE, new JsonObject(JsonFormatterBuilder
                        .buildSimpleJsonFormatter(false, AJEntityCourse.COURSE_FIELDS_FOR_COLLECTION).toJson(course)));
                }
                
                String collectionId = collection.getString(AJEntityCollection.ID);
                Integer resourceCount = resourceCountByCollection.get(collectionId);
                Integer questionCount = questionCountByCollection.get(collectionId);
                result.put(AJEntityCollection.RESOURCE_COUNT, resourceCount != null ? resourceCount : 0);
                result.put(AJEntityCollection.QUESTION_COUNT, questionCount != null ? questionCount : 0);
                collectionArray.add(result);
            });
        }

        JsonObject responseBody = new JsonObject();
        responseBody.put(HelperConstants.RESP_JSON_KEY_COLLECTIONS, collectionArray);
        responseBody.put(HelperConstants.RESP_JSON_KEY_OWNER_DETAILS, getOwnerDetails(collectionList));
        responseBody.put(HelperConstants.RESP_JSON_KEY_FILTERS, getFiltersJson());
        return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
            ExecutionStatus.SUCCESSFUL);
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
        // Assuming that preview parameter only exists when user want to view
        // his
        // profile as public
        return Boolean.parseBoolean(preview);
    }

    private JsonObject getFiltersJson() {
        return new JsonObject().put(HelperConstants.RESP_JSON_KEY_STANDARD, standard)
            .put(HelperConstants.RESP_JSON_KEY_FILTERBY, filterBy).put(HelperConstants.RESP_JSON_KEY_SORTON, sortOn)
            .put(HelperConstants.RESP_JSON_KEY_ORDER, order).put(HelperConstants.RESP_JSON_KEY_LIMIT, limit)
            .put(HelperConstants.RESP_JSON_KEY_OFFSET, offset);
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

    @SuppressWarnings("rawtypes")
    private static JsonArray getOwnerDetails(LazyList<AJEntityCollection> collectionList) {
        Set<String> ownerIdList = new HashSet<>();
        collectionList.stream()
            .forEach(collection -> ownerIdList.add(collection.getString(AJEntityCollection.OWNER_ID)));

        LazyList<AJEntityUserDemographic> userDemographics = AJEntityUserDemographic.findBySQL(
            AJEntityUserDemographic.SELECT_DEMOGRAPHICS_MULTIPLE, HelperUtility.toPostgresArrayString(ownerIdList));
        List<Map> usernames = Base.findAll(AJEntityUserIdentity.SELECT_USERNAME_MULIPLE,
            HelperUtility.toPostgresArrayString(ownerIdList));
        Map<String, String> usernamesById = new HashMap<>();
        usernames.stream().forEach(username -> usernamesById.put(username.get(AJEntityUserIdentity.USER_ID).toString(),
            username.get(AJEntityUserIdentity.USERNAME).toString()));

        JsonArray userDetailsArray = new JsonArray();
        if (!userDemographics.isEmpty()) {
            userDemographics.forEach(user -> {
                JsonObject userDemographic = new JsonObject(JsonFormatterBuilder
                    .buildSimpleJsonFormatter(false, AJEntityUserDemographic.DEMOGRAPHIC_FIELDS).toJson(user));
                userDemographic.put(AJEntityUserIdentity.USERNAME,
                    usernamesById.get(user.getString(AJEntityUserDemographic.ID)));
                userDetailsArray.add(userDemographic);
            });
        }

        return userDetailsArray;
    }
}
