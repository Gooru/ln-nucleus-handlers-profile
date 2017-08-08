package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbutils;

import java.util.Set;

import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUsers;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.profiles.processors.utils.HelperUtility;
import org.javalite.activejdbc.LazyList;

import io.vertx.core.json.JsonArray;

public final class DBHelperUtility {
    
    private DBHelperUtility() {
        throw new AssertionError();
    }

    public static JsonArray getOwnerDemographics(Set<String> idlist) {
        LazyList<AJEntityUsers> userDemographics = AJEntityUsers.findBySQL(
            AJEntityUsers.SELECT_MULTIPLE_BY_ID, HelperUtility.toPostgresArrayString(idlist));
        JsonArray userDetailsArray = new JsonArray(JsonFormatterBuilder
            .buildSimpleJsonFormatter(false, AJEntityUsers.SUMMARY_FIELDS).toJson(userDemographics));
        return userDetailsArray;
    }
}
