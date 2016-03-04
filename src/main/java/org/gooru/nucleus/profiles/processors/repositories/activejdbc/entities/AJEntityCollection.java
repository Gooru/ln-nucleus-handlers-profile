package org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("collection")
public class AJEntityCollection extends Model {

  public static final String ID = "id";
  public static final String TITLE = "title";
  public static final String UPDATED_AT = "updated_at";
  public static final String FORMAT = "format";
  public static final String PUBLISH_STATUS = "publish_status";
  public static final String PUBLISH_DATE = "publish_date";
  public static final String THUMBNAIL = "thumbnail";
  public static final String TAXONOMY = "taxonomy";
  public static final String COLLABORATOR = "collaborator";
  public static final String VISIBLE_ON_PROFILE = "visible_on_profile";
  public static final String COURSE_ID = "course_id";
  
  public static final String COLLECTION_ID = "collection_id";
  public static final String RESOURCE_COUNT = "resource_count";
  public static final String QUESTION_COUNT = "question_count";
  
  public static final String SELECT_COLLECTIONS = 
    "SELECT id, title, course_id, publish_status, thumbnail, taxonomy, collaborator, visible_on_profile FROM collection WHERE owner_id = ?::uuid AND format ="
    + " 'collection'::content_container_type AND is_deleted = false";
  
  public static final String SELECT_COLLECTIONS_BY_TAXONOMY = 
    "SELECT distinct(id), title, course_id, publish_status, thumbnail, collaborator, visible_on_profile FROM collection col, jsonb_array_elements_text(col.taxonomy) as tx WHERE"
    + " owner_id = ?::uuid AND format = 'collection'::content_container_type AND is_deleted = false AND tx like ?";
  
  public static final String SELECT_ASSESSMENTS =
    "SELECT id, title, publish_status, thumbnail, taxonomy, collaborator, visible_on_profile FROM collection WHERE owner_id = ?::uuid AND format = "
    + "'assessment'::content_container_type AND is_deleted = false";
  
  public static final String SELECT_ASSESSMENTS_BY_TAXONOMY = 
    "SELECT distinct(id), title, publish_status, thumbnail, collaborator, visible_on_profile FROM collection col, jsonb_array_elements_text(col.taxonomy) as tx WHERE"
    + " owner_id = ?::uuid AND format = 'assessment'::content_container_type AND is_deleted = false AND tx like ?";
  
  /*
   * "SELECT count(unit_id) as unit_count, course_id FROM unit WHERE course_id = ANY"
    + " (?::uuid[]) AND is_deleted = false GROUP BY course_id";
   */
  public static final String SELECT_RESOURCES_COUNT_FOR_COLLECTION = "SELECT count(id) as resource_count, collection_id FROM content WHERE"
    + " collection_id = ANY (?::uuid[]) AND is_deleted = false AND content_format = 'resource'::content_format_type GROUP BY collection_id";
  
  public static final String SELECT_QUESTIONS_COUNT_FOR_COLLECTION = "SELECT count(id) as question_count, collection_id FROM content WHERE"
    + " collection_id = ANY (?::uuid[]) AND is_deleted = false AND content_format = 'question'::content_format_type GROUP BY collection_id";
  
  public static final String OP_AND = "AND";
  public static final String CRITERIA_TITLE = "title ilike ?";
  public static final String CRITERIA_PUBLIC = "visible_on_profile = true";
  public static final String CRITERIA_INCOURSE = "course_id IS NOT NULL";
  public static final String CRITERIA_NOT_INCOURSE = "course_id IS NULL";
  public static final String CLAUSE_ORDERBY = "ORDER BY";
  public static final String CLAUSE_LIMIT_OFFSET = "LIMIT ? OFFSET ?";
  
  public static final List<String> COLLECTION_LIST = Arrays.asList(ID, TITLE, COURSE_ID, PUBLISH_STATUS, THUMBNAIL, COLLABORATOR, VISIBLE_ON_PROFILE);
  public static final List<String> ASSESSMENT_LIST = Arrays.asList(ID, TITLE, COURSE_ID, PUBLISH_STATUS, THUMBNAIL, COLLABORATOR, VISIBLE_ON_PROFILE);
  
  public static final String ORDER_DESC = "desc";
  public static final String ORDER_ASC = "asc";
  
  public static final int DEFAULT_LIMIT = 20;
  public static final int DEFAULT_OFFSET = 0;
  public static final String DEFAULT_SORTON = TITLE;
  public static final String DEFAULT_ORDER = ORDER_ASC;
  public static final boolean DEFAULT_INCOURSE = true; 
  
  public static final List<String> VALID_SORTON_FIELDS = Arrays.asList(TITLE, UPDATED_AT);
  public static final List<String> VALID_ORDER_FIELDS = Arrays.asList(ORDER_DESC, ORDER_ASC);

}
