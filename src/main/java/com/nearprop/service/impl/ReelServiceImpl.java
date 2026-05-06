//package com.nearprop.service.impl;
//
//import com.nearprop.service.SubscriptionService;
//import com.nearprop.config.AwsConfig;
//import com.nearprop.dto.ReelDto;
//import com.nearprop.dto.ReelInteractionDto;
//import com.nearprop.dto.UserDto;
//import com.nearprop.dto.payment.InitiatePaymentRequest;
//import com.nearprop.dto.payment.PaymentResponse;
//import com.nearprop.entity.PaymentTransaction;
//import com.nearprop.entity.Property;
//import com.nearprop.entity.Reel;
//import com.nearprop.entity.ReelInteraction;
//import com.nearprop.entity.Role;
//import com.nearprop.entity.User;
//import com.nearprop.entity.SubscriptionPlanFeature;
//import com.nearprop.exception.BadRequestException;
//import com.nearprop.exception.EntityNotFoundException;
//import com.nearprop.exception.ForbiddenException;
//import com.nearprop.repository.PaymentTransactionRepository;
//import com.nearprop.repository.PropertyRepository;
//import com.nearprop.repository.ReelInteractionRepository;
//import com.nearprop.repository.ReelRepository;
//import com.nearprop.repository.SubscriptionPlanFeatureRepository;
//import com.nearprop.repository.UserRepository;
//import com.nearprop.service.PaymentService;
//import com.nearprop.service.ReelService;
//import com.nearprop.service.S3Service;
//import com.nearprop.service.UserService;
//import com.nearprop.service.VideoProcessingService;
//import com.nearprop.service.UserFollowingService;
//import com.nearprop.exception.ResourceNotFoundException;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.CachePut;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//import com.nearprop.entity.Subscription;
//import com.nearprop.entity.SubscriptionPlan;
//import java.io.IOException;
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class ReelServiceImpl implements ReelService
//{
//    private final SubscriptionService subscriptionService;
//    private final ReelRepository reelRepository;
//    private final ReelInteractionRepository reelInteractionRepository;
//    private final PropertyRepository propertyRepository;
//    private final UserRepository userRepository;
//    private final SubscriptionPlanFeatureRepository subscriptionPlanFeatureRepository;
//    private final VideoProcessingService videoProcessingService;
//    private final S3Service s3Service;
//    private final UserService userService;
//    private final AwsConfig awsConfig;
//    private final UserFollowingService userFollowingService;
//    private final PaymentService paymentService;
//    private final PaymentTransactionRepository paymentTransactionRepository;
//
//    @Value("${reels.price:99.00}")
//    private String reelPrice;
//
//    @Override
//    @Transactional
//    @org.springframework.cache.annotation.CacheEvict(value = {"reels", "properties", "property-search", "user-properties"}, allEntries = true)
//    public ReelDto uploadReel(ReelDto reelDto, MultipartFile videoFile) {
//        // Get current user
//        User currentUser = userService.getCurrentUser();
//
//        // Validate the property exists and belongs to the user
//        Property property = propertyRepository.findById(reelDto.getPropertyId())
//                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + reelDto.getPropertyId()));
//
//        // Strict property ownership validation - ONLY property owner can add reels
//        if (!property.getOwner().getId().equals(currentUser.getId())) {
//            throw new ForbiddenException("Only the property owner can upload reels");
//        }
//
//        // Check if this reel requires payment (any reel after the first one)
//        boolean paymentRequired = isAdditionalReelPaymentRequired(property.getId());
//
//        // For testing, create a default plan
//        SubscriptionPlanFeature plan = new SubscriptionPlanFeature();
//        plan.setPlanName("Test Plan");
//        plan.setPlanType(SubscriptionPlanFeature.PlanType.SELLER);
//        plan.setMaxProperties(10);
//        plan.setMaxReelsPerProperty(5);
//        plan.setMaxTotalReels(20);
//        plan.setMaxReelDurationSeconds(60);
//        plan.setMaxReelFileSizeMb(40);
//        plan.setAllowedVideoFormats("mp4,mov,avi,webm");
//        plan.setIsActive(true);
//        plan.setMonthlyPrice(0.0);
//
//        // Validate video file
//        if (videoFile == null || videoFile.isEmpty()) {
//            throw new BadRequestException("Video file cannot be empty");
//        }
//
//        // Process video and extract metadata
//        Map<String, Object> videoMetadata;
//
//        try {
//            // Validate video constraints from subscription plan
//            boolean isValidVideo = videoProcessingService.validateVideo(
//                    videoFile,
//                    plan.getMaxReelDurationSeconds(),
//                    plan.getMaxReelFileSizeMb(),
//                    plan.getAllowedVideoFormats());
//
//            if (!isValidVideo) {
//                throw new BadRequestException("Video does not meet requirements. Check duration, file size, and format.");
//            }
//
//            videoMetadata = videoProcessingService.processVideo(videoFile);
//        } catch (IOException e) {
//            log.error("Error processing video file: {}", e.getMessage(), e);
//            throw new BadRequestException("Failed to process video file: " + e.getMessage());
//        }
//
//        // Upload video and thumbnail to S3 with the structured directory path
//        String uniquePublicId = UUID.randomUUID().toString();
//        Map<String, String> uploadUrls = s3Service.uploadReel(
//                videoFile,
//                (byte[]) videoMetadata.get("thumbnail"),
//                property.getId() + "-" + reelDto.getTitle().replaceAll("[^a-zA-Z0-9]", "-"),
//                currentUser,
//                property);
//
//        // Create and save Reel entity with location data
//        Reel reel = new Reel();
//        reel.setProperty(property);
//        reel.setOwner(currentUser);
//        reel.setTitle(reelDto.getTitle());
//        reel.setDescription(reelDto.getDescription());
//        reel.setVideoUrl(uploadUrls.get("videoUrl"));
//        reel.setThumbnailUrl(uploadUrls.get("thumbnailUrl"));
//        reel.setDurationSeconds((Integer) videoMetadata.get("durationSeconds"));
//        reel.setFileSize(videoFile.getSize());
//
//        // Set status based on payment requirement
//        if (paymentRequired) {
//            reel.setStatus(Reel.ReelStatus.DRAFT);
//            reel.setPaymentRequired(true);
//        } else {
//            reel.setStatus(Reel.ReelStatus.PUBLISHED);
//            reel.setPaymentRequired(false);
//        }
//
//        reel.setProcessingStatus(Reel.ProcessingStatus.COMPLETED);
//        reel.setPublicId(uniquePublicId);
//        reel.setViewCount(0L);
//        reel.setLikeCount(0L);
//        reel.setCommentCount(0L);
//        reel.setShareCount(0L);
//        reel.setSaveCount(0L);
//        // Copy location data from property
//        reel.setLatitude(property.getLatitude());
//        reel.setLongitude(property.getLongitude());
//        reel.setDistrict(property.getDistrictName());
//        reel.setCity(property.getCity());
//        reel.setState(property.getState());
//
//        Reel savedReel = reelRepository.save(reel);
//
//        return mapToReelDto(savedReel);
//    }
//
//    @Override
//    @Cacheable(value = "reels", key = "#reelId")
//    public ReelDto getReel(Long reelId) {
//        Reel reel = findReelById(reelId);
//        return mapToReelDto(reel, getCurrentUser());
//    }
//
//    @Override
//    @Cacheable(value = "reels", key = "'public_' + #publicId")
//    public ReelDto getReelByPublicId(String publicId) {
//        Reel reel = reelRepository.findByPublicId(publicId)
//                .orElseThrow(() -> new EntityNotFoundException("Reel not found with public ID: " + publicId));
//        return mapToReelDto(reel, getCurrentUser());
//    }
//
//    @Override
//    public Page<ReelDto> getReelsByProperty(Long propertyId, Pageable pageable) {
//        Property property = propertyRepository.findById(propertyId)
//                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + propertyId));
//
//        User currentUser = getCurrentUser();
//        Page<Reel> reels = reelRepository.findByPropertyId(propertyId, pageable);
//
//        if (reels.isEmpty()) {
//            // Create empty page but no error
//            return new PageImpl<>(Collections.emptyList(), pageable, 0);
//        }
//
//        return reels.map(reel -> mapToReelDto(reel, currentUser));
//    }
//
//    @Override
//    public Page<ReelDto> getReelsByUser(Long userId, Pageable pageable) {
//        // Verify user exists
//        userRepository.findById(userId)
//                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
//
//        // If current user is trying to see their reels but doesn't have the right permissions
//        User currentUser = getCurrentUser();
//        if (userId.equals(currentUser.getId()) &&
//                !(currentUser.getRoles().contains("ROLE_SELLER") ||
//                  currentUser.getRoles().contains("ROLE_ADVISOR") ||
//                  currentUser.getRoles().contains("ROLE_DEVELOPER") ||
//                  currentUser.getRoles().contains("ADMIN"))) {
//            throw new ForbiddenException("You don't have permission to upload reels. Upgrade your account to create reels.");
//        }
//
//        Page<Reel> reels = reelRepository.findByOwnerId(userId, pageable);
//
//        if (reels.isEmpty()) {
//            // Create empty page but no error
//            return new PageImpl<>(Collections.emptyList(), pageable, 0);
//        }
//
//        return reels.map(reel -> mapToReelDto(reel, currentUser));
//    }
//
//    @Override
//    @Transactional
//    public ReelDto updateReel(Long reelId, ReelDto reelDto) {
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//
//        // Verify user owns the reel
//        if (!reel.getOwner().getId().equals(currentUser.getId())) {
//            throw new ForbiddenException("You don't have permission to update this reel");
//        }
//
//        // Update fields
//        reel.setTitle(reelDto.getTitle());
//        reel.setDescription(reelDto.getDescription());
//
//        Reel updatedReel = reelRepository.save(reel);
//        return mapToReelDto(updatedReel);
//    }
//
//    @Override
//    @Transactional
//    @org.springframework.cache.annotation.CacheEvict(value = {"reels", "properties", "property-search", "user-properties"}, allEntries = true)
//    public void deleteReel(Long reelId) {
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//
//        // Verify user owns the reel or is admin
//       /* if (!reel.getOwner().getId().equals(currentUser.getId()) &&
//                !currentUser.getRoles().contains("ADMIN")) {
//            throw new ForbiddenException("You don't have permission to delete this reel");
//        }*/
//
//	boolean isAdmin = currentUser.getRoles().stream()
//                .anyMatch(r -> r.name().equalsIgnoreCase("ADMIN"));
//
//        if (!reel.getOwner().getId().equals(currentUser.getId()) && !isAdmin) {
//            throw new ForbiddenException("You don't have permission to delete this reel");
//        }
//
//
//        try {
//            // Delete all interactions first to avoid constraint violations
//            List<ReelInteraction> interactions = reelInteractionRepository.findByReelIdAndType(
//                    reelId, ReelInteraction.InteractionType.LIKE);
//            reelInteractionRepository.deleteAll(interactions);
//
//            interactions = reelInteractionRepository.findByReelIdAndType(
//                    reelId, ReelInteraction.InteractionType.COMMENT);
//            reelInteractionRepository.deleteAll(interactions);
//
//            interactions = reelInteractionRepository.findByReelIdAndType(
//                    reelId, ReelInteraction.InteractionType.FOLLOW);
//            reelInteractionRepository.deleteAll(interactions);
//
//            interactions = reelInteractionRepository.findByReelIdAndType(
//                    reelId, ReelInteraction.InteractionType.VIEW);
//            reelInteractionRepository.deleteAll(interactions);
//
//            interactions = reelInteractionRepository.findByReelIdAndType(
//                    reelId, ReelInteraction.InteractionType.SAVE);
//            reelInteractionRepository.deleteAll(interactions);
//
//            interactions = reelInteractionRepository.findByReelIdAndType(
//                    reelId, ReelInteraction.InteractionType.SHARE);
//            reelInteractionRepository.deleteAll(interactions);
//
//            // Get S3 URLs before deleting the reel
//            String videoUrl = reel.getVideoUrl();
//            String thumbnailUrl = reel.getThumbnailUrl();
//
//            // Delete the reel from database
//            reelRepository.delete(reel);
//
//            // Now delete from S3 - after successful DB deletion
//            if (videoUrl != null && thumbnailUrl != null) {
//                try {
//                    s3Service.deleteReel(videoUrl, thumbnailUrl);
//                    log.info("Successfully deleted reel files from S3: {}", reelId);
//                } catch (Exception e) {
//                    log.error("Failed to delete S3 files for reel {}: {}", reelId, e.getMessage());
//                    // Continue execution - we at least deleted from database
//                }
//            }
//        } catch (Exception e) {
//            log.error("Error deleting reel {}: {}", reelId, e.getMessage(), e);
//            throw new RuntimeException("Failed to delete reel: " + e.getMessage(), e);
//        }
//    }
//
//    @Override
//    @Transactional
//    public ReelDto likeReel(Long reelId) {
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//
//        // Check if user already liked the reel
//        boolean alreadyLiked = reelInteractionRepository.existsByReelIdAndUserIdAndType(
//                reelId, currentUser.getId(), ReelInteraction.InteractionType.LIKE);
//
//        if (alreadyLiked) {
//            throw new BadRequestException("You have already liked this reel");
//        }
//
//        // Create like interaction
//        ReelInteraction like = ReelInteraction.builder()
//                .reel(reel)
//                .user(currentUser)
//                .type(ReelInteraction.InteractionType.LIKE)
//                .build();
//
//        reelInteractionRepository.save(like);
//
//        // Update like count
//        reel.setLikeCount(reel.getLikeCount() + 1);
//        Reel updatedReel = reelRepository.save(reel);
//
//        return mapToReelDto(updatedReel, currentUser);
//    }
//
//    @Override
//    @Transactional
//    public ReelDto unlikeReel(Long reelId) {
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//
//        // Find and delete like interaction
//        boolean deleted = reelInteractionRepository.existsByReelIdAndUserIdAndType(
//                reelId, currentUser.getId(), ReelInteraction.InteractionType.LIKE);
//
//        if (!deleted) {
//            throw new BadRequestException("You have not liked this reel");
//        }
//
//        reelInteractionRepository.deleteByReelIdAndUserIdAndType(
//                reelId, currentUser.getId(), ReelInteraction.InteractionType.LIKE);
//
//        // Update like count
//        if (reel.getLikeCount() > 0) {
//            reel.setLikeCount(reel.getLikeCount() - 1);
//        }
//
//        Reel updatedReel = reelRepository.save(reel);
//        return mapToReelDto(updatedReel, currentUser);
//    }
//
//    @Override
//    @Transactional
//    public ReelDto commentReel(Long reelId, String comment) {
//        if (comment == null || comment.trim().isEmpty()) {
//            throw new BadRequestException("Comment cannot be empty");
//        }
//
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//
//        try {
//            // Create comment interaction with the original comment text
//            ReelInteraction commentInteraction = ReelInteraction.builder()
//                    .reel(reel)
//                    .user(currentUser)
//                    .type(ReelInteraction.InteractionType.COMMENT)
//                    .comment(comment)
//                    .build();
//
//            reelInteractionRepository.save(commentInteraction);
//
//            // Update comment count
//            reel.setCommentCount(reel.getCommentCount() + 1);
//            Reel updatedReel = reelRepository.save(reel);
//
//            return mapToReelDto(updatedReel, currentUser);
//        } catch (Exception e) {
//            log.error("Failed to add comment: {}", e.getMessage(), e);
//            throw e;
//        }
//    }
//
//    @Override
//    @Transactional
//    public ReelDto followReel(Long reelId) {
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//        User reelOwner = reel.getOwner();
//
//        // Prevent following your own reel
//        if (currentUser.getId().equals(reelOwner.getId())) {
//            throw new BadRequestException("You cannot follow your own reel");
//        }
//
//        // Use UserFollowingService to follow the reel owner
//        try {
//            userFollowingService.followUser(reelOwner.getId());
//        } catch (BadRequestException e) {
//            // User is already following this user, just ignore
//        }
//
//        return mapToReelDto(reel, currentUser);
//    }
//
//    @Override
//    @Transactional
//    public ReelDto unfollowReel(Long reelId) {
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//        User reelOwner = reel.getOwner();
//
//        // Prevent unfollowing your own reel
//        if (currentUser.getId().equals(reelOwner.getId())) {
//            throw new BadRequestException("You cannot unfollow your own reel");
//        }
//
//        // Use UserFollowingService to unfollow the reel owner
//        try {
//            userFollowingService.unfollowUser(reelOwner.getId());
//        } catch (BadRequestException e) {
//            // User is not following this user, just ignore
//        }
//
//        return mapToReelDto(reel, currentUser);
//    }
//
//    @Override
//    @Transactional
//    public ReelDto viewReel(Long reelId) {
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//
//        // Create view interaction if not already viewed
//        if (!reelInteractionRepository.existsByReelIdAndUserIdAndType(
//                reelId, currentUser.getId(), ReelInteraction.InteractionType.VIEW)) {
//
//            ReelInteraction view = ReelInteraction.builder()
//                    .reel(reel)
//                    .user(currentUser)
//                    .type(ReelInteraction.InteractionType.VIEW)
//                    .build();
//
//            reelInteractionRepository.save(view);
//        }
//
//        // Update view count
//        reel.setViewCount(reel.getViewCount() + 1);
//        Reel updatedReel = reelRepository.save(reel);
//
//        return mapToReelDto(updatedReel, currentUser);
//    }
//
//    @Override
//    @Transactional
//    public ReelDto shareReel(Long reelId) {
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//
//        // Create share interaction
//        ReelInteraction share = ReelInteraction.builder()
//                .reel(reel)
//                .user(currentUser)
//                .type(ReelInteraction.InteractionType.SHARE)
//                .build();
//
//        reelInteractionRepository.save(share);
//
//        // Update share count
//        reel.setShareCount(reel.getShareCount() + 1);
//        Reel updatedReel = reelRepository.save(reel);
//
//        return mapToReelDto(updatedReel, currentUser);
//    }
//
//    @Override
//    public boolean canUploadMoreReels(Long propertyId) {
//        // Check only if there's a property with that ID, not about payment
//        propertyRepository.findById(propertyId)
//            .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + propertyId));
//
//        // Anyone can upload reels now, they'll just have to pay for additional ones
//        return true;
//    }
//
//    @Override
//    public Page<ReelDto> getNearbyReels(Double latitude, Double longitude, Double radius, Pageable pageable) {
//        User currentUser = getCurrentUser();
//
//        if (latitude == null || longitude == null) {
//            // If no location provided, return reels sorted by newest first
//            Page<Reel> reels = reelRepository.findByStatus(Reel.ReelStatus.PUBLISHED, pageable);
//            return reels.map(reel -> mapToReelDto(reel, currentUser));
//        }
//
//        // Use geospatial query to find nearby reels within radius
//        List<Reel> nearbyReels = reelRepository.findNearbyReels(latitude, longitude, radius);
//
//        // Calculate total elements for pagination
//        long totalElements = nearbyReels.size();
//
//        // Apply pagination manually
//        int start = (int) pageable.getOffset();
//        int end = Math.min((start + pageable.getPageSize()), nearbyReels.size());
//
//        // Handle case where start is beyond the list size
//        if (start >= totalElements) {
//            return Page.empty(pageable);
//        }
//
//        List<Reel> paginatedReels = nearbyReels.subList(start, end);
//
//        // Convert to DTOs with distance information
//        List<ReelDto> reelDtos = paginatedReels.stream()
//                .map(reel -> {
//                    ReelDto dto = mapToReelDto(reel, currentUser);
//
//                    // Calculate distance if coordinates are available
//                    if (reel.getLatitude() != null && reel.getLongitude() != null) {
//                        double distance = calculateDistance(
//                                latitude, longitude,
//                                reel.getLatitude(), reel.getLongitude());
//                        dto.setDistanceKm(distance);
//                    }
//
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//        return new org.springframework.data.domain.PageImpl<>(reelDtos, pageable, totalElements);
//    }
//
//    @Override
//    public Page<ReelDto> getReelsByCity(String city, Pageable pageable) {
//        User currentUser = getCurrentUser();
//        Page<Reel> reels = reelRepository.findByCity(city, pageable);
//
//        if (reels.isEmpty()) {
//            // Create empty page but no error
//            return new PageImpl<>(Collections.emptyList(), pageable, 0);
//        }
//
//        return reels.map(reel -> mapToReelDto(reel, currentUser));
//    }
//
//    @Override
//    public Page<ReelDto> getReelsByDistrict(String district, Pageable pageable) {
//        User currentUser = getCurrentUser();
//        Page<Reel> reels = reelRepository.findByDistrict(district, pageable);
//
//        if (reels.isEmpty()) {
//            // Create empty page but no error
//            return new PageImpl<>(Collections.emptyList(), pageable, 0);
//        }
//
//        return reels.map(reel -> mapToReelDto(reel, currentUser));
//    }
//
//    @Override
//    public String generateShareableLink(Long reelId) {
//        Reel reel = findReelById(reelId);
//
//        // Create shareable link using the public ID
//        String baseUrl = "https://nearprop.com/reels/";
//        return baseUrl + reel.getPublicId();
//    }
//
//    @Override
//    @Transactional
//    public ReelDto saveReel(Long reelId) {
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//
//        // Check if user already saved the reel
//        boolean alreadySaved = reelInteractionRepository.existsByReelIdAndUserIdAndType(
//                reelId, currentUser.getId(), ReelInteraction.InteractionType.SAVE);
//
//        if (alreadySaved) {
//            throw new BadRequestException("You have already saved this reel");
//        }
//
//        // Create save interaction
//        ReelInteraction save = ReelInteraction.builder()
//                .reel(reel)
//                .user(currentUser)
//                .type(ReelInteraction.InteractionType.SAVE)
//                .build();
//
//        reelInteractionRepository.save(save);
//
//        // Update save count
//        reel.setSaveCount(reel.getSaveCount() + 1);
//        Reel updatedReel = reelRepository.save(reel);
//
//        return mapToReelDto(updatedReel, currentUser);
//    }
//
//    @Override
//    @Transactional
//    public ReelDto unsaveReel(Long reelId) {
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//
//        // Find and delete save interaction
//        boolean saved = reelInteractionRepository.existsByReelIdAndUserIdAndType(
//                reelId, currentUser.getId(), ReelInteraction.InteractionType.SAVE);
//
//        if (!saved) {
//            throw new BadRequestException("You have not saved this reel");
//        }
//
//        reelInteractionRepository.deleteByReelIdAndUserIdAndType(
//                reelId, currentUser.getId(), ReelInteraction.InteractionType.SAVE);
//
//        // Update save count
//        if (reel.getSaveCount() > 0) {
//            reel.setSaveCount(reel.getSaveCount() - 1);
//        }
//
//        Reel updatedReel = reelRepository.save(reel);
//        return mapToReelDto(updatedReel, currentUser);
//    }
//
//    @Override
//    public Page<ReelDto> getSavedReelsByUser(Long userId, Pageable pageable) {
//        User currentUser = getCurrentUser();
//
//        // If requesting other user's saved reels, check if current user is admin
//        if (!userId.equals(currentUser.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
//            throw new ForbiddenException("You do not have permission to view this user's saved reels");
//        }
//
//        Page<ReelInteraction> savedInteractions = reelInteractionRepository.findByUserIdAndType(
//                userId, ReelInteraction.InteractionType.SAVE, pageable);
//
//        return savedInteractions.map(interaction -> mapToReelDto(interaction.getReel(), currentUser));
//    }
//
//    @Override
//    public Page<ReelInteractionDto> getReelComments(Long reelId, Pageable pageable) {
//        // Verify reel exists
//        findReelById(reelId);
//
//        // Get comments
//        Page<ReelInteraction> comments = reelInteractionRepository.findByReelIdAndType(
//                reelId, ReelInteraction.InteractionType.COMMENT, pageable);
//
//        return comments.map(this::mapToReelInteractionDto);
//    }
//
//    @Override
//    public Map<String, Object> checkUploadLimit(Long propertyId) {
//        // Get property
//        Property property = propertyRepository.findById(propertyId)
//                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + propertyId));
//
//        // Verify ownership
//        User currentUser = getCurrentUser();
//        if (!property.getOwner().getId().equals(currentUser.getId()) &&
//                !currentUser.getRoles().contains("ADMIN")) {
//            throw new ForbiddenException("You don't have permission to check upload limits for this property");
//        }
//
//        // Get current count
//        List<Reel> existingReels = reelRepository.findPublishedReelsByPropertyId(propertyId);
//        int uploadedCount = existingReels.size();
//        boolean paymentRequired = isAdditionalReelPaymentRequired(propertyId);
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("canUpload", true); // Anyone can upload, but might need to pay
//        result.put("uploadedCount", uploadedCount);
//        result.put("paymentRequired", paymentRequired);
//
//        if (paymentRequired) {
//            result.put("reelPrice", reelPrice);
//            result.put("currency", "INR");
//        }
//
//        // Add reel information
//        List<Map<String, Object>> reelsInfo = existingReels.stream()
//                .map(reel -> {
//                    Map<String, Object> reelInfo = new HashMap<>();
//                    reelInfo.put("id", reel.getId());
//                    reelInfo.put("title", reel.getTitle());
//                    reelInfo.put("videoUrl", reel.getVideoUrl());
//                    reelInfo.put("thumbnailUrl", reel.getThumbnailUrl());
//                    reelInfo.put("createdAt", reel.getCreatedAt());
//                    return reelInfo;
//                })
//                .collect(Collectors.toList());
//
//        result.put("reels", reelsInfo);
//
//        return result;
//    }
//
//    @Override
//    public Page<ReelDto> getReelsByLocation(Double latitude, Double longitude, Double radiusKm, Pageable pageable) {
//        User currentUser = getCurrentUser();
//
//        // Find reels within radius using repository query
//        Page<Object[]> reelsWithDistance = reelRepository.findReelsWithinRadius(
//                latitude, longitude, radiusKm, pageable);
//
//        // Convert to ReelDto objects with distance information
//        List<ReelDto> reelDtos = reelsWithDistance.getContent().stream()
//                .map(result -> {
//                    Reel reel = (Reel) result[0];
//                    Double distance = (Double) result[1];
//
//                    ReelDto dto = mapToReelDto(reel, currentUser);
//                    dto.setDistanceKm(distance);
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//        // Return as page
//        return new PageImpl<>(reelDtos, pageable, reelsWithDistance.getTotalElements());
//    }
//
//    @Override
//    public Page<ReelDto> getReelsFeed(Pageable pageable) {
//        User currentUser = getCurrentUser();
//
//        // Get popular reels (most viewed)
//        Sort sort = Sort.by(Sort.Direction.DESC, "viewCount");
//        PageRequest pageRequest = PageRequest.of(
//            pageable.getPageNumber(),
//            pageable.getPageSize(),
//            sort
//        );
//
//        Page<Reel> popularReels = reelRepository.findAll(pageRequest);
//
//        return popularReels.map(reel -> mapToReelDto(reel, currentUser));
//    }
//
//    @Override
//    public Page<ReelDto> getReelsByLocationWithFallback(Double latitude, Double longitude, double radius, Pageable pageable) {
//        User currentUser = getCurrentUser();
//
//        // First try to get nearby reels
//        Page<Object[]> reelsWithDistance = reelRepository.findReelsWithinRadius(
//                latitude, longitude, radius, pageable);
//
//        List<ReelDto> reelDtos = new ArrayList<>();
//
//        // Convert nearby reels to DTOs
//        reelsWithDistance.getContent().forEach(result -> {
//            Reel reel = (Reel) result[0];
//            Double distance = (Double) result[1];
//
//            ReelDto dto = mapToReelDto(reel, currentUser);
//            dto.setDistanceKm(distance);
//            reelDtos.add(dto);
//        });
//
//        // If we don't have enough nearby reels to fill the page, get more from general feed
//        if (reelDtos.size() < pageable.getPageSize()) {
//            int remainingSize = pageable.getPageSize() - reelDtos.size();
//
//            // Create new pageable to get the remaining reels
//            Pageable remainingPageable = PageRequest.of(0, remainingSize, pageable.getSort());
//
//            // Get popular reels, exclude ones we already have
//            List<Long> existingReelIds = reelDtos.stream()
//                    .map(ReelDto::getId)
//                    .collect(Collectors.toList());
//
//            List<Reel> popularReels;
//            if (existingReelIds.isEmpty()) {
//                // If no existing reels, just get the first page
//                popularReels = reelRepository.findAll(remainingPageable).getContent();
//            } else {
//                // Otherwise, exclude reels we already have
//                popularReels = reelRepository.findByIdNotIn(existingReelIds, remainingPageable);
//            }
//
//            // Add to our result set
//            popularReels.forEach(reel -> {
//                ReelDto dto = mapToReelDto(reel, currentUser);
//                dto.setDistanceKm(null); // No distance for non-nearby reels
//                reelDtos.add(dto);
//            });
//        }
//
//        // Return as page
//        return new PageImpl<>(reelDtos, pageable,
//                reelsWithDistance.getTotalElements() +
//                (reelDtos.size() > reelsWithDistance.getContent().size() ?
//                 Long.MAX_VALUE - reelsWithDistance.getTotalElements() : 0));
//    }
//
//    /*@Override
//    public boolean isAdditionalReelPaymentRequired(Long propertyId) {
//        // Check if this property already has at least one published reel
//        Long publishedReelsCount = reelRepository.countPublishedReelsByPropertyId(propertyId);
//        return publishedReelsCount >= 1; // If there's already 1 or more reels, payment is required
//    }*/
//
//    @Override
//	public boolean isAdditionalReelPaymentRequired(Long propertyId) {
//	    // Count reels for this property
//	    Long publishedReelsCount = reelRepository.countPublishedReelsByPropertyId(propertyId);
//
//	    // Get current user
//	    User currentUser = getCurrentUser();
//
//	    // Get active subscription of user
//	    Optional<Subscription> activeSubOpt = subscriptionService.getSubscriptionWithAvailablePropertySlots(
//	            currentUser.getId(), SubscriptionPlan.PlanType.PROPERTY
//	    );
//
//	    if (activeSubOpt.isEmpty()) {
//	        // No subscription => default: only 1 reel free
//	        return publishedReelsCount >= 1;
//	    }
//
//	    Subscription subscription = activeSubOpt.get();
//	    SubscriptionPlan plan = subscription.getPlan();
//
//	    // Unlimited reels allowed
//	    if (plan.hasUnlimitedReels()) {
//	        return false;
//	    }
//
//	    // Property-level limit (e.g. max 15 reels per property)
//	    if (plan.getMaxReelsPerProperty() != null && plan.getMaxReelsPerProperty() > 0) {
//	        if (publishedReelsCount >= plan.getMaxReelsPerProperty()) {
//	            return true; // exceeded property limit
//	        }
//	    }
//
//	    // Global limit (across all properties)
//	    if (plan.getMaxTotalReels() != null && plan.getMaxTotalReels() > 0) {
//	        long totalReels = reelRepository.countReelsByUserId(currentUser.getId());
//	        if (totalReels >= plan.getMaxTotalReels()) {
//	            return true; // exceeded global limit
//	        }
//	    }
//
//	    return false; // Still within limits
//	}
//
//    @Override
//    @Transactional
//    public PaymentResponse initiateReelPayment(Long reelId) {
//        // Get reel
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//
//        // Verify reel ownership
//        if (!reel.getOwner().getId().equals(currentUser.getId())) {
//            throw new ForbiddenException("You don't have permission to pay for this reel");
//        }
//
//        // Verify reel requires payment
//        if (!reel.getPaymentRequired()) {
//            throw new BadRequestException("This reel does not require payment");
//        }
//
//        // Verify reel is in DRAFT status
//        if (reel.getStatus() != Reel.ReelStatus.DRAFT) {
//            throw new BadRequestException("This reel cannot be published at this time");
//        }
//
//        // Create payment request
//        InitiatePaymentRequest paymentRequest = InitiatePaymentRequest.builder()
//                .amount(new BigDecimal(reelPrice))
//                .currency("INR")
//                .paymentType(PaymentTransaction.PaymentType.REEL_PURCHASE)
//                .customerName(currentUser.getName())
//                .customerEmail(currentUser.getEmail())
//                .customerPhone(currentUser.getMobileNumber())
//                .build();
//
//        // Initiate payment
//        PaymentResponse paymentResponse = paymentService.initiatePayment(paymentRequest);
//
//        return paymentResponse;
//    }
//
//    @Override
//    @Transactional
//    public ReelDto publishReelAfterPayment(Long reelId, String paymentReferenceId) {
//        // Get reel
//        Reel reel = findReelById(reelId);
//        User currentUser = getCurrentUser();
//
//        // Verify reel ownership
//        if (!reel.getOwner().getId().equals(currentUser.getId())) {
//            throw new ForbiddenException("You don't have permission to publish this reel");
//        }
//
//        // Verify payment exists and is completed
//        PaymentTransaction payment = paymentTransactionRepository.findByReferenceId(paymentReferenceId)
//                .orElseThrow(() -> new BadRequestException("Payment not found"));
//
//        if (!payment.isSuccessful()) {
//            throw new BadRequestException("Payment has not been completed");
//        }
//
//        if (payment.getPaymentType() != PaymentTransaction.PaymentType.REEL_PURCHASE) {
//            throw new BadRequestException("Invalid payment type for reel publishing");
//        }
//
//        // Update reel
//        reel.setStatus(Reel.ReelStatus.PUBLISHED);
//        reel.setPaymentTransactionId(paymentReferenceId);
//
//        Reel updatedReel = reelRepository.save(reel);
//
//        return mapToReelDto(updatedReel);
//    }
//
//    @Override
//    public List<ReelDto> getAllNearbyReelsWithFullDetails(Double latitude, Double longitude) {
//        User currentUser = getCurrentUser();
//        // Fetch all published reels with location
//        List<Reel> reels = reelRepository.findByStatus(Reel.ReelStatus.PUBLISHED);
//        // Calculate distance for each reel and sort
//        List<ReelDto> reelDtos = reels.stream()
//            .filter(reel -> reel.getLatitude() != null && reel.getLongitude() != null)
//            .map(reel -> {
//                ReelDto dto = mapToReelDto(reel, currentUser);
//                double distance = calculateDistance(latitude, longitude, reel.getLatitude(), reel.getLongitude());
//                dto.setDistanceKm(distance);
//                // Add all users who liked this reel
//                List<ReelInteraction> likes = reelInteractionRepository.findByReelIdAndType(reel.getId(), ReelInteraction.InteractionType.LIKE);
//                dto.setLikedBy(likes.stream().map(like -> like.getUser().getId()).collect(Collectors.toList()));
//                // Add all comments (already included by mapToReelDto)
//                // Add view and share count (already included)
//                return dto;
//            })
//            .sorted(Comparator.comparingDouble(ReelDto::getDistanceKm))
//            .collect(Collectors.toList());
//        return reelDtos;
//    }
//
//    @Override
//    @Transactional
//    @org.springframework.cache.annotation.CacheEvict(value = {"reels", "properties", "property-search", "user-properties"}, allEntries = true)
//    public void deleteAllReelsOfProperty(Long propertyId, com.nearprop.entity.User currentUser) {
//        Property property = propertyRepository.findById(propertyId)
//                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + propertyId));
//        // Only admin or property owner can delete all reels
//        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.name().equals("ADMIN"));
//        boolean isOwner = property.getOwner().getId().equals(currentUser.getId());
//        if (!isAdmin && !isOwner) {
//            throw new ForbiddenException("You don't have permission to delete all reels for this property");
//        }
//        List<Reel> reels = reelRepository.findByPropertyId(propertyId, org.springframework.data.domain.Pageable.unpaged()).getContent();
//        for (Reel reel : reels) {
//            try {
//                deleteReel(reel.getId());
//            } catch (Exception e) {
//                log.warn("Failed to delete reel {}: {}", reel.getId(), e.getMessage());
//            }
//        }
//    }
//
//    @Override
//    @Transactional
//    @org.springframework.cache.annotation.CacheEvict(value = {"reels", "properties", "property-search", "user-properties"}, allEntries = true)
//    public void deleteAllReelsAndInteractions() {
//        List<Reel> reels = reelRepository.findAll();
//        for (Reel reel : reels) {
//            try {
//                // Delete all interactions for this reel
//                List<ReelInteraction> interactions = reelInteractionRepository.findByReelId(reel.getId());
//                reelInteractionRepository.deleteAll(interactions);
//                // Delete from S3
//                String videoUrl = reel.getVideoUrl();
//                String thumbnailUrl = reel.getThumbnailUrl();
//                reelRepository.delete(reel);
//                if (videoUrl != null && thumbnailUrl != null) {
//                    try {
//                        s3Service.deleteReel(videoUrl, thumbnailUrl);
//                        log.info("Deleted S3 files for reel {}", reel.getId());
//                    } catch (Exception e) {
//                        log.warn("Failed to delete S3 files for reel {}: {}", reel.getId(), e.getMessage());
//                    }
//                }
//            } catch (Exception e) {
//                log.warn("Failed to delete reel {}: {}", reel.getId(), e.getMessage());
//            }
//        }
//    }
//
//    // Helper methods
//
//    private Reel findReelById(Long reelId) {
//        return reelRepository.findById(reelId)
//                .orElseThrow(() -> new EntityNotFoundException("Reel not found with ID: " + reelId));
//    }
//
//    private User getCurrentUser() {
//        return userService.getCurrentUser();
//    }
//
//    private Optional<SubscriptionPlanFeature> getUserSubscriptionPlan(User user) {
//        // In a real implementation, this would look up the user's active subscription
//        // For now, determine plan based on user roles
//
//        if (user.getRoles().contains("ROLE_DEVELOPER")) {
//            // If user is a developer, get the developer plan
//            return subscriptionPlanFeatureRepository.findByPlanName("Basic Developer");
//        } else if (user.getRoles().contains("ROLE_ADVISOR")) {
//            // If user is an advisor, get the advisor plan
//            return subscriptionPlanFeatureRepository.findByPlanName("Basic Advisor");
//        } else if (user.getRoles().contains("ROLE_SELLER")) {
//            // If user is a seller, get the seller plan
//            return subscriptionPlanFeatureRepository.findByPlanName("Basic Seller");
//        } else {
//            // Default plan for regular users
//            return subscriptionPlanFeatureRepository.findByPlanName("Free User");
//        }
//    }
//
//    private ReelDto mapToReelDto(Reel reel) {
//        return mapToReelDto(reel, null);
//    }
//
//    private ReelDto mapToReelDto(Reel reel, User currentUser) {
//        boolean liked = false;
//        boolean followed = false;
//        boolean saved = false;
//
//        if (currentUser != null) {
//            // Check if current user liked and saved the reel
//            liked = reelInteractionRepository.existsByReelIdAndUserIdAndType(
//                    reel.getId(), currentUser.getId(), ReelInteraction.InteractionType.LIKE);
//            saved = reelInteractionRepository.existsByReelIdAndUserIdAndType(
//                    reel.getId(), currentUser.getId(), ReelInteraction.InteractionType.SAVE);
//
//            // Check if current user follows the reel owner (not the reel itself)
//            if (!currentUser.getId().equals(reel.getOwner().getId())) {  // Don't check for yourself
//                try {
//                    followed = userFollowingService.isFollowing(reel.getOwner().getId());
//                } catch (Exception e) {
//                    log.warn("Error checking if user follows reel owner: {}", e.getMessage());
//                    followed = false;
//                }
//            }
//        }
//
//        // Map owner to UserDto with all available fields
//        User owner = reel.getOwner();
//        UserDto ownerDto = UserDto.builder()
//                .id(owner.getId())
//                .name(owner.getName())
//                .email(owner.getEmail())
//                .mobileNumber(owner.getMobileNumber())
//                .username(owner.getUsername())
//                .phoneNumber(owner.getMobileNumber())
//                .profileImageUrl(owner.getProfileImageUrl())
//                .roles(owner.getRoles())
//                .createdAt(owner.getCreatedAt())
//                .updatedAt(owner.getUpdatedAt())
//                .isFollowing(followed)
//                .build();
//
//        // Include property summary information
//        Property property = reel.getProperty();
//
//        // Get comments for this reel
//        List<ReelInteraction> comments = reelInteractionRepository.findByReelIdAndType(
//                reel.getId(), ReelInteraction.InteractionType.COMMENT);
//
//        List<ReelInteractionDto> commentDtos = comments.stream()
//                .map(this::mapToReelInteractionDto)
//                .collect(Collectors.toList());
//
//        return ReelDto.builder()
//                .id(reel.getId())
//                .propertyId(reel.getProperty().getId())
//                // Set property details directly instead of through a separate DTO
//                .property(null) // We'll include essential property data in the feed API endpoint
//                .owner(ownerDto)
//                .title(reel.getTitle())
//                .description(reel.getDescription())
//                .videoUrl(reel.getVideoUrl())
//                .thumbnailUrl(reel.getThumbnailUrl())
//                .durationSeconds(reel.getDurationSeconds())
//                .status(reel.getStatus())
//                .processingStatus(reel.getProcessingStatus())
//                .publicId(reel.getPublicId())
//                .viewCount(reel.getViewCount())
//                .likeCount(reel.getLikeCount())
//                .commentCount(reel.getCommentCount())
//                .shareCount(reel.getShareCount())
//                .saveCount(reel.getSaveCount())
//                // Location data
//                .latitude(reel.getLatitude())
//                .longitude(reel.getLongitude())
//                .district(reel.getDistrict())
//                .city(reel.getCity())
//                .state(reel.getState())
//                .createdAt(reel.getCreatedAt())
//                .updatedAt(reel.getUpdatedAt())
//                .liked(liked)
//                .followed(followed)
//                .saved(saved)
//                .comments(commentDtos)
//                .build();
//    }
//
//    /**
//     * Calculate distance between two points using Haversine formula
//     *
//     * @param lat1 Latitude of point 1
//     * @param lon1 Longitude of point 1
//     * @param lat2 Latitude of point 2
//     * @param lon2 Longitude of point 2
//     * @return Distance in kilometers
//     */
//    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
//        // Radius of the Earth in km
//        final double R = 6371.0;
//
//        // Convert degrees to radians
//        double latDistance = Math.toRadians(lat2 - lat1);
//        double lonDistance = Math.toRadians(lon2 - lon1);
//
//        // Haversine formula
//        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
//
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//
//        return R * c;
//    }
//
//    /**
//     * Map ReelInteraction to ReelInteractionDto
//     *
//     * @param interaction The ReelInteraction to map
//     * @return A ReelInteractionDto
//     */
//    private ReelInteractionDto mapToReelInteractionDto(ReelInteraction interaction) {
//        // Create user DTO with all available fields
//        User user = interaction.getUser();
//        UserDto userDto = UserDto.builder()
//                .id(user.getId())
//                .name(user.getName())
//                .email(user.getEmail())
//                .mobileNumber(user.getMobileNumber())
//                .username(user.getUsername())
//                .phoneNumber(user.getMobileNumber())
//                .profileImageUrl(user.getProfileImageUrl())
//                .roles(user.getRoles())
//                .createdAt(user.getCreatedAt())
//                .updatedAt(user.getUpdatedAt())
//                .build();
//
//        // Build and return DTO
//        return ReelInteractionDto.builder()
//                .id(interaction.getId())
//                .reelId(interaction.getReel().getId())
//                .user(userDto)
//                .type(interaction.getType())
//                .comment(interaction.getComment())
//                .createdAt(interaction.getCreatedAt())
//                .build();
//    }
//
//    /**
//     * Get subscription plan feature
//     *
//     * @param user The user
//     * @param featureCode The feature code
//     * @return The subscription plan feature
//     */
//    private SubscriptionPlanFeature getSubscriptionPlanFeature(User user, String featureCode) {
//        // For testing, use default limits
//        SubscriptionPlanFeature feature = new SubscriptionPlanFeature();
//        if ("REELS_PER_PROPERTY".equals(featureCode)) {
//            feature.setValue("5");  // Default: 5 reels per property
//        }
//        return feature;
//    }
//
//    public void deleteComment(Long reelId, Long commentId, User currentUser) {
//        ReelInteraction comment = reelInteractionRepository.findById(commentId)
//                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
//
//        //  Check if comment belongs to this reel
//        if (!comment.getReel().getId().equals(reelId)) {
//            throw new IllegalArgumentException("Comment does not belong to this reel");
//        }
//
//        //  Role checks: Owner, Admin, or SubAdmin can delete
//        boolean isOwner = comment.getUser().getId().equals(currentUser.getId());
//        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));
//        System.out.println("Comment User ID: " + comment.getUser().getId());
//        System.out.println("Current User ID: " + currentUser.getId());
//
//        boolean isSubAdmin = currentUser.getRoles().stream()
//                .anyMatch(role -> role.getName().equalsIgnoreCase("SUBADMIN"));
//
//        if (!isOwner && !isAdmin && !isSubAdmin) {
//            throw new SecurityException("You are not authorized to delete this comment");
//        }
//
//        //  If authorized, delete the comment
//        reelInteractionRepository.delete(comment);
//    }
//}




