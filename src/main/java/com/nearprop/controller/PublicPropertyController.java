package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.PropertyDto;
import com.nearprop.entity.PropertyStatus;
import com.nearprop.entity.PropertyType;
import com.nearprop.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Public API controller for property data that doesn't require authentication
 */
@RestController
@RequestMapping("/public/properties")
@RequiredArgsConstructor
@Slf4j
public class PublicPropertyController {

    private final PropertyService propertyService;
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        health.put("application", "NearProp");
        health.put("version", "1.0.0");
        health.put("endpoint", "public-properties-health");
        return ResponseEntity.ok(health);
    }
    
    /**
     * Get all properties with optional filtering
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getAllProperties() {
        List<PropertyDto> properties = propertyService.getAllPropertiesWithoutPagination();
        return ResponseEntity.ok(ApiResponse.success("Properties retrieved successfully", properties));
    }
    
    /**
     * Filter properties with multiple criteria
     */
    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> filterProperties(
            @RequestParam(required = false) PropertyStatus category,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Double minArea,
            @RequestParam(required = false) Double maxArea,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) Integer bathrooms,
            @RequestParam(required = false) String keyword) {
        
        log.info("Filtering properties with: category={}, city={}, district={}, location=({},{}), radius={}, " +
                "propertyType={}, price={}to{}, area={}to{}, bedrooms={}, bathrooms={}, keyword={}", 
                category, city, district, latitude, longitude, radius, propertyType, 
                minPrice, maxPrice, minArea, maxArea, bedrooms, bathrooms, keyword);
        
        // Call the advanced search method without pagination
        List<PropertyDto> properties = propertyService.advancedSearchWithoutPagination(
                category, city, district, latitude, longitude, radius, propertyType,
                minPrice, maxPrice, minArea, maxArea, bedrooms, bathrooms, keyword);
                
        return ResponseEntity.ok(ApiResponse.success("Filtered properties retrieved successfully", properties));
    }
    
    /**
     * Get featured properties
     */
    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getFeaturedProperties() {
        List<PropertyDto> properties = propertyService.getFeaturedPropertiesWithoutPagination();
        return ResponseEntity.ok(ApiResponse.success("Featured properties retrieved successfully", properties));
    }
    
    /**
     * Get property by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PropertyDto> getProperty(@PathVariable("id") Long propertyId) {
        try {
            PropertyDto property = propertyService.getProperty(propertyId);
            
            // Only return the property if it's approved and active
            if (property.getApproved() != null && property.getApproved() && 
                property.getActive() != null && property.getActive()) {
                return ResponseEntity.ok(property);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error retrieving property with ID {}: {}", propertyId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get all districts
     */
    @GetMapping("/districts")
    public ResponseEntity<List<String>> getAllDistricts() {
        List<String> districts = propertyService.getAllDistricts();
        return ResponseEntity.ok(districts);
    }
    
    /**
     * Get all states
     */
    @GetMapping("/states")
    public ResponseEntity<List<String>> getAllStates() {
        List<String> states = propertyService.getAllStates();
        return ResponseEntity.ok(states);
    }
    
    /**
     * Get property count by type
     */
    @GetMapping("/stats/by-type")
    public ResponseEntity<ApiResponse<Object>> getPropertyCountByType() {
        try {
            Map<PropertyType, Long> stats = propertyService.getPropertyCountByType();
            return ResponseEntity.ok(ApiResponse.success("Property counts by type", stats));
        } catch (Exception e) {
            log.error("Error retrieving property counts by type: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.error("Failed to retrieve property counts"));
        }
    }
} 