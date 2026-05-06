package com.nearprop.advertisement.service;

import com.nearprop.advertisement.dto.AdvertisementDto;
import com.nearprop.advertisement.dto.CreateAdvertisementDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdvertisementService {
    
    /**
     * Create a new advertisement
     * @param dto Advertisement details
     * @param userId ID of the user creating the advertisement
     * @return Created advertisement
     */
    AdvertisementDto createAdvertisement(CreateAdvertisementDto dto, Long userId);
    
    /**
     * Update an existing advertisement
     * @param id Advertisement ID
     * @param dto Updated advertisement details
     * @param userId ID of the user updating the advertisement
     * @return Updated advertisement
     */
    AdvertisementDto updateAdvertisement(Long id, CreateAdvertisementDto dto, Long userId);
    
    /**
     * Get an advertisement by ID
     * @param id Advertisement ID
     * @return Advertisement details
     */
    AdvertisementDto getAdvertisement(Long id);
    
    /**
     * Get all active advertisements
     * @param pageable Pagination information
     * @return Page of active advertisements
     */
    Page<AdvertisementDto> getAllActiveAdvertisements(Pageable pageable);
    
    /**
     * Get advertisements created by a specific user
     * @param userId User ID
     * @param pageable Pagination information
     * @return Page of advertisements created by the user
     */
    Page<AdvertisementDto> getUserAdvertisements(Long userId, Pageable pageable);
    
    /**
     * Get advertisements targeting a specific district
     * @param districtName District name
     * @param pageable Pagination information
     * @return Page of advertisements targeting the district
     */
    Page<AdvertisementDto> getAdvertisementsByDistrict(String districtName, Pageable pageable);
    
    /**
     * Get top 5 advertisements for a specific district
     * @param districtName District name
     * @return List of up to 5 advertisements for the district
     */
    List<AdvertisementDto> getTop5AdvertisementsByDistrict(String districtName);
    
    /**
     * Get advertisements near a specific location
     * @param latitude Latitude
     * @param longitude Longitude
     * @param pageable Pagination information
     * @return Page of advertisements near the location
     */
    Page<AdvertisementDto> getAdvertisementsNearLocation(Double latitude, Double longitude, Pageable pageable);
    
    /**
     * Activate or deactivate an advertisement
     * @param id Advertisement ID
     * @param active Whether to activate or deactivate
     * @param userId ID of the user making the change
     * @return Updated advertisement
     */
    AdvertisementDto setAdvertisementActive(Long id, boolean active, Long userId);
    
    /**
     * Delete an advertisement
     * @param id Advertisement ID
     * @param userId ID of the user deleting the advertisement
     */
    void deleteAdvertisement(Long id, Long userId);

    void deleteAdvertisementSubAdmin(Long id, Long userId);

    List<AdvertisementDto> getAllAdvertisements();
} 