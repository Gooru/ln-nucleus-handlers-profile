package org.gooru.nucleus.handlers.profiles.app.components;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.ehcache.spi.loaderwriter.CacheWritingException;
import org.gooru.nucleus.handlers.profiles.bootstrap.shutdown.Finalizer;
import org.gooru.nucleus.handlers.profiles.bootstrap.startup.Initializer;
import org.gooru.nucleus.handlers.profiles.constants.HelperConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author ashish on 7/12/16.
 */
public class DemographicsCacheStore implements Initializer, Finalizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemographicsCacheStore.class);
    private volatile boolean initialized = false;
    private JsonObject config;
    private static final String DEMOGRAPHICS_CACHE = "__demographics__";

    private static final int DEFAULT_SESSION_TIMEOUT = 3600;
    private static final int DEFAULT_CACHE_SIZE = 128;

    private CacheManager cacheManager;
    private Cache<String, JsonObject> sessionCache;

    public static DemographicsCacheStore getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void finalizeComponent() {
        cacheManager.removeCache(DEMOGRAPHICS_CACHE);
        cacheManager.close();
    }

    @Override
    public void initializeComponent(Vertx vertx, JsonObject config) {
        if (!initialized) {
            synchronized (Holder.INSTANCE) {
                if (!initialized) {
                    Integer expiryIdleTimeSeconds, cacheSize;

                    LOGGER.debug("Initializing the Demographics Cache Store");

                    this.config = config.getJsonObject(HelperConstants.DEMOGRAPHICS_CACHE);
                    expiryIdleTimeSeconds = getExpiryIdleTime();
                    cacheSize = getCacheSize();

                    LOGGER.info("Using cache size '{}' and session timeout '{}'", cacheSize, expiryIdleTimeSeconds);

                    cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build();
                    cacheManager.init();

                    sessionCache = cacheManager.createCache(DEMOGRAPHICS_CACHE, CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(String.class, JsonObject.class,
                            ResourcePoolsBuilder.heap(cacheSize)).withExpiry(
                            Expirations.timeToIdleExpiration(Duration.of(expiryIdleTimeSeconds, TimeUnit.SECONDS)))
                        .build());

                    initialized = true;
                }
            }
        }

    }

    private static final class Holder {
        private static final DemographicsCacheStore INSTANCE = new DemographicsCacheStore();
    }

    public void store(String userId, JsonObject profileData) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(profileData);
        sessionCache.put(userId, profileData);
    }

    public JsonObject fetch(String userId) {
        Objects.requireNonNull(userId);
        return sessionCache.get(userId);
    }

    public void remove(String userId) {
        Objects.requireNonNull(userId);
        try {
            sessionCache.remove(userId);
        } catch (CacheWritingException e) {
            LOGGER.warn("Not able to remove key: {}", userId, e);
        }
    }

    private Integer getCacheSize() {
        return config.getInteger(HelperConstants.DEMOGRAPHICS_CACHE_SIZE) == null ? DEFAULT_CACHE_SIZE :
            config.getInteger(HelperConstants.DEMOGRAPHICS_CACHE_SIZE);
    }

    private Integer getExpiryIdleTime() {
        return config.getInteger(HelperConstants.DEMOGRAPHICS_CACHE_TIMEOUT) == null ? DEFAULT_SESSION_TIMEOUT :
            config.getInteger(HelperConstants.DEMOGRAPHICS_CACHE_TIMEOUT);
    }

}
