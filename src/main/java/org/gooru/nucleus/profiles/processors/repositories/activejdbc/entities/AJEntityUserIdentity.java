package org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("user_identity")
public class AJEntityUserIdentity extends Model {

  public static final String USER_ID = "user_id";
  public static final String USERNAME = "username";
  
  public static final String SELECT_USER_TO_VALIDATE = "SELECT id FROM user_identity WHERE user_id = ?::uuid";
  public static final String SELECT_USERNAME = "SELECT username FROM user_identity WHERE user_id = ?::uuid";
  
  public static final String SELECT_USERNAME_MULIPLE = "SELECT user_id, username FROM user_identity WHERE user_id = ANY(?::uuid[])";
  
}
