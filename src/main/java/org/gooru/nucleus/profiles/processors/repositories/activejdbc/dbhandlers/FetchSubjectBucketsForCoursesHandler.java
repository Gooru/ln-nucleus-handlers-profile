package org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbhandlers;

import java.util.List;

import org.gooru.nucleus.profiles.constants.HelperConstants;
import org.gooru.nucleus.profiles.processors.ProcessorContext;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.dbauth.AuthorizerBuilder;
import org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities.AJEntityCourse;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult.ExecutionStatus;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.profiles.processors.responses.MessageResponseFactory;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class FetchSubjectBucketsForCoursesHandler implements DBHandler {

  private final ProcessorContext context;
  private static final Logger LOGGER = LoggerFactory.getLogger(FetchSubjectBucketsForCoursesHandler.class);
  private boolean isPublic = false;

  public FetchSubjectBucketsForCoursesHandler(ProcessorContext context) {
    this.context = context;
  }

  @Override
  public ExecutionResult<MessageResponse> checkSanity() {
    if (context.userIdFromURL() == null || context.userIdFromURL().isEmpty()) {
      LOGGER.warn("Invalid user id");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse("Invalid user id"), ExecutionStatus.FAILED);
    }

    // identify whether the request is for public or owner
    isPublic = checkPublic();
    String standardFramework = context.prefs().getString(HelperConstants.PREFS_SFCODE);

    LOGGER.debug("checkSanity() OK");
    return new ExecutionResult<>(null, ExecutionStatus.CONTINUE_PROCESSING);
  }

  @Override
  public ExecutionResult<MessageResponse> validateRequest() {
    return AuthorizerBuilder.buildUserAuthorizer(context).authorize(null);
  }

  @Override
  public ExecutionResult<MessageResponse> executeRequest() {
    StringBuffer query = new StringBuffer(AJEntityCourse.SELECT_SUBJECT_BUCKETS);

    if (isPublic) {
      query.append(AJEntityCourse.OP_AND).append(AJEntityCourse.CRITERIA_PUBLIC);
    }

    List subjectBuckets = Base.firstColumn(query.toString(), context.userIdFromURL());

    JsonArray responseArray = new JsonArray();
    for (Object bucket : subjectBuckets) {
      if (bucket != null && !bucket.toString().isEmpty()) {
        responseArray.add(bucket.toString());
      } else {
        responseArray.add(HelperConstants.SUBJECT_OTHER);
      }
    }

    return new ExecutionResult<>(
            MessageResponseFactory.createGetResponse(new JsonObject().put(HelperConstants.RESP_JSON_KEY_SUBJECTBUCKETS, responseArray)),
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
    // Assuming that preview parameter only exists when user want to view his
    // profile as public
    return Boolean.parseBoolean(preview);
  }

}
