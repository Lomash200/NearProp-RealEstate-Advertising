package com.nearprop.service;

import com.nearprop.dto.CreatePropertyDto;
import com.nearprop.dto.PropertyDto;
import com.nearprop.dto.PropertyFormDto;
import com.nearprop.dto.DeveloperPropertyFormDto;
import com.nearprop.dto.franchisee.DistrictDto;
import com.nearprop.entity.PropertyStatus;
import com.nearprop.entity.PropertyType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface PropertyService {
    PropertyDto createProperty(CreatePropertyDto propertyDto, Long userId);
    
    /**
     * Create a property from form data with file uploads
     * 
     * @param propertyFormDto Property form data
     * @param images Image files to upload
     * @param videoFile Video file to upload (optional)
     * @param userId ID of the user creating the property
     * @return The created property
     */
    PropertyDto createPropertyFromForm(PropertyFormDto propertyFormDto, List<MultipartFile> images, MultipartFile videoFile, Long userId);
    
    /**
     * Create a developer property from form data with file uploads and unit information
     * 
     * @param propertyFormDto Developer property form data with unit information
     * @param images Image files to upload
     * @param videoFile Video file to upload (optional)
     * @param userId ID of the developer creating the property
     * @return The created property
     */
    PropertyDto createDeveloperPropertyFromForm(DeveloperPropertyFormDto propertyFormDto, List<MultipartFile> images, MultipartFile videoFile, Long userId);

    PropertyDto updateProperty(Long propertyId, CreatePropertyDto propertyDto, Long userId);
    
    /**
     * Update a property from form data with file uploads
     * 
     * @param propertyId ID of the property to update
     * @param propertyFormDto Property form data
     * @param images Image files to upload (optional, can be empty)
     * @param videoFile Video file to upload (optional)
     * @param userId ID of the user updating the property
     * @return The updated property
     */
    PropertyDto updatePropertyFromForm(Long propertyId, PropertyFormDto propertyFormDto, List<MultipartFile> images, MultipartFile videoFile, Long userId);
    
    /**
     * Update the stock of a developer property
     * 
     * @param propertyId ID of the property to update
     * @param stock New stock value
     * @param userId ID of the developer updating the stock
     * @return The updated property
     */
    PropertyDto updatePropertyStock(Long propertyId, Integer stock, Long userId);
    
    PropertyDto updateDeveloperPropertyDetails(Long propertyId, String unitType, Integer unitCount, Integer stock, Long userId);
    
    PropertyDto getProperty(Long id);
    
    /**
     * Get property by permanent ID
     * 
     * @param permanentId The permanent ID of the property
     * @return The property
     */
    PropertyDto getPropertyByPermanentId(String permanentId);
    
    Page<PropertyDto> getAllProperties(Pageable pageable);
    
    List<PropertyDto> getAllPropertiesWithoutPagination();
    
    List<PropertyDto> getAllPropertiesWithoutPagination(Double latitude, Double longitude, Double radius);
    
    Page<PropertyDto> getApprovedProperties(Pageable pageable);
    
    List<PropertyDto> getApprovedPropertiesWithoutPagination();
    
    Page<PropertyDto> getFeaturedProperties(Pageable pageable);
    
    List<PropertyDto> getFeaturedPropertiesWithoutPagination();
    
    Page<PropertyDto> getPendingProperties(Pageable pageable);
    
    List<PropertyDto> getPendingApprovalPropertiesWithoutPagination();
    
    Page<PropertyDto> getUserProperties(Long userId, Pageable pageable);
    
    List<PropertyDto> getUserPropertiesWithoutPagination(Long userId);
    
    Page<PropertyDto> searchProperties(
            PropertyType type, 
            PropertyStatus status,
            String district,
            BigDecimal minPrice, 
            BigDecimal maxPrice, 
            Integer minBedrooms,
            Pageable pageable);
    
    List<PropertyDto> searchPropertiesWithoutPagination(
            PropertyType type, 
            PropertyStatus status,
            String district,
            BigDecimal minPrice, 
            BigDecimal maxPrice, 
            Integer minBedrooms);

    Page<PropertyDto> advancedSearch(
            PropertyStatus category, 
            String city,
            String district,
            Double latitude,
            Double longitude,
            Double radius,
            PropertyType propertyType,
            BigDecimal minPrice, 
            BigDecimal maxPrice,
            Double minArea,
            Double maxArea, 
            Integer bedrooms,
            Integer bathrooms,
            String keyword,
            Pageable pageable);
    
    List<PropertyDto> advancedSearchWithoutPagination(
            PropertyStatus category, 
            String city,
            String district,
            Double latitude,
            Double longitude,
            Double radius,
            PropertyType propertyType,
            BigDecimal minPrice, 
            BigDecimal maxPrice,
            Double minArea,
            Double maxArea, 
            Integer bedrooms,
            Integer bathrooms,
            String keyword);

    void deleteProperty(Long propertyId, Long userId);
    
    /**
     * Admin method to delete a property
     * 
     * @param propertyId The property ID
     * @return True if property was deleted successfully
     */
    boolean adminDeleteProperty(Long propertyId);
    
    PropertyDto approveProperty(Long propertyId);
    
    PropertyDto rejectProperty(Long propertyId);
    
    /**
     * Deactivate a property (admin only)
     * 
     * @param propertyId The property ID
     * @return The updated property
     */
    PropertyDto deactivateProperty(Long propertyId);
    
    /**
     * Hold a property (pause subscription, admin only)
     * 
     * @param propertyId The property ID
     * @return The updated property
     */
    PropertyDto holdProperty(Long propertyId);
    
    /**
     * Block a property (admin only)
     * 
     * @param propertyId The property ID
     * @param reason Reason for blocking
     * @return The updated property
     */
    PropertyDto blockProperty(Long propertyId, String reason);
    
    /**
     * Unblock a property (admin only)
     * 
     * @param propertyId The property ID
     * @return The updated property
     */
    PropertyDto unblockProperty(Long propertyId);
    
    PropertyDto markFeatured(Long propertyId, boolean featured);
    
    /**
     * Activate a property with a subscription
     * 
     * @param propertyId The property ID
     * @param subscriptionId The subscription ID
     * @return The updated property
     */
    PropertyDto activatePropertyWithSubscription(Long propertyId, Long subscriptionId);
    
    /**
     * Get all unique districts
     * 
     * @return List of district names
     * @deprecated Use {@link com.nearprop.service.franchisee.DistrictService#getActiveDistricts()} instead
     */
    @Deprecated
    List<String> getAllDistricts();
    
    /**
     * Get all unique states from the properties database
     * 
     * @return List of state names
     * @deprecated Use {@link com.nearprop.service.franchisee.DistrictService#getAllStates()} instead 
     */
    @Deprecated
    List<String> getAllStates();
    
    /**
     * Get all districts by state
     * 
     * @param state State name
     * @return List of district names in the state
     * @deprecated Use {@link com.nearprop.service.franchisee.DistrictService#getDistrictsByState(String)} instead
     */
    @Deprecated
    List<String> getDistrictsByState(String state);
    
    Map<PropertyType, Long> getPropertyCountByType();
    
    Map<String, Long> getPropertyCountByDistrict();

    Page<PropertyDto> getPendingApprovalProperties(Pageable pageable);
    
    /**
     * Activates a property with a subscription
     * 
     * @param propertyId The property ID
     * @param userId The user ID
     * @param subscriptionId The subscription ID
     * @param expiryDate The expiry date of the subscription
     * @return The updated property
     */
    PropertyDto activateProperty(Long propertyId, Long userId, Long subscriptionId, LocalDateTime expiryDate);

    void deletePropertybySubAdmin(Long propertyId, Long userId);

    PropertyDto createPropertyFromFormBySubAdmin(@Valid PropertyFormDto propertyFormDto, List<MultipartFile> images, MultipartFile videoFile, Long id);

    PropertyDto updatePropertyFromFormBySubAdmin(Long propertyId, PropertyFormDto propertyFormDto,
                                                 List<MultipartFile> images, MultipartFile videoFile, Long subAdminId);

    void hardDelete(Long id, Long id1);
    /**
     * Check if user has an active PROPERTY type subscription
     * * @param userId ID of the user
     * @return True if active profile subscription exists
     */
    boolean hasActivePropertySubscription(Long userId);
    boolean hasActiveProfileSubscription(Long userId);


} 