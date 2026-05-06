package com.nearprop.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import java.util.Properties;

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
@Data
@Slf4j
public class EmailConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private Properties properties = new Properties();

    @Data
    public static class Properties {
        private Mail mail = new Mail();

        @Data
        public static class Mail {
            private Smtp smtp = new Smtp();

            @Data
            public static class Smtp {
                private boolean auth;
                private Starttls starttls = new Starttls();

                @Data
                public static class Starttls {
                    private boolean enable;
                }
            }
        }
    }
    
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);
        
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            log.info("Configuring JavaMailSender with credentials");
            mailSender.setUsername(username);
            mailSender.setPassword(password);
            
            java.util.Properties javaMailProperties = new java.util.Properties();
            javaMailProperties.put("mail.smtp.auth", properties.getMail().getSmtp().isAuth());
            javaMailProperties.put("mail.smtp.starttls.enable", properties.getMail().getSmtp().getStarttls().isEnable());
            mailSender.setJavaMailProperties(javaMailProperties);
        } else {
            log.warn("Email credentials not provided. Email functionality will be unavailable.");
        }
        
        return mailSender;
    }
} 