package org.gooru.nucleus.handlers.profiles.constants;

public final class HelperConstants {

  public static final String REQ_PARAM_PREVIEW = "preview";
  public static final String REQ_PARAM_LEVEL = "level";
  public static final String REQ_PARAM_SUBJECT = "subject";
  public static final String REQ_PARAM_STANDARD = "standard";
  public static final String REQ_PARAM_SORTON = "sortOn";
  public static final String REQ_PARAM_ORDER = "order";
  public static final String REQ_PARAM_LIMIT = "limit";
  public static final String REQ_PARAM_OFFSET = "offset";
  public static final String REQ_PARAM_FILTERBY = "filterBy";
  public static final String REQ_PARAM_DETAILS = "details";

  public static final String REQ_PARAM_DETAILS_FOLLOWERS = "followers";
  public static final String REQ_PARAM_DETAILS_FOLLOWINGS = "followings";

  public static final String TAXONOMY_SEPARATOR = "-";
  public static final String PREFS_SFCODE = "standard_framework_code";

  public static final String RESP_JSON_KEY_COURSES = "courses";
  public static final String RESP_JSON_KEY_COURSE = "course";
  public static final String RESP_JSON_KEY_COLLECTIONS = "collections";
  public static final String RESP_JSON_KEY_ASSESSMENTS = "assessments";
  public static final String RESP_JSON_KEY_ASSESSMENT = "assessment";
  public static final String RESP_JSON_KEY_RESOURCES = "resources";
  public static final String RESP_JSON_KEY_QUESTIONS = "questions";
  public static final String RESP_JSON_KEY_RUBRICS = "rubrics";
  public static final String RESP_JSON_KEY_FOLLOWERS = "followers";
  public static final String RESP_JSON_KEY_FOLLOWINGS = "followings";
  public static final String RESP_JSON_KEY_USERS = "users";
  public static final String RESP_JSON_KEY_SUBJECTBUCKETS = "subject_buckets";
  public static final String RESP_JSON_KEY_FILTERS = "filters";
  public static final String RESP_JSON_KEY_SUBJECT = "subject";
  public static final String RESP_JSON_KEY_STANDARD = "standard";
  public static final String RESP_JSON_KEY_TAXONOMY = "taxonomy";
  public static final String RESP_JSON_KEY_SORTON = "sortOn";
  public static final String RESP_JSON_KEY_ORDER = "order";
  public static final String RESP_JSON_KEY_LIMIT = "limit";
  public static final String RESP_JSON_KEY_OFFSET = "offset";
  public static final String RESP_JSON_KEY_FILTERBY = "filterBy";
  public static final String RESP_JSON_KEY_ISFOLLOWING = "isFollowing";
  public static final String RESP_JSON_KEY_OWNER_DETAILS = "owner_details";
  public static final String RESP_JSON_KEY_OFFLINE_ACTIVITIES = "offline_activities";

  public static final String PERCENTAGE = "%";
  public static final String SPACE = " ";
  public static final String KEY_LEVELS = "levels";
  public static final String KEY_SUBJECTS = "subjects";
  public static final String KEY_STANDARDS = "standards";

  public static final String SUBJECT_OTHER = "Other";

  public static final String FILTERBY_INCOURSE = "inCourse";
  public static final String FILTERBY_NOT_INCOURSE = "notInCourse";
  public static final String FILTERBY_INCOLLECTION = "inCollection";
  public static final String FILTERBY_NOT_INCOLLECTION = "notInCollection";
  public static final String FILTERBY_ORIGINAL = "original";
  public static final String FILTERBY_COPIES = "copies";

  public static final int DEFAULT_OFFSET = 0;

  private HelperConstants() {
    throw new AssertionError();
  }
}
