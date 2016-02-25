package org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("course")
public class AJEntityCourse extends Model {

  public static final String ID = "id";
  public static final String TITLE = "title";
  public static final String CREATED_AT = "created_at";
  public static final String UPDATED_AT = "updated_at";
  public static final String OWNER_ID = "owner_id";
  public static final String CREATOR_ID = "creator_id";
  public static final String MODIFIER_ID = "modifier_id";
  public static final String ORIGINAL_CREATOR_ID = "original_creator_id";
  public static final String ORIGINAL_COURSE_ID = "original_course_id";
  public static final String PUBLISH_DATE = "publish_date";
  public static final String THUMBNAIL = "thumbnail";
  public static final String AUDIENCE = "audience";
  public static final String METADATA = "metadata";
  public static final String TAXONOMY = "taxonomy";
  public static final String COLLABORATOR = "collaborator";
  public static final String VISIBLE_ON_PROFILE = "visible_on_profile";
  public static final String IS_DELETED = "is_deleted";
  
  public static final String SELECT_COURSES_FOR_PUBLIC =
    "SELECT id, title, publish_date, thumbnail, taxonomy, collaborator FROM course WHERE owner_id = ?::uuid AND visible_on_profile = true AND"
    + " is_deleted = false";
  public static final String SELECT_COURSES_FOR_OWNER =
    "SELECT id, title, publish_date, thumbnail, taxonomy, collaborator FROM course WHERE owner_id = ?::uuid AND is_deleted = false";
  
  public static final List<String> COURSE_LIST = Arrays.asList(ID, TITLE, PUBLISH_DATE, THUMBNAIL, COLLABORATOR);
}