package com.nearprop.service.impl;

import com.google.common.base.Verify;
import com.nearprop.service.SubscriptionService;
import com.nearprop.config.AwsConfig;
import com.nearprop.dto.ReelDto;
import com.nearprop.dto.ReelInteractionDto;
import com.nearprop.dto.UserDto;
import com.nearprop.dto.payment.InitiatePaymentRequest;
import com.nearprop.dto.payment.PaymentResponse;
import com.nearprop.entity.PaymentTransaction;
import com.nearprop.entity.Property;
import com.nearprop.entity.Reel;
import com.nearprop.entity.ReelInteraction;
import com.nearprop.entity.Role;
import com.nearprop.entity.User;
import com.nearprop.entity.SubscriptionPlanFeature;
import com.nearprop.exception.BadRequestException;
import com.nearprop.exception.EntityNotFoundException;
import com.nearprop.exception.ForbiddenException;
import com.nearprop.repository.PaymentTransactionRepository;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.repository.ReelInteractionRepository;
import com.nearprop.repository.ReelRepository;
import com.nearprop.repository.SubscriptionPlanFeatureRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.PaymentService;
import com.nearprop.service.ReelService;
import com.nearprop.service.S3Service;
import com.nearprop.service.UserService;
import com.nearprop.service.VideoProcessingService;
import com.nearprop.service.UserFollowingService;
import com.nearprop.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.nearprop.entity.Subscription;
import com.nearprop.entity.SubscriptionPlan;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReelServiceImpl implements ReelService
{
    private final SubscriptionService subscriptionService;
    private final ReelRepository reelRepository;
    private final ReelInteractionRepository reelInteractionRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final SubscriptionPlanFeatureRepository subscriptionPlanFeatureRepository;
    private final VideoProcessingService videoProcessingService;
    private final S3Service s3Service;
    private final UserService userService;
    private final AwsConfig awsConfig;
    private final UserFollowingService userFollowingService;
    private final PaymentService paymentService;
    private final PaymentTransactionRepository paymentTransactionRepository;

    @Value("${reels.price:99.00}")
    private String reelPrice;

//    @Override
//    @Transactional
//    @org.springframework.cache.annotation.CacheEvict(value = {"reels", "properties", "property-search", "user-properties"}, allEntries = true)
//    public ReelDto uploadReel(ReelDto reelDto, MultipartFile videoFile) {
//        // Get current user
//        User currentUser = userService.getCurrentUser();
//
//        // Validate the property exists and belongs to the user
//        Property property = propertyRepository.findById(reelDto.getPropertyId())
//                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + reelDto.getPropertyId()));
//
//        // Strict property ownership validation - ONLY property owner can add reels
//        if (!property.getOwner().getId().equals(currentUser.getId())) {
//            throw new ForbiddenException("Only the property owner can upload reels");
//        }
//
//        // Check if this reel requires payment (any reel after the first one)
//        boolean paymentRequired = isAdditionalReelPaymentRequired(property.getId());
//
//        // For testing, create a default plan
//        SubscriptionPlanFeature plan = new SubscriptionPlanFeature();
//        plan.setPlanName("Test Plan");
//        plan.setPlanType(SubscriptionPlanFeature.PlanType.SELLER);
//        plan.setMaxProperties(10);
//        plan.setMaxReelsPerProperty(5);
//        plan.setMaxTotalReels(20);
//        plan.setMaxReelDurationSeconds(60);
//        plan.setMaxReelFileSizeMb(40);
//        plan.setAllowedVideoFormats("mp4,mov,avi,webm");
//        plan.setIsActive(true);
//        plan.setMonthlyPrice(0.0);
//
//        // Validate video file
//        if (videoFile == null || videoFile.isEmpty()) {
//            throw new BadRequestException("Video file cannot be empty");
//        }
//
//        // Process video and extract metadata
//        Map<String, Object> videoMetadata;
//
//        try {
//            // Validate video constraints from subscription plan
//            boolean isValidVideo = videoProcessingService.validateVideo(
//                    videoFile,
//                    plan.getMaxReelDurationSeconds(),
//                    plan.getMaxReelFileSizeMb(),
//                    plan.getAllowedVideoFormats());
//
//            if (!isValidVideo) {
//                throw new BadRequestException("Video does not meet requirements. Check duration, file size, and format.");
//            }
//
//            videoMetadata = videoProcessingService.processVideo(videoFile);
//        } catch (IOException e) {
//            log.error("Error processing video file: {}", e.getMessage(), e);
//            throw new BadRequestException("Failed to process video file: " + e.getMessage());
//        }
//
//        // Upload video and thumbnail to S3 with the structured directory path
//        String uniquePublicId = UUID.randomUUID().toString();
//        Map<String, String> uploadUrls = s3Service.uploadReel(
//                videoFile,
//                (byte[]) videoMetadata.get("thumbnail"),
//                property.getId() + "-" + reelDto.getTitle().replaceAll("[^a-zA-Z0-9]", "-"),
//                currentUser,
//                property);
//
//        // Create and save Reel entity with location data
//        Reel reel = new Reel();
//        reel.setProperty(property);
//        reel.setOwner(currentUser);
//        reel.setTitle(reelDto.getTitle());
//        reel.setDescription(reelDto.getDescription());
//        reel.setVideoUrl(uploadUrls.get("videoUrl"));
//        reel.setThumbnailUrl(uploadUrls.get("thumbnailUrl"));
//        reel.setDurationSeconds((Integer) videoMetadata.get("durationSeconds"));
//        reel.setFileSize(videoFile.getSize());
//
//        // Set status based on payment requirement
//        if (paymentRequired) {
//            reel.setStatus(Reel.ReelStatus.DRAFT);
//            reel.setPaymentRequired(true);
//        } else {
//            // ✅ IMPORTANT CHANGE: Always set to PENDING instead of PUBLISHED
//            reel.setStatus(Reel.ReelStatus.PENDING);
//            reel.setPaymentRequired(false);
//        }
//
//        reel.setProcessingStatus(Reel.ProcessingStatus.COMPLETED);
//        reel.setPublicId(uniquePublicId);
//        reel.setViewCount(0L);
//        reel.setLikeCount(0L);
//        reel.setCommentCount(0L);
//        reel.setShareCount(0L);
//        reel.setSaveCount(0L);
//        // Copy location data from property
//        reel.setLatitude(property.getLatitude());
//        reel.setLongitude(property.getLongitude());
//        reel.setDistrict(property.getDistrictName());
//        reel.setCity(property.getCity());
//        reel.setState(property.getState());
//
//        Reel savedReel = reelRepository.save(reel);
//
//        return mapToReelDto(savedReel);
//    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = {"reels", "properties", "property-search", "user-properties"}, allEntries = true)
    public ReelDto uploadReel(ReelDto reelDto, MultipartFile videoFile) {
        // Get current user
        User currentUser = userService.getCurrentUser();

        // Validate the property exists and belongs to the user
        Property property = propertyRepository.findById(reelDto.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + reelDto.getPropertyId()));

        // Strict property ownership validation - ONLY property owner can add reels
        if (!property.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only the property owner can upload reels");
        }

        // ❌ REMOVED PAYMENT CHECK
        // boolean paymentRequired = isAdditionalReelPaymentRequired(property.getId());

        // For testing, create a default plan
        SubscriptionPlanFeature plan = new SubscriptionPlanFeature();
        plan.setPlanName("Test Plan");
        plan.setPlanType(SubscriptionPlanFeature.PlanType.SELLER);
        plan.setMaxProperties(10);
        plan.setMaxReelsPerProperty(5);
        plan.setMaxTotalReels(20);
        plan.setMaxReelDurationSeconds(60);
        plan.setMaxReelFileSizeMb(40);
        plan.setAllowedVideoFormats("mp4,mov,avi,webm");
        plan.setIsActive(true);
        plan.setMonthlyPrice(0.0);

        // Validate video file
        if (videoFile == null || videoFile.isEmpty()) {
            throw new BadRequestException("Video file cannot be empty");
        }

        // Process video and extract metadata
        Map<String, Object> videoMetadata;

        try {
            // Validate video constraints from subscription plan
            boolean isValidVideo = videoProcessingService.validateVideo(
                    videoFile,
                    plan.getMaxReelDurationSeconds(),
                    plan.getMaxReelFileSizeMb(),
                    plan.getAllowedVideoFormats());

            if (!isValidVideo) {
                throw new BadRequestException("Video does not meet requirements. Check duration, file size, and format.");
            }

            videoMetadata = videoProcessingService.processVideo(videoFile);
        } catch (IOException e) {
            log.error("Error processing video file: {}", e.getMessage(), e);
            throw new BadRequestException("Failed to process video file: " + e.getMessage());
        }

        // Upload video and thumbnail to S3 with the structured directory path
        String uniquePublicId = UUID.randomUUID().toString();
        Map<String, String> uploadUrls = s3Service.uploadReel(
                videoFile,
                (byte[]) videoMetadata.get("thumbnail"),
                property.getId() + "-" + reelDto.getTitle().replaceAll("[^a-zA-Z0-9]", "-"),
                currentUser,
                property);

        // Create and save Reel entity with location data
        Reel reel = new Reel();
        reel.setProperty(property);
        reel.setOwner(currentUser);
        reel.setTitle(reelDto.getTitle());
        reel.setDescription(reelDto.getDescription());
        reel.setVideoUrl(uploadUrls.get("videoUrl"));
        reel.setThumbnailUrl(uploadUrls.get("thumbnailUrl"));
        reel.setDurationSeconds((Integer) videoMetadata.get("durationSeconds"));
        reel.setFileSize(videoFile.getSize());

        // ✅ SIMPLE: Always PENDING, no payment
        reel.setStatus(Reel.ReelStatus.PENDING);
        reel.setPaymentRequired(false); // Always false

        reel.setProcessingStatus(Reel.ProcessingStatus.COMPLETED);
        reel.setPublicId(uniquePublicId);
        reel.setViewCount(0L);
        reel.setLikeCount(0L);
        reel.setCommentCount(0L);
        reel.setShareCount(0L);
        reel.setSaveCount(0L);
        // Copy location data from property
        reel.setLatitude(property.getLatitude());
        reel.setLongitude(property.getLongitude());
        reel.setDistrict(property.getDistrictName());
        reel.setCity(property.getCity());
        reel.setState(property.getState());

        Reel savedReel = reelRepository.save(reel);

        return mapToReelDto(savedReel);
    }

    @Override
    @Cacheable(value = "reels", key = "#reelId")
    public ReelDto getReel(Long reelId) {
        Reel reel = findReelById(reelId);
        User currentUser = getCurrentUser();

        // Check if user can view this reel
        // Admin can view all reels
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase("ADMIN"));

        // Owner can view their own reels
        boolean isOwner = reel.getOwner().getId().equals(currentUser.getId());

        // For non-owners and non-admins, only show APPROVED reels
        if (!isOwner && !isAdmin && reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new ForbiddenException("You don't have permission to view this reel");
        }

        return mapToReelDto(reel, currentUser);
    }

    @Override
    @Cacheable(value = "reels", key = "'public_' + #publicId")
    public ReelDto getReelByPublicId(String publicId) {
        Reel reel = reelRepository.findByPublicId(publicId)
                .orElseThrow(() -> new EntityNotFoundException("Reel not found with public ID: " + publicId));

        User currentUser = getCurrentUser();

        // Check if user can view this reel
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase("ADMIN"));

        boolean isOwner = reel.getOwner().getId().equals(currentUser.getId());

        // For non-owners and non-admins, only show APPROVED reels
        if (!isOwner && !isAdmin && reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new ForbiddenException("You don't have permission to view this reel");
        }

        return mapToReelDto(reel, currentUser);
    }

    @Override
    public Page<ReelDto> getReelsByProperty(Long propertyId, Pageable pageable) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + propertyId));

        User currentUser = getCurrentUser();

        // Check if user is admin or property owner
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase("ADMIN"));

        boolean isOwner = property.getOwner().getId().equals(currentUser.getId());

        Page<Reel> reels;

        if (isAdmin || isOwner) {
            // Admin or owner can see all reels (including PENDING, DRAFT, etc.)
            reels = reelRepository.findByPropertyId(propertyId, pageable);
        } else {
            // Others can only see APPROVED reels
            reels = reelRepository.findByPropertyIdAndStatus(propertyId, Reel.ReelStatus.APPROVED, pageable);
        }

        if (reels.isEmpty()) {
            // Create empty page but no error
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        return reels.map(reel -> mapToReelDto(reel, currentUser));
    }

    @Override
    public Page<ReelDto> getReelsByUser(Long userId, Pageable pageable) {
        // Verify user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        User currentUser = getCurrentUser();

        // Check if current user is admin or viewing their own reels
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase("ADMIN"));

        boolean isSelf = userId.equals(currentUser.getId());

        Page<Reel> reels;

        if (isAdmin || isSelf) {
            // Admin or user themselves can see all reels
            reels = reelRepository.findByOwnerId(userId, pageable);
        } else {
            // Others can only see APPROVED reels
            reels = reelRepository.findByOwnerIdAndStatus(userId, Reel.ReelStatus.APPROVED, pageable);
        }

        if (reels.isEmpty()) {
            // Create empty page but no error
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        return reels.map(reel -> mapToReelDto(reel, currentUser));
    }

    @Override
    @Transactional
    public ReelDto updateReel(Long reelId, ReelDto reelDto) {
        Reel reel = findReelById(reelId);
        User currentUser = getCurrentUser();

        // Verify user owns the reel
        if (!reel.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You don't have permission to update this reel");
        }

        // Don't allow updates if reel is approved (unless admin)
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase("ADMIN"));

        if (reel.getStatus() == Reel.ReelStatus.APPROVED && !isAdmin) {
            throw new BadRequestException("Cannot update an approved reel. Please contact admin.");
        }

        // Update fields
        reel.setTitle(reelDto.getTitle());
        reel.setDescription(reelDto.getDescription());

        // If reel was approved and admin is updating, set back to PENDING for review
        if (reel.getStatus() == Reel.ReelStatus.APPROVED && isAdmin) {
            reel.setStatus(Reel.ReelStatus.PENDING);
        }

        Reel updatedReel = reelRepository.save(reel);
        return mapToReelDto(updatedReel);
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = {"reels", "properties", "property-search", "user-properties"}, allEntries = true)
    public void deleteReel(Long reelId) {
        Reel reel = findReelById(reelId);
        User currentUser = getCurrentUser();

        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase("ADMIN"));

        if (!reel.getOwner().getId().equals(currentUser.getId()) && !isAdmin) {
            throw new ForbiddenException("You don't have permission to delete this reel");
        }

        try {
            // Delete all interactions first to avoid constraint violations
            List<ReelInteraction> interactions = reelInteractionRepository.findByReelIdAndType(
                    reelId, ReelInteraction.InteractionType.LIKE);
            reelInteractionRepository.deleteAll(interactions);

            interactions = reelInteractionRepository.findByReelIdAndType(
                    reelId, ReelInteraction.InteractionType.COMMENT);
            reelInteractionRepository.deleteAll(interactions);

            interactions = reelInteractionRepository.findByReelIdAndType(
                    reelId, ReelInteraction.InteractionType.FOLLOW);
            reelInteractionRepository.deleteAll(interactions);

            interactions = reelInteractionRepository.findByReelIdAndType(
                    reelId, ReelInteraction.InteractionType.VIEW);
            reelInteractionRepository.deleteAll(interactions);

            interactions = reelInteractionRepository.findByReelIdAndType(
                    reelId, ReelInteraction.InteractionType.SAVE);
            reelInteractionRepository.deleteAll(interactions);

            interactions = reelInteractionRepository.findByReelIdAndType(
                    reelId, ReelInteraction.InteractionType.SHARE);
            reelInteractionRepository.deleteAll(interactions);

            // Get S3 URLs before deleting the reel
            String videoUrl = reel.getVideoUrl();
            String thumbnailUrl = reel.getThumbnailUrl();

            // Delete the reel from database
            reelRepository.delete(reel);

            // Now delete from S3 - after successful DB deletion
            if (videoUrl != null && thumbnailUrl != null) {
                try {
                    s3Service.deleteReel(videoUrl, thumbnailUrl);
                    log.info("Successfully deleted reel files from S3: {}", reelId);
                } catch (Exception e) {
                    log.error("Failed to delete S3 files for reel {}: {}", reelId, e.getMessage());
                    // Continue execution - we at least deleted from database
                }
            }
        } catch (Exception e) {
            log.error("Error deleting reel {}: {}", reelId, e.getMessage(), e);
            throw new RuntimeException("Failed to delete reel: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ReelDto likeReel(Long reelId) {
        Reel reel = findReelById(reelId);

        // Check if reel is approved
        if (reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new BadRequestException("Cannot like a reel that is not approved");
        }

        User currentUser = getCurrentUser();

        // Check if user already liked the reel
        boolean alreadyLiked = reelInteractionRepository.existsByReelIdAndUserIdAndType(
                reelId, currentUser.getId(), ReelInteraction.InteractionType.LIKE);

        if (alreadyLiked) {
            throw new BadRequestException("You have already liked this reel");
        }

        // Create like interaction
        ReelInteraction like = ReelInteraction.builder()
                .reel(reel)
                .user(currentUser)
                .type(ReelInteraction.InteractionType.LIKE)
                .build();

        reelInteractionRepository.save(like);

        // Update like count
        reel.setLikeCount(reel.getLikeCount() + 1);
        Reel updatedReel = reelRepository.save(reel);

        return mapToReelDto(updatedReel, currentUser);
    }

    @Override
    @Transactional
    public ReelDto unlikeReel(Long reelId) {
        Reel reel = findReelById(reelId);

        // Check if reel is approved
        if (reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new BadRequestException("Cannot unlike a reel that is not approved");
        }

        User currentUser = getCurrentUser();

        // Find and delete like interaction
        boolean deleted = reelInteractionRepository.existsByReelIdAndUserIdAndType(
                reelId, currentUser.getId(), ReelInteraction.InteractionType.LIKE);

        if (!deleted) {
            throw new BadRequestException("You have not liked this reel");
        }

        reelInteractionRepository.deleteByReelIdAndUserIdAndType(
                reelId, currentUser.getId(), ReelInteraction.InteractionType.LIKE);

        // Update like count
        if (reel.getLikeCount() > 0) {
            reel.setLikeCount(reel.getLikeCount() - 1);
        }

        Reel updatedReel = reelRepository.save(reel);
        return mapToReelDto(updatedReel, currentUser);
    }

    @Override
    @Transactional
    public ReelDto commentReel(Long reelId, String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new BadRequestException("Comment cannot be empty");
        }

        Reel reel = findReelById(reelId);

        // Check if reel is approved
        if (reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new BadRequestException("Cannot comment on a reel that is not approved");
        }

        User currentUser = getCurrentUser();

        try {
            // Create comment interaction with the original comment text
            ReelInteraction commentInteraction = ReelInteraction.builder()
                    .reel(reel)
                    .user(currentUser)
                    .type(ReelInteraction.InteractionType.COMMENT)
                    .comment(comment)
                    .build();

            reelInteractionRepository.save(commentInteraction);

            // Update comment count
            reel.setCommentCount(reel.getCommentCount() + 1);
            Reel updatedReel = reelRepository.save(reel);

            return mapToReelDto(updatedReel, currentUser);
        } catch (Exception e) {
            log.error("Failed to add comment: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public ReelDto followReel(Long reelId) {
        Reel reel = findReelById(reelId);

        // Check if reel is approved
        if (reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new BadRequestException("Cannot follow a reel that is not approved");
        }

        User currentUser = getCurrentUser();
        User reelOwner = reel.getOwner();

        // Prevent following your own reel
        if (currentUser.getId().equals(reelOwner.getId())) {
            throw new BadRequestException("You cannot follow your own reel");
        }

        // Use UserFollowingService to follow the reel owner
        try {
            userFollowingService.followUser(reelOwner.getId());
        } catch (BadRequestException e) {
            // User is already following this user, just ignore
        }

        return mapToReelDto(reel, currentUser);
    }

    @Override
    @Transactional
    public ReelDto unfollowReel(Long reelId) {
        Reel reel = findReelById(reelId);

        // Check if reel is approved
        if (reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new BadRequestException("Cannot unfollow a reel that is not approved");
        }

        User currentUser = getCurrentUser();
        User reelOwner = reel.getOwner();

        // Prevent unfollowing your own reel
        if (currentUser.getId().equals(reelOwner.getId())) {
            throw new BadRequestException("You cannot unfollow your own reel");
        }

        // Use UserFollowingService to unfollow the reel owner
        try {
            userFollowingService.unfollowUser(reelOwner.getId());
        } catch (BadRequestException e) {
            // User is not following this user, just ignore
        }

        return mapToReelDto(reel, currentUser);
    }

    @Override
    @Transactional
    public ReelDto viewReel(Long reelId) {
        Reel reel = findReelById(reelId);

        // Check if reel is approved
        if (reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new BadRequestException("Cannot view a reel that is not approved");
        }

        User currentUser = getCurrentUser();

        // Create view interaction if not already viewed
        if (!reelInteractionRepository.existsByReelIdAndUserIdAndType(
                reelId, currentUser.getId(), ReelInteraction.InteractionType.VIEW)) {

            ReelInteraction view = ReelInteraction.builder()
                    .reel(reel)
                    .user(currentUser)
                    .type(ReelInteraction.InteractionType.VIEW)
                    .build();

            reelInteractionRepository.save(view);
        }

        // Update view count
        reel.setViewCount(reel.getViewCount() + 1);
        Reel updatedReel = reelRepository.save(reel);

        return mapToReelDto(updatedReel, currentUser);
    }

    @Override
    @Transactional
    public ReelDto shareReel(Long reelId) {
        Reel reel = findReelById(reelId);

        // Check if reel is approved
        if (reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new BadRequestException("Cannot share a reel that is not approved");
        }

        User currentUser = getCurrentUser();

        // Create share interaction
        ReelInteraction share = ReelInteraction.builder()
                .reel(reel)
                .user(currentUser)
                .type(ReelInteraction.InteractionType.SHARE)
                .build();

        reelInteractionRepository.save(share);

        // Update share count
        reel.setShareCount(reel.getShareCount() + 1);
        Reel updatedReel = reelRepository.save(reel);

        return mapToReelDto(updatedReel, currentUser);
    }

    @Override
    public boolean canUploadMoreReels(Long propertyId) {
        // Check only if there's a property with that ID, not about payment
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + propertyId));

        // Anyone can upload reels now, they'll just have to pay for additional ones
        return true;
    }

    @Override
    public Page<ReelDto> getNearbyReels(Double latitude, Double longitude, Double radius, Pageable pageable) {
        User currentUser = getCurrentUser();

        if (latitude == null || longitude == null) {
            // ✅ IMPORTANT: Only show APPROVED reels in public feeds
            Page<Reel> reels = reelRepository.findByStatus(Reel.ReelStatus.APPROVED, pageable);
            return reels.map(reel -> mapToReelDto(reel, currentUser));
        }

        // Use geospatial query to find nearby reels within radius
        // ✅ IMPORTANT: Only show APPROVED reels
        List<Reel> nearbyReels = reelRepository.findNearbyReelsWithStatus(latitude, longitude, radius, "APPROVED");

        // Calculate total elements for pagination
        long totalElements = nearbyReels.size();

        // Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), nearbyReels.size());

        // Handle case where start is beyond the list size
        if (start >= totalElements) {
            return Page.empty(pageable);
        }

        List<Reel> paginatedReels = nearbyReels.subList(start, end);

        // Convert to DTOs with distance information
        List<ReelDto> reelDtos = paginatedReels.stream()
                .map(reel -> {
                    ReelDto dto = mapToReelDto(reel, currentUser);

                    // Calculate distance if coordinates are available
                    if (reel.getLatitude() != null && reel.getLongitude() != null) {
                        double distance = calculateDistance(
                                latitude, longitude,
                                reel.getLatitude(), reel.getLongitude());
                        dto.setDistanceKm(distance);
                    }

                    return dto;
                })
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(reelDtos, pageable, totalElements);
    }

    @Override
    public Page<ReelDto> getReelsByCity(String city, Pageable pageable) {
        User currentUser = getCurrentUser();

        // ✅ IMPORTANT: Only show APPROVED reels
        Page<Reel> reels = reelRepository.findByCityAndStatus(city, Reel.ReelStatus.APPROVED, pageable);

        if (reels.isEmpty()) {
            // Create empty page but no error
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        return reels.map(reel -> mapToReelDto(reel, currentUser));
    }

    @Override
    public Page<ReelDto> getReelsByDistrict(String district, Pageable pageable) {
        User currentUser = getCurrentUser();

        // ✅ IMPORTANT: Only show APPROVED reels
        Page<Reel> reels = reelRepository.findByDistrictAndStatus(district, Reel.ReelStatus.APPROVED, pageable);

        if (reels.isEmpty()) {
            // Create empty page but no error
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        return reels.map(reel -> mapToReelDto(reel, currentUser));
    }

    @Override
    public String generateShareableLink(Long reelId) {
        Reel reel = findReelById(reelId);

        // Check if reel is approved
        if (reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new BadRequestException("Cannot share a reel that is not approved");
        }

        // Create shareable link using the public ID
        String baseUrl = "https://nearprop.com/reels/";
        return baseUrl + reel.getPublicId();
    }

    @Override
    @Transactional
    public ReelDto saveReel(Long reelId) {
        Reel reel = findReelById(reelId);

        // Check if reel is approved
        if (reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new BadRequestException("Cannot save a reel that is not approved");
        }

        User currentUser = getCurrentUser();

        // Check if user already saved the reel
        boolean alreadySaved = reelInteractionRepository.existsByReelIdAndUserIdAndType(
                reelId, currentUser.getId(), ReelInteraction.InteractionType.SAVE);

        if (alreadySaved) {
            throw new BadRequestException("You have already saved this reel");
        }

        // Create save interaction
        ReelInteraction save = ReelInteraction.builder()
                .reel(reel)
                .user(currentUser)
                .type(ReelInteraction.InteractionType.SAVE)
                .build();

        reelInteractionRepository.save(save);

        // Update save count
        reel.setSaveCount(reel.getSaveCount() + 1);
        Reel updatedReel = reelRepository.save(reel);

        return mapToReelDto(updatedReel, currentUser);
    }

    @Override
    @Transactional
    public ReelDto unsaveReel(Long reelId) {
        Reel reel = findReelById(reelId);

        // Check if reel is approved
        if (reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new BadRequestException("Cannot unsave a reel that is not approved");
        }

        User currentUser = getCurrentUser();

        // Find and delete save interaction
        boolean saved = reelInteractionRepository.existsByReelIdAndUserIdAndType(
                reelId, currentUser.getId(), ReelInteraction.InteractionType.SAVE);

        if (!saved) {
            throw new BadRequestException("You have not saved this reel");
        }

        reelInteractionRepository.deleteByReelIdAndUserIdAndType(
                reelId, currentUser.getId(), ReelInteraction.InteractionType.SAVE);

        // Update save count
        if (reel.getSaveCount() > 0) {
            reel.setSaveCount(reel.getSaveCount() - 1);
        }

        Reel updatedReel = reelRepository.save(reel);
        return mapToReelDto(updatedReel, currentUser);
    }

    @Override
    public Page<ReelDto> getSavedReelsByUser(Long userId, Pageable pageable) {
        User currentUser = getCurrentUser();

        // If requesting other user's saved reels, check if current user is admin
        if (!userId.equals(currentUser.getId()) && !currentUser.getRoles().contains(Role.ADMIN)) {
            throw new ForbiddenException("You do not have permission to view this user's saved reels");
        }

        Page<ReelInteraction> savedInteractions = reelInteractionRepository.findByUserIdAndType(
                userId, ReelInteraction.InteractionType.SAVE, pageable);

        // Filter only approved reels
        List<ReelDto> approvedReels = savedInteractions.getContent().stream()
                .filter(interaction -> interaction.getReel().getStatus() == Reel.ReelStatus.APPROVED)
                .map(interaction -> mapToReelDto(interaction.getReel(), currentUser))
                .collect(Collectors.toList());

        return new PageImpl<>(approvedReels, pageable, savedInteractions.getTotalElements());
    }

    @Override
    public Page<ReelInteractionDto> getReelComments(Long reelId, Pageable pageable) {
        // Verify reel exists and is approved
        Reel reel = findReelById(reelId);

        if (reel.getStatus() != Reel.ReelStatus.APPROVED) {
            throw new BadRequestException("Cannot view comments of a reel that is not approved");
        }

        // Get comments
        Page<ReelInteraction> comments = reelInteractionRepository.findByReelIdAndType(
                reelId, ReelInteraction.InteractionType.COMMENT, pageable);

        return comments.map(this::mapToReelInteractionDto);
    }

    @Override
    public Map<String, Object> checkUploadLimit(Long propertyId) {
        // Get property
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + propertyId));

        // Verify ownership
        User currentUser = getCurrentUser();
        if (!property.getOwner().getId().equals(currentUser.getId()) &&
                !currentUser.getRoles().contains("ADMIN")) {
            throw new ForbiddenException("You don't have permission to check upload limits for this property");
        }

        // Get current count
        List<Reel> existingReels = reelRepository.findReelsByPropertyId(propertyId);
        int uploadedCount = existingReels.size();
        boolean paymentRequired = isAdditionalReelPaymentRequired(propertyId);

        Map<String, Object> result = new HashMap<>();
        result.put("canUpload", true); // Anyone can upload, but might need to pay
        result.put("uploadedCount", uploadedCount);
        result.put("paymentRequired", paymentRequired);

        if (paymentRequired) {
            result.put("reelPrice", reelPrice);
            result.put("currency", "INR");
        }

        // Add reel information
        List<Map<String, Object>> reelsInfo = existingReels.stream()
                .map(reel -> {
                    Map<String, Object> reelInfo = new HashMap<>();
                    reelInfo.put("id", reel.getId());
                    reelInfo.put("title", reel.getTitle());
                    reelInfo.put("status", reel.getStatus());
                    reelInfo.put("videoUrl", reel.getVideoUrl());
                    reelInfo.put("thumbnailUrl", reel.getThumbnailUrl());
                    reelInfo.put("createdAt", reel.getCreatedAt());
                    return reelInfo;
                })
                .collect(Collectors.toList());

        result.put("reels", reelsInfo);

        return result;
    }

    @Override
    public Page<ReelDto> getReelsByLocation(Double latitude, Double longitude, Double radiusKm, Pageable pageable) {
        User currentUser = getCurrentUser();

        // Find reels within radius using repository query
        // ✅ IMPORTANT: Only show APPROVED reels
        Page<Object[]> reelsWithDistance = reelRepository.findApprovedReelsWithinRadius(
                latitude, longitude, radiusKm, pageable);

        // Convert to ReelDto objects with distance information
        List<ReelDto> reelDtos = reelsWithDistance.getContent().stream()
                .map(result -> {
                    Reel reel = (Reel) result[0];
                    Double distance = (Double) result[1];

                    ReelDto dto = mapToReelDto(reel, currentUser);
                    dto.setDistanceKm(distance);
                    return dto;
                })
                .collect(Collectors.toList());

        // Return as page
        return new PageImpl<>(reelDtos, pageable, reelsWithDistance.getTotalElements());
    }

    @Override
    public Page<ReelDto> getReelsFeed(Pageable pageable) {
        User currentUser = getCurrentUser();

        // Get popular reels (most viewed)
        // ✅ IMPORTANT: Only show APPROVED reels
        Sort sort = Sort.by(Sort.Direction.DESC, "viewCount");
        PageRequest pageRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                sort
        );

        Page<Reel> popularReels = reelRepository.findByStatus(Reel.ReelStatus.APPROVED, pageRequest);

        return popularReels.map(reel -> mapToReelDto(reel, currentUser));
    }

    @Override
    public Page<ReelDto> getReelsByLocationWithFallback(Double latitude, Double longitude, double radius, Pageable pageable) {
        User currentUser = getCurrentUser();

        // First try to get nearby reels
        // ✅ IMPORTANT: Only show APPROVED reels
        Page<Object[]> reelsWithDistance = reelRepository.findApprovedReelsWithinRadius(
                latitude, longitude, radius, pageable);

        List<ReelDto> reelDtos = new ArrayList<>();

        // Convert nearby reels to DTOs
        reelsWithDistance.getContent().forEach(result -> {
            Reel reel = (Reel) result[0];
            Double distance = (Double) result[1];

            ReelDto dto = mapToReelDto(reel, currentUser);
            dto.setDistanceKm(distance);
            reelDtos.add(dto);
        });

        // If we don't have enough nearby reels to fill the page, get more from general feed
        if (reelDtos.size() < pageable.getPageSize()) {
            int remainingSize = pageable.getPageSize() - reelDtos.size();

            // Create new pageable to get the remaining reels
            Pageable remainingPageable = PageRequest.of(0, remainingSize, pageable.getSort());

            // Get popular reels, exclude ones we already have
            List<Long> existingReelIds = reelDtos.stream()
                    .map(ReelDto::getId)
                    .collect(Collectors.toList());

            List<Reel> popularReels;
            if (existingReelIds.isEmpty()) {
                // If no existing reels, just get the first page
                popularReels = reelRepository.findByStatus(Reel.ReelStatus.APPROVED, remainingPageable).getContent();
            } else {
                // Otherwise, exclude reels we already have
                popularReels = reelRepository.findByIdNotInAndStatus(existingReelIds, Reel.ReelStatus.APPROVED, remainingPageable);
            }

            // Add to our result set
            popularReels.forEach(reel -> {
                ReelDto dto = mapToReelDto(reel, currentUser);
                dto.setDistanceKm(null); // No distance for non-nearby reels
                reelDtos.add(dto);
            });
        }

        // Return as page
        return new PageImpl<>(reelDtos, pageable,
                reelsWithDistance.getTotalElements() +
                        (reelDtos.size() > reelsWithDistance.getContent().size() ?
                                Long.MAX_VALUE - reelsWithDistance.getTotalElements() : 0));
    }

    @Override
    public boolean isAdditionalReelPaymentRequired(Long propertyId) {
        // Count reels for this property
        Long publishedReelsCount = reelRepository.countReelsByPropertyId(propertyId);

        // Get current user
        User currentUser = getCurrentUser();

        // Get active subscription of user
        Optional<Subscription> activeSubOpt = subscriptionService.getSubscriptionWithAvailablePropertySlots(
                currentUser.getId(), SubscriptionPlan.PlanType.PROPERTY
        );

        if (activeSubOpt.isEmpty()) {
            // No subscription => default: only 1 reel free
            return publishedReelsCount >= 1;
        }

        Subscription subscription = activeSubOpt.get();
        SubscriptionPlan plan = subscription.getPlan();

        // Unlimited reels allowed
        if (plan.hasUnlimitedReels()) {
            return false;
        }

        // Property-level limit (e.g. max 15 reels per property)
        if (plan.getMaxReelsPerProperty() != null && plan.getMaxReelsPerProperty() > 0) {
            if (publishedReelsCount >= plan.getMaxReelsPerProperty()) {
                return true; // exceeded property limit
            }
        }

        // Global limit (across all properties)
        if (plan.getMaxTotalReels() != null && plan.getMaxTotalReels() > 0) {
            long totalReels = reelRepository.countReelsByUserId(currentUser.getId());
            if (totalReels >= plan.getMaxTotalReels()) {
                return true; // exceeded global limit
            }
        }

        return false; // Still within limits
    }

    @Override
    @Transactional
    public PaymentResponse initiateReelPayment(Long reelId) {
        // Get reel
        Reel reel = findReelById(reelId);
        User currentUser = getCurrentUser();

        // Verify reel ownership
        if (!reel.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You don't have permission to pay for this reel");
        }

        // Verify reel requires payment
        if (!reel.getPaymentRequired()) {
            throw new BadRequestException("This reel does not require payment");
        }

        // Verify reel is in DRAFT status
//        if (reel.getStatus() != Reel.ReelStatus.DRAFT) {
//            throw new BadRequestException("This reel cannot be paid for at this time");
//        }

        // Create payment request
        InitiatePaymentRequest paymentRequest = InitiatePaymentRequest.builder()
                .amount(new BigDecimal(reelPrice))
                .currency("INR")
                .paymentType(PaymentTransaction.PaymentType.REEL_PURCHASE)
                .customerName(currentUser.getName())
                .customerEmail(currentUser.getEmail())
                .customerPhone(currentUser.getMobileNumber())
                .build();

        // Initiate payment
        PaymentResponse paymentResponse = paymentService.initiatePayment(paymentRequest);

        return paymentResponse;
    }

    @Override
    @Transactional
    public ReelDto publishReelAfterPayment(Long reelId, String paymentReferenceId) {
        // Get reel
        Reel reel = findReelById(reelId);
        User currentUser = getCurrentUser();

        // Verify reel ownership
        if (!reel.getOwner().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You don't have permission to publish this reel");
        }

        // Verify payment exists and is completed
        PaymentTransaction payment = paymentTransactionRepository.findByReferenceId(paymentReferenceId)
                .orElseThrow(() -> new BadRequestException("Payment not found"));

        if (!payment.isSuccessful()) {
            throw new BadRequestException("Payment has not been completed");
        }

        if (payment.getPaymentType() != PaymentTransaction.PaymentType.REEL_PURCHASE) {
            throw new BadRequestException("Invalid payment type for reel publishing");
        }

        // ✅ IMPORTANT: Set to PENDING for admin approval, not APPROVED
        reel.setStatus(Reel.ReelStatus.PENDING);
     reel.setPaymentTransactionId(paymentReferenceId);

        Reel updatedReel = reelRepository.save(reel);

        return mapToReelDto(updatedReel);
    }

    @Override
    public List<ReelDto> getAllNearbyReelsWithFullDetails(Double latitude, Double longitude) {
        User currentUser = getCurrentUser();
        // Fetch all APPROVED reels with location
        // ✅ IMPORTANT: Only show APPROVED reels
        List<Reel> reels = reelRepository.findByStatus(Reel.ReelStatus.APPROVED);
        // Calculate distance for each reel and sort
        List<ReelDto> reelDtos = reels.stream()
                .filter(reel -> reel.getLatitude() != null && reel.getLongitude() != null)
                .map(reel -> {
                    ReelDto dto = mapToReelDto(reel, currentUser);
                    double distance = calculateDistance(latitude, longitude, reel.getLatitude(), reel.getLongitude());
                    dto.setDistanceKm(distance);
                    // Add all users who liked this reel
                    List<ReelInteraction> likes = reelInteractionRepository.findByReelIdAndType(reel.getId(), ReelInteraction.InteractionType.LIKE);
                    dto.setLikedBy(likes.stream().map(like -> like.getUser().getId()).collect(Collectors.toList()));
                    // Add all comments (already included by mapToReelDto)
                    // Add view and share count (already included)
                    return dto;
                })
                .sorted(Comparator.comparingDouble(ReelDto::getDistanceKm))
                .collect(Collectors.toList());
        return reelDtos;
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = {"reels", "properties", "property-search", "user-properties"}, allEntries = true)
    public void deleteAllReelsOfProperty(Long propertyId, com.nearprop.entity.User currentUser) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with ID: " + propertyId));
        // Only admin or property owner can delete all reels
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(r -> r.name().equals("ADMIN"));
        boolean isOwner = property.getOwner().getId().equals(currentUser.getId());
        if (!isAdmin && !isOwner) {
            throw new ForbiddenException("You don't have permission to delete all reels for this property");
        }
        List<Reel> reels = reelRepository.findByPropertyId(propertyId, org.springframework.data.domain.Pageable.unpaged()).getContent();
        for (Reel reel : reels) {
            try {
                deleteReel(reel.getId());
            } catch (Exception e) {
                log.warn("Failed to delete reel {}: {}", reel.getId(), e.getMessage());
            }
        }
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = {"reels", "properties", "property-search", "user-properties"}, allEntries = true)
    public void deleteAllReelsAndInteractions() {
        List<Reel> reels = reelRepository.findAll();
        for (Reel reel : reels) {
            try {
                // Delete all interactions for this reel
                List<ReelInteraction> interactions = reelInteractionRepository.findByReelId(reel.getId());
                reelInteractionRepository.deleteAll(interactions);
                // Delete from S3
                String videoUrl = reel.getVideoUrl();
                String thumbnailUrl = reel.getThumbnailUrl();
                reelRepository.delete(reel);
                if (videoUrl != null && thumbnailUrl != null) {
                    try {
                        s3Service.deleteReel(videoUrl, thumbnailUrl);
                        log.info("Deleted S3 files for reel {}", reel.getId());
                    } catch (Exception e) {
                        log.warn("Failed to delete S3 files for reel {}: {}", reel.getId(), e.getMessage());
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to delete reel {}: {}", reel.getId(), e.getMessage());
            }
        }
    }

    // ✅ NEW: ADMIN APPROVAL METHODS
    @Override
    @Transactional
    public ReelDto approveReel(Long reelId) {
        Reel reel = findReelById(reelId);
        User currentUser = getCurrentUser();

        // Check if user is admin
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase("ADMIN"));

        if (!isAdmin) {
            throw new ForbiddenException("Only admin can approve reels");
        }

        // Check if reel is in PENDING status
        if (reel.getStatus() != Reel.ReelStatus.PENDING) {
            throw new BadRequestException("Only pending reels can be approved");
        }

        // Approve the reel
        reel.setStatus(Reel.ReelStatus.APPROVED);
        Reel updatedReel = reelRepository.save(reel);

        log.info("Reel {} approved by admin {}", reelId, currentUser.getId());

        return mapToReelDto(updatedReel);
    }

    @Override
    @Transactional
    public ReelDto rejectReel(Long reelId, String reason) {
        Reel reel = findReelById(reelId);
        User currentUser = getCurrentUser();

        // Check if user is admin
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase("ADMIN"));

        if (!isAdmin) {
            throw new ForbiddenException("Only admin can reject reels");
        }

        // Check if reel is in PENDING status
        if (reel.getStatus() != Reel.ReelStatus.PENDING) {
            throw new BadRequestException("Only pending reels can be rejected");
        }

        // Reject the reel
        reel.setStatus(Reel.ReelStatus.REJECTED);
        Reel updatedReel = reelRepository.save(reel);

        log.info("Reel {} rejected by admin {}. Reason: {}", reelId, currentUser.getId(), reason);

        return mapToReelDto(updatedReel);
    }

    @Override
    public Page<ReelDto> getPendingReels(Pageable pageable) {
        User currentUser = getCurrentUser();

        // Check if user is admin
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase("ADMIN"));

        if (!isAdmin) {
            throw new ForbiddenException("Only admin can view pending reels");
        }

        // Get pending reels
        Page<Reel> pendingReels = reelRepository.findByStatus(Reel.ReelStatus.PENDING, pageable);

        return pendingReels.map(reel -> mapToReelDto(reel, currentUser));
    }

    @Override
    public Page<ReelDto> getReelsByStatus(Reel.ReelStatus status, Pageable pageable) {
        User currentUser = getCurrentUser();

        // Check if user is admin
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(r -> r.name().equalsIgnoreCase("ADMIN"));

        if (!isAdmin) {
            throw new ForbiddenException("Only admin can filter reels by status");
        }

        Page<Reel> reels = reelRepository.findByStatus(status, pageable);

        return reels.map(reel -> mapToReelDto(reel, currentUser));
    }
    @Override
    public Page<ReelDto> getPendingReelsByUser(Long userId, Pageable pageable) {
        return reelRepository
                .findByOwnerIdAndStatus(userId, Reel.ReelStatus.PENDING, pageable)
                .map(this::mapToReelDto);
    }


