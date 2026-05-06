//package com.nearprop.service;
//
//import com.nearprop.dto.ReelDto;
//import com.nearprop.dto.ReelInteractionDto;
//import com.nearprop.dto.payment.PaymentResponse;
//import com.nearprop.entity.Reel;
//import com.nearprop.entity.User;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//public interface ReelService {
//
//    /**
//     * Upload a new reel
//     * @param reelDto Reel metadata
//     * @param videoFile Video file
//     * @return Created reel
//     */
//    ReelDto uploadReel(ReelDto reelDto, MultipartFile videoFile);
//
//    /**
//     * Get a reel by ID
//     * @param reelId Reel ID
//     * @return Reel if found
//     */
//    ReelDto getReel(Long reelId);
//
//    /**
//     * Get a reel by public ID
//     * @param publicId Public ID
//     * @return Reel if found
//     */
//    ReelDto getReelByPublicId(String publicId);
//
//    /**
//     * Get reels by property ID
//     * @param propertyId Property ID
//     * @param pageable Pagination info
//     * @return Page of reels
//     */
//    Page<ReelDto> getReelsByProperty(Long propertyId, Pageable pageable);
//
//    /**
//     * Get reels by user ID
//     * @param userId User ID
//     * @param pageable Pagination info
//     * @return Page of reels
//     */
//    Page<ReelDto> getReelsByUser(Long userId, Pageable pageable);
//
//    /**
//     * Update a reel's metadata
//     * @param reelId Reel ID
//     * @param reelDto Updated reel data
//     * @return Updated reel
//     */
//    ReelDto updateReel(Long reelId, ReelDto reelDto);
//
//    /**
//     * Delete a reel
//     * @param reelId Reel ID
//     */
//    void deleteReel(Long reelId);
//
//    /**
//     * Like a reel
//     * @param reelId Reel ID
//     * @return Updated reel
//     */
//    ReelDto likeReel(Long reelId);
//
//    /**
//     * Unlike a reel
//     * @param reelId Reel ID
//     * @return Updated reel
//     */
//    ReelDto unlikeReel(Long reelId);
//
//    /**
//     * Comment on a reel
//     * @param reelId Reel ID
//     * @param comment Comment text
//     * @return Updated reel
//     */
//    ReelDto commentReel(Long reelId, String comment);
//
//    /**
//     * Follow a reel
//     * @param reelId Reel ID
//     * @return Updated reel
//     */
//    ReelDto followReel(Long reelId);
//
//    /**
//     * Unfollow a reel
//     * @param reelId Reel ID
//     * @return Updated reel
//     */
//    ReelDto unfollowReel(Long reelId);
//
//    /**
//     * Record a view for a reel
//     * @param reelId Reel ID
//     * @return Updated reel
//     */
//    ReelDto viewReel(Long reelId);
//
//    /**
//     * Share a reel
//     * @param reelId Reel ID
//     * @return Updated reel
//     */
//    ReelDto shareReel(Long reelId);
//
//    /**
//     * Save a reel to user's bookmarks
//     * @param reelId Reel ID
//     * @return Updated reel
//     */
//    ReelDto saveReel(Long reelId);
//
//    /**
//     * Remove a reel from user's bookmarks
//     * @param reelId Reel ID
//     * @return Updated reel
//     */
//    ReelDto unsaveReel(Long reelId);
//
//    /**
//     * Get all reels saved by a user
//     * @param userId User ID
//     * @param pageable Pagination info
//     * @return Page of saved reels
//     */
//    Page<ReelDto> getSavedReelsByUser(Long userId, Pageable pageable);
//
//    /**
//     * Check if current user can upload more reels for a property
//     * @param propertyId Property ID
//     * @return True if user can upload more reels for this property
//     */
//    boolean canUploadMoreReels(Long propertyId);
//
//    /**
//     * Get a list of reels near a location
//     * @param latitude Latitude
//     * @param longitude Longitude
//     * @param radius Radius in km
//     * @param pageable Pagination info
//     * @return Page of reels
//     */
//    Page<ReelDto> getNearbyReels(Double latitude, Double longitude, Double radius, Pageable pageable);
//
//    /**
//     * Get a list of reels near a location, with fallback to general feed if not enough nearby reels
//     * @param latitude Latitude
//     * @param longitude Longitude
//     * @param radius Radius in km
//     * @param pageable Pagination info
//     * @return Page of reels, prioritizing nearby reels
//     */
//    Page<ReelDto> getReelsByLocationWithFallback(Double latitude, Double longitude, double radius, Pageable pageable);
//
//    /**
//     * Get reels by city
//     * @param city City name
//     * @param pageable Pagination info
//     * @return Page of reels
//     */
//    Page<ReelDto> getReelsByCity(String city, Pageable pageable);
//
//    /**
//     * Get reels by district
//     * @param district District name
//     * @param pageable Pagination info
//     * @return Page of reels
//     */
//    Page<ReelDto> getReelsByDistrict(String district, Pageable pageable);
//
//    /**
//     * Generate a shareable link for a reel
//     * @param reelId Reel ID
//     * @return Shareable link
//     */
//    String generateShareableLink(Long reelId);
//
//    /**
//     * Get the upload limits for a property
//     * @param propertyId Property ID
//     * @return Map containing upload limit information
//     */
//    Map<String, Object> checkUploadLimit(Long propertyId);
//
//    /**
//     * Get comments for a reel
//     * @param reelId Reel ID
//     * @param pageable Pagination info
//     * @return Page of comments
//     */
//    Page<ReelInteractionDto> getReelComments(Long reelId, Pageable pageable);
//
//    /**
//     * Get reels by location
//     * @param latitude Latitude
//     * @param longitude Longitude
//     * @param radiusKm Radius in km
//     * @param pageable Pagination info
//     * @return Page of reels
//     */
//    Page<ReelDto> getReelsByLocation(Double latitude, Double longitude, Double radiusKm, Pageable pageable);
//
//    /**
//     * Get a personalized feed of reels
//     * @param pageable Pagination info
//     * @return Page of reels
//     */
//    Page<ReelDto> getReelsFeed(Pageable pageable);
//
//    /**
//     * Initiate payment for publishing an additional reel
//     * @param reelId ID of the reel to be published
//     * @return Payment response with payment details
//     */
//    PaymentResponse initiateReelPayment(Long reelId);
//
//    /**
//     * Check if a reel is the first free reel for a property or needs payment
//     * @param propertyId Property ID
//     * @return True if the next reel requires payment
//     */
//    boolean isAdditionalReelPaymentRequired(Long propertyId);
//
//    /**
//     * Publish a reel after successful payment
//     * @param reelId Reel ID
//     * @param paymentReferenceId Payment reference ID
//     * @return Updated reel
//     */
//    ReelDto publishReelAfterPayment(Long reelId, String paymentReferenceId);
//
//    /**
//     * Get all nearby reels sorted by distance, with all details (no pagination)
//     */
//    List<ReelDto> getAllNearbyReelsWithFullDetails(Double latitude, Double longitude);
//
//    /**
//     * Delete all reels for a property (admin or property owner only)
//     * @param propertyId Property ID
//     * @param currentUser Current user
//     */
//    void deleteAllReelsOfProperty(Long propertyId, com.nearprop.entity.User currentUser);
//
//    /**
//     * Delete all reels and their interactions (admin only)
//     */
//    void deleteAllReelsAndInteractions();
//    void deleteComment(Long reelId, Long commentId, User currentUser);
//}


