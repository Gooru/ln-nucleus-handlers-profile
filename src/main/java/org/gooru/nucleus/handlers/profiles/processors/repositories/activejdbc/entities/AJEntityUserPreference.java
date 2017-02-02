package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;
import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author szgooru Created On: 02-Feb-2017
 */
@Table("user_preference")
@IdName("user_id")
public class AJEntityUserPreference extends Model {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AJEntityUserPreference.class);

    public static final String USER_ID = "user_id";
    public static final String PREFERENCE_SETTINGS = "preference_settings";

    public static final String SELECT_BY_USERID = "user_id = ?::uuid";
    
    public static final List<String> RESPONSE_FIELDS = Arrays.asList(PREFERENCE_SETTINGS);
    
    public static final String UUID_TYPE = "uuid";
    public static final String JSONB_TYPE = "jsonb";
    
    public void setUserId(String userId) {
        this.setPGObject(USER_ID, UUID_TYPE, userId);
    }
    
    public void setPreferenceSettings(String preferenceSettings) {
        this.setPGObject(PREFERENCE_SETTINGS, JSONB_TYPE, preferenceSettings);
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
