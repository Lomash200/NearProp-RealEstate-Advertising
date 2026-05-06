package com.nearprop.advertisement.service.impl;

import com.nearprop.advertisement.dto.AdvertisementDto;
import com.nearprop.advertisement.entity.Advertisement;
import com.nearprop.advertisement.mapper.AdvertisementMapper;
import com.nearprop.advertisement.repository.AdvertisementRepository;
import com.nearprop.advertisement.service.PropertyAdvertisementService;
import com.nearprop.entity.Property;
import com.nearprop.exception.EntityNotFoundException;
import com.nearprop.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PropertyAdvertisementServiceImpl implements PropertyAdvertisementService {

    private final PropertyRepository propertyRepository;
    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementMapper advertisementMapper;

    @Override
    public List<AdvertisementDto> getAdvertisementsForProperty(Long propertyId, int limit) {
        log.info("Finding advertisements for property ID: {} with limit: {}", propertyId, limit);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> {
                    log.error("Property not found with ID: {}", propertyId);
                    return new EntityNotFoundException("Property not found with ID: " + propertyId);
                });

        // Get ads for the property's district
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, limit);
        
        log.debug("Searching advertisements for district: {}", property.getDistrictName());
        Page<Advertisement> adsPage = advertisementRepository
                .findByActiveTrueAndDistrictNameAndValidFromBeforeAndValidUntilAfter(
                        property.getDistrictName(), now, now, pageable);
        
        List<AdvertisementDto> result = adsPage.getContent().stream()
                .map(advertisementMapper::toDto)
                .collect(Collectors.toList());
        
        log.info("Found {} advertisements for property ID: {}", result.size(), propertyId);
        return result;
    }

    @Override
    public List<AdvertisementDto> getAdvertisementsForLocation(Double latitude, Double longitude, int limit) {
        log.info("Finding advertisements near location: lat={}, long={} with limit: {}", latitude, longitude, limit);
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, limit);
        
        Page<Advertisement> adsPage = advertisementRepository.findActiveAdsNearLocation(latitude, longitude, now, pageable);
        
        List<AdvertisementDto> result = adsPage.getContent().stream()
                .map(advertisementMapper::toDto)
                .collect(Collectors.toList());
        
        log.info("Found {} advertisements near location", result.size());
        return result;
    }

    @Override
    public List<AdvertisementDto> getAdvertisementsForDistrict(String districtName, int limit) {
        log.info("Finding advertisements for district: {} with limit: {}", districtName, limit);
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, limit);
        
        Page<Advertisement> adsPage = advertisementRepository
                .findByActiveTrueAndDistrictNameAndValidFromBeforeAndValidUntilAfter(
                        districtName, now, now, pageable);
        
        List<AdvertisementDto> result = adsPage.getContent().stream()
                .map(advertisementMapper::toDto)
                .collect(Collectors.toList());
        
        log.info("Found {} advertisements for district: {}", result.size(), districtName);
        return result;
    }
} 