package com.nearprop.service.franchisee;

import com.nearprop.dto.franchisee.CreateFranchiseRequestDto;
import com.nearprop.dto.franchisee.FranchiseRequestDto;
import com.nearprop.entity.FranchiseRequest.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FranchiseRequestService {
    
    /**
     * Submit a new franchise request
     * 
     * @param requestDto Request details
     * @param documents Optional list of document files
     * @param userId User ID submitting the request
     * @return Created franchise request
     */
    FranchiseRequestDto submitRequest(CreateFranchiseRequestDto requestDto, List<MultipartFile> documents, Long userId);
    
    /**
     * Get request by ID
     * 
     * @param requestId Request ID
     * @return Franchise request details
     */
    FranchiseRequestDto getRequest(Long requestId);
    
    /**
     * Get all requests by status with pagination
     * 
     * @param status Request status
     * @param pageable Pagination information
     * @return Page of franchise requests
     */
    Page<FranchiseRequestDto> getRequestsByStatus(RequestStatus status, Pageable pageable);
    
    /**
     * Get all franchise requests from a user
     * 
     * @param userId User ID
     * @param pageable Pagination information
     * @return Page of franchise requests
     */
    Page<FranchiseRequestDto> getUserRequests(Long userId, Pageable pageable);
    
    /**
     * Approve a franchise request (admin only)
     * 
     * @param requestId Request ID
     * @param comments Optional comments
     * @param endDate Optional end date in yyyy-MM-dd format
     * @param adminId Admin user ID
     * @return Updated franchise request
     */
    FranchiseRequestDto approveRequest(Long requestId, String comments, String endDate, Long adminId);
    
    /**
     * Reject a franchise request (admin only)
     * 
     * @param requestId Request ID
     * @param comments Rejection reason
     * @param adminId Admin user ID
     * @return Updated franchise request
     */
    FranchiseRequestDto rejectRequest(Long requestId, String comments, Long adminId);
    
    /**
     * Check if a district already has an approved franchisee
     * 
     * @param districtId District ID
     * @return true if district has an approved franchisee
     */
    boolean isDistrictAssigned(Long districtId);
    
    /**
     * Get requests for a district
     * 
     * @param districtId District ID
     * @return List of franchise requests for the district
     */
    List<FranchiseRequestDto> getRequestsByDistrict(Long districtId);
    
    /**
     * Get request statistics
     * 
     * @return Map of status and count
     */
    Map<RequestStatus, Long> getRequestStatistics();
    
    /**
     * Cancel a franchise request (user can only cancel their own pending requests)
     * 
     * @param requestId Request ID
     * @param userId User ID
     */
    void cancelRequest(Long requestId, Long userId);

    FranchiseRequestDto approveRequestBySubAdmin(Long requestId, String comments, String endDate, Long subAdminId);

    FranchiseRequestDto rejectRequestBySubAdmin(Long requestId, String comments, Long subAdminId);

} 