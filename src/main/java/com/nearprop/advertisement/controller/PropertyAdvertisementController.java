package com.nearprop.advertisement.controller;

import com.nearprop.advertisement.dto.AdvertisementDto;
import com.nearprop.advertisement.service.PropertyAdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/properties")
@RequiredArgsConstructor
@Slf4j
public class PropertyAdvertisementController {
    
    private final PropertyAdvertisementService propertyAdvertisementService;
    
    @GetMapping("/{id}/advertisements")
    public ResponseEntity<List<AdvertisementDto>> getAdvertisementsForProperty(
            @PathVariable("id") Long propertyId,
            @RequestParam(defaultValue = "3") int limit) {
        log.info("Fetching advertisements for property ID: {} with limit: {}", propertyId, limit);
        List<AdvertisementDto> advertisements = propertyAdvertisementService.getAdvertisementsForProperty(propertyId, limit);
        log.debug("Retrieved {} advertisements for property ID: {}", advertisements.size(), propertyId);
        return ResponseEntity.ok(advertisements);
    }
    
    @GetMapping("/advertisements/nearby")
    public ResponseEntity<List<AdvertisementDto>> getAdvertisementsNearLocation(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "3") int limit) {
        log.info("Fetching advertisements near location: lat={}, long={} with limit: {}", latitude, longitude, limit);
        List<AdvertisementDto> advertisements = propertyAdvertisementService.getAdvertisementsForLocation(latitude, longitude, limit);
        log.debug("Retrieved {} advertisements near location", advertisements.size());
        return ResponseEntity.ok(advertisements);
    }
    
    @GetMapping("/advertisements/district/{districtName}")
    public ResponseEntity<List<AdvertisementDto>> getAdvertisementsForDistrict(
            @PathVariable String districtName,
            @RequestParam(defaultValue = "3") int limit) {
        log.info("Fetching advertisements for district: {} with limit: {}", districtName, limit);
        List<AdvertisementDto> advertisements = propertyAdvertisementService.getAdvertisementsForDistrict(districtName, limit);
        log.debug("Retrieved {} advertisements for district: {}", advertisements.size(), districtName);
        return ResponseEntity.ok(advertisements);
    }
} 