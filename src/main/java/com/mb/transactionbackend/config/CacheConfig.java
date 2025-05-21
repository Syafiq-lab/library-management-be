package com.mb.transactionbackend.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@Profile("prod")
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));


        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();


        cacheConfigs.put("booksList", defaultConfig.entryTtl(Duration.ofMinutes(15)));
        cacheConfigs.put("usersList", defaultConfig.entryTtl(Duration.ofMinutes(15)));


        cacheConfigs.put("books", defaultConfig.entryTtl(Duration.ofMinutes(60)));
        cacheConfigs.put("users", defaultConfig.entryTtl(Duration.ofMinutes(60)));


        cacheConfigs.put("loansByUser", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigs.put("loansByBook", defaultConfig.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}