package com.nearprop.repository.franchisee;

import com.nearprop.entity.FranchiseRequest;

import com.nearprop.entity.FranchiseRequest.RequestStatus;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FranchiseRequestRepository extends JpaRepository<FranchiseRequest, Long> {
    
    Page<FranchiseRequest> findByStatus(RequestStatus status, Pageable pageable);
    
    Page<FranchiseRequest> findByUserId(Long userId, Pageable pageable);
    
    Optional<FranchiseRequest> findByUserIdAndDistrictId(Long userId, Long districtId);
    
    List<FranchiseRequest> findByDistrictId(Long districtId);
    
    @Query("SELECT COUNT(f) > 0 FROM FranchiseRequest f WHERE f.districtId = :districtId AND f.status = 'APPROVED'")
    boolean existsApprovedRequestByDistrictId(Long districtId);
    
    @Query("SELECT f FROM FranchiseRequest f WHERE f.districtId = :districtId AND f.status = 'APPROVED'")
    Optional<FranchiseRequest> findApprovedRequestByDistrictId(Long districtId);
    
    @Query("SELECT f FROM FranchiseRequest f WHERE f.user.id = :userId AND f.status = 'APPROVED' ORDER BY f.updatedAt DESC")
    Optional<FranchiseRequest> findApprovedByUserId(Long userId);
    
    @Query("SELECT COUNT(f) FROM FranchiseRequest f WHERE f.status = :status")
    Long countByStatus(RequestStatus status);
    
    Optional<FranchiseRequest> findFirstByUserIdAndStatusOrderByUpdatedAtDesc(Long userId, RequestStatus status);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM FranchiseRequest f WHERE f.user.id = :userId AND f.districtId = :districtId")
    void deleteByUserIdAndDistrictId(Long userId, Long districtId);


    
}
