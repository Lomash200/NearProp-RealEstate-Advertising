package com.nearprop.service.franchisee;

import com.nearprop.dto.franchisee.CreateDistrictDto;
import com.nearprop.dto.franchisee.DistrictDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DistrictService {
    
    /**
     * Create a new district
     * 
     * @param createDistrictDto District creation data
     * @return Created district
     */
    DistrictDto createDistrict(CreateDistrictDto createDistrictDto);
    
    /**
     * Get a district by ID
     * 
     * @param districtId District ID
     * @return District details
     */
    DistrictDto getDistrict(Long districtId);
    
    /**
     * Update a district
     * 
     * @param districtId District ID
     * @param updateDistrictDto District update data
     * @return Updated district
     */
    DistrictDto updateDistrict(Long districtId, CreateDistrictDto updateDistrictDto);
    
    /**
     * Delete a district
     * 
     * @param districtId District ID
     */
    void deleteDistrict(Long districtId);
    
    /**
     * Get all districts with pagination
     * 
     * @param pageable Pagination information
     * @return Page of districts
     */
    Page<DistrictDto> getAllDistricts(Pageable pageable);
    
    /**
     * Get active districts
     * 
     * @return List of active districts
     */
    List<DistrictDto> getActiveDistricts();
    
    /**
     * Get districts by city
     * 
     * @param city City name
     * @return List of districts in the city
     */
    List<DistrictDto> getDistrictsByCity(String city);
    
    /**
     * Get districts by state
     * 
     * @param state State name
     * @return List of districts in the state
     */
    List<DistrictDto> getDistrictsByState(String state);
    
    /**
     * Get all unique states
     * 
     * @return List of state names
     */
    List<String> getAllStates();
    
    /**
     * Find districts near a location
     * 
     * @param latitude Latitude
     * @param longitude Longitude
     * @param radiusKm Radius in kilometers
     * @return List of nearby districts
     */
    List<DistrictDto> findNearbyDistricts(Double latitude, Double longitude, Double radiusKm);
    
    /**
     * Get districts managed by a franchisee
     * 
     * @param franchiseeId Franchisee user ID
     * @return List of districts managed by the franchisee
     */
    List<DistrictDto> getDistrictsByFranchiseeId(Long franchiseeId);
} 