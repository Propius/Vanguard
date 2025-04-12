package com.example.vanguard.common.config;

import java.util.Objects;
import javax.cache.CacheManager;
import javax.cache.Caching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {
  @Bean
  public JCacheCacheManager cacheManager() throws Exception {
    CacheManager cacheManager =
        Caching.getCachingProvider()
            .getCacheManager(
                Objects.requireNonNull(getClass().getResource("/ehcache.xml")).toURI(),
                getClass().getClassLoader());
    return new JCacheCacheManager(cacheManager);
  }
}
