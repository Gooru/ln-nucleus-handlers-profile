package org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("collection")
public class AJEntityCollection extends Model {

  public static final String ID = "id";
  public static final String TITLE = "title";
  public static final String FORMAT = "format";
  public static final String PUBLISH_DATE = "publish_date";
  public static final String THUMBNAIL = "thumbnail";
  public static final String TAXONOMY = "taxonomy";
  public static final String COLLABORATOR = "collaborator";

  public static final String SELECT_COLLECTIONS_FOR_PUBLIC =
    "SELECT id, title, publish_date, thumbnail, taxonomy, collaborator FROM collection WHERE owner_id = ?::uuid AND format = "
    + "'collection'::content_container_type AND visible_on_profile = true AND is_deleted = false";
  public static final String SELECT_COLLECTIONS_FOR_OWNER =
    "SELECT id, title, publish_date, thumbnail, taxonomy, collaborator FROM collection WHERE owner_id = ?::uuid AND format = "
    + "'collection'::content_container_type AND is_deleted = false";
  
  public static final String SELECT_ASSESSMENTS_FOR_PUBLIC =
    "SELECT id, title, publish_date, thumbnail, taxonomy, collaborator FROM collection WHERE owner_id = ?::uuid AND format = "
    + "'assessment'::content_container_type AND visible_on_profile = true AND is_deleted = false";
  public static final String SELECT_ASSESSMENTS_FOR_OWNER =
    "SELECT id, title, publish_date, thumbnail, taxonomy, collaborator FROM collection WHERE owner_id = ?::uuid AND format = "
    + "'assessment'::content_container_type AND is_deleted = false";
  
  public static final String SEARCH_COLLECTIONS_FOR_PUBLIC =
    "SELECT distinct(id), title, publish_date, thumbnail, taxonomy, collaborator FROM collection col, jsonb_array_elements_text(col.taxonomy)"
    + " as tx WHERE owner_id = ?::uuid AND format = 'collection'::content_container_type AND visible_on_profile = true AND is_deleted = false"
    + " AND tx like ?";
  public static final String SEARCH_COLLECTIONS_FOR_OWNER =
    "SELECT distinct(id), title, publish_date, thumbnail, taxonomy, collaborator FROM collection col, jsonb_array_elements_text(col.taxonomy)"
    + " as tx WHERE owner_id = ?::uuid AND format = 'collection'::content_container_type AND is_deleted = false AND tx like ?";
  
  public static final String SEARCH_ASSESSMENTS_FOR_PUBLIC =
    "SELECT distinct(id), title, publish_date, thumbnail, taxonomy, collaborator FROM collection col, jsonb_array_elements_text(col.taxonomy)"
    + " as tx WHERE owner_id = ?::uuid AND format = 'assessment'::content_container_type AND visible_on_profile = true AND is_deleted = false"
    + " AND tx like ?";
  public static final String SEARCH_ASSESSMENTS_FOR_OWNER =
    "SELECT distinct(id), title, publish_date, thumbnail, taxonomy, collaborator FROM collection col, jsonb_array_elements_text(col.taxonomy)"
    + " as tx WHERE owner_id = ?::uuid AND format = 'assessment'::content_container_type AND is_deleted = false AND tx like ?";
        
  public static final String OP_AND = " AND ";
  public static final String CRITERIA_TITLE = "title ilike ?";
  
  public static final List<String> COLLECTION_LIST = Arrays.asList(ID, TITLE, PUBLISH_DATE, THUMBNAIL, TAXONOMY, COLLABORATOR);
  public static final List<String> ASSESSMENT_LIST = Arrays.asList(ID, TITLE, PUBLISH_DATE, THUMBNAIL, TAXONOMY, COLLABORATOR);

}
