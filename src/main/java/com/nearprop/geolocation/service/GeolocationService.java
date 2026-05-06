package com.nearprop.geolocation.service;

import java.util.Optional;

public interface GeolocationService {
    
    /**
     * Geocode an address to coordinates
     * 
     * @param address The address to geocode
     * @return Optional with latitude and longitude if successful
     */
    Optional<double[]> geocodeAddress(String address);
    
    /**
     * Calculate distance between two points
     * 
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    double calculateDistance(double lat1, double lon1, double lat2, double lon2);
    
    /**
     * Check if a location is within a radius of another location
     * 
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @param radiusKm Radius in kilometers
     * @return True if the points are within the radius
     */
    boolean isWithinRadius(double lat1, double lon1, double lat2, double lon2, double radiusKm);
} 