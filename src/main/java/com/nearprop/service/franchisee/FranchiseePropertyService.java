package com.nearprop.service.franchisee;

import com.nearprop.dto.CreatePropertyDto;
import com.nearprop.dto.PropertyDto;
import com.nearprop.dto.PropertyFormDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service for franchisee property operations
 */
public interface FranchiseePropertyService {
    
    /**
     * Create a property on behalf of another user
     * @param ownerPermanentId The permanent ID of the property owner
     * @param propertyDto The property details
     * @param franchiseeId The ID of the franchisee creating the property
     * @return The created property
     */
    PropertyDto createPropertyOnBehalf(String ownerPermanentId, CreatePropertyDto propertyDto, Long franchiseeId);
    
    /**
     * Create a property using form data on behalf of another user
     * @param ownerPermanentId The permanent ID of the property owner
     * @param propertyFormDto The property form data
     * @param images The property images
     * @param videoFile The property video
     * @param franchiseeId The ID of the franchisee creating the property
     * @return The created property
     */
    PropertyDto createPropertyFormOnBehalf(
            String ownerPermanentId,
            PropertyFormDto propertyFormDto,
            List<MultipartFile> images,
            MultipartFile videoFile,
            Long franchiseeId);
    
    /**
     * Validate a user's permanent ID
     * @param permanentId The permanent ID to validate
     * @return True if valid, false otherwise
     */
    boolean validateUserId(String permanentId);
} 