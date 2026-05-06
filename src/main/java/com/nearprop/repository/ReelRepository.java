//package com.nearprop.repository;
//
//import com.nearprop.entity.Reel;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface ReelRepository extends JpaRepository<Reel, Long> {
//
//    Page<Reel> findByPropertyId(Long propertyId, Pageable pageable);
//
//    Page<Reel> findByOwnerId(Long userId, Pageable pageable);
//
//    Page<Reel> findByStatus(Reel.ReelStatus status, Pageable pageable);
//
//    @Query("SELECT r FROM Reel r WHERE r.id NOT IN :ids")
//    List<Reel> findByIdNotIn(@Param("ids") List<Long> ids, Pageable pageable);
//
//    @Query("SELECT r FROM Reel r WHERE r.property.id = :propertyId AND r.status = 'PUBLISHED'")
//    List<Reel> findPublishedReelsByPropertyId(@Param("propertyId") Long propertyId);
//
//    @Query("SELECT COUNT(r) FROM Reel r WHERE r.property.id = :propertyId AND r.status = 'PUBLISHED'")
//    Long countPublishedReelsByPropertyId(@Param("propertyId") Long propertyId);
//
//    @Query("SELECT COUNT(r) FROM Reel r WHERE r.owner.id = :userId")
//    Long countReelsByUserId(@Param("userId") Long userId);
//
//    Optional<Reel> findByPublicId(String publicId);
//
//    // Find reels by city
//    @Query("SELECT r FROM Reel r WHERE r.city = :city AND r.status = 'PUBLISHED'")
//    Page<Reel> findByCity(@Param("city") String city, Pageable pageable);
//
//    // Find reels by district
//    @Query("SELECT r FROM Reel r WHERE r.district = :district AND r.status = 'PUBLISHED'")
//    Page<Reel> findByDistrict(@Param("district") String district, Pageable pageable);
//
//    // Find reels by state
//    @Query("SELECT r FROM Reel r WHERE r.state = :state AND r.status = 'PUBLISHED'")
//    Page<Reel> findByState(@Param("state") String state, Pageable pageable);
//
//    // Find reels by coordinates with distance calculation using the Haversine formula
//    @Query(value = "SELECT r.*, " +
//           "(6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) * " +
//           "cos(radians(r.longitude) - radians(:longitude)) + " +
//           "sin(radians(:latitude)) * sin(radians(r.latitude)))) AS distance " +
//           "FROM property_reels r WHERE r.status = 'PUBLISHED' " +
//           "HAVING distance < :radiusKm " +
//           "ORDER BY distance",
//           nativeQuery = true)
//    List<Reel> findNearbyReels(
//            @Param("latitude") Double latitude,
//            @Param("longitude") Double longitude,
//            @Param("radiusKm") Double radiusKm);
//
//    // Find reels by coordinates with distance calculation using the Haversine formula
//    @Query(value = "SELECT r.*, " +
//           "( 6371 * acos( cos( radians(:lat) ) * cos( radians( r.latitude ) ) " +
//           "* cos( radians( r.longitude ) - radians(:lon) ) + sin( radians(:lat) ) " +
//           "* sin( radians( r.latitude ) ) ) ) AS distance " +
//           "FROM Reel r " +
//           "WHERE r.status = 'PUBLISHED' " +
//           "HAVING distance < :radius " +
//           "ORDER BY distance",
//           countQuery = "SELECT COUNT(*) FROM Reel r " +
//           "WHERE ( 6371 * acos( cos( radians(:lat) ) * cos( radians( r.latitude ) ) " +
//           "* cos( radians( r.longitude ) - radians(:lon) ) + sin( radians(:lat) ) " +
//           "* sin( radians( r.latitude ) ) ) ) < :radius " +
//           "AND r.status = 'PUBLISHED'",
//           nativeQuery = true)
//    Page<Object[]> findReelsWithinRadius(
//            @Param("lat") Double latitude,
//            @Param("lon") Double longitude,
//            @Param("radius") Double radiusKm,
//            Pageable pageable);
//
//    // Fetch all published reels (no pagination)
//    List<Reel> findByStatus(Reel.ReelStatus status);
//    @Query("SELECT SUM(r.viewCount) FROM Reel r WHERE r.owner.id = :userId")
//	Optional<Long> sumViewCountsByOwnerId(@Param("userId") Long userId);
//
//    // Find all active reels by propertyId
//    @Query("SELECT r FROM Reel r WHERE r.property.id = :propertyId AND r.status = 'PUBLISHED'")
//    List<Reel> findActiveReelsByPropertyId(@Param("propertyId") Long propertyId);
//
//}
package com.nearprop.repository;

