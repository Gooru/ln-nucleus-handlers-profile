package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.List;

import org.gooru.nucleus.handlers.profiles.constants.HelperConstants;
import org.gooru.nucleus.handlers.profiles.processors.ProcessorContext;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityCollection;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.handlers.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.handlers.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.handlers.profiles.processors.utils.HelperUtility;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class FetchTaxonomyForCollectionsHandler implements DBHandler {

    private final ProcessorContext context;
    private static final Logger LOGGER = LoggerFactory.getLogger(FetchTaxonomyForCollectionsHandler.class);
    private boolean isPublic = false;

    public FetchTaxonomyForCollectionsHandler(ProcessorContext context) {
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

        // identify whether the request is for public or owner
        isPublic = HelperUtility.checkPublic(context);
        LOGGER.debug("isPublic:{}", isPublic);
        LOGGER.debug("checkSanity() OK");
        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ExecutionResult<MessageResponse> executeRequest() {
        List<String> taxonomyList = null;
        if (isPublic) {
            taxonomyList =
                Base.firstColumn(AJEntityCollection.SELECT_TAXONOMY_FOR_COLLECTIONS_PUBLIC, context.userIdFromURL());
        } else {
            taxonomyList = Base.firstColumn(AJEntityCollection.SELECT_TAXONOMY_FOR_COLLECTIONS, context.userIdFromURL(),
                context.userIdFromURL());
        }

        JsonArray taxonomyArray = new JsonArray();
        if (taxonomyList != null) {
            taxonomyList.forEach(value -> taxonomyArray.add(value));
        }

        JsonObject responseBody = new JsonObject();
        responseBody.put(HelperConstants.KEY_STANDARDS, taxonomyArray);
        return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
            ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public boolean handlerReadOnly() {
        return true;
    }
}
