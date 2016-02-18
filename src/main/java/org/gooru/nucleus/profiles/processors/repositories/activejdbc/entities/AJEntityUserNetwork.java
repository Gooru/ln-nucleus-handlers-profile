package org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("user_network")
@IdName("user_id")
public class AJEntityUserNetwork extends Model {

  public static final String TABLE = "user_network";
  public static final String SELECT_FOLLOWERS_COUNT = "user_id = ?::uuid AND is_deleted = false";
  public static final String SELECT_FOLLOWINGS_COUNT = "follow_on_user_id = ?::uuid AND is_deleted = false";
}
