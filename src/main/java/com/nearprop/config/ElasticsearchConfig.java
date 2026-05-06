package com.nearprop.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchClientAutoConfiguration;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
@Data
@Slf4j
@ConditionalOnProperty(prefix = "elasticsearch", name = {"username", "password"})
@Import({ElasticsearchClientAutoConfiguration.class, ElasticsearchRestClientAutoConfiguration.class})
public class ElasticsearchConfig {
    private String host;
    private int port;
    private String username;
    private String password;
} 