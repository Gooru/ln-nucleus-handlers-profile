package org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.dbutils;

import io.vertx.core.json.JsonArray;
import java.util.Set;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.entities.AJEntityUsers;
import org.gooru.nucleus.handlers.profiles.processors.repositories.activejdbc.formatter.JsonFormatterBuilder;
import org.gooru.nucleus.handlers.profiles.processors.utils.HelperUtility;
import org.javalite.activejdbc.LazyList;

public final class DBHelperUtility {

  private DBHelperUtility() {
    throw new AssertionError();
  }

  public static JsonArray getOwnerDemographics(Set<String> idlist) {
    LazyList<AJEntityUsers> userDemographics = AJEntityUsers.findBySQL(
        AJEntityUsers.SELECT_MULTIPLE_BY_ID, HelperUtility.toPostgresArrayString(idlist));
    return new JsonArray(JsonFormatterBuilder
        .buildSimpleJsonFormatter(false, AJEntityUsers.SUMMARY_FIELDS).toJson(userDemographics));
  }
}
