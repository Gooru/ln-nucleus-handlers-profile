package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUsers;
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

/**
 * @author szgooru Created On: 20-Jan-2017
 */
public class SearchProfileHandler implements DBHandler {

    private final ProcessorContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchProfileHandler.class);

    private String searchValue;
    private SearchType searchType = SearchType.NONE;
    private List<String> userIds;

    public SearchProfileHandler(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> checkSanity() {
        JsonArray inputArray = context.request().getJsonArray("userids");
        searchValue = inputArray != null ? inputArray.getString(0) : null;
        if (searchValue == null || searchValue.isEmpty()) {
            inputArray = context.request().getJsonArray("username");
            searchValue = inputArray != null ? inputArray.getString(0) : null;
            if (searchValue == null || searchValue.isEmpty()) {
                inputArray = context.request().getJsonArray("email");
                searchValue = inputArray != null ? inputArray.getString(0) : null;
                if (searchValue != null && !searchValue.isEmpty()) {
                    searchType = SearchType.EMAIL;
                }
            } else {
                searchType = SearchType.USERNAME;
            }
        } else {
            searchType = SearchType.USERIDS;
        }

        if (searchType == SearchType.NONE || searchValue == null || searchValue.isEmpty()) {
            LOGGER.warn("Invalid search criteria");
            return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid search criteria"),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        LOGGER.debug("checkSanity: OK");
        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        LOGGER.debug("Search BY: {}, SearchValue: {}", searchType, searchValue);
        if (searchType == SearchType.USERIDS) {
            userIds = Arrays.asList(searchValue.split(","));
            if (userIds.size() > 50) {
                return new ExecutionResult<>(
                    MessageResponseFactory.createInvalidRequestResponse("50 userids accepted per request."),
                    ExecutionResult.ExecutionStatus.FAILED);
            }

            for (String userId : userIds) {
                try {
                    UUID.fromString(userId);
                } catch (IllegalArgumentException e) {
                    return new ExecutionResult<>(
                        MessageResponseFactory.createInvalidRequestResponse("Invalid user id passed in the URL"),
                        ExecutionResult.ExecutionStatus.FAILED);
                }
            }
        }
        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        switch (searchType) {
        case USERNAME:
            return searchByUsername(searchValue.toLowerCase());
        case EMAIL:
            return searchByEmail(searchValue.toLowerCase());
        case USERIDS:
            return searchByUserids(userIds);
        default:
            LOGGER.warn("Search criteria did not match the specified ones");
        }
        JsonObject responseBody = new JsonObject();
        return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
            ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public boolean handlerReadOnly() {
        return true;
    }

    private enum SearchType {
        NONE, USERIDS, USERNAME, EMAIL;
    }

    private ExecutionResult<MessageResponse> searchByUsername(String username) {
        LazyList<AJEntityUsers> users = AJEntityUsers.find(AJEntityUsers.SELECT_BY_USERNAME, username);
        if (users.isEmpty()) {
            return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse(),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        JsonObject result = new JsonObject(
            JsonFormatterBuilder.buildSimpleJsonFormatter(false, AJEntityUsers.ALL_FIELDS).toJson(users.get(0)));
        return new ExecutionResult<MessageResponse>(MessageResponseFactory.createGetResponse(result),
            ExecutionResult.ExecutionStatus.SUCCESSFUL);
    }

    private ExecutionResult<MessageResponse> searchByEmail(String email) {
        LazyList<AJEntityUsers> users = AJEntityUsers.find(AJEntityUsers.SELECT_BY_EMAIL, email);
        if (users.isEmpty()) {
            return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse(),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        JsonObject result = new JsonObject(
            JsonFormatterBuilder.buildSimpleJsonFormatter(false, AJEntityUsers.ALL_FIELDS).toJson(users.get(0)));
        return new ExecutionResult<MessageResponse>(MessageResponseFactory.createGetResponse(result),
            ExecutionResult.ExecutionStatus.SUCCESSFUL);
    }

    private ExecutionResult<MessageResponse> searchByUserids(List<String> userIds) {
        LazyList<AJEntityUsers> users =
            AJEntityUsers.find(AJEntityUsers.SELECT_BY_IDS, HelperUtility.toPostgresArrayString(userIds));
        if (users.isEmpty()) {
            return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse(),
                ExecutionResult.ExecutionStatus.FAILED);
        }

        JsonObject result = new JsonObject().put("users", new JsonArray(
            JsonFormatterBuilder.buildSimpleJsonFormatter(false, AJEntityUsers.ALL_FIELDS).toJson(users)));
        return new ExecutionResult<MessageResponse>(MessageResponseFactory.createGetResponse(result),
            ExecutionResult.ExecutionStatus.SUCCESSFUL);
    }

}
