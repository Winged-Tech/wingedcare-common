package com.wingedtech.common.autoconfigure.multitenancy.cache;

import org.springframework.boot.autoconfigure.cache.CacheType;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class MultiTenantCacheConfigurations {

    private static final Map<CacheType, Class<?>> MAPPINGS;

    static {
        Map<CacheType, Class<?>> mappings = new EnumMap<>(CacheType.class);
        mappings.put(CacheType.SIMPLE, SimpleCacheConfiguration.class);
        mappings.put(CacheType.NONE, NoOpCacheConfiguration.class);
        MAPPINGS = Collections.unmodifiableMap(mappings);
    }

    private MultiTenantCacheConfigurations() {
    }

    public static String getConfigurationClass(CacheType cacheType) {
        Class<?> configurationClass = MAPPINGS.get(cacheType);
        if (configurationClass != null) {
            return configurationClass.getName();
        }
        else {
            return null;
        }
    }

    public static CacheType getType(String configurationClassName) {
        for (Map.Entry<CacheType, Class<?>> entry : MAPPINGS.entrySet()) {
            if (entry.getValue().getName().equals(configurationClassName)) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException(
                "Unknown configuration class " + configurationClassName);
    }
}
