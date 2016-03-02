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
  public static final String PUBLISH_STATUS = "publish_status";
  public static final String THUMBNAIL = "thumbnail";
  public static final String AUDIENCE = "audience";
  public static final String METADATA = "metadata";
  public static final String TAXONOMY = "taxonomy";
  public static final String COLLABORATOR = "collaborator";
  public static final String VISIBLE_ON_PROFILE = "visible_on_profile";
  public static final String IS_DELETED = "is_deleted";
  public static final String SEQUENCE_ID = "sequence_id";
  public static final String SUBJECT_BUCKET = "subject_bucket";
  
  public static final String SELECT_COURSES =
    "SELECT id, title, publish_status, thumbnail, collaborator, sequence_id FROM course WHERE owner_id = ?::uuid AND is_deleted = false";
  
  public static final String SELECT_COURSES_BY_SUBJECT =
    "SELECT id, title, publish_status, thumbnail, collaborator, sequence_id FROM course WHERE owner_id = ?::uuid AND is_deleted = false"
    + " AND subject_bucket like ?";
  
  //public static final String SELECT_COURSES_FOR_TXCOUNT = "SELECT taxonomy FROM course WHERE owner_id = ?::uuid AND is_deleted = false";
  public static final String SELECT_COURSES_COUNTBY_SUBJECT = "SELECT count(id) AS course_count, subject_bucket FROM course WHERE owner_id = ?::uuid"
    + " AND is_deleted = false";
  
  public static final String OP_AND = " AND ";
  public static final String CRITERIA_TITLE = "title ilike ?";
  public static final String CRITERIA_PUBLIC = "visible_on_profile = true";
  public static final String ORDERBY_SEQUENCE = " ORDER BY sequence_id";
  public static final String GROUPBY_SUBJECT = " GROUP BY subject_bucket";

  public static final List<String> COURSE_LIST = Arrays.asList(ID, TITLE, PUBLISH_STATUS, THUMBNAIL, COLLABORATOR, SEQUENCE_ID);
  
  public static final String KEY_COURSE_COUNT = "course_count";
}
