package org.gooru.nucleus.handlers.profiles.app.components;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.gooru.nucleus.handlers.profiles.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.handlers.profiles.bootstrap.startup.Initializer;
import org.gooru.nucleus.handlers.profiles.processors.utils.PreferenceSettingsUtil;

public class UtilityManager implements Initializer, Finalizer {

  private static final UtilityManager ourInstance = new UtilityManager();

  public static UtilityManager getInstance() {
    return ourInstance;
  }

  private UtilityManager() {
  }

  @Override
  public void finalizeComponent() {

  }

  @Override
  public void initializeComponent(Vertx vertx, JsonObject config) {
    PreferenceSettingsUtil.initialize();
  }

}