//
@Override
@Transactional
public ReelDto updateReelStatus(Long reelId, Reel.ReelStatus status, String reason) {

    // 🔐 ADMIN CHECK — YE ADD KARNA THA
    User currentUser = userService.getCurrentUser();

    boolean isAdmin = currentUser.getRoles().stream()
            .anyMatch(role -> role.name().equalsIgnoreCase("ADMIN"));

    if (!isAdmin) {
        throw new ForbiddenException("Only admin can update reel status");
    }

    Reel reel = findReelById(reelId);

    if (reel.getStatus() != Reel.ReelStatus.PENDING) {
        throw new BadRequestException("Only PENDING reels can be updated");
    }

    if (status == Reel.ReelStatus.APPROVED) {
        reel.setStatus(Reel.ReelStatus.APPROVED);
        reel.setRejectionReason(null);
    }
    else if (status == Reel.ReelStatus.REJECTED) {
        reel.setStatus(Reel.ReelStatus.REJECTED);
        reel.setRejectionReason(reason);
    }
    else {
        throw new BadRequestException("Invalid status update");
    }

    return mapToReelDto(reelRepository.save(reel), currentUser);
}



    // Helper methods

    private Reel findReelById(Long reelId) {
        return reelRepository.findById(reelId)
                .orElseThrow(() -> new EntityNotFoundException("Reel not found with ID: " + reelId));
    }

    private User getCurrentUser() {
        return userService.getCurrentUser();
    }

    private Optional<SubscriptionPlanFeature> getUserSubscriptionPlan(User user) {
        // In a real implementation, this would look up the user's active subscription
        // For now, determine plan based on user roles

        if (user.getRoles().contains("ROLE_DEVELOPER")) {
            // If user is a developer, get the developer plan
            return subscriptionPlanFeatureRepository.findByPlanName("Basic Developer");
        } else if (user.getRoles().contains("ROLE_ADVISOR")) {
            // If user is an advisor, get the advisor plan
            return subscriptionPlanFeatureRepository.findByPlanName("Basic Advisor");
        } else if (user.getRoles().contains("ROLE_SELLER")) {
            // If user is a seller, get the seller plan
            return subscriptionPlanFeatureRepository.findByPlanName("Basic Seller");
        } else {
            // Default plan for regular users
            return subscriptionPlanFeatureRepository.findByPlanName("Free User");
        }
    }

    private ReelDto mapToReelDto(Reel reel) {
        return mapToReelDto(reel, null);
    }

    private ReelDto mapToReelDto(Reel reel, User currentUser) {
        boolean liked = false;
        boolean followed = false;
        boolean saved = false;

        if (currentUser != null) {
            // Check if current user liked and saved the reel
            liked = reelInteractionRepository.existsByReelIdAndUserIdAndType(
                    reel.getId(), currentUser.getId(), ReelInteraction.InteractionType.LIKE);
            saved = reelInteractionRepository.existsByReelIdAndUserIdAndType(
                    reel.getId(), currentUser.getId(), ReelInteraction.InteractionType.SAVE);

            // Check if current user follows the reel owner (not the reel itself)
            if (!currentUser.getId().equals(reel.getOwner().getId())) {  // Don't check for yourself
                try {
                    followed = userFollowingService.isFollowing(reel.getOwner().getId());
                } catch (Exception e) {
                    log.warn("Error checking if user follows reel owner: {}", e.getMessage());
                    followed = false;
                }
            }
        }

        // Map owner to UserDto with all available fields
        User owner = reel.getOwner();
        UserDto ownerDto = UserDto.builder()
                .id(owner.getId())
                .name(owner.getName())
                .email(owner.getEmail())
                .mobileNumber(owner.getMobileNumber())
                .username(owner.getUsername())
                .phoneNumber(owner.getMobileNumber())
                .profileImageUrl(owner.getProfileImageUrl())
                .roles(owner.getRoles())
                .createdAt(owner.getCreatedAt())
                .updatedAt(owner.getUpdatedAt())
                .isFollowing(followed)
                .build();

        // Include property summary information
        Property property = reel.getProperty();

        // Get comments for this reel
        List<ReelInteraction> comments = reelInteractionRepository.findByReelIdAndType(
                reel.getId(), ReelInteraction.InteractionType.COMMENT);

        List<ReelInteractionDto> commentDtos = comments.stream()
                .map(this::mapToReelInteractionDto)
                .collect(Collectors.toList());

        return ReelDto.builder()
                .id(reel.getId())
                .propertyId(reel.getProperty().getId())
                // Set property details directly instead of through a separate DTO
                .property(null) // We'll include essential property data in the feed API endpoint
                .owner(ownerDto)
                .title(reel.getTitle())
                .description(reel.getDescription())
                .videoUrl(reel.getVideoUrl())
                .thumbnailUrl(reel.getThumbnailUrl())
                .durationSeconds(reel.getDurationSeconds())
                .status(reel.getStatus()) // ✅ Include status in DTO
                .processingStatus(reel.getProcessingStatus())
                .publicId(reel.getPublicId())
                .viewCount(reel.getViewCount())
                .likeCount(reel.getLikeCount())
                .commentCount(reel.getCommentCount())
                .shareCount(reel.getShareCount())
                .saveCount(reel.getSaveCount())
                // Location data
                .latitude(reel.getLatitude())
                .longitude(reel.getLongitude())
                .district(reel.getDistrict())
                .city(reel.getCity())
                .state(reel.getState())
                .createdAt(reel.getCreatedAt())
                .updatedAt(reel.getUpdatedAt())
                .liked(liked)
                .followed(followed)
                .saved(saved)
                .comments(commentDtos)
                .build();
    }

    /**
     * Calculate distance between two points using Haversine formula
     *
     * @param lat1 Latitude of point 1
     * @param lon1 Longitude of point 1
     * @param lat2 Latitude of point 2
     * @param lon2 Longitude of point 2
     * @return Distance in kilometers
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Radius of the Earth in km
        final double R = 6371.0;

        // Convert degrees to radians
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        // Haversine formula
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    /**
     * Map ReelInteraction to ReelInteractionDto
     *
     * @param interaction The ReelInteraction to map
     * @return A ReelInteractionDto
     */
    private ReelInteractionDto mapToReelInteractionDto(ReelInteraction interaction) {
        // Create user DTO with all available fields
        User user = interaction.getUser();
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .username(user.getUsername())
                .phoneNumber(user.getMobileNumber())
                .profileImageUrl(user.getProfileImageUrl())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        // Build and return DTO
        return ReelInteractionDto.builder()
                .id(interaction.getId())
                .reelId(interaction.getReel().getId())
                .user(userDto)
                .type(interaction.getType())
                .comment(interaction.getComment())
                .createdAt(interaction.getCreatedAt())
                .build();
    }

    /**
     * Get subscription plan feature
     *
     * @param user The user
     * @param featureCode The feature code
     * @return The subscription plan feature
     */
    private SubscriptionPlanFeature getSubscriptionPlanFeature(User user, String featureCode) {
        // For testing, use default limits
        SubscriptionPlanFeature feature = new SubscriptionPlanFeature();
        if ("REELS_PER_PROPERTY".equals(featureCode)) {
            feature.setValue("5");  // Default: 5 reels per property
        }
        return feature;
    }

    public void deleteComment(Long reelId, Long commentId, User currentUser) {
        ReelInteraction comment = reelInteractionRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        //  Check if comment belongs to this reel
        if (!comment.getReel().getId().equals(reelId)) {
            throw new IllegalArgumentException("Comment does not belong to this reel");
        }

        //  Role checks: Owner, Admin, or SubAdmin can delete
        boolean isOwner = comment.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("ADMIN"));
        System.out.println("Comment User ID: " + comment.getUser().getId());
        System.out.println("Current User ID: " + currentUser.getId());

        boolean isSubAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("SUBADMIN"));

        if (!isOwner && !isAdmin && !isSubAdmin) {
            throw new SecurityException("You are not authorized to delete this comment");
        }

        //  If authorized, delete the comment
        reelInteractionRepository.delete(comment);
    }
}