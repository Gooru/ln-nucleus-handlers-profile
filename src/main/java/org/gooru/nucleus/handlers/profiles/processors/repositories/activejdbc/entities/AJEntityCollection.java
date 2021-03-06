package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("collection")
public class AJEntityCollection extends Model {

  public static final String ID = "id";
  private static final String TITLE = "title";
  private static final String UPDATED_AT = "updated_at";
  private static final String FORMAT = "format";
  private static final String PUBLISH_STATUS = "publish_status";
  private static final String THUMBNAIL = "thumbnail";
  private static final String TAXONOMY = "taxonomy";
  private static final String COLLABORATOR = "collaborator";
  private static final String VISIBLE_ON_PROFILE = "visible_on_profile";
  public static final String COURSE_ID = "course_id";
  private static final String LEARNING_OBJECTIVE = "learning_objective";
  public static final String OWNER_ID = "owner_id";
  private static final String ORIGINAL_CREATOR_ID = "original_creator_id";
  private static final String PRIMARY_LANGUAGE = "primary_language";
  private static final String MAX_SCORE = "max_score";
  private static final String DURATION_HOURS = "duration_hours";
  private static final String REFERENCE = "reference";
  private static final String EXEMPLAR = "exemplar";

  public static final String COLLECTION_ID = "collection_id";
  public static final String RESOURCE_COUNT = "resource_count";
  public static final String QUESTION_COUNT = "question_count";
  public static final String OA_ID = "oa_id";
  public static final String TASK_COUNT = "task_count";

  public static final String SELECT_COLLECTIONS =
      "SELECT id, title, course_id, publish_status, thumbnail, taxonomy, collaborator, visible_on_profile, learning_objective, owner_id,"
          + " original_creator_id, primary_language, format FROM collection WHERE "
          + " (format = 'collection'::content_container_type OR format = 'collection-external'::content_container_type) AND is_deleted = false";

  public static final String SELECT_ASSESSMENTS =
      "SELECT id, title, course_id, publish_status, thumbnail, taxonomy, collaborator, visible_on_profile, learning_objective, owner_id,"
          + " original_creator_id, primary_language, format FROM collection WHERE "
          + " (format = 'assessment'::content_container_type OR format = 'assessment-external'::content_container_type) AND is_deleted = false";

  public static final String SELECT_OFFLINE_ACTIVITES =
      "SELECT id, title, course_id, publish_status, thumbnail, taxonomy, collaborator, visible_on_profile, learning_objective, owner_id,"
          + " original_creator_id, primary_language, format, reference, duration_hours, max_score, exemplar FROM collection WHERE "
          + " format = 'offline-activity'::content_container_type AND is_deleted = false";

  public static final String SELECT_QUESTIONS_COUNT_FOR_COLLECTION =
      "SELECT count(id) as question_count, collection_id FROM content WHERE"
          + " collection_id = ANY (?::uuid[]) AND is_deleted = false AND content_format = 'question'::content_format_type GROUP BY collection_id";

  public static final String SELECT_CONTENT_COUNTS_FOR_COLLECTIONS =
      "SELECT SUM(CASE WHEN content_format = 'resource' THEN 1 ELSE 0 END) AS resource_count, SUM(CASE WHEN content_format = 'question' THEN 1"
          + " ELSE 0 END) AS question_count, collection_id FROM content WHERE collection_id = ANY (?::uuid[]) AND is_deleted = false GROUP BY collection_id";

  public static final String SELECT_TASK_COUNTS_FOR_OA =
      "SELECT COUNT(1) as task_count, oa_id FROM oa_tasks WHERE oa_id = ANY (?::uuid[])  GROUP BY oa_id";

  public static final String SELECT_ASSESSMENT_FOR_QUESTION =
      "SELECT id, title, visible_on_profile, format FROM collection WHERE id = ANY (?::uuid[]) AND is_deleted = false";

  public static final String OP_AND = "AND";
  public static final String CRITERIA_MYPROFILE = "(owner_id = ?::uuid OR collaborator ?? ?)";
  public static final String CRITERIA_PUBLIC = "owner_id = ?::uuid AND visible_on_profile = true";
  public static final String CLAUSE_ORDERBY = "ORDER BY";
  public static final String CLAUSE_LIMIT_OFFSET = "LIMIT ? OFFSET ?";

  public static final List<String> COLLECTION_LIST = Arrays.asList(ID, TITLE, COURSE_ID,
      PUBLISH_STATUS, THUMBNAIL, TAXONOMY, COLLABORATOR, VISIBLE_ON_PROFILE, LEARNING_OBJECTIVE,
      OWNER_ID, ORIGINAL_CREATOR_ID, PRIMARY_LANGUAGE, FORMAT);
  public static final List<String> ASSESSMENT_LIST = Arrays.asList(ID, TITLE, COURSE_ID,
      PUBLISH_STATUS, THUMBNAIL, TAXONOMY, COLLABORATOR, VISIBLE_ON_PROFILE, LEARNING_OBJECTIVE,
      OWNER_ID, ORIGINAL_CREATOR_ID, PRIMARY_LANGUAGE, FORMAT);
  public static final List<String> ASSESSMENT_FIELDS_FOR_QUESTION =
      Arrays.asList(ID, TITLE, VISIBLE_ON_PROFILE, FORMAT);
  public static final List<String> OFFLINE_ACTIVITY_LIST =
      Arrays.asList(ID, TITLE, COURSE_ID, PUBLISH_STATUS, THUMBNAIL, TAXONOMY, COLLABORATOR,
          VISIBLE_ON_PROFILE, LEARNING_OBJECTIVE, OWNER_ID, ORIGINAL_CREATOR_ID, PRIMARY_LANGUAGE,
          FORMAT, MAX_SCORE, DURATION_HOURS, REFERENCE, EXEMPLAR);

  private static final String ORDER_DESC = "desc";
  private static final String ORDER_ASC = "asc";

  public static final String DEFAULT_SORTON = UPDATED_AT;
  public static final String DEFAULT_ORDER = ORDER_DESC;

  public static final List<String> VALID_SORTON_FIELDS = Arrays.asList(TITLE, UPDATED_AT);
  public static final List<String> VALID_ORDER_FIELDS = Arrays.asList(ORDER_DESC, ORDER_ASC);

}
