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

    private static final String ID = "id";
    private static final String USERNAME = "username";
    private static final String REFERENCE_ID = "reference_id";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";
    private static final String LOGIN_TYPE = "login_type";
    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String PARENT_USER_ID = "parent_user_id";
    private static final String USER_CATEGORY = "user_category";
    private static final String ROLES = "roles";
    private static final String BIRTH_DATE = "birth_date";
    private static final String GRADE = "grade";
    private static final String COURSE = "course";
    private static final String THUMBNAIL = "thumbnail";
    private static final String GENDER = "gender";
    private static final String ABOUT = "about";
    private static final String SCHOOL_ID = "school_id";
    private static final String SCHOOL = "school";
    private static final String SCHOOL_DISTRICT_ID = "school_district_id";
    private static final String SCHOOL_DISTRICT = "school_district";
    private static final String COUNTRY_ID = "country_id";
    private static final String COUNTRY = "country";
    private static final String STATE_ID = "state_id";
    private static final String STATE = "state";
    private static final String METADATA = "metadata";
    private static final String ROSTER_ID = "roster_id";
    private static final String ROSTER_GLOBAL_USERID = "roster_global_userid";
    private static final String TENANT_ROOT = "tenant_root";
    private static final String TENANT_ID = "tenant_id";
    private static final String PARENT_ID = "partner_id";
    private static final String IS_DELETED = "is_deleted";
    private static final String CREATED_AT = "created_at";
    private static final String UPDATED_AT = "updated_at";

    public static final String SELECT_MULTIPLE_BY_ID =
        "SELECT id, username, first_name, last_name, thumbnail, school_district_id, school_district, country, "
            + "country_id  FROM users WHERE id ="
            + " ANY (?::uuid[])";

    public static final String SELECT_USERNAME_MULIPLE = "SELECT id, username FROM users WHERE id = ANY(?::uuid[])";

    public static final List<String> ALL_FIELDS = Arrays
        .asList(ID, USERNAME, FIRST_NAME, LAST_NAME, PARENT_USER_ID, USER_CATEGORY, BIRTH_DATE, GRADE, COURSE,
            THUMBNAIL, GENDER, ABOUT, SCHOOL_ID, SCHOOL, SCHOOL_DISTRICT_ID, SCHOOL_DISTRICT, EMAIL, COUNTRY_ID,
            COUNTRY, STATE_ID, STATE, METADATA, ROSTER_ID, ROSTER_GLOBAL_USERID, CREATED_AT, UPDATED_AT);

    public static final List<String> SUMMARY_FIELDS = Arrays
        .asList(ID, USERNAME, FIRST_NAME, LAST_NAME, THUMBNAIL, SCHOOL_DISTRICT_ID, SCHOOL_DISTRICT, COUNTRY_ID,
            COUNTRY);

    public String getTenantRoot() {
        return this.getString(TENANT_ROOT);
    }

    public String getTenant() {
        return this.getString(TENANT_ID);
    }
}
