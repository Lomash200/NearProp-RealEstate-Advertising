package com.nearprop.repository.franchisee;

import com.nearprop.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LegacyDistrictRepository extends JpaRepository<District, Long> {
    
    List<District> findByActiveTrue();
    
    List<District> findByCity(String city);
    
    List<District> findByState(String state);
    
    Optional<District> findByNameAndCity(String name, String city);
    
    @Query("SELECT d FROM District d WHERE d.active = true AND " +
           "function('earth_distance', " +
           "function('ll_to_earth', d.latitude, d.longitude), " +
           "function('ll_to_earth', :lat, :lng)) <= :radiusMeters")
    List<District> findNearby(Double lat, Double lng, Double radiusMeters);
    
    @Query("SELECT d FROM District d WHERE d.active = true AND " +
           "d.id IN (SELECT fd.districtId FROM FranchiseeDistrict fd WHERE fd.user.id = :franchiseeId AND fd.active = true)")
    List<District> findByFranchiseeId(Long franchiseeId);
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.district.id = :districtId")
    Long countPropertiesByDistrict(Long districtId);
    
    Page<District> findByActive(boolean active, Pageable pageable);
} 