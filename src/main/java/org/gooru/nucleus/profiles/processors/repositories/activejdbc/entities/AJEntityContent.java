package org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("content")
public class AJEntityContent extends Model {

  public static final String ID = "id";
  public static final String TITLE = "title";
  public static final String FORMAT = "format";
  public static final String PUBLISH_DATE = "publish_date";
  public static final String THUMBNAIL = "thumbnail";
  public static final String TAXONOMY = "taxonomy";
  
  public static final String SELECT_RESOURCES = 
    "SELECT id, title, publish_date, thumbnail FROM content WHERE creator_id = ?::uuid AND original_content_id IS NULL AND"
    +" content_format = 'resource'::content_format_type AND is_deleted = false";
  
  public static final String SELECT_RESOURCES_BY_TAXONOMY = 
    "SELECT distinct(id), title, publish_date, thumbnail FROM content con, jsonb_array_elements_text(con.taxonomy) as tx WHERE"
    + " creator_id = ?::uuid AND original_content_id IS NULL AND content_format = 'resource'::content_format_type AND is_deleted = false AND tx like ?";
  
  public static final String SELECT_QUESTIONS = 
    "SELECT id, title, publish_date, thumbnail FROM content WHERE creator_id = ?::uuid AND content_format = 'question'::content_format_type"
    + " AND is_deleted = false";
  
  public static final String SELECT_QUESTIONS_BY_TAXONOMY = 
    "SELECT distinct(id), title, publish_date, thumbnail FROM content con, jsonb_array_elements_text(con.taxonomy) as tx WHERE creator_id = ?::uuid AND"
    +" content_format = 'question'::content_format_type AND is_deleted = false AND tx like ?";
  
  public static final String OP_AND = " AND ";
  public static final String CRITERIA_TITLE = "(title ilike ? OR description ilike ?)";
  public static final String CRITERIA_PUBLIC = "visible_on_profile = true";
  
  public static final List<String> RESOURCE_LIST = Arrays.asList(ID, TITLE, PUBLISH_DATE, THUMBNAIL);
  public static final List<String> QUESTION_LIST = Arrays.asList(ID, TITLE, PUBLISH_DATE, THUMBNAIL);
}
