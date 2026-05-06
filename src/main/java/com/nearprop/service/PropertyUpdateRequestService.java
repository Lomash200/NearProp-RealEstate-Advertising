package com.nearprop.service;

import com.nearprop.dto.CreatePropertyUpdateRequestDto;
import com.nearprop.dto.PropertyUpdateRequestDto;
import com.nearprop.dto.ReviewPropertyUpdateRequestDto;
import com.nearprop.entity.PropertyUpdateRequest.RequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PropertyUpdateRequestService {
    
    /**
     * Create a new property update request
     * 
     * @param requestDto The request details
     * @param images New images to upload (optional)
     * @param videoFile New video to upload (optional)
     * @param userId ID of the user creating the request
     * @return The created request
     */
    PropertyUpdateRequestDto createUpdateRequest(
            CreatePropertyUpdateRequestDto requestDto, 
            List<MultipartFile> images,
            MultipartFile videoFile,
            Long userId);
    
    /**
     * Create a new property update request on behalf of a property owner (franchisee only)
     * 
     * @param ownerPermanentId The permanent ID of the property owner
     * @param requestDto The request details
     * @param images New images to upload (optional)
     * @param videoFile New video to upload (optional)
     * @param franchiseeId ID of the franchisee creating the request
     * @return The created request
     */
    PropertyUpdateRequestDto createUpdateRequestOnBehalf(
            String ownerPermanentId,
            CreatePropertyUpdateRequestDto requestDto, 
            List<MultipartFile> images,
            MultipartFile videoFile,
            Long franchiseeId);
    
    /**
     * Get a property update request by ID
     * 
     * @param requestId The request ID
     * @return The request
     */
    PropertyUpdateRequestDto getUpdateRequest(Long requestId);
    
    /**
     * Get a property update request by permanent ID
     * 
     * @param requestId The permanent request ID
     * @return The request
     */
    PropertyUpdateRequestDto getUpdateRequestByPermanentId(String requestId);
    
    /**
     * Get all property update requests for a property
     * 
     * @param propertyId The property ID
     * @param pageable Pagination information
     * @return Page of requests
     */
    Page<PropertyUpdateRequestDto> getUpdateRequestsByProperty(Long propertyId, Pageable pageable);
    
    /**
     * Get all property update requests created by a user
     * 
     * @param userId The user ID
     * @param pageable Pagination information
     * @return Page of requests
     */
    Page<PropertyUpdateRequestDto> getUpdateRequestsByUser(Long userId, Pageable pageable);
    
    /**
     * Get all property update requests with a specific status
     * 
     * @param status The request status
     * @param pageable Pagination information
     * @return Page of requests
     */
    Page<PropertyUpdateRequestDto> getUpdateRequestsByStatus(RequestStatus status, Pageable pageable);
    
    /**
     * Get all pending property update requests for a specific district
     * 
     * @param district The district name
     * @param status The request status
     * @param pageable Pagination information
     * @return Page of requests
     */
    Page<PropertyUpdateRequestDto> getUpdateRequestsByDistrictAndStatus(
            String district, RequestStatus status, Pageable pageable);
    
    /**
     * Get all property update requests for a specific district
     * 
     * @param district The district name
     * @param pageable Pagination information
     * @return Page of requests
     */
    Page<PropertyUpdateRequestDto> getUpdateRequestsByDistrict(String district, Pageable pageable);
    
    /**
     * Get all pending property update requests that haven't been reviewed by franchisee yet
     * 
     * @param district The district name
     * @param pageable Pagination information
     * @return Page of requests
     */
    Page<PropertyUpdateRequestDto> getPendingRequestsForFranchiseeReview(String district, Pageable pageable);
    
    /**
     * Get all pending property update requests that haven't been reviewed by admin yet
     * 
     * @param pageable Pagination information
     * @return Page of requests
     */
    Page<PropertyUpdateRequestDto> getPendingRequestsForAdminReview(Pageable pageable);
    
    /**
     * Review a property update request (admin or franchisee)
     * 
     * @param reviewDto The review details
     * @param reviewerId ID of the admin/franchisee reviewing the request
     * @return The updated request
     */
    PropertyUpdateRequestDto reviewUpdateRequest(ReviewPropertyUpdateRequestDto reviewDto, Long reviewerId);
    
    /**
     * Cancel a property update request
     * 
     * @param requestId The request ID
     * @param userId ID of the user cancelling the request
     * @return The cancelled request
     */
    PropertyUpdateRequestDto cancelUpdateRequest(Long requestId, Long userId);
} 