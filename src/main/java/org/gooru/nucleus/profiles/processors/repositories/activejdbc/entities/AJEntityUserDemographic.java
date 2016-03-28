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
  public static final String TEMPLATE_PATH = "thumbnail_path";
  public static final String SCHOOL_DISTRICT_ID = "school_district_id";
  public static final String ABOUT_ME = "about_me";
  public static final String SCHOOL_ID = "school_id";
  public static final String COUNTRY_ID = "country_id";
  public static final String STATE_ID = "state_id";
  
  public static final String SELECT_DEMOGRAPHICS = "SELECT id, firstname, lastname, thumbnail_path, about_me, school_id, school_district_id, country_id, state_id FROM user_demographic WHERE id = ?::uuid";
  public static final String SELECT_DEMOGRAPHICS_MULTIPLE = "SELECT id, firstname, lastname, thumbnail_path, school_district_id FROM user_demographic"
    + " WHERE id = ANY (?::uuid[])";
  
  public static final List<String> ALL_FIELDS = Arrays.asList(ID, FIRSTNAME, LASTNAME, TEMPLATE_PATH, SCHOOL_DISTRICT_ID, ABOUT_ME, SCHOOL_ID,
    COUNTRY_ID, STATE_ID);
  public static final List<String> DEMOGRAPHIC_FIELDS = Arrays.asList(ID, FIRSTNAME, LASTNAME, TEMPLATE_PATH, SCHOOL_DISTRICT_ID);

}
