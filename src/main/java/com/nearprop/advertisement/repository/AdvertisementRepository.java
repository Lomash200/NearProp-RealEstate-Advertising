package com.nearprop.advertisement.repository;

import com.nearprop.advertisement.entity.Advertisement;
import com.nearprop.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    
    // Find active advertisements
    Page<Advertisement> findByActiveTrue(Pageable pageable);
    
    // Find advertisements by creator
    Page<Advertisement> findByCreatedById(Long userId, Pageable pageable);
    
    // Find advertisements by creator (non-paginated)
    List<Advertisement> findByCreatedById(Long userId);
    
    // Find advertisements by district name
    Page<Advertisement> findByDistrictName(String districtName, Pageable pageable);
    
    // Find advertisements by district name (non-paginated)
    List<Advertisement> findByDistrictName(String districtName);
    
    // Find advertisements by district name that are active and valid
    Page<Advertisement> findByActiveTrueAndDistrictNameAndValidFromBeforeAndValidUntilAfter(
            String districtName, LocalDateTime now, LocalDateTime now2, Pageable pageable);
    
    // Find all active and valid advertisements
    List<Advertisement> findByActiveTrueAndValidFromBeforeAndValidUntilAfter(
            LocalDateTime now, LocalDateTime now2, Pageable pageable);
    
    // Find advertisements near a location
    @Query(value = "SELECT a FROM Advertisement a WHERE " +
            "a.active = true AND " +
            "a.validFrom <= CURRENT_TIMESTAMP AND " +
            "a.validUntil > CURRENT_TIMESTAMP AND " +
            "((6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) * " +
            "cos(radians(a.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(a.latitude))))) <= a.radiusKm")
    Page<Advertisement> findNearLocation(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            Pageable pageable);
    
    // Find advertisements expiring soon
    List<Advertisement> findByActiveTrueAndValidUntilBeforeAndExpiryNotificationSentFalse(
            LocalDateTime expiryDate);
    
    // Find advertisements with day-before notification not sent
    List<Advertisement> findByActiveTrueAndValidUntilBeforeAndDayBeforeNotificationSentFalse(
            LocalDateTime notificationDate);
    
    // Find advertisements with hours-before notification not sent
    List<Advertisement> findByActiveTrueAndValidUntilBeforeAndHoursBeforeNotificationSentFalse(
            LocalDateTime notificationDate);
    
    // Custom query to find advertisements near a specific location (lat/long)
    @Query(value = "SELECT a.* FROM advertisements a " +
           "WHERE a.active = true " +
           "AND a.valid_from <= :now " +
           "AND a.valid_until >= :now " +
           "AND (6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) * " +
           "cos(radians(a.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(a.latitude)))) <= a.radius_km " +
           "ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(a.latitude)) * " +
           "cos(radians(a.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(a.latitude)))) ASC",
           nativeQuery = true)
    Page<Advertisement> findActiveAdsNearLocation(
            @Param("latitude") Double latitude, 
            @Param("longitude") Double longitude, 
            @Param("now") LocalDateTime now,
            Pageable pageable);
    
    // Count advertisements by district
    @Query("SELECT COUNT(a) FROM Advertisement a WHERE a.districtName = :districtName AND a.active = true AND a.validFrom <= :now AND a.validUntil >= :now")
    Long countActiveAdsByDistrict(@Param("districtName") String districtName, @Param("now") LocalDateTime now);
    
    // Find active ads by district with limit of 5
    @Query(value = "SELECT * FROM advertisements a " +
           "WHERE a.active = true " +
           "AND a.district_name = :districtName " +
           "AND a.valid_from <= :now " +
           "AND a.valid_until >= :now " +
           "ORDER BY a.created_at DESC " +
           "LIMIT 5",
           nativeQuery = true)
    List<Advertisement> findTop5ActiveAdsByDistrict(
            @Param("districtName") String districtName, 
            @Param("now") LocalDateTime now);
    
    // Find active ads where district is in the target districts
    @Query("SELECT a FROM Advertisement a JOIN a.targetDistricts d WHERE a.active = true AND a.validFrom <= :now AND a.validUntil >= :now AND d.name = :districtName")
    Page<Advertisement> findByActiveTrueAndTargetDistrictsContainingAndValidFromBeforeAndValidUntilAfter(
            @Param("districtName") String districtName, 
            @Param("now") LocalDateTime now, 
            Pageable pageable);
    
    // Find ads expiring soon (for email notifications)
    @Query("SELECT a FROM Advertisement a WHERE a.active = true AND a.validUntil BETWEEN :start AND :end AND a.dayBeforeNotificationSent = false")
    List<Advertisement> findAdsExpiringInOneDay(
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
    
    @Query("SELECT a FROM Advertisement a WHERE a.active = true AND a.validUntil BETWEEN :start AND :end AND a.hoursBeforeNotificationSent = false")
    List<Advertisement> findAdsExpiringInFiveHours(
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
    
    // Find expired ads that haven't been notified yet
    @Query("SELECT a FROM Advertisement a WHERE a.active = true AND a.validUntil < :now AND a.expiryNotificationSent = false")
    List<Advertisement> findExpiredAdsNotNotified(@Param("now") LocalDateTime now);
    
    @Query("SELECT COUNT(a) FROM Advertisement a WHERE a.createdBy.id = :createdById")
    Long countByCreatedById(Long createdById);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM Advertisement a WHERE a.createdBy.id = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}