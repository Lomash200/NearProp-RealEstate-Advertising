package com.nearprop.geolocation.controller;

import com.nearprop.geolocation.service.GeolocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/v1/geolocation")
@RequiredArgsConstructor
@Slf4j
public class GeolocationController {

    private final GeolocationService geolocationService;

    @PostMapping("/geocode")
    public ResponseEntity<Map<String, Object>> geocodeAddress(@RequestBody Map<String, String> request) {
        log.info("Geocoding address: {}", request.get("address"));
        
        String address = request.get("address");
        if (address == null || address.isEmpty()) {
            log.warn("Address is empty");
            return ResponseEntity.badRequest().body(Map.of("error", "Address is required"));
        }
        
        Optional<double[]> coordinates = geolocationService.geocodeAddress(address);
        
        if (coordinates.isPresent()) {
            double[] coords = coordinates.get();
            Map<String, Object> response = new HashMap<>();
            response.put("latitude", coords[0]);
            response.put("longitude", coords[1]);
            response.put("address", address);
            
            log.info("Geocoding successful for address: {}", address);
            return ResponseEntity.ok(response);
        } else {
            log.warn("Geocoding failed for address: {}", address);
            return ResponseEntity.badRequest().body(Map.of("error", "Failed to geocode address"));
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testEndpoint() {
        log.info("Test endpoint called");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Geolocation service is working");
        response.put("status", "OK");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/distance")
    public ResponseEntity<Map<String, Object>> calculateDistance(@RequestBody Map<String, Double> request) {
        log.info("Calculating distance between points: ({}, {}) and ({}, {})", 
                request.get("lat1"), request.get("lon1"), request.get("lat2"), request.get("lon2"));
        
        Double lat1 = request.get("lat1");
        Double lon1 = request.get("lon1");
        Double lat2 = request.get("lat2");
        Double lon2 = request.get("lon2");
        
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            log.warn("Missing coordinates");
            return ResponseEntity.badRequest().body(Map.of("error", "All coordinates are required"));
        }
        
        double distance = geolocationService.calculateDistance(lat1, lon1, lat2, lon2);
        
        Map<String, Object> response = new HashMap<>();
        response.put("distanceKm", distance);
        response.put("from", Map.of("latitude", lat1, "longitude", lon1));
        response.put("to", Map.of("latitude", lat2, "longitude", lon2));
        
        log.info("Distance calculation successful: {} km", distance);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/within-radius")
    public ResponseEntity<Map<String, Object>> isWithinRadius(@RequestBody Map<String, Double> request) {
        log.info("Checking if point is within radius: {} km", request.get("radiusKm"));
        
        Double lat1 = request.get("lat1");
        Double lon1 = request.get("lon1");
        Double lat2 = request.get("lat2");
        Double lon2 = request.get("lon2");
        Double radiusKm = request.get("radiusKm");
        
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null || radiusKm == null) {
            log.warn("Missing parameters");
            return ResponseEntity.badRequest().body(Map.of("error", "All parameters are required"));
        }
        
        boolean isWithin = geolocationService.isWithinRadius(lat1, lon1, lat2, lon2, radiusKm);
        double distance = geolocationService.calculateDistance(lat1, lon1, lat2, lon2);
        
        Map<String, Object> response = new HashMap<>();
        response.put("withinRadius", isWithin);
        response.put("distanceKm", distance);
        response.put("radiusKm", radiusKm);
        response.put("from", Map.of("latitude", lat1, "longitude", lon1));
        response.put("to", Map.of("latitude", lat2, "longitude", lon2));
        
        log.info("Within radius check result: {}, distance: {} km, radius: {} km", 
                isWithin, distance, radiusKm);
        return ResponseEntity.ok(response);
    }
} 