package com.nearprop.advertisement.service.impl;

import com.nearprop.advertisement.dto.AdvertisementDto;
import com.nearprop.advertisement.dto.CreateAdvertisementDto;
import com.nearprop.advertisement.entity.Advertisement;
import com.nearprop.advertisement.mapper.AdvertisementMapper;
import com.nearprop.advertisement.repository.AdvertisementRepository;
import com.nearprop.advertisement.service.AdvertisementService;
import com.nearprop.entity.District;
import com.nearprop.entity.Role;
import com.nearprop.entity.User;
import com.nearprop.exception.BadRequestException;
import com.nearprop.exception.EntityNotFoundException;
import com.nearprop.exception.UnauthorizedAccessException;
import com.nearprop.repository.DistrictRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementServiceImpl implements AdvertisementService {
    
    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;
    private final AdvertisementMapper advertisementMapper;
    private final S3Service s3Service;
    
    private static final int MAX_ADS_PER_DISTRICT = 5;



    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "advertisements", allEntries = true)
    public void deleteAdvertisementSubAdmin(Long id, Long userId) {
        log.info("Deleting advertisement ID: {} by user ID: {}", id, userId);
        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow(() -> {
            log.error("Advertisement not found with ID: {}", id);
            return new EntityNotFoundException("Advertisement not found with ID: " + id);
        });
        advertisementRepository.delete(advertisement);
        log.info("Advertisement deleted with ID: {}", id);
    }

    @Override
    public List<AdvertisementDto> getAllAdvertisements() {
        List<Advertisement> ads = advertisementRepository.findAll();
        return ads.stream().map(advertisementMapper::toDto).toList();
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "advertisements", allEntries = true)
    public AdvertisementDto createAdvertisement(CreateAdvertisementDto dto, Long userId) {
        log.info("Creating advertisement request received from user ID: {}", userId);
        
        // Get user entity
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new EntityNotFoundException("User not found with ID: " + userId);
                });
        
        // Process file uploads if provided
        processFileUploads(dto, user);
        
        // Get primary district
        District primaryDistrict = null;
        if (dto.getDistrictId() != null) {
            primaryDistrict = districtRepository.findById(dto.getDistrictId())
                    .orElseThrow(() -> {
                        log.error("District not found with ID: {}", dto.getDistrictId());
                        return new EntityNotFoundException("District not found with ID: " + dto.getDistrictId());
                    });
        }
        
        // Get target districts if IDs are provided
        Set<District> targetDistricts = new HashSet<>();
        if (dto.getTargetDistrictIds() != null && !dto.getTargetDistrictIds().isEmpty()) {
            targetDistricts = dto.getTargetDistrictIds().stream()
                    .map(districtId -> districtRepository.findById(districtId)
                            .orElseThrow(() -> {
                                log.error("District not found with ID: {}", districtId);
                                return new EntityNotFoundException("District not found with ID: " + districtId);
                            }))
                    .collect(Collectors.toSet());
        }
        
        Advertisement advertisement = advertisementMapper.toEntity(dto, primaryDistrict, targetDistricts, user);
        Advertisement savedAdvertisement = advertisementRepository.save(advertisement);
        
        log.info("Advertisement created with ID: {}", savedAdvertisement.getId());
        return advertisementMapper.toDto(savedAdvertisement);
    }
    
    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "advertisements", allEntries = true)
    public AdvertisementDto updateAdvertisement(Long id, CreateAdvertisementDto dto, Long userId) {
        log.info("Updating advertisement ID: {} by user ID: {}", id, userId);
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Advertisement not found with ID: {}", id);
                    return new EntityNotFoundException("Advertisement not found with ID: " + id);
                });
        
        // Check if user is authorized to update this advertisement
        if (!advertisement.getCreatedBy().getId().equals(userId)) {
            log.warn("Unauthorized access: User {} attempted to update advertisement {}", userId, id);
            throw new UnauthorizedAccessException("Not authorized to update this advertisement");
        }
        
        // Get user entity for S3 upload
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new EntityNotFoundException("User not found with ID: " + userId);
                });
                
        // Process file uploads if provided
        processFileUploads(dto, user);
        
        // Get primary district
        District primaryDistrict = null;
        if (dto.getDistrictId() != null) {
            primaryDistrict = districtRepository.findById(dto.getDistrictId())
                    .orElseThrow(() -> {
                        log.error("District not found with ID: {}", dto.getDistrictId());
                        return new EntityNotFoundException("District not found with ID: " + dto.getDistrictId());
                    });
        }
        
        // Get target districts if IDs are provided
        Set<District> targetDistricts = new HashSet<>();
        if (dto.getTargetDistrictIds() != null && !dto.getTargetDistrictIds().isEmpty()) {
            targetDistricts = dto.getTargetDistrictIds().stream()
                    .map(districtId -> districtRepository.findById(districtId)
                            .orElseThrow(() -> {
                                log.error("District not found with ID: {}", districtId);
                                return new EntityNotFoundException("District not found with ID: " + districtId);
                            }))
                    .collect(Collectors.toSet());
        }
        
        // Update the advertisement entity
        advertisementMapper.updateEntityFromDto(dto, advertisement, primaryDistrict, targetDistricts);
        
        Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);
        log.info("Advertisement updated with ID: {}", updatedAdvertisement.getId());
        
        return advertisementMapper.toDto(updatedAdvertisement);
    }
    
    @Override
    public AdvertisementDto getAdvertisement(Long id) {
        log.info("Fetching advertisement with ID: {}", id);
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Advertisement not found with ID: {}", id);
                    return new EntityNotFoundException("Advertisement not found with ID: " + id);
                });
        
        log.debug("Advertisement found: {}", advertisement.getTitle());
        return advertisementMapper.toDto(advertisement);
    }
    
    @Override
    public Page<AdvertisementDto> getAllActiveAdvertisements(Pageable pageable) {
        log.info("Fetching all active advertisements with pagination: {}", pageable);
        Page<Advertisement> advertisements = advertisementRepository.findByActiveTrue(pageable);
        log.debug("Found {} active advertisements", advertisements.getTotalElements());
        return advertisements.map(advertisementMapper::toDto);
    }
    
    @Override
    public Page<AdvertisementDto> getUserAdvertisements(Long userId, Pageable pageable) {
        log.info("Fetching advertisements for user ID: {} with pagination: {}", userId, pageable);
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            log.error("User not found with ID: {}", userId);
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        
        Page<Advertisement> advertisements = advertisementRepository.findByCreatedById(userId, pageable);
        log.debug("Found {} advertisements for user ID: {}", advertisements.getTotalElements(), userId);
        return advertisements.map(advertisementMapper::toDto);
    }
    
    @Override
    @Cacheable(value = "advertisements", key = "'district_' + #districtName + '_' + #pageable.pageNumber + '_' + #pageable.pageSize + '_' + #pageable.sort")
    public Page<AdvertisementDto> getAdvertisementsByDistrict(String districtName, Pageable pageable) {
        log.info("Fetching advertisements for district: {} with pagination: {}", districtName, pageable);
        LocalDateTime now = LocalDateTime.now();
        
        // First check for ads with this district as primary district
        Page<Advertisement> advertisements = advertisementRepository
                .findByActiveTrueAndDistrictNameAndValidFromBeforeAndValidUntilAfter(
                        districtName, now, now, pageable);
        
        // If we have less than 5 ads, we could also include ads that target this district
        if (advertisements.getContent().size() < MAX_ADS_PER_DISTRICT) {
            // Get ads that target this district but don't have it as primary district
            Page<Advertisement> targetingAds = advertisementRepository
                    .findByActiveTrueAndTargetDistrictsContainingAndValidFromBeforeAndValidUntilAfter(
                            districtName, now, pageable);
            
            // Combine the results (up to MAX_ADS_PER_DISTRICT)
            List<Advertisement> allAds = advertisements.getContent();
            for (Advertisement ad : targetingAds.getContent()) {
                if (allAds.size() >= MAX_ADS_PER_DISTRICT) {
                    break;
                }
                // Only add if not already in the list
                if (allAds.stream().noneMatch(a -> a.getId().equals(ad.getId()))) {
                    allAds.add(ad);
                }
            }
            
            // Convert to DTOs
            List<AdvertisementDto> dtos = allAds.stream()
                    .map(advertisementMapper::toDto)
                    .collect(Collectors.toList());
            
            log.debug("Found {} advertisements for district: {}", dtos.size(), districtName);
            return new org.springframework.data.domain.PageImpl<>(dtos, pageable, dtos.size());
        }
        
        log.debug("Found {} advertisements for district: {}", advertisements.getTotalElements(), districtName);
        return advertisements.map(advertisementMapper::toDto);
    }
    
    @Override
    public List<AdvertisementDto> getTop5AdvertisementsByDistrict(String districtName) {
        log.info("Fetching top 5 advertisements for district: {}", districtName);
        LocalDateTime now = LocalDateTime.now();
        
        List<Advertisement> advertisements = advertisementRepository.findTop5ActiveAdsByDistrict(districtName, now);
        
        List<AdvertisementDto> dtos = advertisements.stream()
                .map(advertisementMapper::toDto)
                .collect(Collectors.toList());
        
        log.debug("Found {} top advertisements for district: {}", dtos.size(), districtName);
        return dtos;
    }
    
    @Override
    public Page<AdvertisementDto> getAdvertisementsNearLocation(Double latitude, Double longitude, Pageable pageable) {
        log.info("Fetching advertisements near location: lat={}, long={} with pagination: {}", 
                latitude, longitude, pageable);
        LocalDateTime now = LocalDateTime.now();
        Page<Advertisement> advertisements = advertisementRepository.findActiveAdsNearLocation(
                latitude, longitude, now, pageable);
        
        log.debug("Found {} advertisements near location", advertisements.getTotalElements());
        return advertisements.map(advertisementMapper::toDto);
    }
    
    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "advertisements", allEntries = true)
    public AdvertisementDto setAdvertisementActive(Long id, boolean active, Long userId) {
        log.info("Setting advertisement ID: {} active status to: {} by user ID: {}", id, active, userId);
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Advertisement not found with ID: {}", id);
                    return new EntityNotFoundException("Advertisement not found with ID: " + id);
                });
        
        // Check if user is authorized to update this advertisement
        if (!advertisement.getCreatedBy().getId().equals(userId)) {
            log.warn("Unauthorized access: User {} attempted to update advertisement {}", userId, id);
            throw new UnauthorizedAccessException("Not authorized to update this advertisement");
        }
        
        // If activating, check if district already has maximum number of advertisements
        if (active && !advertisement.isActive() && advertisement.getDistrict() != null) {
            LocalDateTime now = LocalDateTime.now();
            Long districtAdCount = advertisementRepository.countActiveAdsByDistrict(advertisement.getDistrictName(), now);
            if (districtAdCount >= MAX_ADS_PER_DISTRICT) {
                log.error("District {} already has maximum number of advertisements ({})", 
                        advertisement.getDistrictName(), MAX_ADS_PER_DISTRICT);
                throw new BadRequestException("District " + advertisement.getDistrictName() + 
                        " already has the maximum number of advertisements (" + MAX_ADS_PER_DISTRICT + ")");
            }
        }
        
        advertisement.setActive(active);
        Advertisement updatedAdvertisement = advertisementRepository.save(advertisement);
        
        log.info("Advertisement {} with ID: {}", active ? "activated" : "deactivated", updatedAdvertisement.getId());
        return advertisementMapper.toDto(updatedAdvertisement);
    }
    
    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "advertisements", allEntries = true)
    public void deleteAdvertisement(Long id, Long userId) {
        log.info("Deleting advertisement ID: {} by user ID: {}", id, userId);
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Advertisement not found with ID: {}", id);
                    return new EntityNotFoundException("Advertisement not found with ID: " + id);
                });
        
        // Check if user is authorized to delete this advertisement
        if (!advertisement.getCreatedBy().getId().equals(userId)) {
            log.warn("Unauthorized access: User {} attempted to delete advertisement {}", userId, id);
            throw new UnauthorizedAccessException("Not authorized to delete this advertisement");
        }
        
        advertisementRepository.delete(advertisement);
        log.info("Advertisement deleted with ID: {}", id);
    }
    
    /**
     * Process file uploads from form data and store in AWS S3 with structured folders
     * @param dto The DTO containing potential file uploads
     * @param user The user creating/updating the advertisement
     */
    private void processFileUploads(CreateAdvertisementDto dto, User user) {
        try {
            // Create structured folder path for advertisement
            String sanitizedTitle = sanitizeForPath(dto.getTitle());
            String userRole = getUserRole(user);
            String advertiserName = sanitizeForPath(user.getName());
            
            // Base directory structure: media/advertisements/{role}/{advertiser_id}_{advertiser_name}/{ad_title}/
            String baseDirectory = String.format("media/advertisements/%s/%d_%s/%s",
                    userRole, user.getId(), advertiserName, sanitizedTitle);
            
            // Process banner image if provided
            if (dto.getBannerImage() != null && !dto.getBannerImage().isEmpty()) {
                // Images go to: media/advertisements/{role}/{advertiser_id}_{advertiser_name}/{ad_title}/images/
                String imageDirectory = baseDirectory + "/images";
                String bannerImagePath = s3Service.uploadAdvertisementMedia(
                    dto.getBannerImage(), 
                    user, 
                    sanitizedTitle,
                    imageDirectory
                );
                dto.setBannerImageUrl(bannerImagePath);
                log.info("Uploaded banner image to S3: {}", bannerImagePath);
            }
            
            // Process video file if provided
            if (dto.getVideoFile() != null && !dto.getVideoFile().isEmpty()) {
                // Videos go to: media/advertisements/{role}/{advertiser_id}_{advertiser_name}/{ad_title}/videos/
                String videoDirectory = baseDirectory + "/videos";
                String videoPath = s3Service.uploadAdvertisementMedia(
                    dto.getVideoFile(), 
                    user, 
                    sanitizedTitle,
                    videoDirectory
                );
                dto.setVideoUrl(videoPath);
                log.info("Uploaded video to S3: {}", videoPath);
            }
        } catch (Exception e) {
            log.error("Error processing file uploads", e);
            throw new RuntimeException("Failed to process file uploads: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get user role as a string for folder structure
     * @param user User entity
     * @return Role as lowercase string
     */
    private String getUserRole(User user) {
        if (user.getRoles().contains(Role.ADMIN)) return "admin";
        if (user.getRoles().contains(Role.ADVISOR)) return "advisor";
        if (user.getRoles().contains(Role.DEVELOPER)) return "developer";
        if (user.getRoles().contains(Role.SELLER)) return "seller";
        if (user.getRoles().contains(Role.FRANCHISEE)) return "franchisee";
        return "user";
    }
    
    /**
     * Sanitize a string for use in a path
     * @param input String to sanitize
     * @return Sanitized string
     */
    private String sanitizeForPath(String input) {
        if (input == null) return "unnamed";
        return input.toLowerCase()
                .replaceAll("[^a-z0-9]", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    /**
     * Save an uploaded file to the server (legacy method, not used anymore)
     * @param file The file to save
     * @param directory The directory to save to
     * @return The URL path to the saved file
     * @throws IOException If there is an error saving the file
     */
    @Deprecated
    private String saveFile(MultipartFile file, String directory) throws IOException {
        // Create a unique filename
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        
        // Create the directory if it doesn't exist
        Path uploadPath = Paths.get("uploads", directory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Save the file
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);
        
        // Return the URL path to access the file
        return "/uploads/" + directory + "/" + fileName;
    }
} 