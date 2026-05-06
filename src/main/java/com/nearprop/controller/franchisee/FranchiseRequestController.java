package com.nearprop.controller.franchisee;

import com.nearprop.dto.franchisee.CreateFranchiseRequestDto;
import com.nearprop.dto.franchisee.FranchiseRequestDto;
import com.nearprop.entity.FranchiseRequest.RequestStatus;
import com.nearprop.entity.User;
import com.nearprop.service.franchisee.FranchiseRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/franchisee/requests")
@RequiredArgsConstructor
@Slf4j
public class FranchiseRequestController {
    
    private final FranchiseRequestService franchiseRequestService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FranchiseRequestDto> submitRequest(
            @RequestParam("districtId") Long districtId,
            @RequestParam("businessName") String businessName,
            @RequestParam("businessAddress") String businessAddress,
            @RequestParam(value = "businessRegistrationNumber", required = false) String businessRegistrationNumber,
            @RequestParam(value = "gstNumber", required = false) String gstNumber,
            @RequestParam("panNumber") String panNumber,
            @RequestParam("aadharNumber") String aadharNumber,
            @RequestParam("contactEmail") String contactEmail,
            @RequestParam("contactPhone") String contactPhone,
            @RequestParam("yearsOfExperience") Integer yearsOfExperience,
            @RequestParam(value = "documents", required = false) List<MultipartFile> documents,
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Received franchise request submission for district: {}, business: {}", districtId, businessName);
        if (documents != null) {
            log.info("Received {} documents", documents.size());
            for (MultipartFile doc : documents) {
                log.info("Document: {}, size: {}, content type: {}", 
                         doc.getOriginalFilename(), doc.getSize(), doc.getContentType());
            }
        } else {
            log.info("No documents received");
        }
        
        // Create DTO from individual parts
        CreateFranchiseRequestDto requestDto = CreateFranchiseRequestDto.builder()
                .districtId(districtId)
                .businessName(businessName)
                .businessAddress(businessAddress)
                .businessRegistrationNumber(businessRegistrationNumber)
                .gstNumber(gstNumber)
                .panNumber(panNumber)
                .aadharNumber(aadharNumber)
                .contactEmail(contactEmail)
                .contactPhone(contactPhone)
                .yearsOfExperience(yearsOfExperience)
                .build();
        
        return ResponseEntity.ok(franchiseRequestService.submitRequest(requestDto, documents, currentUser.getId()));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'FRANCHISEE')")
    public ResponseEntity<FranchiseRequestDto> getRequest(
            @PathVariable("id") Long requestId,
            @AuthenticationPrincipal User currentUser) {
        
        return ResponseEntity.ok(franchiseRequestService.getRequest(requestId));
    }
    
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FranchiseRequestDto>> getRequestsByStatus(
            @PathVariable RequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortBy);
        
        return ResponseEntity.ok(franchiseRequestService.getRequestsByStatus(status, pageable));
    }
    
    @GetMapping("/my-requests")
    @PreAuthorize("hasAnyRole('USER', 'FRANCHISEE')")
    public ResponseEntity<Page<FranchiseRequestDto>> getMyRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @AuthenticationPrincipal User currentUser) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortBy);
        
        return ResponseEntity.ok(franchiseRequestService.getUserRequests(currentUser.getId(), pageable));
    }
    
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FranchiseRequestDto> approveRequest(
            @PathVariable("id") Long requestId,
            @RequestParam(required = false) String comments,
            @RequestParam(required = false) String endDate,
            @AuthenticationPrincipal User currentUser) {
        
        return ResponseEntity.ok(franchiseRequestService.approveRequest(requestId, comments, endDate, currentUser.getId()));
    }
    
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FranchiseRequestDto> rejectRequest(
            @PathVariable("id") Long requestId,
            @RequestParam String comments,
            @AuthenticationPrincipal User currentUser) {
        
        return ResponseEntity.ok(franchiseRequestService.rejectRequest(requestId, comments, currentUser.getId()));
    }
    
    @GetMapping("/district/{districtId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FranchiseRequestDto>> getRequestsByDistrict(
            @PathVariable Long districtId) {
        
        return ResponseEntity.ok(franchiseRequestService.getRequestsByDistrict(districtId));
    }
    
    @GetMapping("/is-district-assigned/{districtId}")
    public ResponseEntity<Map<String, Boolean>> isDistrictAssigned(
            @PathVariable Long districtId) {
        
        boolean isAssigned = franchiseRequestService.isDistrictAssigned(districtId);
        return ResponseEntity.ok(Map.of("assigned", isAssigned));
    }
    
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<RequestStatus, Long>> getRequestStatistics() {
        return ResponseEntity.ok(franchiseRequestService.getRequestStatistics());
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'FRANCHISEE')")
    public ResponseEntity<Void> cancelRequest(
            @PathVariable("id") Long requestId,
            @AuthenticationPrincipal User currentUser) {
        
        franchiseRequestService.cancelRequest(requestId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping(value = "/test", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> testEndpoint() {
        return ResponseEntity.ok(Map.of("status", "success", "message", "Test endpoint working"));
    }
} 