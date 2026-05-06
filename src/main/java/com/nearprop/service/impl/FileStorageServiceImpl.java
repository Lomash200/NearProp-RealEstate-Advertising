package com.nearprop.service.impl;

import com.nearprop.config.FileStorageProperties;
import com.nearprop.exception.FileStorageException;
import com.nearprop.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageProperties fileStorageProperties;
    private final Path fileStorageLocation;
    
    // Constructor with initialization
    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file, String directory) throws IOException {
        // Normalize file name and create a unique name to prevent duplicates
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        
        // Create directory if it doesn't exist
        Path directoryPath = this.fileStorageLocation.resolve(directory);
        Files.createDirectories(directoryPath);
        
        // Copy file to the target location
        Path targetLocation = directoryPath.resolve(uniqueFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        // Return the relative path from upload dir for storage in DB
        return directory + "/" + uniqueFileName;
    }

    @Override
    public String generateThumbnail(String fileUrl) throws IOException {
        Path filePath = this.fileStorageLocation.resolve(fileUrl);
        if (!Files.exists(filePath)) {
            throw new FileStorageException("File not found: " + fileUrl);
        }
        
        // Extract directory and filename
        String fileName = filePath.getFileName().toString();
        String directory = fileUrl.substring(0, fileUrl.lastIndexOf('/'));
        String thumbnailDirectory = directory + "/thumbnails";
        String thumbnailFileName = "thumb_" + fileName;
        
        // Create thumbnails directory if it doesn't exist
        Path thumbnailDirectoryPath = this.fileStorageLocation.resolve(thumbnailDirectory);
        Files.createDirectories(thumbnailDirectoryPath);
        
        // Generate thumbnail
        Path thumbnailPath = thumbnailDirectoryPath.resolve(thumbnailFileName);
        createThumbnail(filePath.toFile(), thumbnailPath.toFile(), 200, 200);
        
        // Return the relative path of the thumbnail
        return thumbnailDirectory + "/" + thumbnailFileName;
    }

    @Override
    public void deleteFile(String fileUrl) throws IOException {
        Path filePath = this.fileStorageLocation.resolve(fileUrl);
        Files.deleteIfExists(filePath);
    }
    
    // Helper methods
    private void createThumbnail(File sourceFile, File destFile, int width, int height) throws IOException {
        BufferedImage img = ImageIO.read(sourceFile);
        BufferedImage thumbnailImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g = thumbnailImg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, width, height, null);
        g.dispose();
        
        String extension = getFileExtension(destFile.getName());
        ImageIO.write(thumbnailImg, extension, destFile);
    }
    
    private String getFileExtension(String fileName) {
        if (fileName.lastIndexOf('.') == -1) {
            return "jpg"; // Default extension
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
    
    public Resource loadFileAsResource(String filePath) {
        try {
            Path file = this.fileStorageLocation.resolve(filePath).normalize();
            Resource resource = new UrlResource(file.toUri());
            
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileStorageException("File not found: " + filePath);
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found: " + filePath, ex);
        }
    }
} 