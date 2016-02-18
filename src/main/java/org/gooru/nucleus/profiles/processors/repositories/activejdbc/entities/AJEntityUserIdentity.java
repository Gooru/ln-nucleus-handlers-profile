package org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("user_identity")
public class AJEntityUserIdentity extends Model {

  public static final String SELECT_USER_TO_VALIDATE = "SELECT id FROM user_identity WHERE user_id = ?::uuid";
}
