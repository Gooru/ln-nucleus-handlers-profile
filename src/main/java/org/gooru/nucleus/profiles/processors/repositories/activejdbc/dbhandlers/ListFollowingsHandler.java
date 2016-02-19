package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityUserDemographic;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityUserNetwork;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.javalite.activejdbc.LazyList;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.profiles.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ListFollowingsHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(ListFollowingsHandler.class);

  public ListFollowingsHandler(ProcessorContext context) {
    this.context = context;
  }
  
  @Override
  public ExecutionResult<MessageResponse> checkSanity() {

    if (context.userIdFromURL() == null || context.userIdFromURL().isEmpty()) {
      LOGGER.warn("Invalid user id");
      return new ExecutionResult<MessageResponse>(MessageResponseFactory.createInvalidRequestResponse("Invalid user id"), ExecutionStatus.FAILED);
    }

    LOGGER.debug("checkSanity() OK");
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    LazyList<AJEntityUserNetwork> followers = AJEntityUserNetwork.findBySQL(AJEntityUserNetwork.SELECT_FOLLOWINGS, context.userIdFromURL());
    JsonArray userDemographics = new JsonArray();
    for (AJEntityUserNetwork ajEntityUserNetwork : followers) {
      LazyList<AJEntityUserDemographic> demographics = AJEntityUserDemographic.findBySQL(AJEntityUserDemographic.SELECT_DEMOGRAPHICS,
              ajEntityUserNetwork.getString(AJEntityUserNetwork.USER_ID));
      if (!demographics.isEmpty()) {
        userDemographics.add(new JsonObject(
                new JsonFormatterBuilder().buildSimpleJsonFormatter(false, AJEntityUserDemographic.DEMOGRAPHIC_FIELDS).toJson(demographics.get(0))));
      }
    }
    return new ExecutionResult<>(MessageResponseFactory.createGetResponse(new JsonObject().put("followings", userDemographics)),
            ExecutionStatus.SUCCESSFUL);
  }

  @Override
  public boolean handlerReadOnly() {
    return true;
  }

}
