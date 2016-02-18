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
  
  public static final String SELECT_DEMOGRAPHICS = "SELECT id, firstname, lastname, thumbnail_path FROM user_demographic WHERE id = ?::uuid";
  
  public static final List<String> DEMOGRAPHIC_FIELDS = Arrays.asList(ID, FIRSTNAME, LASTNAME, TEMPLATE_PATH);

}
