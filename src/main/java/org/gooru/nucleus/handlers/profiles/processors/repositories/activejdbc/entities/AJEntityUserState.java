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
 * @author szgooru Created On: 08-Dec-2017
 */
@Table("user_state")
@IdName("user_id")
public class AJEntityUserState extends Model {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AJEntityUserState.class);

    public static final String USER_ID = "user_id";
    public static final String CLIENT_STATE = "client_state";
    
    private static final String JSONB_TYPE = "jsonb";
    private static final String UUID_TYPE = "uuid";
    
    public void setUserId(String userId) {
        setPGObject(USER_ID, UUID_TYPE, userId);
    }
    
    public void setClientState(String clientState) {
        setPGObject(CLIENT_STATE, JSONB_TYPE, clientState);
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