import com.nearprop.entity.Reel;
import com.nearprop.entity.Reel.ReelStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReelRepository extends JpaRepository<Reel, Long> {

    /* ================= BASIC ================= */

    Page<Reel> findByPropertyId(Long propertyId, Pageable pageable);
    Page<Reel> findByOwnerId(Long userId, Pageable pageable);
    Page<Reel> findByStatus(ReelStatus status, Pageable pageable);
    List<Reel> findByStatus(ReelStatus status);
    Optional<Reel> findByPublicId(String publicId);

    @Query("SELECT r FROM Reel r WHERE r.id NOT IN :ids")
    List<Reel> findByIdNotIn(@Param("ids") List<Long> ids, Pageable pageable);

    /* ================= PROPERTY BASED ================= */

    @Query("SELECT r FROM Reel r WHERE r.property.id = :propertyId AND r.status = :status")
    List<Reel> findReelsByPropertyIdAndStatus(
            @Param("propertyId") Long propertyId,
            @Param("status") ReelStatus status
    );

    @Query("SELECT COUNT(r) FROM Reel r WHERE r.property.id = :propertyId AND r.status = :status")
    Long countReelsByPropertyIdAndStatus(
            @Param("propertyId") Long propertyId,
            @Param("status") ReelStatus status
    );

    /* ================= LOCATION BASED ================= */

    @Query("SELECT r FROM Reel r WHERE r.city = :city AND r.status = :status")
    Page<Reel> findByCityAndStatus(
            @Param("city") String city,
            @Param("status") ReelStatus status,
            Pageable pageable
    );

    @Query("SELECT r FROM Reel r WHERE r.district = :district AND r.status = :status")
    Page<Reel> findByDistrictAndStatus(
            @Param("district") String district,
            @Param("status") ReelStatus status,
            Pageable pageable
    );

    @Query("SELECT r FROM Reel r WHERE r.state = :state AND r.status = :status")
    Page<Reel> findByState(
            @Param("state") String state,
            @Param("status") ReelStatus status,
            Pageable pageable
    );

    /* ================= GEO SEARCH ================= */

    @Query(value = "SELECT r.*, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) * " +
            "cos(radians(r.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(r.latitude)))) AS distance " +
            "FROM property_reels r " +
            "WHERE r.status = :status " +
            "HAVING distance < :radiusKm " +
            "ORDER BY distance",
            nativeQuery = true)
    List<Reel> findNearbyReels(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusKm") Double radiusKm,
            @Param("status") String status
    );

    /* ================= ANALYTICS ================= */

    @Query("SELECT SUM(r.viewCount) FROM Reel r WHERE r.owner.id = :userId")
    Optional<Long> sumViewCountsByOwnerId(@Param("userId") Long userId);

    /* ================= ACTIVE REELS ================= */

    @Query("SELECT r FROM Reel r WHERE r.property.id = :propertyId AND r.status = 'APPROVED'")
    List<Reel> findActiveReelsByPropertyId(@Param("propertyId") Long propertyId);

    /* ================= ADMIN APPROVAL METHODS ================= */

    @Query("SELECT r FROM Reel r WHERE r.property.id = :propertyId AND r.status = :status")
    Page<Reel> findByPropertyIdAndStatus(
            @Param("propertyId") Long propertyId,
            @Param("status") ReelStatus status,
            Pageable pageable
    );

    @Query("SELECT r FROM Reel r WHERE r.owner.id = :ownerId AND r.status = :status")
    Page<Reel> findByOwnerIdAndStatus(
            @Param("ownerId") Long ownerId,
            @Param("status") ReelStatus status,
            Pageable pageable
    );

    @Query(value = "SELECT r.*, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) * " +
            "cos(radians(r.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(r.latitude)))) AS distance " +
            "FROM property_reels r " +
            "WHERE r.status = :status " +
            "HAVING distance < :radius " +
            "ORDER BY distance",
            nativeQuery = true)
    List<Reel> findNearbyReelsWithStatus(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radius") Double radius,
            @Param("status") String status
    );

    @Query(value = "SELECT r.*, " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) * " +
            "cos(radians(r.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(r.latitude)))) AS distance " +
            "FROM property_reels r " +
            "WHERE r.status = 'APPROVED' " +
            "HAVING distance < :radiusKm " +
            "ORDER BY distance",
            countQuery = "SELECT COUNT(*) FROM property_reels r " +
                    "WHERE r.status = 'APPROVED' AND " +
                    "(6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) * " +
                    "cos(radians(r.longitude) - radians(:longitude)) + " +
                    "sin(radians(:latitude)) * sin(radians(r.latitude)))) < :radiusKm",
            nativeQuery = true)
    Page<Object[]> findApprovedReelsWithinRadius(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable
    );

    @Query("SELECT r FROM Reel r WHERE r.id NOT IN :ids AND r.status = :status")
    List<Reel> findByIdNotInAndStatus(
            @Param("ids") List<Long> ids,
            @Param("status") ReelStatus status,
            Pageable pageable
    );

    @Query("SELECT r FROM Reel r WHERE r.property.id = :propertyId")
    List<Reel> findReelsByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT COUNT(r) FROM Reel r WHERE r.property.id = :propertyId")
    Long countReelsByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT COUNT(r) FROM Reel r WHERE r.owner.id = :userId")
    Long countReelsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(r) FROM Reel r WHERE r.property.id = :propertyId AND r.status = 'APPROVED'")
    Long countApprovedReelsByPropertyId(@Param("propertyId") Long propertyId);

    /* ================= DRAFT STATUS SUPPORT ================= */

    @Query("SELECT COUNT(r) FROM Reel r WHERE r.property.id = :propertyId AND r.status = 'DRAFT'")
    Long countDraftReelsByPropertyId(@Param("propertyId") Long propertyId);

    @Query("SELECT r FROM Reel r WHERE r.status = 'DRAFT'")
    List<Reel> findByDraftStatus();

    @Query("SELECT r FROM Reel r WHERE r.status = 'DRAFT' AND r.owner.id = :ownerId")
    List<Reel> findDraftReelsByOwnerId(@Param("ownerId") Long ownerId);
}