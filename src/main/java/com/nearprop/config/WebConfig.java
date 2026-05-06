package com.nearprop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.ComponentScan;

@Configuration
@ComponentScan(basePackages = {
    "com.nearprop.controller",
    "com.nearprop.controller.franchisee" // Explicitly include the franchisee package
})
public class WebConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
} 