package com.nearprop.service.impl;

import com.nearprop.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Slf4j
@Primary
public class LocalFileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Override
    public String storeFile(MultipartFile file, String directory) throws IOException {
        Path uploadPath = Paths.get(uploadDir, directory);
        
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        String fileName = file.getOriginalFilename();
        Path targetLocation = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        log.info("Stored file locally: {}", targetLocation);
        return directory + "/" + fileName;
    }
    
    @Override
    public String generateThumbnail(String fileUrl) throws IOException {
        // Simple implementation - just return the original file URL
        // In a real implementation, this would generate a thumbnail
        log.info("Thumbnail generation not implemented, returning original URL: {}", fileUrl);
        return fileUrl;
    }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        try {
            Path filePath = Paths.get(uploadDir, fileUrl);
            Files.deleteIfExists(filePath);
            log.info("Deleted file locally: {}", filePath);
        } catch (IOException e) {
            log.error("Error deleting file: {}", fileUrl, e);
            throw e;
        }
    }
} 