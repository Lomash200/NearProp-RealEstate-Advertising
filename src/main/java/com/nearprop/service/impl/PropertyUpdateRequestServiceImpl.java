package com.nearprop.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nearprop.dto.CreatePropertyUpdateRequestDto;
import com.nearprop.dto.PropertyUpdateRequestDto;
import com.nearprop.dto.ReviewPropertyUpdateRequestDto;
import com.nearprop.dto.ReviewPropertyUpdateRequestDto.ReviewerType;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.entity.Property;
import com.nearprop.entity.PropertyUpdateRequest;
import com.nearprop.entity.PropertyUpdateRequest.RequestStatus;
import com.nearprop.entity.Role;
import com.nearprop.entity.User;
import com.nearprop.exception.BadRequestException;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.exception.UnauthorizedException;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.repository.PropertyUpdateRequestRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.PropertyService;
import com.nearprop.service.PropertyUpdateRequestService;
import com.nearprop.service.S3Service;
import com.nearprop.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyUpdateRequestServiceImpl implements PropertyUpdateRequestService {

    private final PropertyUpdateRequestRepository updateRequestRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyService propertyService;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;
    
    @Override
    @Transactional
    public PropertyUpdateRequestDto createUpdateRequest(
            CreatePropertyUpdateRequestDto requestDto, 
            List<MultipartFile> images,
            MultipartFile videoFile,
            Long userId) {
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        Property property = propertyRepository.findById(requestDto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + requestDto.getPropertyId()));
        
        // Verify ownership
        if (!property.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You can only update properties you own");
        }
        
        // Check if there's already a pending update request for this property
        List<PropertyUpdateRequest> pendingRequests = updateRequestRepository.findByPropertyIdAndStatus(
                property.getId(), RequestStatus.PENDING);
        
        if (!pendingRequests.isEmpty()) {
            throw new BadRequestException("There is already a pending update request for this property. " +
                    "Please wait for it to be processed or cancel it before creating a new one.");
        }
        
        // Create new update request
        PropertyUpdateRequest updateRequest = PropertyUpdateRequest.builder()
                .requestId(IdGenerator.generatePropertyUpdateRequestId())
                .property(property)
                .requestedBy(user)
                .status(RequestStatus.PENDING)
                .requestNotes(requestDto.getRequestNotes())
                .franchiseeRequest(false)
                .district(property.getDistrictValue())
                .adminReviewed(false)
                .franchiseeReviewed(false)
                .build();
        
        // Extract and store old and new values
        Map<String, String> oldValues = new HashMap<>();
        Map<String, String> newValues = new HashMap<>();
        
        // Process fields that can be updated
        processFieldChanges(property, requestDto, oldValues, newValues);
        
        // Handle file uploads if needed
        handleFileUploads(images, videoFile, property, oldValues, newValues);
        
        // Set the maps
        updateRequest.setOldValues(oldValues);
        updateRequest.setNewValues(newValues);
        
        // Save the request
        PropertyUpdateRequest savedRequest = updateRequestRepository.save(updateRequest);
        
        return mapToDto(savedRequest);
    }
    
    @Override
    @Transactional
    public PropertyUpdateRequestDto createUpdateRequestOnBehalf(
            String ownerPermanentId,
            CreatePropertyUpdateRequestDto requestDto, 
            List<MultipartFile> images,
            MultipartFile videoFile,
            Long franchiseeId) {
        
        User franchisee = userRepository.findById(franchiseeId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchisee not found with ID: " + franchiseeId));
        
        // Verify franchisee role
        if (!franchisee.getRoles().contains(Role.FRANCHISEE)) {
            throw new UnauthorizedException("Only franchisees can create update requests on behalf of property owners");
        }
        
        User owner = userRepository.findByPermanentId(ownerPermanentId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with permanent ID: " + ownerPermanentId));
        
        Property property = propertyRepository.findById(requestDto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + requestDto.getPropertyId()));
        
        // Verify ownership
        if (!property.getOwner().getId().equals(owner.getId())) {
            throw new UnauthorizedException("The specified user is not the owner of this property");
        }
        
        // Check if there's already a pending update request for this property
        List<PropertyUpdateRequest> pendingRequests = updateRequestRepository.findByPropertyIdAndStatus(
                property.getId(), RequestStatus.PENDING);
        
        if (!pendingRequests.isEmpty()) {
            throw new BadRequestException("There is already a pending update request for this property. " +
                    "Please wait for it to be processed or cancel it before creating a new one.");
        }
        
        // Create new update request
        PropertyUpdateRequest updateRequest = PropertyUpdateRequest.builder()
                .requestId(IdGenerator.generatePropertyUpdateRequestId())
                .property(property)
                .requestedBy(owner)
                .status(RequestStatus.PENDING)
                .requestNotes(requestDto.getRequestNotes())
                .franchiseeRequest(true)
                .franchisee(franchisee)
                .district(property.getDistrictValue())
                .adminReviewed(false)
                .franchiseeReviewed(false)
                .build();
        
        // Extract and store old and new values
        Map<String, String> oldValues = new HashMap<>();
        Map<String, String> newValues = new HashMap<>();
        
        // Process fields that can be updated
        processFieldChanges(property, requestDto, oldValues, newValues);
        
        // Handle file uploads if needed
        handleFileUploads(images, videoFile, property, oldValues, newValues);
        
        // Set the maps
        updateRequest.setOldValues(oldValues);
        updateRequest.setNewValues(newValues);
        
        // Save the request
        PropertyUpdateRequest savedRequest = updateRequestRepository.save(updateRequest);
        
        return mapToDto(savedRequest);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PropertyUpdateRequestDto getUpdateRequest(Long requestId) {
        PropertyUpdateRequest updateRequest = updateRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Update request not found with ID: " + requestId));
        
        return mapToDto(updateRequest);
    }
    
    @Override
    @Transactional(readOnly = true)
    public PropertyUpdateRequestDto getUpdateRequestByPermanentId(String requestId) {
        PropertyUpdateRequest updateRequest = updateRequestRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Update request not found with ID: " + requestId));
        
        return mapToDto(updateRequest);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PropertyUpdateRequestDto> getUpdateRequestsByProperty(Long propertyId, Pageable pageable) {
        return updateRequestRepository.findByPropertyId(propertyId, pageable)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PropertyUpdateRequestDto> getUpdateRequestsByUser(Long userId, Pageable pageable) {
        return updateRequestRepository.findByRequestedById(userId, pageable)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PropertyUpdateRequestDto> getUpdateRequestsByStatus(RequestStatus status, Pageable pageable) {
        return updateRequestRepository.findByStatus(status, pageable)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PropertyUpdateRequestDto> getUpdateRequestsByDistrictAndStatus(
            String district, RequestStatus status, Pageable pageable) {
        return updateRequestRepository.findByDistrictAndStatus(district, status, pageable)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PropertyUpdateRequestDto> getUpdateRequestsByDistrict(String district, Pageable pageable) {
        return updateRequestRepository.findByDistrict(district, pageable)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PropertyUpdateRequestDto> getPendingRequestsForFranchiseeReview(String district, Pageable pageable) {
        return updateRequestRepository.findByDistrictAndFranchiseeReviewedFalseAndStatus(
                district, RequestStatus.PENDING, pageable)
                .map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<PropertyUpdateRequestDto> getPendingRequestsForAdminReview(Pageable pageable) {
        return updateRequestRepository.findByAdminReviewedFalseAndStatus(RequestStatus.PENDING, pageable)
                .map(this::mapToDto);
    }
    
//    @Override
//    @Transactional
//    public PropertyUpdateRequestDto reviewUpdateRequest(ReviewPropertyUpdateRequestDto reviewDto, Long reviewerId) {
//        User reviewer = userRepository.findById(reviewerId)
//                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found with ID: " + reviewerId));
//
//        PropertyUpdateRequest updateRequest = updateRequestRepository.findById(reviewDto.getRequestId())
//                .orElseThrow(() -> new ResourceNotFoundException("Update request not found with ID: " + reviewDto.getRequestId()));
//
//        // Verify request is pending
//        if (updateRequest.getStatus() != RequestStatus.PENDING) {
//            throw new BadRequestException("Only pending requests can be reviewed");
//        }
//
//        // Verify reviewer role and permissions
//        if (reviewDto.getReviewerType() == ReviewerType.ADMIN) {
//            // Admin review
//            if (!reviewer.getRoles().contains(Role.ADMIN)) {
//                throw new UnauthorizedException("Only admins can review as admin");
//            }
//
//            // Check if already reviewed by admin
//            if (updateRequest.isAdminReviewed()) {
//                throw new BadRequestException("This request has already been reviewed by an admin");
//            }
//
//            // Update admin review fields
//            updateRequest.setReviewedByAdmin(reviewer);
//            updateRequest.setAdminReviewedAt(LocalDateTime.now());
//            updateRequest.setAdminNotes(reviewDto.getNotes());
//            updateRequest.setAdminReviewed(true);
//            updateRequest.setAdminApproved(reviewDto.getApproved());
//
//        } else if (reviewDto.getReviewerType() == ReviewerType.FRANCHISEE) {
//            // Franchisee review
//            if (!reviewer.getRoles().contains(Role.FRANCHISEE)) {
//                throw new UnauthorizedException("Only franchisees can review as franchisee");
//            }
//
//            // Verify franchisee is from the same district as the property
//            if (!reviewer.getDistrict().equals(updateRequest.getDistrict())) {
//                throw new UnauthorizedException("You can only review property update requests in your district");
//            }
//
//            // Check if already reviewed by franchisee
//            if (updateRequest.isFranchiseeReviewed()) {
//                throw new BadRequestException("This request has already been reviewed by a franchisee");
//            }
//
//            // Update franchisee review fields
//            updateRequest.setReviewedByFranchisee(reviewer);
//            updateRequest.setFranchiseeReviewedAt(LocalDateTime.now());
//            updateRequest.setFranchiseeNotes(reviewDto.getNotes());
//            updateRequest.setFranchiseeReviewed(true);
//            updateRequest.setFranchiseeApproved(reviewDto.getApproved());
//        }
//
//        // Determine overall status based on reviews
//        updateOverallStatus(updateRequest, reviewDto);
//
//        PropertyUpdateRequest savedRequest = updateRequestRepository.save(updateRequest);
//
//        return mapToDto(savedRequest);
//    }

    @Override
    @Transactional
    public PropertyUpdateRequestDto reviewUpdateRequest(ReviewPropertyUpdateRequestDto reviewDto, Long reviewerId) {
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("Reviewer not found with ID: " + reviewerId));

        PropertyUpdateRequest updateRequest = updateRequestRepository.findById(reviewDto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Update request not found with ID: " + reviewDto.getRequestId()));

        // Ensure request is pending
        if (updateRequest.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be reviewed");
        }

        ReviewerType reviewerType = reviewDto.getReviewerType();

        if (reviewerType == ReviewerType.ADMIN) {
            // ✅ Allow both ADMIN and SUBADMIN
            if (!(reviewer.getRoles().contains(Role.ADMIN) || reviewer.getRoles().contains(Role.SUBADMIN))) {
                throw new UnauthorizedException("Only admins or sub-admins can review as admin");
            }

            // Check if already reviewed by admin/subadmin
            if (updateRequest.isAdminReviewed()) {
                throw new BadRequestException("This request has already been reviewed by an admin or sub-admin");
            }

            // Fill the same admin review fields
            updateRequest.setReviewedByAdmin(reviewer);
            updateRequest.setAdminReviewedAt(LocalDateTime.now());
            updateRequest.setAdminNotes(reviewDto.getNotes());
            updateRequest.setAdminReviewed(true);
            updateRequest.setAdminApproved(reviewDto.getApproved());
        }
        else if (reviewerType == ReviewerType.FRANCHISEE) {
            if (!reviewer.getRoles().contains(Role.FRANCHISEE)) {
                throw new UnauthorizedException("Only franchisees can review as franchisee");
            }

            if (!reviewer.getDistrict().equals(updateRequest.getDistrict())) {
                throw new UnauthorizedException("You can only review property update requests in your district");
            }

            if (updateRequest.isFranchiseeReviewed()) {
                throw new BadRequestException("This request has already been reviewed by a franchisee");
            }

            updateRequest.setReviewedByFranchisee(reviewer);
            updateRequest.setFranchiseeReviewedAt(LocalDateTime.now());
            updateRequest.setFranchiseeNotes(reviewDto.getNotes());
            updateRequest.setFranchiseeReviewed(true);
            updateRequest.setFranchiseeApproved(reviewDto.getApproved());
        }

        // Update overall status
        updateOverallStatus(updateRequest, reviewDto);

        PropertyUpdateRequest savedRequest = updateRequestRepository.save(updateRequest);
        return mapToDto(savedRequest);
    }


    private void updateOverallStatus(PropertyUpdateRequest updateRequest, ReviewPropertyUpdateRequestDto reviewDto) {
        // If this is a rejection, update status immediately
        if (!reviewDto.getApproved()
        ) {
            updateRequest.setStatus(RequestStatus.REJECTED);
            
            // Rejection reason is required if rejected
            if (!StringUtils.hasText(reviewDto.getRejectionReason())) {
                throw new BadRequestException("Rejection reason is required when rejecting an update request");
            }
            
            updateRequest.setRejectionReason(reviewDto.getRejectionReason());
            return;
        }
        
        // If this is an approval, immediately approve and apply changes
        // Only one approval (either admin or franchisee) is required
        updateRequest.setStatus(RequestStatus.APPROVED);
        applyChangesToProperty(updateRequest);
    }
    
    @Override
    @Transactional
    public PropertyUpdateRequestDto cancelUpdateRequest(Long requestId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        PropertyUpdateRequest updateRequest = updateRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Update request not found with ID: " + requestId));
        
        // Verify ownership or admin role
        if (!updateRequest.getRequestedBy().getId().equals(userId) && 
            !user.getRoles().contains(Role.ADMIN) &&
            !(updateRequest.isFranchiseeRequest() && updateRequest.getFranchisee().getId().equals(userId))) {
            throw new UnauthorizedException("You are not authorized to cancel this update request");
        }
        
        // Verify request is pending
        if (updateRequest.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be cancelled");
        }
        
        // Update the request
        updateRequest.setStatus(RequestStatus.CANCELLED);
        
        PropertyUpdateRequest savedRequest = updateRequestRepository.save(updateRequest);
        
        return mapToDto(savedRequest);
    }
    
    // Helper methods
    
    private void processFieldChanges(Property property, CreatePropertyUpdateRequestDto requestDto, 
                                    Map<String, String> oldValues, Map<String, String> newValues) {
        // Process basic fields
        processBasicFields(property, requestDto, oldValues, newValues);
        
        // Process JSON fields
        processJsonField(property.getAmenities(), requestDto.getAmenities(), "amenities", oldValues, newValues);
        processJsonField(property.getSecurityFeatures(), requestDto.getSecurityFeatures(), "securityFeatures", oldValues, newValues);
        processJsonField(property.getLuxuriousFeatures(), requestDto.getLuxuriousFeatures(), "luxuriousFeatures", oldValues, newValues);
        processJsonField(property.getFeatures(), requestDto.getFeatures(), "features", oldValues, newValues);
        processJsonField(property.getAdditionalDetails(), requestDto.getAdditionalDetails(), "additionalDetails", oldValues, newValues);
    }
    
    private void processBasicFields(Property property, CreatePropertyUpdateRequestDto requestDto, 
                                   Map<String, String> oldValues, Map<String, String> newValues) {
        // Use reflection to get all fields from the DTO
        Field[] fields = CreatePropertyUpdateRequestDto.class.getDeclaredFields();
        
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String fieldName = field.getName();
                
                // Skip fields that are not basic property fields
                if (fieldName.equals("propertyId") || fieldName.equals("requestNotes") || 
                    fieldName.equals("amenities") || fieldName.equals("securityFeatures") || 
                    fieldName.equals("luxuriousFeatures") || fieldName.equals("features") || 
                    fieldName.equals("additionalDetails") || fieldName.equals("ownerPermanentId")) {
                    continue;
                }
                
                Object newValue = field.get(requestDto);
                
                // Only process fields that have a value in the DTO
                if (newValue != null) {
                    // Get the old value from the property
                    Object oldValue = getPropertyValue(property, fieldName);
                    
                    // Only store if values are different
                    if (oldValue == null || !oldValue.toString().equals(newValue.toString())) {
                        oldValues.put(fieldName, oldValue != null ? oldValue.toString() : null);
                        newValues.put(fieldName, newValue.toString());
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("Error accessing field", e);
            }
        }
    }
    
    private Object getPropertyValue(Property property, String fieldName) {
        try {
            BeanWrapper wrapper = new BeanWrapperImpl(property);
            return wrapper.getPropertyValue(fieldName);
        } catch (Exception e) {
            log.error("Error getting property value for field: {}", fieldName, e);
            return null;
        }
    }
    
    private void processJsonField(Object oldObject, String newJsonString, String fieldName,
                                 Map<String, String> oldValues, Map<String, String> newValues) {
        if (newJsonString == null) {
            return;
        }
        
        try {
            String oldJsonString = null;
            if (oldObject != null) {
                oldJsonString = objectMapper.writeValueAsString(oldObject);
            }
            
            // Only store if values are different
            if (oldJsonString == null || !oldJsonString.equals(newJsonString)) {
                oldValues.put(fieldName, oldJsonString);
                newValues.put(fieldName, newJsonString);
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON field: {}", fieldName, e);
        }
    }
    
   /* private void handleFileUploads(List<MultipartFile> images, MultipartFile videoFile, 
                                  Property property, Map<String, String> oldValues, Map<String, String> newValues) {
        // Handle images
        if (images != null && !images.isEmpty()) {
            List<String> oldImageUrls = property.getImages();
            oldValues.put("images", oldImageUrls != null ? String.join(",", oldImageUrls) : null);
            
            // New image URLs will be generated when the changes are applied
            newValues.put("images", "NEW_IMAGES");
        }
        
        // Handle video
        if (videoFile != null && !videoFile.isEmpty()) {
            String oldVideoUrl = property.getVideoUrl();
            oldValues.put("videoUrl", oldVideoUrl);
            
            // New video URL will be generated when the changes are applied
            newValues.put("videoUrl", "NEW_VIDEO");
        }
    }*/

    private void handleFileUploads(List<MultipartFile> images, MultipartFile videoFile, Property property,
            Map<String, String> oldValues, Map<String, String> newValues) {

// Handle images upload to S3
        if (images != null && !images.isEmpty()) {
            List<String> oldImageUrls = property.getImages();
            oldValues.put("images", oldImageUrls != null ? String.join(",", oldImageUrls) : null);

            try {
// Upload images to AWS S3
                List<String> newImageUrls = s3Service.uploadPropertyImages(images, property.getOwner(), property);
                newValues.put("images", String.join(",", newImageUrls));
            } catch (Exception e) {
                log.error("Error uploading property images: {}", e.getMessage());
                throw new RuntimeException("Failed to upload property images");
            }
        }

// Handle video upload to S3
        if (videoFile != null && !videoFile.isEmpty()) {
            String oldVideoUrl = property.getVideoUrl();
            oldValues.put("videoUrl", oldVideoUrl);

            try {
// Upload video to AWS S3
                String newVideoUrl = s3Service.uploadPropertyVideo(videoFile, property.getOwner(), property);
                newValues.put("videoUrl", newVideoUrl);
            } catch (Exception e) {
                log.error("Error uploading property video: {}", e.getMessage());
                throw new RuntimeException("Failed to upload property video");
            }
        }
    }
//
//    private void applyChangesToProperty(PropertyUpdateRequest updateRequest) {
//        Property property = updateRequest.getProperty();
//        Map<String, String> newValues = updateRequest.getNewValues();
//
//        // Apply basic field changes
//        for (Map.Entry<String, String> entry : newValues.entrySet()) {
//            String fieldName = entry.getKey();
//            String newValue = entry.getValue();
//
//            // Skip special fields that need special handling
//            if (fieldName.equals("images") || fieldName.equals("videoUrl") ||
//                fieldName.equals("amenities") || fieldName.equals("securityFeatures") ||
//                fieldName.equals("luxuriousFeatures") || fieldName.equals("features") ||
//                fieldName.equals("additionalDetails")) {
//                continue;
//            }
//
//            try {
//                BeanWrapper wrapper = new BeanWrapperImpl(property);
//                PropertyDescriptor descriptor = wrapper.getPropertyDescriptor(fieldName);
//
//                if (descriptor != null) {
//                    Class<?> propertyType = descriptor.getPropertyType();
//
//                    // Convert the string value to the appropriate type
//                    Object convertedValue = convertStringToType(newValue, propertyType);
//
//                    // Set the value
//                    wrapper.setPropertyValue(fieldName, convertedValue);
//                }
//            } catch (Exception e) {
//                log.error("Error setting property value for field: {}", fieldName, e);
//            }
//        }
//
//        // Apply JSON field changes
//        applyJsonFieldChanges(property, newValues, "amenities", Set.class);
//        applyJsonFieldChanges(property, newValues, "securityFeatures", Set.class);
//        applyJsonFieldChanges(property, newValues, "luxuriousFeatures", Set.class);
//        applyJsonFieldChanges(property, newValues, "features", Set.class);
//        applyJsonFieldChanges(property, newValues, "additionalDetails", Map.class);
//
//        // Save the updated property
//        propertyRepository.save(property);
//    }

    private void applyChangesToProperty(PropertyUpdateRequest updateRequest) {
        Property property = updateRequest.getProperty();
        Map<String, String> newValues = updateRequest.getNewValues();

        // Apply basic field changes
        for (Map.Entry<String, String> entry : newValues.entrySet()) {
            String fieldName = entry.getKey();
            String newValue = entry.getValue();

            // Skip special fields
            if (fieldName.equals("images") || fieldName.equals("videoUrl") ||
                    fieldName.equals("amenities") || fieldName.equals("securityFeatures") ||
                    fieldName.equals("luxuriousFeatures") || fieldName.equals("features") ||
                    fieldName.equals("additionalDetails")) {
                continue;
            }

            try {
                BeanWrapper wrapper = new BeanWrapperImpl(property);
                PropertyDescriptor descriptor = wrapper.getPropertyDescriptor(fieldName);

                if (descriptor != null) {
                    Class<?> propertyType = descriptor.getPropertyType();
                    Object convertedValue = convertStringToType(newValue, propertyType);
                    wrapper.setPropertyValue(fieldName, convertedValue);
                }
            } catch (Exception e) {
                log.error("Error setting property value for field: {}", fieldName, e);
            }
        }

        // Apply JSON field changes
        applyJsonFieldChanges(property, newValues, "amenities", Set.class);
        applyJsonFieldChanges(property, newValues, "securityFeatures", Set.class);
        applyJsonFieldChanges(property, newValues, "luxuriousFeatures", Set.class);
        applyJsonFieldChanges(property, newValues, "features", Set.class);
        applyJsonFieldChanges(property, newValues, "additionalDetails", Map.class);

        //  Apply new image URLs (uploaded to S3)
        if (newValues.containsKey("images")) {
            String newImages = newValues.get("images");
            if (newImages != null && !newImages.isEmpty() && !"NEW_IMAGES".equals(newImages)) {
                try {
                    List<String> imageList = new ArrayList<>(Arrays.asList(newImages.split(",")));
                    property.setImages(imageList);
                } catch (Exception e) {
                    log.error("Error applying image URLs to property", e);
                }
            }
        }

        //  Apply new video URL (uploaded to S3)
        if (newValues.containsKey("videoUrl")) {
            String newVideoUrl = newValues.get("videoUrl");
            if (newVideoUrl != null && !newVideoUrl.isEmpty() && !"NEW_VIDEO".equals(newVideoUrl)) {
                try {
                    property.setVideoUrl(newVideoUrl);
                } catch (Exception e) {
                    log.error("Error applying video URL to property", e);
                }
            }
        }

        //  Save final property updates
        propertyRepository.save(property);
    }



    private Object convertStringToType(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        
        if (targetType == String.class) {
            return value;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        
        return value;
    }
    
    private void applyJsonFieldChanges(Property property, Map<String, String> newValues, 
                                      String fieldName, Class<?> fieldType) {
        String newJsonString = newValues.get(fieldName);
        
        if (newJsonString != null) {
            try {
                Object convertedValue;
                
                if (fieldType == Set.class) {
                    convertedValue = objectMapper.readValue(newJsonString, 
                            objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                } else if (fieldType == Map.class) {
                    convertedValue = objectMapper.readValue(newJsonString, 
                            objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
                } else {
                    convertedValue = objectMapper.readValue(newJsonString, fieldType);
                }
                
                BeanWrapper wrapper = new BeanWrapperImpl(property);
                wrapper.setPropertyValue(fieldName, convertedValue);
            } catch (Exception e) {
                log.error("Error applying JSON field changes for field: {}", fieldName, e);
            }
        }
    }
    
    private PropertyUpdateRequestDto mapToDto(PropertyUpdateRequest updateRequest) {
        return PropertyUpdateRequestDto.builder()
                .id(updateRequest.getId())
                .requestId(updateRequest.getRequestId())
                .propertyId(updateRequest.getProperty().getId())
                .propertyTitle(updateRequest.getProperty().getTitle())
                .propertyPermanentId(updateRequest.getProperty().getPermanentId())
                .requestedBy(mapToUserSummary(updateRequest.getRequestedBy()))
                .reviewedByAdmin(updateRequest.getReviewedByAdmin() != null ? mapToUserSummary(updateRequest.getReviewedByAdmin()) : null)
                .reviewedByFranchisee(updateRequest.getReviewedByFranchisee() != null ? mapToUserSummary(updateRequest.getReviewedByFranchisee()) : null)
                .status(updateRequest.getStatus())
                .requestNotes(updateRequest.getRequestNotes())
                .adminNotes(updateRequest.getAdminNotes())
                .franchiseeNotes(updateRequest.getFranchiseeNotes())
                .rejectionReason(updateRequest.getRejectionReason())
                .district(updateRequest.getDistrict())
                .adminReviewed(updateRequest.isAdminReviewed())
                .franchiseeReviewed(updateRequest.isFranchiseeReviewed())
                .adminApproved(updateRequest.getAdminApproved())
                .franchiseeApproved(updateRequest.getFranchiseeApproved())
                .oldValues(updateRequest.getOldValues())
                .newValues(updateRequest.getNewValues())
                .submittedAt(updateRequest.getSubmittedAt())
                .adminReviewedAt(updateRequest.getAdminReviewedAt())
                .franchiseeReviewedAt(updateRequest.getFranchiseeReviewedAt())
                .updatedAt(updateRequest.getUpdatedAt())
                .franchiseeRequest(updateRequest.isFranchiseeRequest())
                .franchisee(updateRequest.getFranchisee() != null ? mapToUserSummary(updateRequest.getFranchisee()) : null)
                .build();
    }
    
    private UserSummaryDto mapToUserSummary(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getMobileNumber())
                .permanentId(user.getPermanentId())
                .profileImageUrl(user.getProfileImageUrl())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }
} 
