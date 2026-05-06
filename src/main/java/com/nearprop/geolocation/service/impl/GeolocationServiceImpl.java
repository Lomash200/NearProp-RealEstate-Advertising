package com.nearprop.geolocation.service.impl;

import com.nearprop.geolocation.service.GeolocationService;
import com.nearprop.geolocation.util.GeocodingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeolocationServiceImpl implements GeolocationService {
    
    private final GeocodingUtil geocodingUtil;
    
    @Override
    public Optional<double[]> geocodeAddress(String address) {
        log.info("Geocoding address in service: {}", address);
        return geocodingUtil.geocodeAddress(address);
    }
    
    @Override
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        log.info("Calculating distance between points: ({}, {}) and ({}, {})", lat1, lon1, lat2, lon2);
        return geocodingUtil.calculateDistance(lat1, lon1, lat2, lon2);
    }
    
    @Override
    public boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radiusKm) {
        log.info("Checking if points are within radius: {} km", radiusKm);
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        boolean isWithin = distance <= radiusKm;
        log.debug("Distance: {} km, Within radius: {}", distance, isWithin);
        return isWithin;
    }
} 