package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.UUID;

import org.gooru.nucleus.handlers.profiles.constants.HelperConstants;
import org.gooru.nucleus.handlers.profiles.constants.MessageConstants;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUserNetwork;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUsers;
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

import io.vertx.core.json.JsonObject;

public class GetDemographicsHandler implements DBHandler {

    private final ProcessorContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(GetDemographicsHandler.class);

    private AJEntityUsers user;

    public GetDemographicsHandler(ProcessorContext context) {
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

        LOGGER.debug("checkSanity() OK");
        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        user = AJEntityUsers.findById(UUID.fromString(context.userIdFromURL()));
        if (user == null) {
            LOGGER.warn("user not found in database");
            return new ExecutionResult<>(MessageResponseFactory.createNotFoundResponse(), ExecutionStatus.FAILED);
        }

        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        JsonObject responseBody =
            new JsonObject(JsonFormatterBuilder.buildSimpleJsonFormatter(false, AJEntityUsers.ALL_FIELDS).toJson(user));

        Long followers =
            Base.count(AJEntityUserNetwork.TABLE, AJEntityUserNetwork.SELECT_FOLLOWERS_COUNT, context.userIdFromURL());
        Long followings =
            Base.count(AJEntityUserNetwork.TABLE, AJEntityUserNetwork.SELECT_FOLLOWINGS_COUNT, context.userIdFromURL());

        responseBody.put(HelperConstants.RESP_JSON_KEY_FOLLOWERS, followers);
        responseBody.put(HelperConstants.RESP_JSON_KEY_FOLLOWINGS, followings);

        // Check whether user is following other user
        // In case own profile it should be false
        boolean isFollowing = false;
        LOGGER.debug("current logged in user: " + context.userId());
        if (!context.userId().equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)
            && !context.userId().equalsIgnoreCase(context.userIdFromURL())) {
            LazyList<AJEntityUserNetwork> userNetwork = AJEntityUserNetwork.where(AJEntityUserNetwork.CHECK_IF_FOLLOWER,
                context.userId(), context.userIdFromURL());
            if (!userNetwork.isEmpty()) {
                isFollowing = true;
            }
        }
        responseBody.put(HelperConstants.RESP_JSON_KEY_ISFOLLOWING, isFollowing);

        return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
            ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public boolean handlerReadOnly() {
        return true;
    }

}
