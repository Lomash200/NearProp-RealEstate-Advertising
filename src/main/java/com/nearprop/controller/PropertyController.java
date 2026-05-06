package com.nearprop.controller;

import com.nearprop.advertisement.dto.AdvertisementDto;
import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.CreatePropertyDto;
import com.nearprop.dto.PropertyDto;
import com.nearprop.dto.PropertyFormDto;
import com.nearprop.dto.DeveloperPropertyFormDto;
import com.nearprop.entity.PropertyStatus;
import com.nearprop.entity.PropertyType;
import com.nearprop.entity.User;
import com.nearprop.entity.Role;
import com.nearprop.advertisement.service.PropertyAdvertisementService;
import com.nearprop.service.PropertyService;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/properties")
@RequiredArgsConstructor
@Slf4j
public class PropertyController {

    private final PropertyService propertyService;
    private final PropertyAdvertisementService propertyAdvertisementService;
    private final SubscriptionService subscriptionService;

    @GetMapping("/dummy-user")
    public ResponseEntity<?> dummyUserApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADVISOR', 'SELLER', 'ADMIN')")
    public ResponseEntity<PropertyDto> createProperty(
            @Valid @RequestBody CreatePropertyDto propertyDto,
            @AuthenticationPrincipal User currentUser) {
        PropertyDto createdProperty = propertyService.createProperty(propertyDto, currentUser.getId());
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

    @PostMapping(value = "/form", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADVISOR', 'SELLER', 'ADMIN','DEVELOPER')")
    public ResponseEntity<PropertyDto> createPropertyWithForm(
            @Valid @ModelAttribute PropertyFormDto propertyFormDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "video", required = false) MultipartFile videoFile,
            @AuthenticationPrincipal User currentUser) {
        PropertyDto createdProperty = propertyService.createPropertyFromForm(propertyFormDto, images, videoFile, currentUser.getId());
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

    @PostMapping(value = "/developer-form", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<PropertyDto> createDeveloperPropertyWithForm(
            @Valid @ModelAttribute DeveloperPropertyFormDto propertyFormDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "video", required = false) MultipartFile videoFile,
            @AuthenticationPrincipal User currentUser) {

        if (!currentUser.getRoles().contains(Role.DEVELOPER)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        PropertyDto createdProperty = propertyService.createDeveloperPropertyFromForm(propertyFormDto, images, videoFile, currentUser.getId());
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADVISOR', 'SELLER', 'ADMIN')")
    public ResponseEntity<PropertyDto> updateProperty(
            @PathVariable("id") Long propertyId,
            @Valid @RequestBody CreatePropertyDto propertyDto,
            @AuthenticationPrincipal User currentUser) {
        PropertyDto updatedProperty = propertyService.updateProperty(propertyId, propertyDto, currentUser.getId());
        return ResponseEntity.ok(updatedProperty);
    }

    @PutMapping(value = "/{id}/form", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADVISOR', 'SELLER', 'ADMIN')")
    public ResponseEntity<PropertyDto> updatePropertyWithForm(
            @PathVariable("id") Long propertyId,
            @Valid @ModelAttribute PropertyFormDto propertyFormDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "video", required = false) MultipartFile videoFile,
            @AuthenticationPrincipal User currentUser) {
        PropertyDto updatedProperty = propertyService.updatePropertyFromForm(propertyId, propertyFormDto, images, videoFile, currentUser.getId());
        return ResponseEntity.ok(updatedProperty);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDto> getProperty(
            @PathVariable("id") Long propertyId,
            @AuthenticationPrincipal User currentUser) {
        PropertyDto property = propertyService.getProperty(propertyId);

        if (property.getRequiresSpecialAccess() != null && property.getRequiresSpecialAccess()) {
            boolean isOwner = currentUser != null && property.getOwner() != null &&
                    currentUser.getId().equals(property.getOwner().getId());
            boolean isAdmin = currentUser != null && currentUser.getRoles().contains(Role.ADMIN);

            if (!isOwner && !isAdmin) {
                throw new ResourceNotFoundException("Property not found with id: " + propertyId);
            }
        }
        return ResponseEntity.ok(property);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getAllProperties(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radius) {
        List<PropertyDto> properties = propertyService.getAllPropertiesWithoutPagination(latitude, longitude, radius);
        return ResponseEntity.ok(ApiResponse.success("Properties retrieved successfully", properties));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getFeaturedProperties() {
        List<PropertyDto> properties = propertyService.getFeaturedPropertiesWithoutPagination();
        return ResponseEntity.ok(ApiResponse.success("Featured properties retrieved successfully", properties));
    }

    @GetMapping("/my-properties")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getUserProperties(
            @AuthenticationPrincipal User currentUser) {
        List<PropertyDto> properties = propertyService.getUserPropertiesWithoutPagination(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("User properties retrieved successfully", properties));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> searchProperties(
            @RequestParam(required = false) PropertyType type,
            @RequestParam(required = false) PropertyStatus status,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minBedrooms,
            @RequestParam(required = false) Long ownerId
    ) {
        // 👇 POORA SAFE BLOCK
        if (ownerId != null) {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            Long currentUserId = null;
            boolean isAdminOrSubAdmin = false;

            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
                User currentUser = (User) auth.getPrincipal();
                currentUserId = currentUser.getId();

                isAdminOrSubAdmin = auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(role ->
                                role.equals("ROLE_ADMIN") ||
                                        role.equals("ROLE_SUBADMIN")
                        );
            }

            boolean isOwner = currentUserId != null && currentUserId.equals(ownerId);

            // 🔐 ONLY USER needs PROFILE subscription
            if (!isAdminOrSubAdmin && !isOwner) {
                boolean hasProfileSubscription =
                        subscriptionService.hasActiveProfileSubscription(currentUserId);

                if (!hasProfileSubscription) {
                    return ResponseEntity.ok(
                            ApiResponse.success(
                                    "Subscribe to view property listings",
                                    List.of()
                            )
                    );
                }
            }
        }

        // ✅ NORMAL SEARCH FLOW
        List<PropertyDto> properties = propertyService.searchPropertiesWithoutPagination(
                type, status, district, minPrice, maxPrice, minBedrooms
        );

        return ResponseEntity.ok(ApiResponse.success("Search results retrieved successfully", properties));
    }

    @DeleteMapping("/{propertyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse> deleteProperty(
            @PathVariable Long propertyId,
            @AuthenticationPrincipal User currentUser) {
        propertyService.deleteProperty(propertyId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Property deleted successfully"));
    }

    @DeleteMapping("/admin/{propertyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> adminDeleteProperty(@PathVariable Long propertyId) {
        try {
            boolean deleted = propertyService.adminDeleteProperty(propertyId);
            if (deleted) {
                Map<String, Object> data = new HashMap<>();
                data.put("propertyId", propertyId);
                data.put("deleted", true);
                return ResponseEntity.ok(ApiResponse.success("Property deleted successfully by admin", data));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(ApiResponse.error("Failed to delete property. Dependencies exist."));
            }
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/{propertyId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyDto> approveProperty(@PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.approveProperty(propertyId));
    }

    @PutMapping("/{propertyId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyDto> rejectProperty(@PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.rejectProperty(propertyId));
    }

    @PutMapping("/{propertyId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyDto> deactivateProperty(@PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.deactivateProperty(propertyId));
    }

    @PutMapping("/{propertyId}/block")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyDto> blockProperty(
            @PathVariable Long propertyId,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(propertyService.blockProperty(propertyId, reason));
    }

    @PutMapping("/{propertyId}/unblock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyDto> unblockProperty(@PathVariable Long propertyId) {
        return ResponseEntity.ok(propertyService.unblockProperty(propertyId));
    }

    @PutMapping("/{propertyId}/featured")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PropertyDto> markFeatured(
            @PathVariable Long propertyId,
            @RequestParam boolean featured) {
        return ResponseEntity.ok(propertyService.markFeatured(propertyId, featured));
    }

    @GetMapping("/districts")
    public ResponseEntity<List<String>> getAllDistricts() {
        return ResponseEntity.ok(propertyService.getAllDistricts());
    }

    @GetMapping("/stats/by-type")
    public ResponseEntity<Map<PropertyType, Long>> getPropertyCountByType() {
        return ResponseEntity.ok(propertyService.getPropertyCountByType());
    }

    @GetMapping("/stats/by-district")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getPropertyCountByDistrict() {
        return ResponseEntity.ok(propertyService.getPropertyCountByDistrict());
    }


    @GetMapping("/admin/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getPendingApprovalProperties() {
        List<PropertyDto> properties = propertyService.getPendingApprovalPropertiesWithoutPagination();
        return ResponseEntity.ok(ApiResponse.success("Pending properties retrieved successfully", properties));
    }

    @GetMapping("/{id}/advertisements")
    public ResponseEntity<List<AdvertisementDto>> getAdvertisementsForProperty(
            @PathVariable("id") Long propertyId,
            @RequestParam(defaultValue = "3") int limit) {
        return ResponseEntity.ok(propertyAdvertisementService.getAdvertisementsForProperty(propertyId, limit));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADVISOR', 'SELLER', 'ADMIN')")
    public ResponseEntity<PropertyDto> activateProperty(
            @PathVariable("id") Long propertyId,
            @RequestParam("subscriptionId") Long subscriptionId,
            @RequestParam("expiryDate") String expiryDateStr,
            @AuthenticationPrincipal User currentUser) {

        LocalDateTime expiryDate = LocalDateTime.parse(expiryDateStr);
        return ResponseEntity.ok(propertyService.activateProperty(propertyId, currentUser.getId(), subscriptionId, expiryDate));
    }

    @GetMapping("/by-id/{permanentId}")
    public ResponseEntity<PropertyDto> getPropertyByPermanentId(
            @PathVariable("permanentId") String permanentId,
            @AuthenticationPrincipal User currentUser) {
        PropertyDto property = propertyService.getPropertyByPermanentId(permanentId);

        if (property.getRequiresSpecialAccess() != null && property.getRequiresSpecialAccess()) {
            boolean isOwner = currentUser != null && property.getOwner() != null &&
                    currentUser.getId().equals(property.getOwner().getId());
            boolean isAdmin = currentUser != null && currentUser.getRoles().contains(Role.ADMIN);

            if (!isOwner && !isAdmin) {
                throw new ResourceNotFoundException("Property not found with permanent ID: " + permanentId);
            }
        }
        return ResponseEntity.ok(property);
    }

    @PutMapping("/{id}/activate-with-subscription")
    @PreAuthorize("hasAnyRole('ADVISOR', 'SELLER', 'ADMIN')")
    public ResponseEntity<PropertyDto> activatePropertyWithSubscription(
            @PathVariable("id") Long propertyId,
            @RequestParam("subscriptionId") Long subscriptionId) {
        return ResponseEntity.ok(propertyService.activatePropertyWithSubscription(propertyId, subscriptionId));
    }

    @PutMapping("/{id}/update-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'DEVELOPER')")
    public ResponseEntity<PropertyDto> updatePropertyStock(
            @PathVariable Long id,
            @RequestParam Integer stock,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(propertyService.updatePropertyStock(id, stock, currentUser.getId()));
    }

    @PutMapping("/{id}/update-developer-details")
    @PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')")
    public ResponseEntity<PropertyDto> updateDeveloperPropertyDetails(
            @PathVariable Long id,
            @RequestParam(required = false) String unitType,
            @RequestParam(required = false) Integer unitCount,
            @RequestParam(required = false) Integer stock,
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(propertyService.updateDeveloperPropertyDetails(id, unitType, unitCount, stock, currentUser.getId()));
    }
}