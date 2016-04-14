package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.gooru.nucleus.profiles.constants.HelperConstants;
import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityCollection;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityUserDemographic;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityUserIdentity;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityUserNetwork;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.profiles.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class GetNetworkHandler implements DBHandler {

    private final ProcessorContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(GetNetworkHandler.class);
    private String details;

    public GetNetworkHandler(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public ExecutionResult<MessageResponse> checkSanity() {

        if (context.userIdFromURL() == null || context.userIdFromURL().isEmpty()) {
            LOGGER.warn("Invalid user id");
            return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid user id"),
                ExecutionStatus.FAILED);
        }

        details = readRequestParam(HelperConstants.REQ_PARAM_DETAILS);

        LOGGER.debug("checkSanity() OK");
        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        List followers = Base.firstColumn(AJEntityUserNetwork.SELECT_FOLLOWERS, context.userIdFromURL());
        JsonArray followersArray = new JsonArray();
        followers.stream().forEach(value -> followersArray.add(value.toString()));

        List followings = Base.firstColumn(AJEntityUserNetwork.SELECT_FOLLOWINGS, context.userIdFromURL());
        JsonArray followingsArray = new JsonArray();
        followings.stream().forEach(value -> followingsArray.add(value.toString()));

        JsonArray detailsArray = new JsonArray();
        if (details.equalsIgnoreCase(HelperConstants.REQ_PARAM_DETAILS_FOLLOWERS)) {
            Map<String, AJEntityUserDemographic> userDemographicsMap = getDemographics(followers);
            Map<String, Integer> followersCountMap = getFollowersCount(followers);
            Map<String, Integer> followingsCountMap = getFollowingsCount(followers);

            followers.stream()
                .forEach(follower -> detailsArray.add(new JsonObject(new JsonFormatterBuilder()
                    .buildSimpleJsonFormatter(false, AJEntityUserDemographic.DEMOGRAPHIC_FIELDS)
                    .toJson(userDemographicsMap.get(follower.toString())))
                        .put(AJEntityUserNetwork.FOLLOWERS_COUNT, followersCountMap.get(follower.toString()))
                        .put(AJEntityUserNetwork.FOLLOWINGS_COUNT, followingsCountMap.get(follower.toString()))));
        } else if (details.equalsIgnoreCase(HelperConstants.REQ_PARAM_DETAILS_FOLLOWINGS)) {
            Map<String, AJEntityUserDemographic> userDemographicsMap = getDemographics(followings);
            Map<String, Integer> followersCountMap = getFollowersCount(followings);
            Map<String, Integer> followingsCountMap = getFollowingsCount(followings);

            followings.stream()
                .forEach(follower -> detailsArray.add(new JsonObject(new JsonFormatterBuilder()
                    .buildSimpleJsonFormatter(false, AJEntityUserDemographic.DEMOGRAPHIC_FIELDS)
                    .toJson(userDemographicsMap.get(follower.toString())))
                        .put(AJEntityUserNetwork.FOLLOWERS_COUNT, followersCountMap.get(follower.toString()))
                        .put(AJEntityUserNetwork.FOLLOWINGS_COUNT, followingsCountMap.get(follower.toString()))));
        }

        JsonObject responseBody = new JsonObject();
        responseBody.put(HelperConstants.RESP_JSON_KEY_FOLLOWERS, followersArray);
        responseBody.put(HelperConstants.RESP_JSON_KEY_FOLLOWINGS, followingsArray);
        responseBody.put(HelperConstants.REQ_PARAM_DETAILS, detailsArray);
        return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
            ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public boolean handlerReadOnly() {
        return true;
    }

    private Map<String, AJEntityUserDemographic> getDemographics(List input) {
        LazyList<AJEntityUserDemographic> userDemographics = AJEntityUserDemographic
            .findBySQL(AJEntityUserDemographic.SELECT_DEMOGRAPHICS_MULTIPLE, listToPostgresArrayString(input));
        Map<String, AJEntityUserDemographic> userDemographicsMap = new HashMap<>();
        userDemographics.forEach(user -> userDemographicsMap.put(user.getId().toString(), user));
        return userDemographicsMap;
    }

    private Map<String, Integer> getFollowersCount(List input) {
        List<Map> followersCount =
            Base.findAll(AJEntityUserNetwork.SELECT_FOLLOWERS_COUNT_MULTIPLE, listToPostgresArrayString(input));
        Map<String, Integer> followersCountMap = new HashMap<>();
        followersCount.stream().forEach(map -> followersCountMap.put(map.get(AJEntityUserNetwork.USER_ID).toString(),
            Integer.valueOf(map.get(AJEntityUserNetwork.FOLLOWERS_COUNT).toString())));
        return followersCountMap;
    }

    private Map<String, Integer> getFollowingsCount(List input) {
        List<Map> followingsCount =
            Base.findAll(AJEntityUserNetwork.SELECT_FOLLOWINGS_COUNT_MULTIPLE, listToPostgresArrayString(input));
        Map<String, Integer> followingsCountMap = new HashMap<>();
        followingsCount.stream()
            .forEach(map -> followingsCountMap.put(map.get(AJEntityUserNetwork.FOLLOW_ON_USER_ID).toString(),
                Integer.valueOf(map.get(AJEntityUserNetwork.FOLLOWINGS_COUNT).toString())));
        return followingsCountMap;
    }

    private String readRequestParam(String param) {
        JsonArray requestParams = context.request().getJsonArray(param);
        if (requestParams == null || requestParams.isEmpty()) {
            return null;
        }

        String value = requestParams.getString(0);
        return (value != null && !value.isEmpty()) ? value : null;
    }

    private String listToPostgresArrayString(List input) {
        int approxSize = ((input.size() + 1) * 36); // Length of UUID is around
                                                    // 36
                                                    // chars
        Iterator it = input.iterator();
        if (!it.hasNext()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder(approxSize);
        sb.append('{');
        for (;;) {
            String s = it.next().toString();
            sb.append('"').append(s).append('"');
            if (!it.hasNext()) {
                return sb.append('}').toString();
            }
            sb.append(',');
        }
    }
}
