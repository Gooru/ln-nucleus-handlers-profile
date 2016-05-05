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

public class ListCoursesHandler implements DBHandler {

    private final ProcessorContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(ListCoursesHandler.class);
    private boolean isPublic;
    private String subjectCode;
    private int limit;
    private int offset;

    public ListCoursesHandler(ProcessorContext context) {
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
        subjectCode = readRequestParam(HelperConstants.REQ_PARAM_SUBJECT);

        limit = getLimit();
        offset = getOffset();

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

        if (isPublic) {
            query = new StringBuilder(AJEntityCourse.SELECT_COURSES_PUBLIC);
            params.add(context.userIdFromURL());
        } else {
            query = new StringBuilder(AJEntityCourse.SELECT_COURSES);
            params.add(context.userIdFromURL());
            params.add(context.userIdFromURL());
        }

        if (subjectCode != null) {
            query.append(HelperConstants.SPACE).append(AJEntityCourse.OP_AND).append(HelperConstants.SPACE)
                .append(AJEntityCourse.CRITERIA_SUBJECTBUCKET);
            params.add(subjectCode);
            query.append(HelperConstants.SPACE).append(AJEntityCourse.CLAUSE_ORDERBY_SEQUENCE_ID);
        } else {
            query.append(HelperConstants.SPACE).append(AJEntityCourse.CLAUSE_ORDERBY_CREATED_AT);
        }
        
        query.append(HelperConstants.SPACE).append(AJEntityCourse.CLAUSE_LIMIT_OFFSET);
        params.add(limit);
        params.add(offset);

        LOGGER.debug("SelectQuery:{}, paramSize:{}, txCode:{}, limit:{}, offset:{}", query, params.size(), subjectCode,
            limit, offset);
        LazyList<AJEntityCourse> courseList = AJEntityCourse.findBySQL(query.toString(), params.toArray());

        JsonArray courseArray = new JsonArray();
        if (!courseList.isEmpty()) {
            List<String> courseIdList = new ArrayList<>();
            courseList.stream().forEach(course -> courseIdList.add(course.getString(AJEntityCourse.ID)));

            List<Map> unitCounts = Base.findAll(AJEntityCourse.SELECT_UNIT_COUNT_FOR_COURSES,
                HelperUtility.toPostgresArrayString(courseIdList));
            Map<String, Integer> unitCountByCourse = new HashMap<>();
            unitCounts.stream().forEach(map -> unitCountByCourse.put(map.get(AJEntityCourse.COURSE_ID).toString(),
                Integer.valueOf(map.get(AJEntityCourse.UNIT_COUNT).toString())));

            courseList.stream().forEach(course -> {
                Integer unitCount = unitCountByCourse.get(course.getString(AJEntityCourse.ID));
                courseArray.add(new JsonObject(
                    JsonFormatterBuilder.buildSimpleJsonFormatter(false, AJEntityCourse.COURSE_LIST).toJson(course))
                        .put(AJEntityCourse.UNIT_COUNT, unitCount != null ? unitCount : 0));
            });
        }

        JsonObject responseBody = new JsonObject();
        responseBody.put(HelperConstants.RESP_JSON_KEY_COURSES, courseArray);
        responseBody.put(HelperConstants.RESP_JSON_KEY_OWNER_DETAILS, getOwnerDetails(courseList));
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
        return new JsonObject().put(HelperConstants.RESP_JSON_KEY_SUBJECT, subjectCode)
            .put(HelperConstants.RESP_JSON_KEY_LIMIT, limit).put(HelperConstants.RESP_JSON_KEY_OFFSET, offset);
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
    private static JsonArray getOwnerDetails(LazyList<AJEntityCourse> courseList) {
        Set<String> ownerIdList = new HashSet<>();
        courseList.stream().forEach(course -> ownerIdList.add(course.getString(AJEntityCourse.OWNER_ID)));

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
