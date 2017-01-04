package org.gooru.nucleus.handlers.profiles.processors;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

public class ProcessorContext {

    private final String userId;
    private final JsonObject session;
    private final JsonObject request;
    private final String userIdFromURL;
    private final MultiMap requestHeaders;

    public ProcessorContext(String userId, JsonObject session, JsonObject request, String userIdFromURL,
        MultiMap headers) {
        if (session == null || userId == null || session.isEmpty() || headers == null || headers.isEmpty()) {
            throw new IllegalStateException("Processor Context creation failed because of invalid values");
        }
        this.userId = userId;
        this.session = session.copy();
        this.request = request != null ? request.copy() : null;
        this.userIdFromURL = userIdFromURL;
        this.requestHeaders = headers;
    }

    public String userId() {
        return this.userId;
    }

    public JsonObject session() {
        return this.session.copy();
    }

    public JsonObject request() {
        return this.request;
    }

    public String userIdFromURL() {
        return this.userIdFromURL;
    }

    public MultiMap requestHeaders() {
        return this.requestHeaders;
    }
}
