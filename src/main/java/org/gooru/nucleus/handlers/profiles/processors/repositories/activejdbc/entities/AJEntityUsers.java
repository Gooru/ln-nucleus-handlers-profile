package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

/**
 * @author szgooru Created On: 16-Jan-2017
 */
@Table("users")
public class AJEntityUsers extends Model {

    public static final String TABLE = "users";

    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String REFERENCE_ID = "reference_id";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String LOGIN_TYPE = "login_type";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String PARENT_USER_ID = "parent_user_id";
    public static final String USER_CATEGORY = "user_category";
    public static final String ROLES = "roles";
    public static final String BIRTH_DATE = "birth_date";
    public static final String GRADE = "grade";
    public static final String COURSE = "course";
    public static final String THUMBNAIL = "thumbnail";
    public static final String GENDER = "gender";
    public static final String ABOUT = "about";
    public static final String SCHOOL_ID = "school_id";
    public static final String SCHOOL = "school";
    public static final String SCHOOL_DISTRICT_ID = "school_district_id";
    public static final String SCHOOL_DISTRICT = "school_district";
    public static final String COUNTRY_ID = "country_id";
    public static final String COUNTRY = "country";
    public static final String STATE_ID = "state_id";
    public static final String STATE = "state";
    public static final String METADATA = "metadata";
    public static final String ROSTER_ID = "roster_id";
    public static final String ROSTER_GLOBAL_USERID = "roster_global_userid";
    public static final String TENANT_ROOT = "tenant_root";
    public static final String TENANT_ID = "tenant_id";
    public static final String PARENT_ID = "partner_id";
    public static final String IS_DELETED = "is_deleted";
    public static final String CREATED_AT = "created_at";
    public static final String UPDATED_AT = "updated_at";

    public static final String SELECT_MULTIPLE_BY_ID =
        "SELECT id, username, first_name, last_name, thumbnail, school_district_id, school_district, country, country_id  FROM users WHERE id ="
        + " ANY (?::uuid[])";
    
    public static final String SELECT_USERNAME_MULIPLE = "SELECT id, username FROM users WHERE id = ANY(?::uuid[])";

    public static final List<String> ALL_FIELDS =
        Arrays.asList(ID, USERNAME, FIRST_NAME, LAST_NAME, PARENT_USER_ID, USER_CATEGORY, BIRTH_DATE, GRADE, COURSE, THUMBNAIL,
            GENDER, ABOUT, SCHOOL_ID, SCHOOL, SCHOOL_DISTRICT_ID, SCHOOL_DISTRICT, EMAIL, COUNTRY_ID, COUNTRY,
            STATE_ID, STATE, METADATA, ROSTER_ID, ROSTER_GLOBAL_USERID, CREATED_AT, UPDATED_AT);
    
    public static final List<String> SUMMARY_FIELDS =
        Arrays.asList(ID, USERNAME, FIRST_NAME , LAST_NAME, THUMBNAIL, SCHOOL_DISTRICT_ID, SCHOOL_DISTRICT, COUNTRY_ID, COUNTRY);
}
