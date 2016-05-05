package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities;

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
    public static final String METADATA = "metadata";
    public static final String TAXONOMY = "taxonomy";
    public static final String COLLABORATOR = "collaborator";
    public static final String VISIBLE_ON_PROFILE = "visible_on_profile";
    public static final String IS_DELETED = "is_deleted";
    public static final String SEQUENCE_ID = "sequence_id";
    public static final String SUBJECT_BUCKET = "subject_bucket";

    public static final String COURSE_ID = "course_id";
    public static final String UNIT_COUNT = "unit_count";
    public static final String OWNER_INFO = "owner_info";

    public static final String SELECT_COURSES =
        "SELECT id, title, publish_status, thumbnail, owner_id, original_creator_id, original_course_id, collaborator, taxonomy, sequence_id,"
            + " visible_on_profile FROM course WHERE (owner_id = ?::uuid OR collaborator ?? ?) AND is_deleted = false";

    public static final String SELECT_COURSES_PUBLIC =
        "SELECT id, title, publish_status, thumbnail, owner_id, original_creator_id, original_course_id, collaborator, taxonomy, sequence_id,"
            + " visible_on_profile FROM course WHERE owner_id = ?::uuid AND is_deleted = false AND visible_on_profile = true";

    public static final String SELECT_SUBJECT_BUCKETS =
        "SELECT distinct(subject_bucket) as subject_bucket FROM course WHERE"
            + " (owner_id = ?::uuid OR collaborator ?? ?) AND is_deleted = false";

    public static final String SELECT_SUBJECT_BUCKETS_PUBLIC =
        "SELECT distinct(subject_bucket) as subject_bucket FROM course WHERE"
            + " owner_id = ?::uuid AND is_deleted = false AND visible_on_profile = true";

    public static final String SELECT_UNIT_COUNT_FOR_COURSES =
        "SELECT count(unit_id) as unit_count, course_id FROM unit WHERE course_id = ANY"
            + " (?::uuid[]) AND is_deleted = false GROUP BY course_id";
    
    public static final String SELECT_COURSE_FOR_COLLECTION =
        "SELECT id, title, visible_on_profile FROM course WHERE id = ANY (?::uuid[]) AND is_deleted = false";

    public static final String OP_AND = "AND";
    public static final String CRITERIA_SUBJECTBUCKET = "subject_bucket = ?";
    public static final String CRITERIA_TITLE = "title ilike ?";
    public static final String CLAUSE_ORDERBY_SEQUENCE_ID = "ORDER BY sequence_id asc";
    public static final String CLAUSE_ORDERBY_CREATED_AT = "ORDER BY created_at desc";
    public static final String CLAUSE_LIMIT_OFFSET = "LIMIT ? OFFSET ?";

    public static final List<String> COURSE_LIST = Arrays.asList(ID, TITLE, PUBLISH_STATUS, THUMBNAIL, OWNER_ID,
        ORIGINAL_CREATOR_ID, COLLABORATOR, ORIGINAL_COURSE_ID, TAXONOMY, SEQUENCE_ID, VISIBLE_ON_PROFILE);
    
    public static final List<String> COURSE_FIELDS_FOR_COLLECTION = Arrays.asList(ID, TITLE, VISIBLE_ON_PROFILE);

    public static final int DEFAULT_LIMIT = 20;
    public static final int DEFAULT_OFFSET = 0;

}
