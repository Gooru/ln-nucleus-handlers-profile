package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbutils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUserDemographic;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUserIdentity;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.profiles.processors.utils.HelperUtility;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public final class DBHelperUtility {
    
    private DBHelperUtility() {
        throw new AssertionError();
    }

    @SuppressWarnings("rawtypes")
    public static JsonArray getOwnerDemographics(Set<String> idlist) {
        LazyList<AJEntityUserDemographic> userDemographics = AJEntityUserDemographic.findBySQL(
            AJEntityUserDemographic.SELECT_DEMOGRAPHICS_MULTIPLE, HelperUtility.toPostgresArrayString(idlist));
        List<Map> usernames = Base.findAll(AJEntityUserIdentity.SELECT_USERNAME_MULIPLE,
            HelperUtility.toPostgresArrayString(idlist));
        Map<String, String> usernamesById = new HashMap<>();
        usernames.stream().forEach(username -> {
            Object name = username.get(AJEntityUserIdentity.USERNAME);
            String uname = (name != null && !name.toString().isEmpty()) ? name.toString() : null;
            usernamesById.put(username.get(AJEntityUserIdentity.USER_ID).toString(), uname);
        });

        JsonArray userDetailsArray = new JsonArray();
        if (!userDemographics.isEmpty()) {
            userDemographics.forEach(user -> {
                JsonObject userDemographic = new JsonObject(JsonFormatterBuilder
                    .buildSimpleJsonFormatter(false, AJEntityUserDemographic.DEMOGRAPHIC_FIELDS).toJson(user));
                userDemographic.put(AJEntityUserIdentity.USERNAME,
                    usernamesById.get(user.getString(AJEntityUserDemographic.ID)));
                userDetailsArray.add(userDemographic);
            });
        }

        return userDetailsArray;
    }
}
