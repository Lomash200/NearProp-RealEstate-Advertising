package com.nearprop.repository;

import com.nearprop.entity.PropertyUpdateRequest;
import com.nearprop.entity.PropertyUpdateRequest.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PropertyUpdateRequestRepository extends JpaRepository<PropertyUpdateRequest, Long> {
    
    Page<PropertyUpdateRequest> findByPropertyId(Long propertyId, Pageable pageable);
    
    Page<PropertyUpdateRequest> findByRequestedById(Long userId, Pageable pageable);
    
    Page<PropertyUpdateRequest> findByStatus(RequestStatus status, Pageable pageable);
    
    Optional<PropertyUpdateRequest> findByRequestId(String requestId);
    
    List<PropertyUpdateRequest> findByPropertyIdAndStatus(Long propertyId, RequestStatus status);
    
    Page<PropertyUpdateRequest> findByFranchiseeRequestTrueAndFranchiseeId(Long franchiseeId, Pageable pageable);
    
    // New methods for district-based queries
    Page<PropertyUpdateRequest> findByDistrictAndStatus(String district, RequestStatus status, Pageable pageable);
    
    Page<PropertyUpdateRequest> findByDistrict(String district, Pageable pageable);
    
    // Find requests that have not been reviewed by franchisee yet
    Page<PropertyUpdateRequest> findByDistrictAndFranchiseeReviewedFalseAndStatus(
            String district, RequestStatus status, Pageable pageable);
    
    // Find requests that have not been reviewed by admin yet
    Page<PropertyUpdateRequest> findByAdminReviewedFalseAndStatus(RequestStatus status, Pageable pageable);
} 