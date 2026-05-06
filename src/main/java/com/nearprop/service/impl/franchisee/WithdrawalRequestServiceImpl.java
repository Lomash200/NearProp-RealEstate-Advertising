package com.nearprop.service.impl.franchisee;

import com.nearprop.dto.UserSummaryDto;
import com.nearprop.dto.franchisee.CreateWithdrawalRequestDto;
import com.nearprop.dto.franchisee.FranchiseeDistrictSummaryDto;
import com.nearprop.dto.franchisee.WithdrawalRequestDto;
import com.nearprop.dto.franchisee.WithdrawalRequestResponseDto;
import com.nearprop.entity.FranchiseeBankDetail;
import com.nearprop.entity.FranchiseeDistrict;
import com.nearprop.entity.FranchiseeWithdrawalRequest;
import com.nearprop.entity.FranchiseeWithdrawalRequest.WithdrawalStatus;
import com.nearprop.entity.MonthlyRevenueReport;
import com.nearprop.entity.User;
import com.nearprop.entity.DistrictRevenue;
import com.nearprop.entity.DistrictRevenue.PaymentStatus;
import com.nearprop.entity.DistrictRevenue.RevenueType;
import com.nearprop.entity.Subscription;
import com.nearprop.exception.BadRequestException;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.MonthlyRevenueReportRepository;
import com.nearprop.repository.SubscriptionRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.repository.franchisee.DistrictRevenueRepository;
import com.nearprop.repository.franchisee.FranchiseeDistrictRepository;
import com.nearprop.repository.franchisee.WithdrawalRequestRepository;
import com.nearprop.repository.FranchiseeBankDetailRepository;
import com.nearprop.service.franchisee.WithdrawalRequestService;
import com.nearprop.dto.franchisee.FranchiseeDistrictSubscriptionAnalyticsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nearprop.dto.franchisee.WithdrawalHistoryDto;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WithdrawalRequestServiceImpl implements WithdrawalRequestService {
    
    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final FranchiseeDistrictRepository franchiseeDistrictRepository;
    private final DistrictRevenueRepository districtRevenueRepository;
    private final UserRepository userRepository;
    private final ApplicationContext applicationContext;
    private final FranchiseeBankDetailRepository bankDetailRepository;
    private final MonthlyRevenueReportRepository monthlyRevenueReportRepository;
    private final SubscriptionRepository subscriptionRepository;
    
    // Maximum percentage of revenue that can be withdrawn in emergency requests
    private static final BigDecimal MAX_WITHDRAWAL_PERCENTAGE = new BigDecimal("0.30");
    
    @Override
    @Transactional
    public WithdrawalRequestDto createWithdrawalRequest(CreateWithdrawalRequestDto requestDto, Long franchiseeId) {
        log.info("Creating withdrawal request for franchisee: {}, amount: {}", franchiseeId, requestDto.getRequestedAmount());
        
        // Get the franchisee district
        FranchiseeDistrict franchiseeDistrict = franchiseeDistrictRepository.findById(requestDto.getFranchiseeDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("Franchisee district not found with ID: " + requestDto.getFranchiseeDistrictId()));
        
        // Validate that the district belongs to the franchisee
        if (!franchiseeDistrict.getUser().getId().equals(franchiseeId)) {
            throw new AccessDeniedException("You do not have access to this franchisee district");
        }
        
        // Get the total available balance for this franchisee
        BigDecimal availableBalance = getTotalAvailableBalanceForFranchisee(franchiseeId);
        log.info("Available balance for franchisee: {}", availableBalance);
        
        // Calculate maximum withdrawal amount (30% of total available balance)
        BigDecimal maxWithdrawalAmount = availableBalance.multiply(MAX_WITHDRAWAL_PERCENTAGE)
                .setScale(2, RoundingMode.HALF_DOWN);
        log.info("Maximum withdrawal amount (30% of available balance): {}", maxWithdrawalAmount);
        
        // Validate that the requested amount is not more than the maximum withdrawal amount
        if (requestDto.getRequestedAmount().compareTo(maxWithdrawalAmount) > 0) {
            throw new BadRequestException("Requested amount exceeds maximum allowed withdrawal amount. " +
                    "Maximum allowed: " + maxWithdrawalAmount);
        }
        
        // Get the bank detail
        FranchiseeBankDetail bankDetail = null;
        if (requestDto.getBankDetailId() != null) {
            bankDetail = bankDetailRepository.findById(requestDto.getBankDetailId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bank detail not found with ID: " + requestDto.getBankDetailId()));
            
            // Validate that the bank detail belongs to the franchisee
            if (!bankDetail.getUser().getId().equals(franchiseeId)) {
                throw new AccessDeniedException("You do not have access to this bank detail");
            }
        }
        
        // Validate reason is not null or empty
        if (requestDto.getReason() == null || requestDto.getReason().trim().isEmpty()) {
            throw new BadRequestException("Reason for withdrawal is required");
        }
        
        // Create the withdrawal request
        FranchiseeWithdrawalRequest request = new FranchiseeWithdrawalRequest();
        request.setFranchiseeDistrict(franchiseeDistrict);
        request.setRequestedAmount(requestDto.getRequestedAmount());
        request.setReason(requestDto.getReason().trim());
        request.setStatus(WithdrawalStatus.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setBankDetail(bankDetail);
        
        // If bank detail is provided, copy the details to the request for record-keeping
        if (bankDetail != null) {
            request.setAccountNumber(bankDetail.getAccountNumber());
            request.setIfscCode(bankDetail.getIfscCode());
            request.setBankName(bankDetail.getBankName());
        }
        
        FranchiseeWithdrawalRequest savedRequest = withdrawalRequestRepository.save(request);
        log.info("Withdrawal request created with ID: {}", savedRequest.getId());
        
        return mapToDto(savedRequest, availableBalance);
    }
    
    @Override
    public WithdrawalRequestDto getWithdrawalRequest(Long requestId) {
        FranchiseeWithdrawalRequest request = withdrawalRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Withdrawal request not found with ID: " + requestId));
        // Use analytics-based balance
        User franchiseeUser = request.getFranchiseeDistrict().getUser();
        BigDecimal availableBalance = getTotalAvailableBalanceForFranchisee(franchiseeUser.getId());
        return mapToDto(request, availableBalance);
    }
    
    @Override
    public List<WithdrawalRequestDto> getWithdrawalRequestsByFranchisee(Long franchiseeId) {
        return withdrawalRequestRepository.findByFranchiseeId(franchiseeId).stream()
                .map(request -> {
                    User franchiseeUser = request.getFranchiseeDistrict().getUser();
                    BigDecimal availableBalance = getTotalAvailableBalanceForFranchisee(franchiseeUser.getId());
                    return mapToDto(request, availableBalance);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public List<WithdrawalRequestDto> getWithdrawalRequestsByStatus(WithdrawalStatus status) {
        return withdrawalRequestRepository.findByStatus(status).stream()
                .map(request -> {
                    User franchiseeUser = request.getFranchiseeDistrict().getUser();
                    BigDecimal availableBalance = getTotalAvailableBalanceForFranchisee(franchiseeUser.getId());
                    return mapToDto(request, availableBalance);
                })
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public WithdrawalRequestDto processWithdrawalRequest(Long requestId, WithdrawalRequestResponseDto responseDto, Long adminId) {
        FranchiseeWithdrawalRequest request = withdrawalRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Withdrawal request not found with ID: " + requestId));
        
        // Validate admin exists
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + adminId));
        
        // Validate request is in PENDING status
        if (request.getStatus() != WithdrawalStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be processed");
        }
        
        // Get franchisee district and user
        FranchiseeDistrict franchiseeDistrict = request.getFranchiseeDistrict();
        User franchiseeUser = franchiseeDistrict.getUser();
        
        // Calculate original balance before processing
        BigDecimal originalBalance = getTotalAvailableBalanceForFranchisee(franchiseeUser.getId());
        
        // Update request based on admin decision
        request.setStatus(responseDto.getStatus());
        request.setProcessedById(adminId);
        request.setProcessedAt(LocalDateTime.now());
        request.setAdminComments(responseDto.getAdminComments());
        request.setScreenshotUrl(responseDto.getScreenshotUrl());
        
        // Preserve the original reason
        // Don't update reason field as it should remain as submitted by franchisee
        
        // Use bank details from the original request if they're not provided in the response
        if (responseDto.getAccountNumber() != null) {
            request.setAccountNumber(responseDto.getAccountNumber());
        } else if (request.getBankDetail() != null && request.getAccountNumber() == null) {
            request.setAccountNumber(request.getBankDetail().getAccountNumber());
        }
        
        if (responseDto.getIfscCode() != null) {
            request.setIfscCode(responseDto.getIfscCode());
        } else if (request.getBankDetail() != null && request.getIfscCode() == null) {
            request.setIfscCode(request.getBankDetail().getIfscCode());
        }
        
        if (responseDto.getBankName() != null) {
            request.setBankName(responseDto.getBankName());
        } else if (request.getBankDetail() != null && request.getBankName() == null) {
            request.setBankName(request.getBankDetail().getBankName());
        }
        
        request.setMobileNumber(responseDto.getMobileNumber());
        request.setTransactionType(responseDto.getTransactionType());
        request.setTransactionId(responseDto.getTransactionId());
        
        // Store original balance for tracking
        request.setOriginalBalance(originalBalance);
        
        // If the request is being approved or paid, set payment details
        if (responseDto.getStatus() == WithdrawalStatus.APPROVED || 
            responseDto.getStatus() == WithdrawalStatus.PAID) {
            
            request.setPaymentReference(responseDto.getPaymentReference());
            
            if (responseDto.getStatus() == WithdrawalStatus.PAID) {
                request.setPaymentDate(LocalDateTime.now());
            }
            
            // Calculate updated balance after withdrawal
            BigDecimal updatedBalance = originalBalance.subtract(request.getRequestedAmount());
            request.setUpdatedBalance(updatedBalance);
            
            // Update franchisee district available balance
            franchiseeDistrict.setAvailableBalance(updatedBalance);
            
            // Update withdrawal history in franchisee district
            updateWithdrawalHistory(franchiseeDistrict, request);
            
            // Save the updated franchisee district
            franchiseeDistrictRepository.save(franchiseeDistrict);
            
            log.info("Withdrawal request {} processed: Original balance: {}, Requested amount: {}, Updated balance: {}", 
                    requestId, originalBalance, request.getRequestedAmount(), updatedBalance);
        }
        
        FranchiseeWithdrawalRequest updatedRequest = withdrawalRequestRepository.save(request);
        
        // Recalculate available balance after processing
        BigDecimal availableBalance = getTotalAvailableBalanceForFranchisee(franchiseeUser.getId());
        
        return mapToDto(updatedRequest, availableBalance);
    }
    
    /**
     * Update the withdrawal history in the franchisee district
     * 
     * @param district The franchisee district to update
     * @param request The withdrawal request that was processed
     */
    private void updateWithdrawalHistory(FranchiseeDistrict district, FranchiseeWithdrawalRequest request) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            
            // Create a history entry
            Map<String, Object> historyEntry = new HashMap<>();
            historyEntry.put("requestId", request.getId());
            historyEntry.put("amount", request.getRequestedAmount());
            historyEntry.put("status", request.getStatus().name());
            historyEntry.put("processedAt", request.getProcessedAt());
            historyEntry.put("processedById", request.getProcessedById());
            historyEntry.put("originalBalance", request.getOriginalBalance());
            historyEntry.put("updatedBalance", request.getUpdatedBalance());
            historyEntry.put("transactionType", request.getTransactionType());
            historyEntry.put("transactionId", request.getTransactionId());
            historyEntry.put("paymentReference", request.getPaymentReference());
            
            // Get current history or initialize empty array
            List<Map<String, Object>> history;
            if (district.getWithdrawalHistory() != null && !district.getWithdrawalHistory().isEmpty() && !district.getWithdrawalHistory().equals("[]")) {
                history = objectMapper.readValue(district.getWithdrawalHistory(), 
                        new TypeReference<List<Map<String, Object>>>() {});
            } else {
                history = new ArrayList<>();
            }
            
            // Add new entry and update district
            history.add(historyEntry);
            
            // Convert to JSON string
            String historyJson = objectMapper.writeValueAsString(history);
            district.setWithdrawalHistory(historyJson);
            
            log.info("Updated withdrawal history for district {}: {}", district.getId(), history.size());
        } catch (Exception e) {
            log.error("Error updating withdrawal history: {}", e.getMessage(), e);
            // Set default empty array to avoid null issues
            district.setWithdrawalHistory("[]");
        }
    }
    
    @Override
    public BigDecimal calculateAvailableWithdrawalBalance(Long franchiseeDistrictId) {
        // Get total pending commission from subscriptions
        BigDecimal pendingCommission = districtRevenueRepository
                .sumSubscriptionCommissionByFranchiseeDistrictAndStatus(franchiseeDistrictId, PaymentStatus.PENDING);
        
        log.info("Withdrawal balance calculation - franchiseeDistrictId: {}, pendingCommission: {}", franchiseeDistrictId, pendingCommission);
        
        if (pendingCommission == null) {
            pendingCommission = BigDecimal.ZERO;
            log.info("Pending commission was null, setting to zero");
        }
        
        // Calculate maximum amount available for withdrawal (30% of pending commission)
        BigDecimal maxWithdrawal = pendingCommission.multiply(MAX_WITHDRAWAL_PERCENTAGE).setScale(2, RoundingMode.HALF_DOWN);
        log.info("Max withdrawal (30% of pending): {}", maxWithdrawal);
        
        // Get current month's start date to track withdrawals made this month
        LocalDate firstDayOfMonth = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDateTime startOfMonth = LocalDateTime.of(firstDayOfMonth, LocalTime.MIDNIGHT);
        log.info("Start of month for withdrawal tracking: {}", startOfMonth);
        
        // Sum already withdrawn amounts for this month
        BigDecimal alreadyWithdrawn = withdrawalRequestRepository
                .sumWithdrawalAmountsSinceDate(franchiseeDistrictId, startOfMonth);
        
        log.info("Already withdrawn this month: {}", alreadyWithdrawn);
        
        if (alreadyWithdrawn == null) {
            alreadyWithdrawn = BigDecimal.ZERO;
            log.info("Already withdrawn was null, setting to zero");
        }
        
        // Available balance is max withdrawal minus already withdrawn amount
        BigDecimal availableBalance = maxWithdrawal.subtract(alreadyWithdrawn).max(BigDecimal.ZERO);
        log.info("Final available balance for withdrawal: {}", availableBalance);
        
        return availableBalance;
    }
    
    @Override
    public BigDecimal getTotalWithdrawnAmount(Long franchiseeDistrictId, LocalDateTime startDate) {
        BigDecimal totalWithdrawn = withdrawalRequestRepository
                .sumApprovedWithdrawalAmountsByFranchiseeDistrictId(franchiseeDistrictId);
        
        if (totalWithdrawn == null) {
            totalWithdrawn = BigDecimal.ZERO;
        }
        
        log.info("Total withdrawn amount for district {}: {}", franchiseeDistrictId, totalWithdrawn);
        return totalWithdrawn;
    }
    
    @Override
    public List<WithdrawalRequestDto> getAllWithdrawalRequests() {
        List<FranchiseeWithdrawalRequest> requests = withdrawalRequestRepository.findAll();
        return requests.stream().map(request -> {
            User franchiseeUser = request.getFranchiseeDistrict().getUser();
            BigDecimal availableBalance = getTotalAvailableBalanceForFranchisee(franchiseeUser.getId());
            return mapToDto(request, availableBalance);
        }).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public int generateMonthlyRevenueReports() {
        log.info("Generating monthly revenue reports");
        
        // Get all active franchisee districts
        List<FranchiseeDistrict> activeDistricts = franchiseeDistrictRepository.findByActiveTrue();
        log.info("Found {} active franchisee districts", activeDistricts.size());
        
        int reportsGenerated = 0;
        
        // Get previous month's date range
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfPreviousMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfPreviousMonth = now.withDayOfMonth(1).minusDays(1);
        
        LocalDateTime startDate = LocalDateTime.of(firstDayOfPreviousMonth, LocalTime.MIN);
        LocalDateTime endDate = LocalDateTime.of(lastDayOfPreviousMonth, LocalTime.MAX);
        
        log.info("Generating reports for period: {} to {}", startDate, endDate);
        
        for (FranchiseeDistrict district : activeDistricts) {
            try {
                // Get all revenue entries for this district in the previous month
                List<DistrictRevenue> revenues = districtRevenueRepository.findByFranchiseeDistrictAndTransactionDateBetween(
                        district, startDate, endDate);
                
                if (revenues.isEmpty()) {
                    log.info("No revenue entries found for district {} in the previous month, skipping", district.getId());
                    continue;
                }
                
                // Calculate total revenue and commission
                BigDecimal totalRevenue = BigDecimal.ZERO;
                BigDecimal franchiseeCommission = BigDecimal.ZERO;
                int transactionCount = 0;
                
                for (DistrictRevenue revenue : revenues) {
                    if (revenue.getPaymentStatus() != PaymentStatus.CANCELLED) {
                        totalRevenue = totalRevenue.add(revenue.getAmount());
                        franchiseeCommission = franchiseeCommission.add(revenue.getFranchiseeCommission());
                        transactionCount++;
                    }
                }
                
                // Get total withdrawn amount for this period
                BigDecimal withdrawnAmount = withdrawalRequestRepository.sumApprovedWithdrawalAmountsBetweenDates(
                        district.getId(), startDate, endDate);
                
                if (withdrawnAmount == null) {
                    withdrawnAmount = BigDecimal.ZERO;
                }
                
                // Calculate remaining balance and admin share
                BigDecimal remainingBalance = franchiseeCommission.subtract(withdrawnAmount);
                BigDecimal adminShare = totalRevenue.subtract(franchiseeCommission);
                
                // Get user from district
                User franchiseeUser = district.getUser();
                
                // Create monthly report
                MonthlyRevenueReport report = new MonthlyRevenueReport();
                report.setYear(firstDayOfPreviousMonth.getYear());
                report.setMonth(firstDayOfPreviousMonth.getMonthValue());
                report.setFranchisee(franchiseeUser);
                report.setFranchiseeName(franchiseeUser.getName());
                report.setBusinessName(franchiseeUser.getName() + "'s Franchise");
                report.setFranchiseeDistrict(district);
                report.setDistrictId(district.getDistrictId());
                report.setDistrictName(district.getDistrictName());
                report.setState(district.getState());
                report.setTotalRevenue(totalRevenue);
                report.setFranchiseeCommission(franchiseeCommission);
                report.setAdminShare(adminShare);
                report.setTotalSubscriptions(transactionCount);
                report.setEmergencyWithdrawalsAmount(withdrawnAmount);
                report.setCurrentBalance(remainingBalance);
                report.setReportStatus(MonthlyRevenueReport.ReportStatus.PENDING);
                report.setGeneratedAt(LocalDateTime.now());
                
                // Save the report
                MonthlyRevenueReport savedReport = monthlyRevenueReportRepository.save(report);
                log.info("Generated monthly report for district {}: {}", district.getId(), savedReport.getId());
                
                reportsGenerated++;
                
            } catch (Exception e) {
                log.error("Error generating monthly report for district {}: {}", district.getId(), e.getMessage(), e);
            }
        }
        
        log.info("Generated {} monthly revenue reports", reportsGenerated);
        return reportsGenerated;
    }
    
    @Override
    public BigDecimal getTotalAvailableBalanceForFranchisee(Long franchiseeUserId) {
        log.info("Calculating total available balance for franchisee: {}", franchiseeUserId);
        
        // Get the user
        User franchiseeUser = userRepository.findById(franchiseeUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + franchiseeUserId));
        
        // Get all franchisee districts for this user
        List<FranchiseeDistrict> districts = franchiseeDistrictRepository.findByUserId(franchiseeUserId);
        log.info("Found {} franchisee districts for user", districts.size());
        
        if (districts.isEmpty()) {
            log.info("No franchisee districts found, returning zero balance");
            return BigDecimal.ZERO;
        }
        
        BigDecimal totalCommission = BigDecimal.ZERO;
        
        // Sum up commissions across all of the user's franchisee districts
        for (FranchiseeDistrict district : districts) {
            BigDecimal districtCommission = districtRevenueRepository.sumAllCommissionsByFranchiseeDistrictId(district.getId());
            if (districtCommission != null) {
                totalCommission = totalCommission.add(districtCommission);
                log.info("District ID: {}, Commission amount: {}", district.getId(), districtCommission);
            }
        }
        
        log.info("Total commission from all districts: {}", totalCommission);
        
        BigDecimal totalWithdrawn = BigDecimal.ZERO;
        
        // Sum up withdrawals across all of the user's franchisee districts
        for (FranchiseeDistrict district : districts) {
            BigDecimal districtWithdrawn = withdrawalRequestRepository.sumApprovedWithdrawalAmountsByFranchiseeDistrictId(district.getId());
            if (districtWithdrawn != null) {
                totalWithdrawn = totalWithdrawn.add(districtWithdrawn);
                log.info("District ID: {}, Withdrawn amount: {}", district.getId(), districtWithdrawn);
            }
        }
        
        log.info("Total withdrawn from all districts: {}", totalWithdrawn);
        
        // Calculate available balance as total commission minus withdrawn amount
        BigDecimal availableBalance = totalCommission.subtract(totalWithdrawn);
        availableBalance = availableBalance.max(BigDecimal.ZERO);
        
        log.info("CONSISTENT METHOD - Franchisee ID: {}, Total Commission: {}, Total Withdrawn: {}, Available Balance: {}", 
                franchiseeUserId, totalCommission, totalWithdrawn, availableBalance);
        
        // Update the available balance in the franchisee district entities
        for (FranchiseeDistrict district : districts) {
            district.setTotalCommission(totalCommission);
            district.setAvailableBalance(availableBalance);
            franchiseeDistrictRepository.save(district);
        }
        
        return availableBalance;
    }
    
    /**
     * Calculate the franchisee's share directly without using FranchiseeDistrictService
     * to avoid circular dependency
     */
    private BigDecimal calculateFranchiseeShare(User franchiseeUser) {
        List<FranchiseeDistrict> franchiseeDistricts = franchiseeDistrictRepository.findByUserId(franchiseeUser.getId());
        if (franchiseeDistricts.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Get all districts assigned to this franchisee
        List<Long> districtIds = franchiseeDistricts.stream()
                .map(FranchiseeDistrict::getDistrictId)
                .toList();
        
        // Find users in these districts
        List<User> users = userRepository.findByDistrictIdIn(districtIds);
        List<Long> userIds = users.stream()
                .map(User::getId)
                .toList();
        
        // Find subscriptions for these users
        List<Subscription> subscriptions = subscriptionRepository.findByUserIdIn(userIds);
        
        // Calculate total amount
        BigDecimal totalAmount = subscriptions.stream()
                .map(Subscription::getPrice)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Franchisee gets 50% of subscription revenue
        return totalAmount.multiply(BigDecimal.valueOf(0.5));
    }
    
    private WithdrawalRequestDto mapToDto(FranchiseeWithdrawalRequest request, BigDecimal availableBalance) {
        WithdrawalRequestDto dto = new WithdrawalRequestDto();
        dto.setId(request.getId());
        
        // Set franchisee district information
        FranchiseeDistrict district = request.getFranchiseeDistrict();
        dto.setFranchiseeDistrictId(district.getId());
        dto.setDistrictName(district.getDistrictName());
        dto.setState(district.getState());
        
        // Set franchisee information
        User franchisee = district.getUser();
        dto.setFranchiseeId(franchisee.getId());
        dto.setFranchiseeName(franchisee.getName());
        
        dto.setRequestedAmount(request.getRequestedAmount());
        dto.setReason(request.getReason());
        dto.setStatus(request.getStatus());
        dto.setProcessedById(request.getProcessedById());
        
        // Set processed by information if available
        if (request.getProcessedById() != null) {
            try {
                User processedBy = userRepository.findById(request.getProcessedById())
                        .orElse(null);
                if (processedBy != null) {
                    dto.setProcessedByName(processedBy.getName());
                }
            } catch (Exception e) {
                log.warn("Failed to fetch processed by user: {}", e.getMessage());
            }
        }
        
        dto.setProcessedAt(request.getProcessedAt());
        dto.setAdminComments(request.getAdminComments());
        dto.setPaymentReference(request.getPaymentReference());
        dto.setPaymentDate(request.getPaymentDate());
        dto.setCreatedAt(request.getCreatedAt());
        dto.setUpdatedAt(request.getUpdatedAt());
        dto.setScreenshotUrl(request.getScreenshotUrl());
        
        // Set bank details
        dto.setBankDetailId(request.getBankDetail() != null ? request.getBankDetail().getId() : null);
        dto.setAccountNumber(request.getAccountNumber());
        dto.setIfscCode(request.getIfscCode());
        dto.setBankName(request.getBankName());
        
        // Set available balance
        dto.setAvailableBalance(availableBalance);
        
        // Set transaction details
        dto.setTransactionType(request.getTransactionType());
        dto.setTransactionId(request.getTransactionId());
        
        // Set balance tracking information
        dto.setOriginalBalance(request.getOriginalBalance());
        dto.setUpdatedBalance(request.getUpdatedBalance());
        
        return dto;
    }
    
    /**
     * Mask account number to show only last 4 digits
     */
    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return accountNumber;
        }
        
        int visibleDigits = 4;
        int length = accountNumber.length();
        String maskedPart = "X".repeat(length - visibleDigits);
        String visiblePart = accountNumber.substring(length - visibleDigits);
        
        return maskedPart + visiblePart;
    }

    @Override
    public WithdrawalHistoryDto getWithdrawalHistory(Long franchiseeId) {
        log.info("Getting withdrawal history for franchisee: {}", franchiseeId);
        
        // Get the user
        User franchiseeUser = userRepository.findById(franchiseeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + franchiseeId));
        
        // Get all franchisee districts for this user
        List<FranchiseeDistrict> districts = franchiseeDistrictRepository.findByUserId(franchiseeId);
        if (districts.isEmpty()) {
            throw new ResourceNotFoundException("No franchisee districts found for user ID: " + franchiseeId);
        }
        
        // Combine history from all districts
        List<WithdrawalHistoryDto.WithdrawalHistoryEntryDto> allHistory = new ArrayList<>();
        
        for (FranchiseeDistrict district : districts) {
            WithdrawalHistoryDto districtHistory = getWithdrawalHistoryForDistrict(district.getId());
            if (districtHistory != null && districtHistory.getHistory() != null) {
                allHistory.addAll(districtHistory.getHistory());
            }
        }
        
        // Sort by processed date (newest first)
        allHistory.sort((a, b) -> b.getProcessedAt().compareTo(a.getProcessedAt()));
        
        // Get total commission and available balance
        BigDecimal totalCommission = BigDecimal.ZERO;
        for (FranchiseeDistrict district : districts) {
            if (district.getTotalCommission() != null) {
                totalCommission = totalCommission.add(district.getTotalCommission());
            }
        }
        
        BigDecimal availableBalance = getTotalAvailableBalanceForFranchisee(franchiseeId);
        
        // Create the response
        WithdrawalHistoryDto historyDto = new WithdrawalHistoryDto();
        historyDto.setFranchiseeId(franchiseeId);
        historyDto.setFranchiseeName(franchiseeUser.getName());
        historyDto.setTotalCommission(totalCommission);
        historyDto.setAvailableBalance(availableBalance);
        historyDto.setHistory(allHistory);
        
        return historyDto;
    }
    
    @Override
    public WithdrawalHistoryDto getWithdrawalHistoryForDistrict(Long franchiseeDistrictId) {
        log.info("Getting withdrawal history for franchisee district: {}", franchiseeDistrictId);
        
        // Get the district
        FranchiseeDistrict district = franchiseeDistrictRepository.findById(franchiseeDistrictId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchisee district not found with ID: " + franchiseeDistrictId));
        
        // Get the user
        User franchiseeUser = district.getUser();
        
        // Parse the withdrawal history
        List<WithdrawalHistoryDto.WithdrawalHistoryEntryDto> historyEntries = new ArrayList<>();
        
        if (district.getWithdrawalHistory() != null && !district.getWithdrawalHistory().isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                
                List<Map<String, Object>> history = objectMapper.readValue(district.getWithdrawalHistory(), 
                        new TypeReference<List<Map<String, Object>>>() {});
                
                for (Map<String, Object> entry : history) {
                    WithdrawalHistoryDto.WithdrawalHistoryEntryDto historyEntry = new WithdrawalHistoryDto.WithdrawalHistoryEntryDto();
                    
                    // Extract values from the map
                    historyEntry.setRequestId(Long.valueOf(entry.get("requestId").toString()));
                    historyEntry.setAmount(new BigDecimal(entry.get("amount").toString()));
                    historyEntry.setStatus(entry.get("status").toString());
                    
                    // Handle date conversion
                    if (entry.get("processedAt") != null) {
                        if (entry.get("processedAt") instanceof String) {
                            historyEntry.setProcessedAt(LocalDateTime.parse(entry.get("processedAt").toString()));
                        } else {
                            // Jackson might have already converted it to a date object
                            historyEntry.setProcessedAt((LocalDateTime) entry.get("processedAt"));
                        }
                    }
                    
                    if (entry.get("processedById") != null) {
                        historyEntry.setProcessedById(Long.valueOf(entry.get("processedById").toString()));
                        
                        // Get processed by name
                        try {
                            User processedBy = userRepository.findById(historyEntry.getProcessedById()).orElse(null);
                            if (processedBy != null) {
                                historyEntry.setProcessedByName(processedBy.getName());
                            }
                        } catch (Exception e) {
                            log.warn("Failed to get processed by name: {}", e.getMessage());
                        }
                    }
                    
                    if (entry.get("originalBalance") != null) {
                        historyEntry.setOriginalBalance(new BigDecimal(entry.get("originalBalance").toString()));
                    }
                    
                    if (entry.get("updatedBalance") != null) {
                        historyEntry.setUpdatedBalance(new BigDecimal(entry.get("updatedBalance").toString()));
                    }
                    
                    historyEntry.setTransactionType(entry.get("transactionType") != null ? entry.get("transactionType").toString() : null);
                    historyEntry.setTransactionId(entry.get("transactionId") != null ? entry.get("transactionId").toString() : null);
                    historyEntry.setPaymentReference(entry.get("paymentReference") != null ? entry.get("paymentReference").toString() : null);
                    
                    historyEntries.add(historyEntry);
                }
                
                // Sort by processed date (newest first)
                historyEntries.sort((a, b) -> b.getProcessedAt().compareTo(a.getProcessedAt()));
                
            } catch (Exception e) {
                log.error("Error parsing withdrawal history: {}", e.getMessage(), e);
            }
        }
        
        // Create the response
        WithdrawalHistoryDto historyDto = new WithdrawalHistoryDto();
        historyDto.setFranchiseeId(franchiseeUser.getId());
        historyDto.setFranchiseeName(franchiseeUser.getName());
        historyDto.setFranchiseeDistrictId(district.getId());
        historyDto.setDistrictName(district.getDistrictName());
        historyDto.setState(district.getState());
        historyDto.setTotalCommission(district.getTotalCommission());
        historyDto.setAvailableBalance(district.getAvailableBalance());
        historyDto.setHistory(historyEntries);
        
        return historyDto;
    }
}