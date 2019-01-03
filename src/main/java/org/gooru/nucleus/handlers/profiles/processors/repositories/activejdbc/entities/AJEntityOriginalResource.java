package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("original_resource")
public class AJEntityOriginalResource extends Model {

  private static final String ID = "id";
  private static final String TITLE = "title";
  private static final String DESCRIPTION = "description";
  public static final String FORMAT = "format";
  private static final String PUBLISH_STATUS = "publish_status";
  private static final String CONTENT_SUBFORMAT = "content_subformat";
  public static final String THUMBNAIL = "thumbnail";
  private static final String TAXONOMY = "taxonomy";
  private static final String UPDATED_AT = "updated_at";
  public static final String CREATOR_ID = "creator_id";
  public static final String COLLECTION_ID = "collection_id";
  private static final String VISIBLE_ON_PROFILE = "visible_on_profile";

  public static final String CONTENT_FORMAT = "content_format";
  public static final String ORIGINAL_CREATOR_ID = "original_creator_id";
  public static final String RESOURCE_CONTENT_FORMAT = "resource";
  private static final String PRIMARY_LANGUAGE = "primary_language";

  public static final String SELECT_RESOURCES =
      "SELECT id, title, description, publish_status, content_subformat, taxonomy, creator_id, visible_on_profile, primary_language FROM original_resource"
          + " WHERE creator_id = ?::uuid AND is_deleted = false";

  public static final String SELECT_RESOURCES_BY_TAXONOMY =
      "SELECT id, title, description, publish_status, content_subformat, taxonomy, creator_id, visible_on_profile, primary_language FROM original_resource"
          + " WHERE creator_id = ?::uuid AND is_deleted = false AND taxonomy ?? ?";

  public static final String SELECT_TAXONOMY_FOR_RESOURCES =
      "SELECT DISTINCT(jsonb_object_keys(taxonomy)) FROM original_resource WHERE creator_id = ?::uuid AND is_deleted = false";

  public static final String SELECT_TAXONOMY_FOR_RESOURCES_PUBLIC =
      "SELECT DISTINCT(jsonb_object_keys(taxonomy)) FROM original_resource WHERE creator_id = ?::uuid AND is_deleted = false AND visible_on_profile = true";

  public static final List<String> RESOURCE_LIST = Arrays
      .asList(ID, TITLE, DESCRIPTION, PUBLISH_STATUS,
          CONTENT_SUBFORMAT, TAXONOMY, CREATOR_ID, VISIBLE_ON_PROFILE, PRIMARY_LANGUAGE);

  public static final String OP_AND = "AND";
  public static final String CRITERIA_TITLE = "(title ilike ? OR description ilike ?)";
  public static final String CRITERIA_PUBLIC = "visible_on_profile = true";
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
