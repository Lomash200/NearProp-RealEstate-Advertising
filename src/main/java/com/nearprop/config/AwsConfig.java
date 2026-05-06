package com.nearprop.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Data
@Slf4j
public class AwsConfig {
    private String accessKey;
    private String secretKey;
    private String region = "ap-south-1";
    private S3Config s3 = new S3Config();

    @Data
    public static class S3Config {
        private String bucket = "nearprop-documents";
        private String reelsBucket = "nearprop-reels";
        private String reelsFolder = "reels";
        private String thumbnailsFolder = "thumbnails";
        private String propertyImagesFolder = "properties";
        private String ownersFolder = "owners";
        private String advertisementFolder = "advertisements";
        private String userDocumentsFolder = "user-documents";
        private long maxFileSize = 104857600; // 100MB in bytes
    }

    @Bean
    @ConditionalOnProperty(prefix = "aws", name = {"access-key", "secret-key"})
    public S3Client s3Client() {
        if (!StringUtils.hasText(accessKey) || !StringUtils.hasText(secretKey)) {
            throw new IllegalArgumentException("AWS access key and secret key cannot be empty");
        }
        
        log.info("Configuring real AWS S3 client");
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .region(Region.of(region))
                .build();
    }
    
    @Bean
    @ConditionalOnMissingBean(S3Client.class)
    public S3Client mockS3Client() {
        log.warn("Using mock S3Client. AWS credentials not provided. File operations will fail.");
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }
} 