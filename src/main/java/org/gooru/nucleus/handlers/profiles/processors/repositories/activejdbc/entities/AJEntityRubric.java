package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author szgooru Created On: 01-Mar-2017
 */
@Table("rubric")
public class AJEntityRubric extends Model {

  private static final String ID = "id";
  private static final String TITLE = "title";
  public static final String URL = "url";
  public static final String HTTP_PROTOCOL = "http_protocol";
  public static final String HTTP_HOST = "http_host";
  public static final String HTTP_PORT = "http_port";
  public static final String HTTP_DOMAIN = "http_domain";
  public static final String HTTP_PATH = "http_path";
  public static final String HTTP_QUERY = "http_query";
  public static final String IS_REMOTE = "is_remote";
  private static final String DESCRIPTION = "description";
  public static final String CATEGORIES = "categories";
  public static final String FEEDBACK_GUIDANCE = "feedback_guidance";
  public static final String TOTAL_POINTS = "total_points";
  public static final String OVERALL_FEEDBACK_REQUIRED = "overall_feedback_required";
  public static final String OWNER_ID = "owner_id";
  public static final String CREATOR_ID = "creator_id";
  public static final String MODIFIER_ID = "modifier_id";
  private static final String ORIGINAL_CREATOR_ID = "original_creator_id";
  public static final String ORIGINAL_RUBRIC_ID = "original_rubric_id";
  public static final String PARENT_RUBRIC_ID = "parent_rubric_id";
  public static final String PUBLISH_DATE = "publish_date";
  private static final String PUBLISH_STATUS = "publish_status";
  public static final String METADATA = "metadata";
  private static final String TAXONOMY = "taxonomy";
  private static final String THUMBNAIL = "thumbnail";
  public static final String CREATED_AT = "created_at";
  private static final String UPDATED_AT = "updated_at";
  public static final String TENANT = "tenant";
  public static final String TENANT_ROOT = "tenant_root";
  private static final String VISIBLE_ON_PROFILE = "visible_on_profile";
  public static final String IS_DELETED = "is_deleted";
  public static final String CREATOR_SYSTEM = "creator_system";
  private static final String PRIMARY_LANGUAGE = "primary_language";

  public static final String SELECT_RUBRICS =
      "SELECT id, title, description, publish_status, thumbnail, taxonomy, creator_id, original_creator_id,"
          + " visible_on_profile, primary_language FROM rubric WHERE creator_id = ?::uuid AND is_deleted = false AND is_rubric = true";

  public static final String SELECT_RUBRICS_BY_TAXONOMY =
      "SELECT id, title, description, publish_status, thumbnail, taxonomy, creator_id, original_creator_id,"
          + " visible_on_profile, primary_language FROM rubric WHERE creator_id = ?::uuid AND is_deleted = false  AND is_rubric = true AND taxonomy ?? ?";

  public static final List<String> RUBRIC_LIST = Arrays
      .asList(ID, TITLE, DESCRIPTION, PUBLISH_STATUS,
          THUMBNAIL, TAXONOMY, CREATOR_ID, ORIGINAL_CREATOR_ID, VISIBLE_ON_PROFILE,
          PRIMARY_LANGUAGE);

  public static final String OP_AND = "AND";
  public static final String CRITERIA_PUBLIC = "visible_on_profile = true";
  public static final String CRITERIA_STANDALONE = "content_id IS NULL and collection_id is null";
  public static final String CLAUSE_ORDERBY = "ORDER BY";
  public static final String CLAUSE_LIMIT_OFFSET = "LIMIT ? OFFSET ?";

  private static final String ORDER_DESC = "desc";
  private static final String ORDER_ASC = "asc";

  public static final int DEFAULT_LIMIT = 20;
  public static final int DEFAULT_OFFSET = 0;
  public static final String DEFAULT_SORTON = UPDATED_AT;
  public static final String DEFAULT_ORDER = ORDER_DESC;

  public static final List<String> VALID_SORTON_FIELDS = Arrays.asList(TITLE, UPDATED_AT);
  public static final List<String> VALID_ORDER_FIELDS = Arrays.asList(ORDER_DESC, ORDER_ASC);

}
