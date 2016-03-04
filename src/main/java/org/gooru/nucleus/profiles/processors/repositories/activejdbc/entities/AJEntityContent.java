package org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("content")
public class AJEntityContent extends Model {

  public static final String ID = "id";
  public static final String TITLE = "title";
  public static final String DESCRIPTION = "description";
  public static final String FORMAT = "format";
  public static final String PUBLISH_STATUS = "publish_status";
  public static final String CONTENT_FORMAT = "content_format";
  public static final String CONTENT_SUBFORMAT = "content_subformat";
  public static final String THUMBNAIL = "thumbnail";
  public static final String TAXONOMY = "taxonomy";
  public static final String UPDATED_AT = "updated_at";

  public static final String SELECT_RESOURCES =
          "SELECT id, title, description, publish_status, content_format, content_subformat, taxonomy FROM content WHERE"
          + " creator_id = ?::uuid AND original_content_id IS NULL AND content_format = 'resource'::content_format_type AND is_deleted = false";

  public static final String SELECT_RESOURCES_BY_TAXONOMY =
          "SELECT distinct(id), title, description, publish_status, content_format, content_subformat, taxonomy FROM content con,"
          + " jsonb_array_elements_text(con.taxonomy) as tx WHERE creator_id = ?::uuid AND original_content_id IS NULL AND content_format ="
          + " 'resource'::content_format_type AND is_deleted = false AND tx like ?";

  public static final String SELECT_QUESTIONS =
          "SELECT id, title, description, publish_status, content_format, content_subformat, thumbnail, taxonomy FROM content WHERE creator_id ="
          + " ?::uuid AND content_format = 'question'::content_format_type AND is_deleted = false";

  public static final String SELECT_QUESTIONS_BY_TAXONOMY =
          "SELECT distinct(id), title, description, publish_status, content_format, content_subformat, taxonomy FROM content con, "
          + "jsonb_array_elements_text(con.taxonomy) as tx WHERE creator_id = ?::uuid AND content_format = 'question'::content_format_type AND"
          + " is_deleted = false AND tx like ?";

  public static final String OP_AND = "AND";
  public static final String CRITERIA_TITLE = "(title ilike ? OR description ilike ?)";
  public static final String CRITERIA_PUBLIC = "visible_on_profile = true";
  public static final String CLAUSE_ORDERBY = "ORDER BY";
  public static final String CLAUSE_LIMIT_OFFSET = "LIMIT ? OFFSET ?";

  public static final List<String> RESOURCE_LIST = Arrays.asList(ID, TITLE, DESCRIPTION, PUBLISH_STATUS, CONTENT_FORMAT, CONTENT_SUBFORMAT);
  public static final List<String> QUESTION_LIST = Arrays.asList(ID, TITLE, DESCRIPTION, PUBLISH_STATUS, CONTENT_FORMAT, CONTENT_SUBFORMAT);

  public static final String ORDER_DESC = "desc";
  public static final String ORDER_ASC = "asc";

  public static final int DEFAULT_LIMIT = 20;
  public static final int DEFAULT_OFFSET = 0;
  public static final String DEFAULT_SORTON = TITLE;
  public static final String DEFAULT_ORDER = ORDER_ASC;

  public static final List<String> VALID_SORTON_FIELDS = Arrays.asList(TITLE, UPDATED_AT);
  public static final List<String> VALID_ORDER_FIELDS = Arrays.asList(ORDER_DESC, ORDER_ASC);
}
