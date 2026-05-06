package com.nearprop.service;

import com.nearprop.config.AwsConfig;
import com.nearprop.entity.User;
import com.nearprop.entity.Property;
import com.nearprop.exception.FileStorageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final AwsConfig awsConfig;
    
    /**
     * Upload a file to S3
     * 
     * @param file The file to upload
     * @param key The key (path) to store the file under
     * @param bucket The bucket to store the file in
     * @param contentType The content type of the file
     * @return The URL of the uploaded file
     */
    public String uploadFile(byte[] file, String key, String bucket, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(contentType)
                    .build();
                    
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file));
            
            return generateUrl(bucket, key);
        } catch (Exception e) {
            log.error("Error uploading file to S3: {}", e.getMessage(), e);
            throw new FileStorageException("Failed to upload file to S3", e);
        }
    }
    
    /**
     * Upload a video file to S3 with organized directory structure
     * 
     * @param file The video file to upload
     * @param thumbnailData Thumbnail image data
     * @param fileName Base filename for the uploaded files
     * @param user Owner of the reel
     * @param property Property associated with the reel
     * @return Map containing URLs for video and thumbnail
     */
    public Map<String, String> uploadReel(MultipartFile file, byte[] thumbnailData, String fileName, User user, Property property) {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String uniqueFileName = generateUniqueFileName(fileName, fileExtension);
        
        // Create structured path: reels/{role}/{ownerId}_{ownerName}/{propertyId}/{uniqueFileName}
        String role = user.getRoles().contains("ROLE_SELLER") ? "seller" : 
                     (user.getRoles().contains("ROLE_ADVISOR") ? "advisor" : 
                     (user.getRoles().contains("ROLE_DEVELOPER") ? "developer" : "user"));
        
        String ownerDirectory = user.getId() + "_" + sanitizeForPath(user.getName());
        String propertyDirectory = property.getId() + "_" + sanitizeForPath(property.getTitle());
        
        // Upload video file
        String videoKey = awsConfig.getS3().getReelsFolder() + "/" + 
                          role + "/" + 
                          ownerDirectory + "/" + 
                          propertyDirectory + "/" + 
                          uniqueFileName;
                          
        String videoUrl = uploadFile(getBytes(file), videoKey, awsConfig.getS3().getReelsBucket(), file.getContentType());
        
        // Upload thumbnail with same structure but in thumbnails folder
        String thumbnailKey = awsConfig.getS3().getThumbnailsFolder() + "/" + 
                             role + "/" + 
                             ownerDirectory + "/" + 
                             propertyDirectory + "/" + 
                             uniqueFileName.replace(fileExtension, "jpg");
                             
        String thumbnailUrl = uploadFile(thumbnailData, thumbnailKey, awsConfig.getS3().getReelsBucket(), "image/jpeg");
        
        Map<String, String> urls = new HashMap<>();
        urls.put("videoUrl", videoUrl);
        urls.put("thumbnailUrl", thumbnailUrl);
        
        return urls;
    }
    
    /**
     * Upload a property image with organized directory structure
     * 
     * @param file The image file to upload
     * @param user Owner of the property
     * @param property Property associated with the image
     * @return URL of the uploaded image
     */
    public String uploadPropertyImage(MultipartFile file, User user, Property property) {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = property.getId() + "-" + sanitizeForPath(property.getTitle());
        String uniqueFileName = generateUniqueFileName(fileName, fileExtension);
        
        // Create structured path: properties/{role}/{ownerId}_{ownerName}/{propertyId}/{uniqueFileName}
        String role = user.getRoles().contains("ROLE_SELLER") ? "seller" : 
                     (user.getRoles().contains("ROLE_ADVISOR") ? "advisor" : 
                     (user.getRoles().contains("ROLE_DEVELOPER") ? "developer" : "user"));
        
        String ownerDirectory = user.getId() + "_" + sanitizeForPath(user.getName());
        String propertyDirectory = property.getId() + "_" + sanitizeForPath(property.getTitle());
        
        String imageKey = "properties/" + 
                         role + "/" + 
                         ownerDirectory + "/" + 
                         propertyDirectory + "/" + 
                         uniqueFileName;
                         
        return uploadFile(getBytes(file), imageKey, awsConfig.getS3().getBucket(), file.getContentType());
    }
    
    /**
     * Delete a file from S3
     * 
     * @param key The key (path) of the file to delete
     * @param bucket The bucket containing the file
     */
    public void deleteFile(String key, String bucket) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
                    
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted file from S3: bucket={}, key={}", bucket, key);
        } catch (Exception e) {
            log.error("Error deleting file from S3: {}", e.getMessage(), e);
            throw new FileStorageException("Failed to delete file from S3", e);
        }
    }
    
    /**
     * Delete a reel and its thumbnail from S3
     * 
     * @param videoUrl The URL of the video file
     * @param thumbnailUrl The URL of the thumbnail file
     */
    public void deleteReel(String videoUrl, String thumbnailUrl) {
        String videoKey = extractKeyFromUrl(videoUrl);
        String thumbnailKey = extractKeyFromUrl(thumbnailUrl);
        
        deleteFile(videoKey, awsConfig.getS3().getReelsBucket());
        deleteFile(thumbnailKey, awsConfig.getS3().getReelsBucket());
    }
    
    /**
     * Generate a public URL for a file in S3
     * 
     * @param bucket The bucket containing the file
     * @param key The key (path) of the file
     * @return The public URL of the file
     */
    private String generateUrl(String bucket, String key) {
        String region = awsConfig.getRegion();
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
    }
    
    /**
     * Generate a unique filename for a file
     * 
     * @param originalFileName The original filename
     * @param extension The file extension
     * @return A unique filename
     */
    private String generateUniqueFileName(String originalFileName, String extension) {
        String sanitizedFileName = sanitizeForPath(originalFileName);
        return sanitizedFileName + "-" + UUID.randomUUID() + extension;
    }
    
    /**
     * Sanitize a string for use in a path
     * 
     * @param input The string to sanitize
     * @return A sanitized string
     */
    private String sanitizeForPath(String input) {
        if (input == null) return "unnamed";
        return input.toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }
    
    /**
     * Get the extension of a file
     * 
     * @param fileName The filename
     * @return The file extension
     */
    private String getFileExtension(String fileName) {
        if (fileName == null) return ".mp4";
        int lastDot = fileName.lastIndexOf(".");
        return lastDot > 0 ? fileName.substring(lastDot) : ".mp4";
    }
    
    /**
     * Extract the key from a URL
     * 
     * @param url The URL
     * @return The key (path) of the file
     */
    private String extractKeyFromUrl(String url) {
        String bucket = awsConfig.getS3().getReelsBucket();
        String bucketUrl = String.format("https://%s.s3.%s.amazonaws.com/", bucket, awsConfig.getRegion());
        return url.replace(bucketUrl, "");
    }
    
    /**
     * Get bytes from a MultipartFile
     * 
     * @param file The MultipartFile
     * @return The bytes of the file
     */
    private byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            log.error("Error getting bytes from file: {}", e.getMessage(), e);
            throw new FileStorageException("Failed to get bytes from file", e);
        }
    }
    
    /**
     * Upload multiple property images with organized directory structure
     * 
     * @param files The image files to upload
     * @param user Owner of the property
     * @param property Property associated with the images
     * @return List of URLs of the uploaded images
     */
    public List<String> uploadPropertyImages(List<MultipartFile> files, User user, Property property) {
        List<String> imageUrls = new ArrayList<>();
        
        for (MultipartFile file : files) {
            if (file != null && !file.isEmpty()) {
                String imageUrl = uploadPropertyImage(file, user, property);
                imageUrls.add(imageUrl);
            }
        }
        
        return imageUrls;
    }
    
    /**
     * Upload a property video with organized directory structure
     * 
     * @param file The video file to upload
     * @param user Owner of the property
     * @param property Property associated with the video
     * @return URL of the uploaded video
     */
    public String uploadPropertyVideo(MultipartFile file, User user, Property property) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = property.getId() + "-video-" + sanitizeForPath(property.getTitle());
        String uniqueFileName = generateUniqueFileName(fileName, fileExtension);
        
        // Create structured path: media/videos/{role}/{ownerId}_{ownerName}/{propertyId}/{uniqueFileName}
        String role = user.getRoles().contains("ROLE_SELLER") ? "seller" : 
                     (user.getRoles().contains("ROLE_ADVISOR") ? "advisor" : 
                     (user.getRoles().contains("ROLE_DEVELOPER") ? "developer" : "user"));
        
        String ownerDirectory = user.getId() + "_" + sanitizeForPath(user.getName());
        String propertyDirectory = property.getId() + "_" + sanitizeForPath(property.getTitle());
        
        String videoKey = "media/videos/" + 
                         role + "/" + 
                         ownerDirectory + "/" + 
                         propertyDirectory + "/" + 
                         uniqueFileName;
                         
        return uploadFile(getBytes(file), videoKey, awsConfig.getS3().getBucket(), file.getContentType());
    }
    
    /**
     * Upload advertisement media to S3 with organized directory structure
     * 
     * @param file The media file to upload
     * @param user Owner of the advertisement
     * @param adTitle Sanitized advertisement title for path
     * @param directory Base directory structure path
     * @return String URL of the uploaded file
     */
    public String uploadAdvertisementMedia(MultipartFile file, User user, String adTitle, String directory) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String uniqueFileName = generateUniqueFileName(adTitle, fileExtension);
        
        // Create the key with the structured directory path based on awsConfig.getS3().getAdvertisementFolder()
        String key = awsConfig.getS3().getAdvertisementFolder() + "/" + directory + "/" + uniqueFileName;
        
        log.info("Uploading advertisement media to S3: {} for user ID: {}, advertisement: {}", 
                key, user.getId(), adTitle);
                
        return uploadFile(getBytes(file), key, awsConfig.getS3().getBucket(), file.getContentType());
    }
    
    /**
     * Upload a user profile image to S3 with the user's name as part of the filename
     * 
     * @param file The profile image file to upload
     * @param user The user whose profile image is being uploaded
     * @return URL of the uploaded profile image
     */
    public String uploadProfileImage(MultipartFile file, User user) {
        try {
            String fileExtension = getFileExtension(file.getOriginalFilename());
            // Create filename with user's name: username_profile_pic
            String fileName = sanitizeForPath(user.getName()) + "_profile_pic" + fileExtension;
            
            // Create structured path: profiles/{userId}/{fileName}
            String imageKey = "profiles/" + user.getId() + "/" + fileName;
            
            return uploadFile(getBytes(file), imageKey, awsConfig.getS3().getBucket(), file.getContentType());
        } catch (Exception e) {
            log.error("Error uploading profile image to S3: {}", e.getMessage(), e);
            throw new FileStorageException("Failed to upload profile image to S3", e);
        }
    }
    
    /**
     * Upload a payment screenshot for a withdrawal request with better directory structure
     * 
     * @param requestId Withdrawal request ID
     * @param file Screenshot file to upload
     * @return URL of the uploaded screenshot
     */
    public String uploadWithdrawalScreenshot(Long requestId, MultipartFile file) {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String uniqueFileName = "withdrawal-" + requestId + "-proof" + fileExtension;
        
        // Create structured path: franchisee/withdrawals/{requestId}/{uniqueFileName}
        String key = "franchisee/withdrawals/" + requestId + "/" + uniqueFileName;
        
        return uploadFile(getBytes(file), key, awsConfig.getS3().getBucket(), file.getContentType());
    }
    
    /**
     * Upload payment proof for monthly revenue report
     * 
     * @param reportId Report ID
     * @param file Payment proof file
     * @return URL of the uploaded file
     */
    public String uploadMonthlyReportProof(Long reportId, MultipartFile file) {
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String uniqueFileName = "monthly-report-" + reportId + "-payment-proof" + fileExtension;
        
        // Create structured path: franchisee/monthly-reports/{reportId}/{uniqueFileName}
        String key = "franchisee/monthly-reports/" + reportId + "/" + uniqueFileName;
        
        return uploadFile(getBytes(file), key, awsConfig.getS3().getBucket(), file.getContentType());
    }


    public String uploadNotificationImage(MultipartFile image) {
        try {
            String originalName = image.getOriginalFilename();
            String extension = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : ".jpg";

            String fileName = "notification-" + System.currentTimeMillis() + extension;
            String key = "notifications/admin/" + fileName;

            return uploadFile(
                    image.getBytes(),                 // IOException yahin handle ho gaya
                    key,
                    awsConfig.getS3().getBucket(),
                    image.getContentType()
            );
        } catch (IOException e) {
            throw new FileStorageException("Failed to upload notification image", e);
        }
    }

} 