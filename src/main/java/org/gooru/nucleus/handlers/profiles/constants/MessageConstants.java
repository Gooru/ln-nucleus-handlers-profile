package org.gooru.nucleus.handlers.profiles.constants;

public final class MessageConstants {

  public static final String MSG_HEADER_OP = "mb.operation";
  public static final String MSG_HEADER_TOKEN = "session.token";
  public static final String MSG_OP_STATUS = "mb.operation.status";
  public static final String MSG_KEY_SESSION = "session";
  public static final String MSG_OP_STATUS_SUCCESS = "success";
  public static final String MSG_OP_STATUS_ERROR = "error";
  public static final String MSG_OP_STATUS_VALIDATION_ERROR = "error.validation";
  public static final String MSG_USER_ANONYMOUS = "anonymous";
  public static final String MSG_USER_ID = "user_id";
  public static final String MSG_HTTP_STATUS = "http.status";
  public static final String MSG_HTTP_BODY = "http.body";
  public static final String MSG_HTTP_RESPONSE = "http.response";
  public static final String MSG_HTTP_ERROR = "http.error";
  public static final String MSG_HTTP_VALIDATION_ERROR = "http.validation.error";
  public static final String MSG_HTTP_HEADERS = "http.headers";
  public static final String MSG_MESSAGE = "message";

  // Operation names: Also need to be updated in corresponding handlers
  public static final String MSG_OP_PROFILE_COURSE_LIST = "profile.course.list";
  public static final String MSG_OP_PROFILE_COLLECTION_LIST = "profile.collection.list";
  public static final String MSG_OP_PROFILE_ASSESSMENT_LIST = "profile.assessment.list";
  public static final String MSG_OP_PROFILE_RESOURCE_LIST = "profile.resource.list";
  public static final String MSG_OP_PROFILE_QUESTION_LIST = "profile.question.list";
  public static final String MSG_OP_PROFILE_RUBRIC_LIST = "profile.rubric.list";
  public static final String MSG_OP_PROFILE_DEMOGRAPHICS_GET = "profile.demographics.get";
  public static final String MSG_OP_PROFILE_FOLLOW = "profile.follow";
  public static final String MSG_OP_PROFILE_UNFOLLOW = "profile.unfollow";
  public static final String MSG_OP_PROFILE_NETWORK_GET = "profile.network.get";
  public static final String MSG_OP_PROFILE_SEARCH = "profile.search";
  public static final String MSG_OP_PROFILE_PREFERENCE_GET = "profile.preference.get";
  public static final String MSG_OP_PROFILE_PREFERENCE_UPDATE = "profile.preference.update";
  public static final String MSG_OP_PROFILE_STATE_UPDATE = "profile.state.update";
  public static final String MSG_OP_PROFILE_OFFLINE_ACTIVITES_LIST =
      "profile.offline.activities.list";

  // Containers for different responses
  public static final String RESP_CONTAINER_MBUS = "mb.container";
  public static final String RESP_CONTAINER_EVENT = "mb.event";
  public static final String USER_ID_FROM_URL = "userId";

  private MessageConstants() {
    throw new AssertionError();
  }
}
