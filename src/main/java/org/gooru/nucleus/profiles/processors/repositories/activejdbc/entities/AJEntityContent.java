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

  public static final String SELECT_RESOURCES_FOR_PUBLIC =
    "SELECT id, title, publish_date, thumbnail, taxonomy FROM content WHERE creator_id = ?::uuid AND content_format = "
    + "'resource'::content_format_type AND visible_on_profile = true AND is_deleted = false";
  public static final String SELECT_RESOURCES_FOR_OWNER =
    "SELECT id, title, publish_date, thumbnail, taxonomy FROM content WHERE creator_id = ?::uuid AND content_format = "
    + "'resource'::content_format_type AND is_deleted = false";
  public static final String SELECT_QUESTIONS_FOR_PUBLIC =
    "SELECT id, title, publish_date, thumbnail, taxonomy FROM content WHERE creator_id = ?::uuid AND content_format = "
    + "'question'::content_format_type AND visible_on_profile = true AND is_deleted = false";
  public static final String SELECT_QUESTIONS_FOR_OWNER =
    "SELECT id, title, publish_date, thumbnail, taxonomy FROM content WHERE creator_id = ?::uuid AND content_format = "
    + "'question'::content_format_type AND is_deleted = false";

  public static final List<String> RESOURCE_LIST = Arrays.asList(ID, TITLE, PUBLISH_DATE, THUMBNAIL, TAXONOMY);
  public static final List<String> QUESTION_LIST = Arrays.asList(ID, TITLE, PUBLISH_DATE, THUMBNAIL, TAXONOMY);
}
