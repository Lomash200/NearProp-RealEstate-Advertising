package com.nearprop.repository;

import com.nearprop.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    
    List<District> findByActiveTrue();
    
    List<District> findByCity(String city);
    
    List<District> findByState(String state);
    
    Optional<District> findByNameAndCity(String name, String city);
    
    @Query(value = "SELECT d.* FROM districts d WHERE d.active = true AND " +
           "earth_distance(ll_to_earth(d.latitude, d.longitude), ll_to_earth(?1, ?2)) <= ?3 * 1000", 
           nativeQuery = true)
    List<District> findNearbyDistricts(Double latitude, Double longitude, Double radiusKm);
} 