package com.nearprop.repository.franchisee;

import com.nearprop.entity.FranchiseeDistrict;
import com.nearprop.entity.FranchiseeDistrict.FranchiseeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.query.Param;

@Repository
public interface FranchiseeDistrictRepository extends JpaRepository<FranchiseeDistrict, Long> {
    
    List<FranchiseeDistrict> findByUserId(Long userId);
    
    List<FranchiseeDistrict> findByDistrictId(Long districtId);
    
    Optional<FranchiseeDistrict> findByUserIdAndDistrictId(Long userId, Long districtId);
    
    List<FranchiseeDistrict> findByStatus(FranchiseeStatus status);
    
    List<FranchiseeDistrict> findByActive(boolean active);
    
    /**
     * Find all active franchisee districts
     * 
     * @return List of active franchisee districts
     */
    List<FranchiseeDistrict> findByActiveTrue();
    
    @Query("SELECT fd FROM FranchiseeDistrict fd WHERE fd.endDate <= :date AND fd.active = true")
    List<FranchiseeDistrict> findExpiredFranchises(LocalDateTime date);
    
    @Query("SELECT fd FROM FranchiseeDistrict fd WHERE fd.status = :status AND fd.active = true")
    Page<FranchiseeDistrict> findActiveByStatus(FranchiseeStatus status, Pageable pageable);
    
    @Query("SELECT fd FROM FranchiseeDistrict fd WHERE fd.districtId = :districtId AND fd.active = true")
    Optional<FranchiseeDistrict> findActiveFranchiseeForDistrict(Long districtId);
    
    @Query("SELECT COUNT(fd) > 0 FROM FranchiseeDistrict fd WHERE fd.districtId = :districtId AND fd.active = true")
    boolean existsActiveFranchiseeForDistrict(Long districtId);
    
    @Query("SELECT fd FROM FranchiseeDistrict fd " +
           "WHERE fd.totalProperties >= :minProperties " +
           "AND fd.totalTransactions >= :minTransactions")
    List<FranchiseeDistrict> findTopPerformingFranchisees(Integer minProperties, Integer minTransactions, Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.district.id = :districtId")
    Long countPropertiesByDistrictId(Long districtId);

    // Fetch only active franchisee districts for a user
    @Query("SELECT fd FROM FranchiseeDistrict fd WHERE fd.user.id = :userId AND fd.active = true")
    List<FranchiseeDistrict> findByUserIdAndActiveTrue(@Param("userId") Long userId);

    // Fetch only active franchisee district by districtId
    @Query("SELECT fd FROM FranchiseeDistrict fd WHERE fd.districtId = :districtId AND fd.active = true")
    Optional<FranchiseeDistrict> findActiveFranchiseeByDistrictId(@Param("districtId") Long districtId);

    @Query("SELECT COUNT(fd) > 0 FROM FranchiseeDistrict fd " +
    	       "WHERE fd.user.id = :userId AND fd.districtId = :districtId AND fd.active = true")
    	boolean existsActiveFranchiseeForUserAndDistrict(@Param("userId") Long userId,
    	                                                @Param("districtId") Long districtId);

    @Query("""
    SELECT fd FROM FranchiseeDistrict fd 
    WHERE fd.active = true 
      AND fd.status = 'ACTIVE' 
      AND fd.districtId IS NOT NULL
    ORDER BY fd.updatedAt DESC
    """)
    List<FranchiseeDistrict> findAllActiveFranchiseesWithDistrict();

} 
