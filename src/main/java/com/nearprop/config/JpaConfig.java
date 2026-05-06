package com.nearprop.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.nearprop")
@EnableJpaRepositories(basePackages = "com.nearprop")
public class JpaConfig {
    // Configuration class to ensure proper component scanning
}
