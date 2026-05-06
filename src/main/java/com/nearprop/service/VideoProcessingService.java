package com.nearprop.service;

import com.nearprop.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class VideoProcessingService {

    // Default max size is 40MB as per requirement
    private static final int DEFAULT_MAX_SIZE_MB = 40;
    private static final int MIN_DURATION_SECONDS = 15;
    private static final int DEFAULT_DURATION_SECONDS = 30; // Assumed duration
    
    /**
     * Process a video file to extract metadata
     * @param videoFile The video file to process
     * @return Map containing video metadata
     */
    public Map<String, Object> processVideo(MultipartFile videoFile) throws IOException {
        // Create a temporary file
        String originalFilename = videoFile.getOriginalFilename();
        String fileExtension = originalFilename != null ? 
            originalFilename.substring(originalFilename.lastIndexOf(".")) : ".mp4";
            
        File tempFile = File.createTempFile("video-", fileExtension);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(videoFile.getBytes());
        }

        Map<String, Object> result = new HashMap<>();
        
        try {
            // In a real implementation, we would extract metadata using a library
            // For now, we'll provide mock data
            result.put("durationSeconds", DEFAULT_DURATION_SECONDS);
            result.put("width", 1920);
            result.put("height", 1080);
            result.put("frameRate", 30.0);
            result.put("format", fileExtension.substring(1));
            result.put("videoBitrate", 8000000);
            result.put("fileSizeMB", videoFile.getSize() / (1024.0 * 1024.0));
            result.put("fileName", originalFilename);
            
            // In a real implementation, we'd generate a thumbnail
            // For now, we'll simulate having a thumbnail
            result.put("thumbnail", videoFile.getBytes()); // This would be the actual thumbnail bytes
            
        } catch (Exception e) {
            log.error("Error processing video file: {}", e.getMessage(), e);
            throw new IOException("Failed to process video file: " + e.getMessage(), e);
        } finally {
            // Cleanup
            try {
                Files.deleteIfExists(tempFile.toPath());
            } catch (IOException e) {
                log.warn("Failed to delete temp file: {}", tempFile, e);
            }
        }
        
        return result;
    }
    
    /**
     * Validates if the video file meets the requirements
     * @param videoFile The video file to validate
     * @param maxDurationSeconds Maximum allowed duration in seconds
     * @param maxFileSizeMB Maximum allowed file size in MB (defaults to 40MB if less than 1)
     * @param allowedFormats Comma-separated list of allowed formats (mp4,mov,etc)
     * @return ResponseDto with validation result and details
     */
    public ResponseDto<Boolean> validateVideoWithResponse(MultipartFile videoFile, int maxDurationSeconds, 
            int maxFileSizeMB, String allowedFormats) {
        
        // Enforce max file size of 40MB if specified size is larger or not set properly
        int effectiveMaxFileSizeMB = (maxFileSizeMB < 1 || maxFileSizeMB > DEFAULT_MAX_SIZE_MB) 
                ? DEFAULT_MAX_SIZE_MB : maxFileSizeMB;
        
        if (videoFile == null || videoFile.isEmpty()) {
            String errorMsg = "Video file is null or empty";
            log.error(errorMsg);
            return ResponseDto.error(errorMsg);
        }
        
        String fileName = videoFile.getOriginalFilename();
        if (fileName == null) {
            String errorMsg = "Video filename is null";
            log.error(errorMsg);
            return ResponseDto.error(errorMsg);
        }
        
        // Check file size
        long maxSizeBytes = effectiveMaxFileSizeMB * 1024 * 1024L;
        double fileSizeMB = videoFile.getSize() / (1024.0 * 1024.0);
        if (videoFile.getSize() > maxSizeBytes) {
            String errorMsg = String.format("Video file size (%.2f MB) exceeds maximum allowed size of %d MB", 
                    fileSizeMB, effectiveMaxFileSizeMB);
            log.error(errorMsg);
            
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("fileSize", fileSizeMB);
            errorDetails.put("maxFileSize", effectiveMaxFileSizeMB);
            errorDetails.put("fileName", fileName);
            
            return ResponseDto.error(errorMsg, errorDetails);
        }
        
        // Check file format
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        String[] formats = allowedFormats.split(",");
        boolean formatAllowed = false;
        for (String format : formats) {
            if (format.trim().equalsIgnoreCase(fileExtension)) {
                formatAllowed = true;
                break;
            }
        }
        
        if (!formatAllowed) {
            String errorMsg = String.format("Video format %s is not allowed. Allowed formats: %s", 
                    fileExtension, allowedFormats);
            log.error(errorMsg);
            
            Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put("format", fileExtension);
            errorDetails.put("allowedFormats", allowedFormats);
            errorDetails.put("fileName", fileName);
            
            return ResponseDto.error(errorMsg, errorDetails);
        }
        
        // In a real implementation, we would check duration using a library
        // For this implementation, we're assuming the file format is valid and duration is within limits
        
        // All validations passed
        Map<String, Object> successDetails = new HashMap<>();
        successDetails.put("fileSize", fileSizeMB);
        successDetails.put("format", fileExtension);
        successDetails.put("fileName", fileName);
        successDetails.put("assumedDuration", DEFAULT_DURATION_SECONDS);
        
        return ResponseDto.success("Video validation successful", true);
    }
    
    /**
     * Legacy method for backward compatibility
     */
    public boolean validateVideo(MultipartFile videoFile, int maxDurationSeconds, int maxFileSizeMB, String allowedFormats) {
        ResponseDto<Boolean> response = validateVideoWithResponse(videoFile, maxDurationSeconds, 
                Math.min(maxFileSizeMB, DEFAULT_MAX_SIZE_MB), allowedFormats);
        return response.isSuccess();
    }
} 