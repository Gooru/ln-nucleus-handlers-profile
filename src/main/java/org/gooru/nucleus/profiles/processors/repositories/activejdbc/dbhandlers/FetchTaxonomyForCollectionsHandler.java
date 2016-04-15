package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.gooru.nucleus.profiles.constants.HelperConstants;
import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityCollection;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.profiles.processors.responses.MessageResponseFactory;
import org.gooru.nucleus.profiles.processors.utils.HelperUtility;
import org.javalite.activejdbc.LazyList;
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
        isPublic = checkPublic();
        LOGGER.debug("isPublic:{}", isPublic);
        LOGGER.debug("checkSanity() OK");
        return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
    }

    @Override
    public ExecutionResult<MessageResponse> validateRequest() {
        return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
    }

    @Override
    public ExecutionResult<MessageResponse> executeRequest() {

        StringBuilder query = new StringBuilder(AJEntityCollection.SELECT_COLLECTIONS);
        if (isPublic) {
            query.append(HelperConstants.SPACE).append(AJEntityCollection.OP_AND).append(HelperConstants.SPACE)
                .append(AJEntityCollection.CRITERIA_PUBLIC);
        }

        LazyList<AJEntityCollection> collectionList =
            AJEntityCollection.findBySQL(query.toString(), context.userIdFromURL());

        Map<String, Set<String>> taxonomyList = new HashMap<>();
        taxonomyList.put(HelperConstants.KEY_STANDARDS, new HashSet<>());

        for (AJEntityCollection ajEntityCollection : collectionList) {
            String taxonomy = ajEntityCollection.getString(AJEntityCollection.TAXONOMY);
            if (taxonomy != null && !taxonomy.isEmpty()) {
                JsonArray taxonomyArray = new JsonArray(taxonomy);
                for (int i = 0; i < taxonomyArray.size(); i++) {
                    String taxonomyCode = taxonomyArray.getString(i);
                    StringTokenizer tokenizer = new StringTokenizer(taxonomyCode, HelperConstants.TAXONOMY_SEPARATOR);

                    // Replying on number of token in taxonomy tag
                    // 4 for standard
                    if (tokenizer.countTokens() == 4) {
                        taxonomyList.get(HelperConstants.KEY_STANDARDS).add(taxonomyCode);
                    }
                }
            } else {
                taxonomyList.get(HelperConstants.KEY_SUBJECTS).add(HelperConstants.SUBJECT_OTHER);
            }
        }

        JsonObject responseBody = new JsonObject();
        Set<String> keySet = taxonomyList.keySet();
        for (Map.Entry<String, Set<String>> stringSetEntry : taxonomyList.entrySet()) {
            JsonArray tempArray = new JsonArray();
            stringSetEntry.getValue().forEach(tempArray::add);
            responseBody.put(stringSetEntry.getKey(), tempArray);
        }
        // TODO: Transform the taxonomy before reply
        return new ExecutionResult<>(MessageResponseFactory.createGetResponse(responseBody),
            ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public boolean handlerReadOnly() {
        return true;
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

}
