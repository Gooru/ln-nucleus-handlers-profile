package org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities;

import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("user_demographic")
public class AJEntityUserDemographic extends Model {
  
  public static final String ID = "id";
  public static final String FIRSTNAME = "firstname";
  public static final String LASTNAME = "lastname";
  public static final String PARENT_USER_ID = "parent_user_id";
  public static final String USER_CATEGORY = "user_category";
  public static final String BIRTH_DATE = "birth_date";
  public static final String GRADE = "grade";
  public static final String COURSE = "course";
  public static final String TEMPLATE_PATH = "thumbnail_path";
  public static final String GENDER = "gender";
  public static final String ABOUT_ME = "about_me";
  public static final String SCHOOL_ID = "school_id";
  public static final String SCHOOL = "school";
  public static final String SCHOOL_DISTRICT_ID = "school_district_id";
  public static final String SCHOOL_DISTRICT = "school_district";
  public static final String EMAIL = "email_id";
  public static final String COUNTRY_ID = "country_id";
  public static final String COUNTRY = "country";
  public static final String STATE_ID = "state_id";
  public static final String STATE = "state";
  public static final String METADATA = "metadata";
  public static final String ROSTER_ID = "roster_id";
  public static final String ROSTER_GLOBAL_USERID = "roster_global_userid";
  public static final String CREATED_AT = "created_at";
  public static final String UPDATED_AT = "updated_at";
  
  public static final String SELECT_DEMOGRAPHICS = "SELECT id, firstname, lastname, parent_user_id, user_category, birth_date, grade, course,"
    + " thumbnail_path, gender, about_me, school_id, school, school_district_id, school_district, email_id, country_id, country, state_id, state,"
    + " metadata, roster_id, roster_global_userid, created_at, updated_at FROM user_demographic WHERE id = ?::uuid";
  public static final String SELECT_DEMOGRAPHICS_MULTIPLE = "SELECT id, firstname, lastname, thumbnail_path, school_district_id FROM"
    + " user_demographic WHERE id = ANY (?::uuid[])";
  
  public static final List<String> ALL_FIELDS = Arrays.asList(ID, FIRSTNAME, LASTNAME, PARENT_USER_ID, USER_CATEGORY, BIRTH_DATE, GRADE, COURSE,
    TEMPLATE_PATH, GENDER, ABOUT_ME, SCHOOL_ID, SCHOOL, SCHOOL_DISTRICT_ID, SCHOOL_DISTRICT, EMAIL, COUNTRY_ID, COUNTRY, STATE_ID, STATE, METADATA,
    ROSTER_ID, ROSTER_GLOBAL_USERID, CREATED_AT, UPDATED_AT);
  public static final List<String> DEMOGRAPHIC_FIELDS = Arrays.asList(ID, FIRSTNAME, LASTNAME, TEMPLATE_PATH, SCHOOL_DISTRICT_ID);

}