package com.nearprop.service;

import com.nearprop.dto.ReelDto;
import com.nearprop.dto.ReelInteractionDto;
import com.nearprop.dto.payment.PaymentResponse;
import com.nearprop.entity.Reel;
import com.nearprop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ReelService {

    // Core methods
    ReelDto uploadReel(ReelDto reelDto, MultipartFile videoFile);
    ReelDto getReel(Long reelId);
    ReelDto getReelByPublicId(String publicId);
    Page<ReelDto> getReelsByProperty(Long propertyId, Pageable pageable);
    Page<ReelDto> getReelsByUser(Long userId, Pageable pageable);
    ReelDto updateReel(Long reelId, ReelDto reelDto);
    void deleteReel(Long reelId);

    // Interactions
    ReelDto likeReel(Long reelId);
    ReelDto unlikeReel(Long reelId);
    ReelDto commentReel(Long reelId, String comment);
    void deleteComment(Long reelId, Long commentId, User currentUser);
    ReelDto followReel(Long reelId);
    ReelDto unfollowReel(Long reelId);
    ReelDto viewReel(Long reelId);
    ReelDto shareReel(Long reelId);
    ReelDto saveReel(Long reelId);
    ReelDto unsaveReel(Long reelId);

    // Discovery & Feed
    Page<ReelDto> getNearbyReels(Double latitude, Double longitude, Double radius, Pageable pageable);
    Page<ReelDto> getReelsByCity(String city, Pageable pageable);
    Page<ReelDto> getReelsByDistrict(String district, Pageable pageable);
    Page<ReelDto> getReelsFeed(Pageable pageable);
    Page<ReelDto> getReelsByLocation(Double latitude, Double longitude, Double radiusKm, Pageable pageable);
    Page<ReelDto> getReelsByLocationWithFallback(Double latitude, Double longitude, double radius, Pageable pageable);

    // Saved reels & comments
    Page<ReelDto> getSavedReelsByUser(Long userId, Pageable pageable);
    Page<ReelInteractionDto> getReelComments(Long reelId, Pageable pageable);

    // Utility
    boolean canUploadMoreReels(Long propertyId);
    Map<String, Object> checkUploadLimit(Long propertyId);
    String generateShareableLink(Long reelId);
    List<ReelDto> getAllNearbyReelsWithFullDetails(Double latitude, Double longitude);

    // Payment related
    boolean isAdditionalReelPaymentRequired(Long propertyId);
    PaymentResponse initiateReelPayment(Long reelId);
    ReelDto publishReelAfterPayment(Long reelId, String paymentReferenceId);

    // Bulk operations
    void deleteAllReelsOfProperty(Long propertyId, User currentUser);
    void deleteAllReelsAndInteractions();


    // ✅ ADMIN APPROVAL METHODS (NEW)
    ReelDto approveReel(Long reelId);
    ReelDto rejectReel(Long reelId, String reason);
    ReelDto updateReelStatus(Long reelId, Reel.ReelStatus status, String reason);

    Page<ReelDto> getPendingReels(Pageable pageable);
    Page<ReelDto> getReelsByStatus(Reel.ReelStatus status, Pageable pageable);
    Page<ReelDto> getPendingReelsByUser(Long userId, Pageable pageable);
    //Page<Reel> findByOwnerIdAndStatus(Long userId, Reel.ReelStatus status, Pageable pageable);



}