package org.gooru.nucleus.profiles.processors.events;

import io.vertx.core.json.JsonObject;

public interface EventBuilder {

  JsonObject build();
}
