package com.nearprop.advertisement.service;

import com.nearprop.advertisement.dto.AdvertisementDto;
import java.util.List;

public interface PropertyAdvertisementService {
    
    /**
     * Get advertisements relevant to a property based on its district
     * 
     * @param propertyId The ID of the property
     * @param limit Maximum number of ads to return
     * @return List of advertisements
     */
    List<AdvertisementDto> getAdvertisementsForProperty(Long propertyId, int limit);
    
    /**
     * Get advertisements based on user's location
     * 
     * @param latitude User's latitude
     * @param longitude User's longitude
     * @param limit Maximum number of ads to return
     * @return List of advertisements
     */
    List<AdvertisementDto> getAdvertisementsForLocation(Double latitude, Double longitude, int limit);
    
    /**
     * Get advertisements for a specific district
     * 
     * @param districtName Name of the district
     * @param limit Maximum number of ads to return
     * @return List of advertisements
     */
    List<AdvertisementDto> getAdvertisementsForDistrict(String districtName, int limit);
} 