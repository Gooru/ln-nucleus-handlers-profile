package org.gooru.nucleus.profiles.processors;

import java.util.UUID;

import org.gooru.nucleus.profiles.constants.MessageConstants;
import org.gooru.nucleus.profiles.processors.exceptions.InvalidRequestException;
import org.gooru.nucleus.profiles.processors.exceptions.InvalidUserException;
import org.gooru.nucleus.profiles.processors.repositories.RepoBuilder;
import org.gooru.nucleus.profiles.processors.responses.ExecutionResult;
import org.gooru.nucleus.profiles.processors.responses.MessageResponse;
import org.gooru.nucleus.profiles.processors.responses.MessageResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

class MessageProcessor implements Processor {

  private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class);
  private final Message<Object> message;
  private String userId;
  private JsonObject prefs;
  private JsonObject request;

  public MessageProcessor(Message<Object> message) {
    this.message = message;
  }

  @Override
  public MessageResponse process() {
    MessageResponse result;
    try {
      // Validate the message itself
      ExecutionResult<MessageResponse> validateResult = validateAndInitialize();
      if (validateResult.isCompleted()) {
        return validateResult.result();
      }

      final String msgOp = message.headers().get(MessageConstants.MSG_HEADER_OP);
      switch (msgOp) {
        case MessageConstants.MSG_OP_PROFILE_COURSE_LIST:
          result = processCoursesList();
          break;
        case MessageConstants.MSG_OP_PROFILE_COLLECTION_LIST:
          result = processCollectionsList();
          break;
        case MessageConstants.MSG_OP_PROFILE_ASSESSMENT_LIST:
          result = processAssessmentsList();
          break;
        case MessageConstants.MSG_OP_PROFILE_RESOURCE_LIST:
          result = processResourcesList();
          break;
        case MessageConstants.MSG_OP_PROFILE_QUESTION_LIST:
          result = processQuestionsList();
          break;
        case MessageConstants.MSG_OP_PROFILE_DEMOGRAPHICS_GET:
          result = processDemographicsList();
          break;
        case MessageConstants.MSG_OP_PROFILE_FOLLOW:
          result = processFollow();
          break;
        case MessageConstants.MSG_OP_PROFILE_UNFOLLOW:
          result = processUnfollow();
          break;
        case MessageConstants.MSG_OP_PROFILE_FOLLOWERS_LIST:
          result = processFollowersList();
          break;
        case MessageConstants.MSG_OP_PROFILE_FOLLOWINGS_LIST:
          result = processFollowingsList();
          break;
        case MessageConstants.MSG_OP_PROFILE_COURSE_TAXONOMYCOUNT:
          result = processFetchCourseTaxonomyCount();
          break;
        case MessageConstants.MSG_OP_PROFILE_COLLECTION_TAXONOMY:
          result = processFetchCollectionTaxonomy();
          break;
        case MessageConstants.MSG_OP_PROFILE_ASSESSMENT_TAXONOMY:
          result = processFetchAssessmentTaxonomy();
          break;
        case MessageConstants.MSG_OP_PROFILE_RESOURCE_TAXONOMY:
          result = processFetchResourceTaxonomy();
          break;
        case MessageConstants.MSG_OP_PROFILE_QUESTION_TAXONOMY:
          result = processFetchQuestionTaxonomy();
          break;
        default:
          LOGGER.error("Invalid operation type passed in, not able to handle");
          throw new InvalidRequestException();
      }
      return result;
    } catch (InvalidRequestException e) {
      LOGGER.error("Invalid request");
      return MessageResponseFactory.createInternalErrorResponse(e.getMessage());
    } catch (InvalidUserException e) {
      LOGGER.error("User is not valid");
      return MessageResponseFactory.createForbiddenResponse();
    } catch (Throwable t) {
      LOGGER.error("Exception while processing request");
      return MessageResponseFactory.createInternalErrorResponse(t.getMessage());
    }
  }

  private MessageResponse processCoursesList() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildCourseRepo(context).listCourses();
  }

  private MessageResponse processCollectionsList() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildCollectionRepo(context).listCollections();
  }

  private MessageResponse processAssessmentsList() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildAssessmentRepo(context).listAssessments();
  }

  private MessageResponse processResourcesList() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildResourceRepo(context).listResources();
  }

  private MessageResponse processQuestionsList() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildQuestionRepo(context).listQuestions();
  }

  private MessageResponse processDemographicsList() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildProfileRepo(context).listDemographics();
  }
  
  private MessageResponse processFollow() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildProfileRepo(context).follow();
  }

  private MessageResponse processUnfollow() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildProfileRepo(context).unfollow();
  }

  private MessageResponse processFollowersList() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildProfileRepo(context).listFollowers();
  }

  private MessageResponse processFollowingsList() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildProfileRepo(context).listFollowings();
  }
  
  private MessageResponse processFetchCourseTaxonomyCount() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildCourseRepo(context).fetchTaxonomyCountForCourses();
  }

  private MessageResponse processFetchCollectionTaxonomy() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildCollectionRepo(context).fetchTaxonomyForCollections();
  }

  private MessageResponse processFetchAssessmentTaxonomy() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildAssessmentRepo(context).fetchTaxonomyForAssessments();
  }

  private MessageResponse processFetchResourceTaxonomy() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildResourceRepo(context).fetchTaxonomyForResources();
  }

  private MessageResponse processFetchQuestionTaxonomy() {
    ProcessorContext context = createContext();
    return new RepoBuilder().buildQuestionRepo(context).fetchTaxonomyForQuestions();
  }

  private ProcessorContext createContext() {
    String userIdFromURL = message.headers().get(MessageConstants.USER_ID_FROM_URL);
    return new ProcessorContext(userId, prefs, request, userIdFromURL);
  }

  private ExecutionResult<MessageResponse> validateAndInitialize() {
    if (message == null || !(message.body() instanceof JsonObject)) {
      LOGGER.error("Invalid message received, either null or body of message is not JsonObject ");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(), ExecutionResult.ExecutionStatus.FAILED);
    }

    userId = ((JsonObject) message.body()).getString(MessageConstants.MSG_USER_ID);
    if (!validateUser(userId)) {
      LOGGER.error("Invalid user id passed. Not authorized.");
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse(), ExecutionResult.ExecutionStatus.FAILED);
    }

    prefs = ((JsonObject) message.body()).getJsonObject(MessageConstants.MSG_KEY_PREFS);
    request = ((JsonObject) message.body()).getJsonObject(MessageConstants.MSG_HTTP_BODY);

    if (prefs == null || prefs.isEmpty()) {
      LOGGER.error("Invalid preferences obtained, probably not authorized properly");
      return new ExecutionResult<>(MessageResponseFactory.createForbiddenResponse(), ExecutionResult.ExecutionStatus.FAILED);
    }

    if (request == null) {
      LOGGER.error("Invalid JSON payload on Message Bus");
      return new ExecutionResult<>(MessageResponseFactory.createInvalidRequestResponse(), ExecutionResult.ExecutionStatus.FAILED);
    }

    // All is well, continue processing
    return new ExecutionResult<>(null, ExecutionResult.ExecutionStatus.CONTINUE_PROCESSING);
  }

  private boolean validateUser(String userId) {
    if (userId == null) {
      return false;
    } else if (userId.equalsIgnoreCase(MessageConstants.MSG_USER_ANONYMOUS)) {
      return true;
    } else {
      try {
        UUID.fromString(userId);
        return true;
      } catch (IllegalArgumentException e) {
        return false;
      } catch (Exception e) {
        return false;
      }
    }
  }
}
