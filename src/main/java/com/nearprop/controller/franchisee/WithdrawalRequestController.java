package com.nearprop.controller.franchisee;

import com.nearprop.dto.franchisee.CreateWithdrawalRequestDto;
import com.nearprop.dto.franchisee.WithdrawalRequestDto;
import com.nearprop.dto.franchisee.WithdrawalRequestResponseDto;
import com.nearprop.entity.FranchiseeWithdrawalRequest.WithdrawalStatus;
import com.nearprop.entity.User;
import com.nearprop.service.UserService;
import com.nearprop.service.franchisee.WithdrawalRequestService;
import com.nearprop.service.franchisee.FranchiseeDistrictService;
import com.nearprop.dto.franchisee.FranchiseeDistrictSubscriptionAnalyticsDto;
import com.nearprop.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.nearprop.dto.franchisee.WithdrawalHistoryDto;
import com.nearprop.entity.FranchiseeDistrict;
import org.springframework.security.access.AccessDeniedException;

@RestController
@RequestMapping("/franchisee/withdrawal")
@RequiredArgsConstructor
@Slf4j
public class WithdrawalRequestController {
    
    private final WithdrawalRequestService withdrawalRequestService;
    private final UserService userService;
    private final FranchiseeDistrictService franchiseeDistrictService;
    private final S3Service s3Service;
    
    @PostMapping("/request")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<WithdrawalRequestDto> createWithdrawalRequest(
            @Valid @RequestBody CreateWithdrawalRequestDto requestDto,
            @AuthenticationPrincipal User currentUser) {
        WithdrawalRequestDto response = withdrawalRequestService.createWithdrawalRequest(
                requestDto, currentUser.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<WithdrawalRequestDto> getWithdrawalRequest(
            @PathVariable("id") Long requestId) {
        WithdrawalRequestDto response = withdrawalRequestService.getWithdrawalRequest(requestId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/requests")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<List<WithdrawalRequestDto>> getMyWithdrawalRequests(
            @AuthenticationPrincipal User currentUser) {
        List<WithdrawalRequestDto> response = withdrawalRequestService.getWithdrawalRequestsByFranchisee(
                currentUser.getId());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WithdrawalRequestDto>> getWithdrawalRequestsByStatus(
            @PathVariable WithdrawalStatus status) {
        List<WithdrawalRequestDto> response = withdrawalRequestService.getWithdrawalRequestsByStatus(status);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/admin/process/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WithdrawalRequestDto> processWithdrawalRequest(
            @PathVariable("id") Long requestId,
            @RequestParam("status") String status,
            @RequestParam(value = "adminComments", required = false) String adminComments,
            @RequestParam(value = "paymentReference", required = false) String paymentReference,
            @RequestParam(value = "accountNumber", required = false) String accountNumber,
            @RequestParam(value = "ifscCode", required = false) String ifscCode,
            @RequestParam(value = "bankName", required = false) String bankName,
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @RequestParam(value = "paymentScreenshot", required = false) MultipartFile paymentScreenshot,
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Admin processing withdrawal request {} with status {}", requestId, status);
        
        String screenshotUrl = null;
        if (paymentScreenshot != null && !paymentScreenshot.isEmpty()) {
            // Save screenshot to S3 with improved path structure
            screenshotUrl = s3Service.uploadWithdrawalScreenshot(requestId, paymentScreenshot);
            log.info("Screenshot uploaded for withdrawal request {}: {}", requestId, screenshotUrl);
        }
        
        WithdrawalRequestResponseDto responseDto = new WithdrawalRequestResponseDto();
        responseDto.setStatus(WithdrawalStatus.valueOf(status));
        responseDto.setAdminComments(adminComments);
        responseDto.setPaymentReference(paymentReference);
        responseDto.setAccountNumber(accountNumber);
        responseDto.setIfscCode(ifscCode);
        responseDto.setBankName(bankName);
        responseDto.setMobileNumber(mobileNumber);
        responseDto.setScreenshotUrl(screenshotUrl);
        responseDto.setTransactionType(transactionType);
        responseDto.setTransactionId(transactionId);
        
        WithdrawalRequestDto response = withdrawalRequestService.processWithdrawalRequest(
                requestId, responseDto, currentUser.getId());
        
        log.info("Withdrawal request {} processed with status {} by admin {}", 
                requestId, status, currentUser.getId());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/balance")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<BigDecimal> getAvailableWithdrawalBalanceForCurrentUser(@AuthenticationPrincipal User currentUser) {
        log.info("Getting available balance for franchisee: {}", currentUser.getId());
        
        // Use the consistent method to calculate available balance
        BigDecimal availableBalance = withdrawalRequestService.getTotalAvailableBalanceForFranchisee(currentUser.getId());
        
        log.info("BALANCE API - Franchisee ID: {}, Available Balance: {}", 
                currentUser.getId(), availableBalance);
        
        return ResponseEntity.ok(availableBalance);
    }
    
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<WithdrawalRequestDto>> getAllWithdrawalRequestsForAdmin() {
        List<WithdrawalRequestDto> requests = withdrawalRequestService.getAllWithdrawalRequests();
        return ResponseEntity.ok(requests);
    }
    
    @GetMapping("/history")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<WithdrawalHistoryDto> getWithdrawalHistory(@AuthenticationPrincipal User currentUser) {
        log.info("Getting withdrawal history for franchisee: {}", currentUser.getId());
        WithdrawalHistoryDto history = withdrawalRequestService.getWithdrawalHistory(currentUser.getId());
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/district/{districtId}/history")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<WithdrawalHistoryDto> getWithdrawalHistoryForDistrict(
            @PathVariable Long districtId,
            @AuthenticationPrincipal User currentUser) {
        log.info("Getting withdrawal history for district: {}", districtId);
        
        // Verify that the district belongs to the franchisee
        FranchiseeDistrict district = franchiseeDistrictService.getDistrictById(districtId);
        if (!district.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have access to this district");
        }
        
        WithdrawalHistoryDto history = withdrawalRequestService.getWithdrawalHistoryForDistrict(districtId);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/admin/history/franchisee/{franchiseeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WithdrawalHistoryDto> getWithdrawalHistoryForFranchisee(@PathVariable Long franchiseeId) {
        log.info("Admin getting withdrawal history for franchisee: {}", franchiseeId);
        WithdrawalHistoryDto history = withdrawalRequestService.getWithdrawalHistory(franchiseeId);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/admin/history/district/{districtId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WithdrawalHistoryDto> getWithdrawalHistoryForDistrictAdmin(@PathVariable Long districtId) {
        log.info("Admin getting withdrawal history for district: {}", districtId);
        WithdrawalHistoryDto history = withdrawalRequestService.getWithdrawalHistoryForDistrict(districtId);
        return ResponseEntity.ok(history);
    }
} 