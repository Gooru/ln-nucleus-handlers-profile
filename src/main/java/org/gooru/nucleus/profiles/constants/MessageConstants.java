package org.gooru.nucleus.profiles.constants;

public class MessageConstants {

  public static final String MSG_HEADER_OP = "mb.operation";
  public static final String MSG_HEADER_TOKEN = "session.token";
  public static final String MSG_OP_AUTH_WITH_PREFS = "auth.with.prefs";
  public static final String MSG_OP_STATUS = "mb.operation.status";
  public static final String MSG_KEY_PREFS = "prefs";
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
  public static final String MSG_OP_PROFILE_LIST_COURSES = "list.courses";
  public static final String MSG_OP_PROFILE_LIST_COLLECTOIONS = "list.collections";
  public static final String MSG_OP_PROFILE_LIST_ASSESSMENTS = "list.assessments";
  public static final String MSG_OP_PROFILE_LIST_RESOURCES = "list.resources";
  public static final String MSG_OP_PROFILE_LIST_QUESTIONS = "list.questions";
  public static final String MSG_OP_PROFILE_LIST_DEMOGRAPHICS = "list.demographics";
  
  // Containers for different responses
  public static final String RESP_CONTAINER_MBUS = "mb.container";
  public static final String RESP_CONTAINER_EVENT = "mb.event";
  

}
