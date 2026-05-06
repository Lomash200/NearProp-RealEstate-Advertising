package com.nearprop.repository;

import com.nearprop.entity.Property;
import com.nearprop.entity.PropertyStatus;
import com.nearprop.entity.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long>, JpaSpecificationExecutor<Property> {
    Page<Property> findByApprovedTrue(Pageable pageable);
    
    List<Property> findByApprovedTrue();
    
    Page<Property> findByApprovedTrueAndActiveTrue(Pageable pageable);
    
    List<Property> findByApprovedTrueAndActiveTrue();
    
    Page<Property> findByApprovedTrueAndFeaturedTrue(Pageable pageable);
    
    List<Property> findByApprovedTrueAndFeaturedTrue();
    
    Page<Property> findByApprovedFalse(Pageable pageable);
    
    List<Property> findByApprovedFalse();
    
    Page<Property> findByApprovedFalseAndActiveTrue(Pageable pageable);
    
    List<Property> findByApprovedFalseAndActiveTrue();
    
    Page<Property> findByOwnerId(Long ownerId, Pageable pageable);
    
    List<Property> findByOwnerId(Long ownerId);
    
    // Find by permanentId
    Optional<Property> findByPermanentId(String permanentId);
    
    // Check if exists by permanentId
    boolean existsByPermanentId(String permanentId);
    
    @Query("SELECT p FROM Property p WHERE p.approved = true AND p.status = :status")
    Page<Property> findByStatus(PropertyStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Property p WHERE p.approved = true AND p.type = :type")
    Page<Property> findByType(PropertyType type, Pageable pageable);
    
    @Query("SELECT p FROM Property p WHERE p.approved = true AND p.district = :district")
    Page<Property> findByDistrict(String district, Pageable pageable);
    
    @Query("SELECT p FROM Property p WHERE p.approved = true AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Property> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    @Query("SELECT p FROM Property p WHERE p.approved = true AND p.bedrooms >= :minBedrooms")
    Page<Property> findByMinBedrooms(Integer minBedrooms, Pageable pageable);
    
    @Query("SELECT COUNT(p) > 0 FROM Property p WHERE p.id = :id AND p.owner.id = :ownerId")
    boolean isPropertyOwnedByUser(Long id, Long ownerId);
    
    @Query(value = "SELECT DISTINCT district FROM properties ORDER BY district", nativeQuery = true)
    List<String> findAllDistricts();
    
    Page<Property> findByIdIn(Set<Long> propertyIds, Pageable pageable);
    
    // New methods for subscription handling
    List<Property> findBySubscriptionExpiryBefore(LocalDateTime dateTime);
    
    /**
     * Find properties with subscription expiry before the given date and are currently active
     * 
     * @param dateTime The date to check against
     * @return List of properties with expired subscriptions that are still active
     */
    List<Property> findBySubscriptionExpiryBeforeAndActiveTrue(LocalDateTime dateTime);
    
    List<Property> findByScheduledDeletionBefore(LocalDateTime dateTime);
    
    @Query("SELECT p FROM Property p WHERE p.active = false AND p.scheduledDeletion IS NULL")
    List<Property> findInactivePropertiesWithoutScheduledDeletion();
    
    /**
     * Find inactive properties by owner ID
     * 
     * @param ownerId The owner ID
     * @return List of inactive properties
     */
    List<Property> findByOwnerIdAndActiveIsFalse(Long ownerId);
    
    @Query("SELECT p FROM Property p WHERE p.subscriptionId = :subscriptionId")
    List<Property> findBySubscriptionId(@Param("subscriptionId") Long subscriptionId);
    
    List<Property> findByOwnerIdAndActiveFalse(Long ownerId);
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.owner.id = :ownerId AND p.subscriptionId = :subscriptionId")
    long countByOwnerIdAndSubscriptionId(@Param("ownerId") Long ownerId, @Param("subscriptionId") Long subscriptionId);
    
    // Count methods for statistics
    Long countByOwnerId(Long ownerId);
    
    
    Long countByOwnerIdAndApprovedFalse(Long ownerId);
    
    List<Property> findByOwnerIdAndDistrictId(Long ownerId, Long districtId);

    @Query(value = "SELECT *, ST_Distance(geog, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) AS distance " +
           "FROM properties " +
           "WHERE active = true AND approved = true AND geog IS NOT NULL " +
           "ORDER BY distance ASC", nativeQuery = true)
    List<Property> findAllOrderByDistance(@Param("lat") Double latitude, @Param("lon") Double longitude);

    @Query(value = "SELECT *, ST_Distance(geog, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)) AS distance " +
           "FROM properties " +
           "WHERE active = true AND approved = true AND geog IS NOT NULL " +
           "AND ST_DWithin(geog, ST_SetSRID(ST_MakePoint(:lon, :lat), 4326), :radius) " +
           "ORDER BY distance ASC", nativeQuery = true)
    List<Property> findAllWithinRadius(@Param("lat") Double latitude, @Param("lon") Double longitude, @Param("radius") Double radius);
	// Sold properties for a user
	@Query("SELECT COUNT(p) FROM Property p WHERE p.owner.id = :ownerId AND p.status = 'SOLD'")
	int countSoldProperties(@Param("ownerId") Long ownerId);
	// Active properties for a user
    int countByOwnerIdAndActiveTrue(Long ownerId);
	// Total properties for a user
	@Query("SELECT COUNT(p) FROM Property p WHERE p.owner.id = :ownerId")
	int countByOwnerId1(@Param("ownerId") Long ownerId);
	@Query("SELECT COUNT(p) FROM Property p WHERE p.owner.id = :ownerId AND p.subscriptionExpiry BETWEEN :now AND :futureDate")
	int countExpiringProperties(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now,
			@Param("futureDate") LocalDateTime futureDate);


   List<Property> findByDistrictIdIn(List<Long> districtIds);

    long countByDistrictIdIn(List<Long> districtIds);


} 
