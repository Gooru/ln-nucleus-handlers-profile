package org.gooru.nucleus.profiles.constants;

public final class HelperConstants {

  public static final String REQ_PARAM_PREVIEW = "preview";
  public static final String REQ_PARAM_SEARCH_TEXT = "searchText";
  public static final String REQ_PARAM_LEVEL = "level";
  public static final String REQ_PARAM_SUBJECT = "subject";
  public static final String REQ_PARAM_STANDARD = "standard";

  public static final String TAXONOMY_SEPARATOR = "-";
  public static final String PREFS_SFCODE = "standard_framework_code";

  public static final String RESP_JSON_KEY_COURSES = "courses";
  public static final String RESP_JSON_KEY_COLLECTIONS = "collections";
  public static final String RESP_JSON_KEY_ASSESSMENTS = "assessments";
  public static final String RESP_JSON_KEY_RESOURCES = "resourcess";
  public static final String RESP_JSON_KEY_QUESTIONS = "questions";
  public static final String RESP_JSON_KEY_FOLLOWERS = "followers";
  public static final String RESP_JSON_KEY_FOLLOWINGS = "followings";

  public static final String PERCENTAGE = "%";
  public static final String KEY_LEVELS = "levels";
  public static final String KEY_SUBJECTS = "subjects";
  public static final String KEY_STANDARDS = "standards";

  public static final String SUBJECT_OTHER = "Other";

  private HelperConstants() {
    throw new AssertionError();
  }
}
