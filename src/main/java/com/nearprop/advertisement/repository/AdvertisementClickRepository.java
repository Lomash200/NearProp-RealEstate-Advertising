package com.nearprop.advertisement.repository;

import com.nearprop.advertisement.entity.AdvertisementClick;
import com.nearprop.advertisement.entity.AdvertisementClick.ClickType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdvertisementClickRepository extends JpaRepository<AdvertisementClick, Long> {
    
    // Count clicks by advertisement and type
    Long countByAdvertisementIdAndClickType(Long advertisementId, ClickType clickType);
    
    // Count clicks by type across all advertisements
    Long countByClickType(ClickType clickType);
    
    // Count unique users who clicked on an advertisement
    @Query("SELECT COUNT(DISTINCT ac.userId) FROM AdvertisementClick ac WHERE ac.advertisement.id = :adId AND ac.userId IS NOT NULL")
    Long countUniqueUsersByAdvertisementId(@Param("adId") Long advertisementId);
    
    // Count logged-in users who interacted with an advertisement
    @Query("SELECT COUNT(DISTINCT ac.userId) FROM AdvertisementClick ac WHERE ac.advertisement.id = :adId AND ac.userId IS NOT NULL")
    Long countLoggedInUsersByAdvertisementId(@Param("adId") Long advertisementId);
    
    // Count anonymous users who interacted with an advertisement
    @Query("SELECT COUNT(ac) FROM AdvertisementClick ac WHERE ac.advertisement.id = :adId AND ac.userId IS NULL")
    Long countAnonymousUsersByAdvertisementId(@Param("adId") Long advertisementId);
    
    // Get clicks grouped by district
    @Query("SELECT ac.userDistrict, COUNT(ac) FROM AdvertisementClick ac WHERE ac.advertisement.id = :adId AND ac.userDistrict IS NOT NULL GROUP BY ac.userDistrict")
    List<Object[]> countClicksByDistrict(@Param("adId") Long advertisementId);
    
    // Get clicks grouped by date
    @Query("SELECT FUNCTION('DATE', ac.createdAt) as date, COUNT(ac) FROM AdvertisementClick ac WHERE ac.advertisement.id = :adId GROUP BY FUNCTION('DATE', ac.createdAt)")
    List<Object[]> countClicksByDate(@Param("adId") Long advertisementId);
    
    // Get clicks grouped by date and type
    @Query("SELECT FUNCTION('DATE', ac.createdAt) as date, ac.clickType, COUNT(ac) FROM AdvertisementClick ac WHERE ac.advertisement.id = :adId GROUP BY FUNCTION('DATE', ac.createdAt), ac.clickType")
    List<Object[]> countClicksByDateAndType(@Param("adId") Long advertisementId);
    
    // Find clicks between dates
    List<AdvertisementClick> findByAdvertisementIdAndCreatedAtBetween(
            Long advertisementId, LocalDateTime startDate, LocalDateTime endDate);
    
    // Find clicks by advertisement ID
    List<AdvertisementClick> findByAdvertisementId(Long advertisementId);
} 