package org.mongodb.utility.config;

import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class AppConfig {

    private static Map<String, Object> appCache = new HashMap<>();

    public void addToCache(String K, Object V) {
        appCache.put(K, V);
    }
    public Object getFromCache(String K) {
        return appCache.get(K);
    }
    public Map<String, Object> getAppCache() {
        return appCache;
    }

}
