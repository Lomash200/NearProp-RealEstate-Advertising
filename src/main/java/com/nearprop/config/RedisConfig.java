package com.nearprop.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${cache.properties.ttl:15m}")
    private String propertiesTtl;

    @Value("${cache.users.ttl:30m}")
    private String usersTtl;

    @Value("${cache.districts.ttl:60m}")
    private String districtsTtl;

    @Value("${cache.chat-rooms.ttl:5m}")
    private String chatRoomsTtl;

    @Value("${cache.chat-messages.ttl:2m}")
    private String chatMessagesTtl;

    @Value("${cache.reels.ttl:10m}")
    private String reelsTtl;

    @Value("${cache.visits.ttl:5m}")
    private String visitsTtl;

    @Value("${cache.search-results.ttl:5m}")
    private String searchResultsTtl;

    @Value("${cache.advertisements.ttl:10m}")
    private String advertisementsTtl;

    @Value("${cache.coupons.ttl:30m}")
    private String couponsTtl;

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Configure JSON serializer
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        objectMapper.registerModule(new JavaTimeModule());

        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Use String serializer for keys
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Use JSON serializer for values
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.parse("PT" + propertiesTtl))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // Custom TTL configurations for different cache types
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        cacheConfigurations.put("properties", defaultConfig.entryTtl(Duration.parse("PT" + propertiesTtl)));
        cacheConfigurations.put("users", defaultConfig.entryTtl(Duration.parse("PT" + usersTtl)));
        cacheConfigurations.put("districts", defaultConfig.entryTtl(Duration.parse("PT" + districtsTtl)));
        cacheConfigurations.put("chat-rooms", defaultConfig.entryTtl(Duration.parse("PT" + chatRoomsTtl)));
        cacheConfigurations.put("chat-messages", defaultConfig.entryTtl(Duration.parse("PT" + chatMessagesTtl)));
        cacheConfigurations.put("reels", defaultConfig.entryTtl(Duration.parse("PT" + reelsTtl)));
        cacheConfigurations.put("visits", defaultConfig.entryTtl(Duration.parse("PT" + visitsTtl)));
        cacheConfigurations.put("property-search", defaultConfig.entryTtl(Duration.parse("PT" + searchResultsTtl)));
        cacheConfigurations.put("user-properties", defaultConfig.entryTtl(Duration.parse("PT" + propertiesTtl)));
        cacheConfigurations.put("advertisements", defaultConfig.entryTtl(Duration.parse("PT" + advertisementsTtl)));
        cacheConfigurations.put("coupons", defaultConfig.entryTtl(Duration.parse("PT" + couponsTtl)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
} 