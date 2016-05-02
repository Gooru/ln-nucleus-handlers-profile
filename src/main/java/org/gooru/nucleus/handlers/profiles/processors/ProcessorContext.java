package org.gooru.nucleus.handlers.profiles.processors;

import io.vertx.core.json.JsonObject;

public class ProcessorContext {

    private final String userId;
    private final JsonObject prefs;
    private final JsonObject request;
    private final String userIdFromURL;

    public ProcessorContext(String userId, JsonObject prefs, JsonObject request, String userIdFromURL) {
        if (prefs == null || userId == null || prefs.isEmpty()) {
            throw new IllegalStateException("Processor Context creation failed because of invalid values");
        }
        this.userId = userId;
        this.prefs = prefs.copy();
        this.request = request != null ? request.copy() : null;
        this.userIdFromURL = userIdFromURL;
    }

    public String userId() {
        return this.userId;
    }

    public JsonObject prefs() {
        return this.prefs.copy();
    }

    public JsonObject request() {
        return this.request;
    }

    public String userIdFromURL() {
        return this.userIdFromURL;
    }
}
