package org.gooru.nucleus.handlers.profiles.app.components;

import org.gooru.nucleus.handlers.profiles.bootstrap.startup.Initializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class AppConfiguration implements Initializer {
    
    private volatile boolean initialized = false;
    private static final String KEY = "__KEY__";
    private static final String APP_CONFIG_KEY = "app.configuration";
    private static final String MAX_PAGESIZE_KEY = "max.pagesize";
    private static final String DEFAULT_PAGESIZE_KEY = "default.pagesize";
    private static final JsonObject configuration = new JsonObject();
    
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfiguration.class);
    
    public static AppConfiguration getInstance() {
        return Holder.INSTANCE;
    }
    
    private AppConfiguration() {
    }
    
    @Override
    public void initializeComponent(Vertx vertx, JsonObject config) {
        if (!initialized) {
            synchronized (Holder.INSTANCE) {
                if (!initialized) {
                    JsonObject appConfiguration = config.getJsonObject(APP_CONFIG_KEY);
                    if (appConfiguration == null || appConfiguration.isEmpty()) {
                        LOGGER.warn("App configuration is not available");
                    } else {
                        configuration.put(KEY, appConfiguration);
                        initialized = true;
                    }
                }
            }
        }
    }
    
    public Integer getMaxPagesize() {
        return configuration.getJsonObject(KEY).getInteger(MAX_PAGESIZE_KEY);
    }
    
    public Integer getDefaultPagesize() {
        return configuration.getJsonObject(KEY).getInteger(DEFAULT_PAGESIZE_KEY);
    }
    
    private static final class Holder {
        private static final AppConfiguration INSTANCE = new AppConfiguration();
    }

}
