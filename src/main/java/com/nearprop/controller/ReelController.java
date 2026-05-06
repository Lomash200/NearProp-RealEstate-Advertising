//package com.nearprop.controller;
//
//import com.nearprop.dto.PropertyDto;
//import com.nearprop.dto.ReelDto;
//import com.nearprop.dto.ReelInteractionDto;
//import com.nearprop.dto.ResponseDto;
//import com.nearprop.dto.payment.PaymentResponse;
//import com.nearprop.dto.payment.PaymentVerificationRequest;
//import com.nearprop.entity.Reel;
//import com.nearprop.entity.User;
//import com.nearprop.service.PaymentService;
//import com.nearprop.service.PropertyService;
//import com.nearprop.service.ReelService;
//import com.nearprop.service.VideoProcessingService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/reels")
//@RequiredArgsConstructor
//@Slf4j
//public class ReelController {
//
//    private final ReelService reelService;
//    private final VideoProcessingService videoProcessingService;
//    private final PropertyService propertyService;
//    private final PaymentService paymentService;
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
//    public ResponseEntity<?> uploadReel(
//            @RequestPart("video") MultipartFile videoFile,
//            @RequestParam("title") String title,
//            @RequestParam("propertyId") Long propertyId) {
//
//        // Pre-validate the video file before processing
//        ResponseDto<Boolean> validationResult = videoProcessingService.validateVideoWithResponse(
//                videoFile, 60, 40, "mp4,mov,avi,webm");
//
//        if (!validationResult.isSuccess()) {
//            // Return validation error details
//            return ResponseEntity.badRequest().body(validationResult);
//        }
//
//        try {
//            ReelDto reelDto = new ReelDto();
//            reelDto.setTitle(title);
//            reelDto.setPropertyId(propertyId);
//
//            ReelDto createdReel = reelService.uploadReel(reelDto, videoFile);
//
//            // Check if payment is required for this reel
//            Map<String, Object> responseData = new HashMap<>();
//            responseData.put("reel", createdReel);
//
//            if (createdReel.getStatus() == Reel.ReelStatus.DRAFT) {
//                responseData.put("paymentRequired", true);
//                responseData.put("message", "Reel uploaded successfully. Payment is required to publish this reel.");
//            } else {
//                responseData.put("paymentRequired", false);
//                responseData.put("message", "Reel uploaded and published successfully.");
//            }
//
//            ResponseDto<Map<String, Object>> response = ResponseDto.<Map<String, Object>>builder()
//                    .success(true)
//                    .message(responseData.get("message").toString())
//                    .data(responseData)
//                    .build();
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        } catch (Exception e) {
//            log.error("Failed to upload reel: {}", e.getMessage(), e);
//
//            ResponseDto<Object> errorResponse = ResponseDto.builder()
//                    .success(false)
//                    .message("Failed to upload reel: " + e.getMessage())
//                    .build();
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }
//
//    // Added validation endpoint for testing video file validation
//    @PostMapping("/validate-video")
//    public ResponseEntity<ResponseDto<Boolean>> validateVideo(@RequestParam("video") MultipartFile videoFile) {
//        ResponseDto<Boolean> validationResult = videoProcessingService.validateVideoWithResponse(
//                videoFile, 60, 40, "mp4,mov,avi,webm");
//
//        return ResponseEntity.ok(validationResult);
//    }
//
//    @GetMapping("/{reelId}")
//    public ResponseEntity<ReelDto> getReel(@PathVariable Long reelId) {
//        ReelDto reel = reelService.getReel(reelId);
//        return ResponseEntity.ok(reel);
//    }
//
//    @GetMapping("/public/{publicId}")
//    public ResponseEntity<ReelDto> getReelByPublicId(@PathVariable String publicId) {
//        ReelDto reel = reelService.getReelByPublicId(publicId);
//        return ResponseEntity.ok(reel);
//    }
//
//    @GetMapping("/property/{propertyId}")
//    public ResponseEntity<Page<ReelDto>> getReelsByProperty(
//            @PathVariable Long propertyId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "DESC") String direction) {
//
//        Sort sort = direction.equalsIgnoreCase("ASC") ?
//                    Sort.by(sortBy).ascending() :
//                    Sort.by(sortBy).descending();
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//        Page<ReelDto> reels = reelService.getReelsByProperty(propertyId, pageable);
//
//        return ResponseEntity.ok(reels);
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<Page<ReelDto>> getReelsByUser(
//            @PathVariable Long userId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "DESC") String direction) {
//
//        Sort sort = direction.equalsIgnoreCase("ASC") ?
//                    Sort.by(sortBy).ascending() :
//                    Sort.by(sortBy).descending();
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//        Page<ReelDto> reels = reelService.getReelsByUser(userId, pageable);
//
//        return ResponseEntity.ok(reels);
//    }
//
//    @GetMapping("/saved")
//    public ResponseEntity<Page<ReelDto>> getSavedReels(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "DESC") String direction,
//            @AuthenticationPrincipal User currentUser) {
//
//        Sort sort = direction.equalsIgnoreCase("ASC") ?
//                    Sort.by(sortBy).ascending() :
//                    Sort.by(sortBy).descending();
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//        Page<ReelDto> reels = reelService.getSavedReelsByUser(currentUser.getId(), pageable);
//
//        return ResponseEntity.ok(reels);
//    }
//
//    @GetMapping("/city/{city}")
//    public ResponseEntity<Page<ReelDto>> getReelsByCity(
//            @PathVariable String city,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "DESC") String direction) {
//
//        Sort sort = direction.equalsIgnoreCase("ASC") ?
//                    Sort.by(sortBy).ascending() :
//                    Sort.by(sortBy).descending();
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//        Page<ReelDto> reels = reelService.getReelsByCity(city, pageable);
//
//        return ResponseEntity.ok(reels);
//    }
//
//    @GetMapping("/district/{district}")
//    public ResponseEntity<Page<ReelDto>> getReelsByDistrict(
//            @PathVariable String district,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "DESC") String direction) {
//
//        Sort sort = direction.equalsIgnoreCase("ASC") ?
//                    Sort.by(sortBy).ascending() :
//                    Sort.by(sortBy).descending();
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//        Page<ReelDto> reels = reelService.getReelsByDistrict(district, pageable);
//
//        return ResponseEntity.ok(reels);
//    }
//
//    @PutMapping("/{reelId}")
//    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
//    public ResponseEntity<ReelDto> updateReel(
//            @PathVariable Long reelId,
//            @RequestBody @Valid ReelDto reelDto) {
//
//        ReelDto updatedReel = reelService.updateReel(reelId, reelDto);
//        return ResponseEntity.ok(updatedReel);
//    }
//
//    @DeleteMapping("/{reelId}")
//    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
//    public ResponseEntity<Void> deleteReel(@PathVariable Long reelId) {
//        reelService.deleteReel(reelId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PostMapping("/{reelId}/like")
//    public ResponseEntity<ReelDto> likeReel(@PathVariable Long reelId) {
//        ReelDto reel = reelService.likeReel(reelId);
//        return ResponseEntity.ok(reel);
//    }
//
//    @DeleteMapping("/{reelId}/like")
//    public ResponseEntity<ReelDto> unlikeReel(@PathVariable Long reelId) {
//        ReelDto reel = reelService.unlikeReel(reelId);
//        return ResponseEntity.ok(reel);
//    }
//
//    @PostMapping(value = "/{reelId}/comment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ResponseDto<ReelDto>> commentReel(
//            @PathVariable Long reelId,
//            @RequestParam("message") String message) {
//
//        ReelDto reel = reelService.commentReel(reelId, message);
//
//        ResponseDto<ReelDto> response = ResponseDto.<ReelDto>builder()
//                .success(true)
//                .message("Comment added successfully")
//                .data(reel)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/{reelId}/follow")
//    public ResponseEntity<ReelDto> followReel(@PathVariable Long reelId) {
//        ReelDto reel = reelService.followReel(reelId);
//        return ResponseEntity.ok(reel);
//    }
//
//    @DeleteMapping("/{reelId}/follow")
//    public ResponseEntity<ReelDto> unfollowReel(@PathVariable Long reelId) {
//        ReelDto reel = reelService.unfollowReel(reelId);
//        return ResponseEntity.ok(reel);
//    }
//
//    @PostMapping("/{reelId}/save")
//    public ResponseEntity<ReelDto> saveReel(@PathVariable Long reelId) {
//        ReelDto reel = reelService.saveReel(reelId);
//        return ResponseEntity.ok(reel);
//    }
//
//    @DeleteMapping("/{reelId}/save")
//    public ResponseEntity<ReelDto> unsaveReel(@PathVariable Long reelId) {
//        ReelDto reel = reelService.unsaveReel(reelId);
//        return ResponseEntity.ok(reel);
//    }
//
//    @PostMapping("/{reelId}/view")
//    public ResponseEntity<ReelDto> viewReel(@PathVariable Long reelId) {
//        ReelDto reel = reelService.viewReel(reelId);
//        return ResponseEntity.ok(reel);
//    }
//
//    @GetMapping("/{reelId}/comments")
//    public ResponseEntity<ResponseDto<Page<ReelInteractionDto>>> getReelComments(
//            @PathVariable Long reelId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "DESC") String direction) {
//
//        Sort sort = direction.equalsIgnoreCase("ASC") ?
//                    Sort.by(sortBy).ascending() :
//                    Sort.by(sortBy).descending();
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//        Page<ReelInteractionDto> comments = reelService.getReelComments(reelId, pageable);
//
//        ResponseDto<Page<ReelInteractionDto>> response = ResponseDto.<Page<ReelInteractionDto>>builder()
//                .success(true)
//                .message("Comments retrieved successfully")
//                .data(comments)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @PostMapping("/{reelId}/share")
//    public ResponseEntity<ResponseDto<Map<String, Object>>> shareReel(
//            @PathVariable Long reelId,
//            @RequestParam(required = false) String platform) {
//
//        ReelDto reel = reelService.shareReel(reelId);
//        String shareableLink = reelService.generateShareableLink(reelId);
//
//        // Create a deep link format for app integration
//        String appDeepLink = "nearprop://reels/" + reel.getPublicId();
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("shareableLink", shareableLink);
//        result.put("appDeepLink", appDeepLink);
//        result.put("shareCount", reel.getShareCount());
//        result.put("publicId", reel.getPublicId());
//
//        ResponseDto<Map<String, Object>> response = ResponseDto.<Map<String, Object>>builder()
//                .success(true)
//                .message("Share link generated successfully")
//                .data(result)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/check-upload-limit")
//    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
//    public ResponseEntity<ResponseDto<Map<String, Object>>> checkUploadLimit(
//            @RequestParam Long propertyId) {
//
//        Map<String, Object> result = reelService.checkUploadLimit(propertyId);
//
//        ResponseDto<Map<String, Object>> response = ResponseDto.<Map<String, Object>>builder()
//                .success(true)
//                .message(result.get("paymentRequired").equals(true) ?
//                        "You can upload more reels" : "You can upload more reels for free")
//                .data(result)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/feed")
//    public ResponseEntity<ResponseDto<Page<ReelDto>>> getReelFeed(
//            @RequestParam(required = false) Double latitude,
//            @RequestParam(required = false) Double longitude,
//            @RequestParam(required = false) Double radiusKm,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//            @RequestParam(defaultValue = "DESC") String direction) {
//
//        Sort sort = direction.equalsIgnoreCase("ASC") ?
//                    Sort.by(sortBy).ascending() :
//                    Sort.by(sortBy).descending();
//
//        Pageable pageable = PageRequest.of(page, size, sort);
//
//        // Get reels feed - if coordinates provided, get nearby reels first, then other reels on scroll
//        Page<ReelDto> reels;
//        if (latitude != null && longitude != null) {
//            // If radius not provided, use default 10km
//            double radius = (radiusKm != null) ? radiusKm : 10.0;
//            reels = reelService.getReelsByLocationWithFallback(latitude, longitude, radius, pageable);
//        } else {
//            reels = reelService.getReelsFeed(pageable);
//        }
//
//        ResponseDto<Page<ReelDto>> response = ResponseDto.<Page<ReelDto>>builder()
//                .success(true)
//                .message("Reels feed retrieved successfully")
//                .data(reels)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    /**
//     * Enhance reels with property information
//     * @param reelsPage Page of reels
//     * @param currentUser Current user
//     * @return Enhanced page of reels
//     */
//    private Page<ReelDto> enhanceReelsWithPropertyData(Page<ReelDto> reelsPage, User currentUser) {
//        List<ReelDto> enhancedReels = reelsPage.getContent().stream()
//                .map(reel -> {
//                    // Fetch full property details and map to ReelDto
//                    try {
//                        PropertyDto property = propertyService.getProperty(reel.getPropertyId());
//                        reel.setProperty(property);
//                    } catch (Exception e) {
//                        log.warn("Could not fetch property details for reel {}: {}", reel.getId(), e.getMessage());
//                    }
//
//                    return reel;
//                }).collect(Collectors.toList());
//
//        return new PageImpl<>(enhancedReels, reelsPage.getPageable(), reelsPage.getTotalElements());
//    }
//
//    // Initiate payment for a reel
//    @PostMapping("/{reelId}/payment")
//    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
//    public ResponseEntity<ResponseDto<PaymentResponse>> initiateReelPayment(
//            @PathVariable Long reelId) {
//
//        PaymentResponse paymentResponse = reelService.initiateReelPayment(reelId);
//
//        ResponseDto<PaymentResponse> response = ResponseDto.<PaymentResponse>builder()
//                .success(true)
//                .message("Payment initiated successfully")
//                .data(paymentResponse)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    // Verify payment and publish reel
//    @PostMapping("/{reelId}/verify-payment")
//    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
//    public ResponseEntity<ResponseDto<ReelDto>> verifyReelPayment(
//            @PathVariable Long reelId,
//            @RequestBody @Valid PaymentVerificationRequest request) {
//
//        ReelDto publishedReel = reelService.publishReelAfterPayment(reelId, request.getReferenceId());
//
//        ResponseDto<ReelDto> response = ResponseDto.<ReelDto>builder()
//                .success(true)
//                .message("Payment verified and reel published successfully")
//                .data(publishedReel)
//                .build();
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/feed/nearby")
//    public ResponseEntity<ResponseDto<List<ReelDto>>> getNearbyReelsFull(
//            @RequestParam Double latitude,
//            @RequestParam Double longitude) {
//        List<ReelDto> reels = reelService.getAllNearbyReelsWithFullDetails(latitude, longitude);
//        ResponseDto<List<ReelDto>> response = ResponseDto.<List<ReelDto>>builder()
//                .success(true)
//                .message("Nearby reels retrieved successfully")
//                .data(reels)
//                .build();
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/property/{propertyId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'DEVELOPER', 'ADVISOR')")
//    public ResponseEntity<Void> deleteAllReelsOfProperty(@PathVariable Long propertyId, @AuthenticationPrincipal com.nearprop.entity.User currentUser) {
//        reelService.deleteAllReelsOfProperty(propertyId, currentUser);
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/admin/all")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deleteAllReelsAndInteractions() {
//        reelService.deleteAllReelsAndInteractions();
//        return ResponseEntity.noContent().build();
//    }
//
//    @DeleteMapping("/{reelId}/comment/{commentId}")
//    @PreAuthorize("hasAnyRole('SUBADMIN','ADMIN','USER','SELLER,'FRANCHISEE','DEVELOPER')")
//    public ResponseEntity<ResponseDto<String>> deleteComment(@PathVariable Long reelId, @PathVariable Long commentId,
//            @AuthenticationPrincipal User currentUser) {
//
//        reelService.deleteComment(reelId, commentId, currentUser);
//
//        ResponseDto<String> response = ResponseDto.<String>builder().success(true)
//                .message("Comment deleted successfully").build();
//
//        return ResponseEntity.ok(response);
//    }
//}

package com.nearprop.controller;

import com.nearprop.dto.*;
import com.nearprop.dto.payment.PaymentResponse;
import com.nearprop.dto.payment.PaymentVerificationRequest;
import com.nearprop.entity.Reel;
import com.nearprop.entity.User;
import com.nearprop.service.PaymentService;
import com.nearprop.service.PropertyService;
import com.nearprop.service.ReelService;
import com.nearprop.service.VideoProcessingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reels")
@RequiredArgsConstructor
@Slf4j
public class ReelController {

    private final ReelService reelService;
    private final VideoProcessingService videoProcessingService;
    private final PropertyService propertyService;
    private final PaymentService paymentService;

//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
//    public ResponseEntity<?> uploadReel(
//            @RequestPart("video") MultipartFile videoFile,
//            @RequestParam("title") String title,
//            @RequestParam("propertyId") Long propertyId) {
//
//        // Pre-validate the video file before processing
//        ResponseDto<Boolean> validationResult = videoProcessingService.validateVideoWithResponse(
//                videoFile, 60, 40, "mp4,mov,avi,webm");
//
//        if (!validationResult.isSuccess()) {
//            // Return validation error details
//            return ResponseEntity.badRequest().body(validationResult);
//        }
//
//        try {
//            ReelDto reelDto = new ReelDto();
//            reelDto.setTitle(title);
//            reelDto.setPropertyId(propertyId);
//
//            ReelDto createdReel = reelService.uploadReel(reelDto, videoFile);
//
//            // Check if payment is required for this reel
//            Map<String, Object> responseData = new HashMap<>();
//            responseData.put("reel", createdReel);
//
    ////            if (createdReel.getPaymentRequired() != null && createdReel.getPaymentRequired()) {
    ////                responseData.put("paymentRequired", true);
    ////                responseData.put("message", "Reel uploaded successfully. Payment is required to publish this reel.");
    ////            } else {
    ////                responseData.put("paymentRequired", false);
    ////                responseData.put("message", "Reel uploaded successfully. Waiting for admin approval.");
    ////            }
//
//            ResponseDto<Map<String, Object>> response = ResponseDto.<Map<String, Object>>builder()
//                    .success(true)
//                    .message(responseData.get("message").toString())
//                    .data(responseData)
//                    .build();
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        } catch (Exception e) {
//            log.error("Failed to upload reel: {}", e.getMessage(), e);
//
//            ResponseDto<Object> errorResponse = ResponseDto.builder()
//                    .success(false)
//                    .message("Failed to upload reel: " + e.getMessage())
//                    .build();
//
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<?> uploadReel(
            @RequestPart("video") MultipartFile videoFile,
            @RequestParam("title") String title,
            @RequestParam("propertyId") Long propertyId) {

        // 🔍 Pre-validate the video file
        ResponseDto<Boolean> validationResult =
                videoProcessingService.validateVideoWithResponse(
                        videoFile, 60, 40, "mp4,mov,avi,webm");

        if (!validationResult.isSuccess()) {
            return ResponseEntity.badRequest().body(validationResult);
        }

        try {
            // Prepare DTO
            ReelDto reelDto = new ReelDto();
            reelDto.setTitle(title);
            reelDto.setPropertyId(propertyId);

            // 🔥 ACTUAL SERVICE CALL
            ReelDto createdReel = reelService.uploadReel(reelDto, videoFile);

            // 🔥 FINAL RESPONSE (NO PAYMENT, NO DRAFT)
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("reel", createdReel);
            responseData.put("paymentRequired", false);

            ResponseDto<Map<String, Object>> response =
                    ResponseDto.<Map<String, Object>>builder()
                            .success(true)
                            .message("Reel uploaded successfully. Waiting for admin approval.")
                            .data(responseData)
                            .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Failed to upload reel: {}", e.getMessage(), e);

            ResponseDto<Object> errorResponse = ResponseDto.builder()
                    .success(false)
                    .message("Failed to upload reel: " + e.getMessage())
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorResponse);
        }
    }


    // Added validation endpoint for testing video file validation
    @PostMapping("/validate-video")
    public ResponseEntity<ResponseDto<Boolean>> validateVideo(@RequestParam("video") MultipartFile videoFile) {
        ResponseDto<Boolean> validationResult = videoProcessingService.validateVideoWithResponse(
                videoFile, 60, 40, "mp4,mov,avi,webm");

        return ResponseEntity.ok(validationResult);
    }

    @GetMapping("/{reelId}")
    public ResponseEntity<ReelDto> getReel(@PathVariable Long reelId) {
        ReelDto reel = reelService.getReel(reelId);
        return ResponseEntity.ok(reel);
    }

    @GetMapping("/public/{publicId}")
    public ResponseEntity<ReelDto> getReelByPublicId(@PathVariable String publicId) {
        ReelDto reel = reelService.getReelByPublicId(publicId);
        return ResponseEntity.ok(reel);
    }

    @GetMapping("/property/{propertyId}")
    public ResponseEntity<Page<ReelDto>> getReelsByProperty(
            @PathVariable Long propertyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReelDto> reels = reelService.getReelsByProperty(propertyId, pageable);

        return ResponseEntity.ok(reels);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReelDto>> getReelsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReelDto> reels = reelService.getReelsByUser(userId, pageable);

        return ResponseEntity.ok(reels);
    }

    @GetMapping("/saved")
    public ResponseEntity<Page<ReelDto>> getSavedReels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @AuthenticationPrincipal User currentUser) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReelDto> reels = reelService.getSavedReelsByUser(currentUser.getId(), pageable);

        return ResponseEntity.ok(reels);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<Page<ReelDto>> getReelsByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReelDto> reels = reelService.getReelsByCity(city, pageable);

        return ResponseEntity.ok(reels);
    }

    @GetMapping("/district/{district}")
    public ResponseEntity<Page<ReelDto>> getReelsByDistrict(
            @PathVariable String district,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReelDto> reels = reelService.getReelsByDistrict(district, pageable);

        return ResponseEntity.ok(reels);
    }

    @PutMapping("/{reelId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<ReelDto> updateReel(
            @PathVariable Long reelId,
            @RequestBody @Valid ReelDto reelDto) {

        ReelDto updatedReel = reelService.updateReel(reelId, reelDto);
        return ResponseEntity.ok(updatedReel);
    }

    @PutMapping("/admin/{reelId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateReelStatus(
            @PathVariable Long reelId,
            @RequestBody ReelStatusUpdateRequest request) {

        return ResponseEntity.ok(
                reelService.updateReelStatus(
                        reelId,
                        request.getStatus(),
                        request.getReason()
                )
        );
    }

    @DeleteMapping("/{reelId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<Void> deleteReel(@PathVariable Long reelId) {
        reelService.deleteReel(reelId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reelId}/like")
    public ResponseEntity<ReelDto> likeReel(@PathVariable Long reelId) {
        ReelDto reel = reelService.likeReel(reelId);
        return ResponseEntity.ok(reel);
    }

    @DeleteMapping("/{reelId}/like")
    public ResponseEntity<ReelDto> unlikeReel(@PathVariable Long reelId) {
        ReelDto reel = reelService.unlikeReel(reelId);
        return ResponseEntity.ok(reel);
    }

    @PostMapping(value = "/{reelId}/comment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto<ReelDto>> commentReel(
            @PathVariable Long reelId,
            @RequestParam("message") String message) {

        ReelDto reel = reelService.commentReel(reelId, message);

        ResponseDto<ReelDto> response = ResponseDto.<ReelDto>builder()
                .success(true)
                .message("Comment added successfully")
                .data(reel)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reelId}/follow")
    public ResponseEntity<ReelDto> followReel(@PathVariable Long reelId) {
        ReelDto reel = reelService.followReel(reelId);
        return ResponseEntity.ok(reel);
    }

    @DeleteMapping("/{reelId}/follow")
    public ResponseEntity<ReelDto> unfollowReel(@PathVariable Long reelId) {
        ReelDto reel = reelService.unfollowReel(reelId);
        return ResponseEntity.ok(reel);
    }

    @PostMapping("/{reelId}/save")
    public ResponseEntity<ReelDto> saveReel(@PathVariable Long reelId) {
        ReelDto reel = reelService.saveReel(reelId);
        return ResponseEntity.ok(reel);
    }

    @DeleteMapping("/{reelId}/save")
    public ResponseEntity<ReelDto> unsaveReel(@PathVariable Long reelId) {
        ReelDto reel = reelService.unsaveReel(reelId);
        return ResponseEntity.ok(reel);
    }

    @PostMapping("/{reelId}/view")
    public ResponseEntity<ReelDto> viewReel(@PathVariable Long reelId) {
        ReelDto reel = reelService.viewReel(reelId);
        return ResponseEntity.ok(reel);
    }

    @GetMapping("/{reelId}/comments")
    public ResponseEntity<ResponseDto<Page<ReelInteractionDto>>> getReelComments(
            @PathVariable Long reelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReelInteractionDto> comments = reelService.getReelComments(reelId, pageable);

        ResponseDto<Page<ReelInteractionDto>> response = ResponseDto.<Page<ReelInteractionDto>>builder()
                .success(true)
                .message("Comments retrieved successfully")
                .data(comments)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{reelId}/share")
    public ResponseEntity<ResponseDto<Map<String, Object>>> shareReel(
            @PathVariable Long reelId,
            @RequestParam(required = false) String platform) {

        ReelDto reel = reelService.shareReel(reelId);
        String shareableLink = reelService.generateShareableLink(reelId);

        // Create a deep link format for app integration
        String appDeepLink = "nearprop://reels/" + reel.getPublicId();

        Map<String, Object> result = new HashMap<>();
        result.put("shareableLink", shareableLink);
        result.put("appDeepLink", appDeepLink);
        result.put("shareCount", reel.getShareCount());
        result.put("publicId", reel.getPublicId());

        ResponseDto<Map<String, Object>> response = ResponseDto.<Map<String, Object>>builder()
                .success(true)
                .message("Share link generated successfully")
                .data(result)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-upload-limit")
    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<ResponseDto<Map<String, Object>>> checkUploadLimit(
            @RequestParam Long propertyId) {

        Map<String, Object> result = reelService.checkUploadLimit(propertyId);

        ResponseDto<Map<String, Object>> response = ResponseDto.<Map<String, Object>>builder()
                .success(true)
                .message(result.get("paymentRequired").equals(true) ?
                        "You can upload more reels" : "You can upload more reels for free")
                .data(result)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/pending/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReelDto>> getPendingReelsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(
                reelService.getPendingReelsByUser(userId, pageable)
        );
    }

    @GetMapping("/feed")
    public ResponseEntity<ResponseDto<Page<ReelDto>>> getReelFeed(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radiusKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // Get reels feed - if coordinates provided, get nearby reels first, then other reels on scroll
        Page<ReelDto> reels;
        if (latitude != null && longitude != null) {
            // If radius not provided, use default 10km
            double radius = (radiusKm != null) ? radiusKm : 10.0;
            reels = reelService.getReelsByLocationWithFallback(latitude, longitude, radius, pageable);
        } else {
            reels = reelService.getReelsFeed(pageable);
        }

        ResponseDto<Page<ReelDto>> response = ResponseDto.<Page<ReelDto>>builder()
                .success(true)
                .message("Reels feed retrieved successfully")
                .data(reels)
                .build();

        return ResponseEntity.ok(response);
    }

    /**
     * Enhance reels with property information
     * @param reelsPage Page of reels
     * @param currentUser Current user
     * @return Enhanced page of reels
     */
    private Page<ReelDto> enhanceReelsWithPropertyData(Page<ReelDto> reelsPage, User currentUser) {
        List<ReelDto> enhancedReels = reelsPage.getContent().stream()
                .map(reel -> {
                    // Fetch full property details and map to ReelDto
                    try {
                        PropertyDto property = propertyService.getProperty(reel.getPropertyId());
                        reel.setProperty(property);
                    } catch (Exception e) {
                        log.warn("Could not fetch property details for reel {}: {}", reel.getId(), e.getMessage());
                    }

                    return reel;
                }).collect(Collectors.toList());

        return new PageImpl<>(enhancedReels, reelsPage.getPageable(), reelsPage.getTotalElements());
    }

    // Initiate payment for a reel
    @PostMapping("/{reelId}/payment")
    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<ResponseDto<PaymentResponse>> initiateReelPayment(
            @PathVariable Long reelId) {

        PaymentResponse paymentResponse = reelService.initiateReelPayment(reelId);

        ResponseDto<PaymentResponse> response = ResponseDto.<PaymentResponse>builder()
                .success(true)
                .message("Payment initiated successfully")
                .data(paymentResponse)
                .build();

        return ResponseEntity.ok(response);
    }

    // Verify payment and publish reel
    @PostMapping("/{reelId}/verify-payment")
    @PreAuthorize("hasAnyRole('SELLER', 'ADVISOR', 'DEVELOPER', 'ADMIN')")
    public ResponseEntity<ResponseDto<ReelDto>> verifyReelPayment(
            @PathVariable Long reelId,
            @RequestBody @Valid PaymentVerificationRequest request) {

        ReelDto publishedReel = reelService.publishReelAfterPayment(reelId, request.getReferenceId());

        ResponseDto<ReelDto> response = ResponseDto.<ReelDto>builder()
                .success(true)
                .message("Payment verified and reel submitted for admin approval")
                .data(publishedReel)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/feed/nearby")
    public ResponseEntity<ResponseDto<List<ReelDto>>> getNearbyReelsFull(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        List<ReelDto> reels = reelService.getAllNearbyReelsWithFullDetails(latitude, longitude);
        ResponseDto<List<ReelDto>> response = ResponseDto.<List<ReelDto>>builder()
                .success(true)
                .message("Nearby reels retrieved successfully")
                .data(reels)
                .build();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/property/{propertyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'DEVELOPER', 'ADVISOR')")
    public ResponseEntity<Void> deleteAllReelsOfProperty(@PathVariable Long propertyId, @AuthenticationPrincipal com.nearprop.entity.User currentUser) {
        reelService.deleteAllReelsOfProperty(propertyId, currentUser);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAllReelsAndInteractions() {
        reelService.deleteAllReelsAndInteractions();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{reelId}/comment/{commentId}")
    @PreAuthorize("hasAnyRole('SUBADMIN','ADMIN','USER','SELLER','FRANCHISEE','DEVELOPER')")
    public ResponseEntity<ResponseDto<String>> deleteComment(@PathVariable Long reelId, @PathVariable Long commentId,
                                                             @AuthenticationPrincipal User currentUser) {

        reelService.deleteComment(reelId, commentId, currentUser);

        ResponseDto<String> response = ResponseDto.<String>builder().success(true)
                .message("Comment deleted successfully").build();

        return ResponseEntity.ok(response);
    }

    /* ================= ADMIN APPROVAL ENDPOINTS ================= */

    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<Page<ReelDto>>> getPendingReels(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReelDto> pendingReels = reelService.getPendingReels(pageable);

        ResponseDto<Page<ReelDto>> response = ResponseDto.<Page<ReelDto>>builder()
                .success(true)
                .message("Pending reels retrieved successfully")
                .data(pendingReels)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<Page<ReelDto>>> getReelsByStatus(
            @PathVariable Reel.ReelStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ReelDto> reels = reelService.getReelsByStatus(status, pageable);

        ResponseDto<Page<ReelDto>> response = ResponseDto.<Page<ReelDto>>builder()
                .success(true)
                .message("Reels retrieved by status successfully")
                .data(reels)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/{reelId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<ReelDto>> approveReel(@PathVariable Long reelId) {
        ReelDto approvedReel = reelService.approveReel(reelId);

        ResponseDto<ReelDto> response = ResponseDto.<ReelDto>builder()
                .success(true)
                .message("Reel approved successfully")
                .data(approvedReel)
                .build();

        return ResponseEntity.ok(response);
    }




    @PostMapping("/admin/{reelId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseDto<ReelDto>> rejectReel(
            @PathVariable Long reelId,
            @RequestBody(required = false) Map<String, String> request) {

        String reason = request != null ? request.getOrDefault("reason", "No reason provided") : "No reason provided";
        ReelDto rejectedReel = reelService.rejectReel(reelId, reason);

        ResponseDto<ReelDto> response = ResponseDto.<ReelDto>builder()
                .success(true)
                .message("Reel rejected successfully")
                .data(rejectedReel)
                .build();

        return ResponseEntity.ok(response);
    }
}