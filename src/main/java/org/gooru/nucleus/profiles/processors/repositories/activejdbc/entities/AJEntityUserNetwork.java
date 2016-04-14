package org.gooru.nucleus.profiles.processors.repositories.activejdbc.entities;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Table("user_network")
@IdName("user_id")
public class AJEntityUserNetwork extends Model {
    private static final Logger LOGGER = LoggerFactory.getLogger(AJEntityUserNetwork.class);

    public static final String TABLE = "user_network";
    public static final String USER_ID = "user_id";
    public static final String FOLLOW_ON_USER_ID = "follow_on_user_id";

    public static final String SELECT_FOLLOWERS = "SELECT follow_on_user_id FROM user_network WHERE user_id = ?::uuid";
    public static final String SELECT_FOLLOWINGS = "SELECT user_id FROM user_network WHERE follow_on_user_id = ?::uuid";
    public static final String SELECT_FOLLOWERS_COUNT = "follow_on_user_id = ?::uuid";
    public static final String SELECT_FOLLOWINGS_COUNT = "user_id = ?::uuid";
    public static final String QUERY_UNFOLLOW = "user_id = ?::uuid AND follow_on_user_id = ?::uuid";
    public static final String SELECT_FOLLOWERS_COUNT_MULTIPLE =
        "SELECT count(follow_on_user_id) as followers_count, user_id FROM user_network WHERE user_id ="
            + " ANY (?::uuid[]) GROUP BY user_id";
    public static final String SELECT_FOLLOWINGS_COUNT_MULTIPLE =
        "SELECT count(user_id) as followings_count, follow_on_user_id FROM user_network WHERE follow_on_user_id"
            + " = ANY (?::uuid[]) GROUP BY follow_on_user_id";
    public static final String CHECK_IF_FOLLOWER = "user_id = ?::uuid AND follow_on_user_id = ?::uuid";
    public static final List<String> REQUIRED_FIELDS = Arrays.asList(USER_ID);

    public static final String FOLLOWERS_COUNT = "followers_count";
    public static final String FOLLOWINGS_COUNT = "followings_count";

    public static final String UUID_TYPE = "uuid";

    public void setUserId(String userId) {
        setPGObject(USER_ID, UUID_TYPE, userId);
    }

    public void setFollowOnUserId(String followOnUserId) {
        setPGObject(FOLLOW_ON_USER_ID, UUID_TYPE, followOnUserId);
    }

    private void setPGObject(String field, String type, String value) {
        PGobject pgObject = new PGobject();
        pgObject.setType(type);
        try {
            pgObject.setValue(value);
            this.set(field, pgObject);
        } catch (SQLException e) {
            LOGGER.error("Not able to set value for field: {}, type: {}, value: {}", field, type, value);
            this.errors().put(field, value);
        }
    }
}
