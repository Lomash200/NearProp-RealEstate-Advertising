package com.nearprop.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    /**
     * Stores a file in the specified directory
     * 
     * @param file The file to store
     * @param directory The directory to store the file in
     * @return The URL of the stored file
     * @throws IOException If an error occurs during file storage
     */
    String storeFile(MultipartFile file, String directory) throws IOException;
    
    /**
     * Generates a thumbnail for an image file
     * 
     * @param fileUrl The URL of the original image file
     * @return The URL of the generated thumbnail
     * @throws IOException If an error occurs during thumbnail generation
     */
    String generateThumbnail(String fileUrl) throws IOException;
    
    /**
     * Deletes a file from storage
     * 
     * @param fileUrl The URL of the file to delete
     * @throws IOException If an error occurs during file deletion
     */
    void deleteFile(String fileUrl) throws IOException;
} 