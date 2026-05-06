package com.nearprop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtConfig {
    private String secret;
    private ExpirationConfig expiration = new ExpirationConfig();

    @Data
    public static class ExpirationConfig {
        // Default token expiration (used if no specific role expiration is defined)
        private long token = 86400000; // 24 hours in milliseconds
        
        // Non-expiring token for ROLE_USER (100 years in milliseconds)
        private long user = 3153600000000L; // 100 years in milliseconds
        
        // 7-day token for other roles
        private long other = 604800000L; // 7 days in milliseconds
    }
} 