package com.nearprop.service.franchisee.impl;

import com.nearprop.dto.franchisee.MonthlyRevenueReportDto;
import com.nearprop.entity.DistrictRevenue;
import com.nearprop.entity.DistrictRevenue.PaymentStatus;
import com.nearprop.entity.DistrictRevenue.RevenueType;
import com.nearprop.entity.FranchiseeBankDetail;
import com.nearprop.entity.FranchiseeDistrict;
import com.nearprop.entity.FranchiseeWithdrawalRequest;
import com.nearprop.entity.FranchiseeWithdrawalRequest.WithdrawalStatus;
import com.nearprop.entity.MonthlyRevenueReport;
import com.nearprop.entity.MonthlyRevenueReport.ReportStatus;
import com.nearprop.entity.User;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.FranchiseeBankDetailRepository;
import com.nearprop.repository.MonthlyRevenueReportRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.repository.franchisee.DistrictRevenueRepository;
import com.nearprop.repository.franchisee.FranchiseeDistrictRepository;
import com.nearprop.repository.franchisee.WithdrawalRequestRepository;
import com.nearprop.service.franchisee.MonthlyRevenueReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class MonthlyRevenueReportServiceImpl implements MonthlyRevenueReportService {

    private final MonthlyRevenueReportRepository monthlyRevenueReportRepository;
    private final FranchiseeDistrictRepository franchiseeDistrictRepository;
    private final DistrictRevenueRepository districtRevenueRepository;
    private final WithdrawalRequestRepository withdrawalRequestRepository;
    private final FranchiseeBankDetailRepository bankDetailRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public int generateMonthlyReports() {
        log.info("Starting monthly revenue report generation");
        int reportsGenerated = 0;
        
        // Get current month and year (for previous month)
        LocalDate today = LocalDate.now();
        LocalDate lastMonth = today.minusMonths(1);
        int year = lastMonth.getYear();
        int month = lastMonth.getMonthValue();
        
        log.info("Generating reports for year: {}, month: {}", year, month);
        
        // Get all active franchisee districts
        List<FranchiseeDistrict> allDistricts = franchiseeDistrictRepository.findByActive(true);
        log.info("Found {} active franchisee districts", allDistricts.size());
        
        for (FranchiseeDistrict district : allDistricts) {
            try {
                // Check if report already exists for this district and month
                Optional<MonthlyRevenueReport> existingReport = monthlyRevenueReportRepository
                        .findByYearAndMonthAndFranchiseeDistrictId(year, month, district.getId());
                
                if (existingReport.isPresent()) {
                    log.info("Report already exists for district: {}, year: {}, month: {}", 
                            district.getId(), year, month);
                    continue;
                }
                
                // Calculate start and end date for the month
                LocalDate startDate = LocalDate.of(year, month, 1);
                LocalDate endDate = startDate.plusMonths(1).minusDays(1);
                
                LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIDNIGHT);
                LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);
                
                // Get all revenue entries for this district in the month
                List<DistrictRevenue> revenues = districtRevenueRepository
                        .findByFranchiseeDistrictAndTransactionDateBetween(
                            district, startDateTime, endDateTime);
                
                if (revenues.isEmpty()) {
                    log.info("No revenue entries found for district: {} in {}/{}", 
                            district.getId(), month, year);
                    continue;
                }
                
                // Calculate totals
                BigDecimal totalRevenue = BigDecimal.ZERO;
                BigDecimal franchiseeCommission = BigDecimal.ZERO;
                BigDecimal adminShare = BigDecimal.ZERO;
                int totalSubscriptions = 0;
                int newSubscriptions = 0;
                int renewedSubscriptions = 0;
                
                for (DistrictRevenue revenue : revenues) {
                    totalRevenue = totalRevenue.add(revenue.getAmount());
                    franchiseeCommission = franchiseeCommission.add(revenue.getFranchiseeCommission());
                    adminShare = adminShare.add(revenue.getCompanyRevenue());
                    
                    if (revenue.getRevenueType() == RevenueType.SUBSCRIPTION_PAYMENT) {
                        totalSubscriptions++;
                        
                        // Check if this is a new subscription or renewal
                        // This is a simplified approach - in a real system you'd have more data
                        if (revenue.getTransactionId() != null && revenue.getTransactionId().contains("renew")) {
                            renewedSubscriptions++;
                        } else {
                            newSubscriptions++;
                        }
                    }
                }
                
                // Get emergency withdrawals for the month
                List<FranchiseeWithdrawalRequest> withdrawals = withdrawalRequestRepository
                        .findByFranchiseeDistrictAndCreatedAtBetween(
                            district, startDateTime, endDateTime);
                
                BigDecimal withdrawalsAmount = BigDecimal.ZERO;
                int withdrawalsCount = 0;
                
                for (FranchiseeWithdrawalRequest withdrawal : withdrawals) {
                    if (withdrawal.getStatus() == WithdrawalStatus.PAID) {
                        withdrawalsAmount = withdrawalsAmount.add(withdrawal.getRequestedAmount());
                        withdrawalsCount++;
                    }
                }
                
                // Get primary bank account
                FranchiseeBankDetail bankDetail = bankDetailRepository
                        .findByUserIdAndIsPrimaryTrue(district.getUser().getId())
                        .orElse(null);
                
                // Calculate previous balance (from previous month's report)
                BigDecimal previousBalance = BigDecimal.ZERO;
                Optional<MonthlyRevenueReport> previousReport = monthlyRevenueReportRepository
                        .findByYearAndMonthAndFranchiseeDistrictId(
                            month == 1 ? year - 1 : year, 
                            month == 1 ? 12 : month - 1, 
                            district.getId());
                
                if (previousReport.isPresent()) {
                    previousBalance = previousReport.get().getCurrentBalance();
                }
                
                // Calculate current balance
                BigDecimal currentBalance = previousBalance.add(franchiseeCommission).subtract(withdrawalsAmount);
                
                // Create the report
                MonthlyRevenueReport report = MonthlyRevenueReport.builder()
                        .year(year)
                        .month(month)
                        .reportStatus(ReportStatus.PENDING)
                        .franchisee(district.getUser())
                        .franchiseeName(district.getUser().getName())
                        .businessName(district.getUser().getName() + "'s Franchise") // Use user name as business name
                        .franchiseeDistrict(district)
                        .districtId(district.getDistrictId())
                        .districtName(district.getDistrictName())
                        .state(district.getState())
                        .totalRevenue(totalRevenue)
                        .franchiseeCommission(franchiseeCommission)
                        .adminShare(adminShare)
                        .totalSubscriptions(totalSubscriptions)
                        .newSubscriptions(newSubscriptions)
                        .renewedSubscriptions(renewedSubscriptions)
                        .paymentDueDate(LocalDate.now().plusDays(15)) // Due in 15 days
                        .emergencyWithdrawalsAmount(withdrawalsAmount)
                        .emergencyWithdrawalsCount(withdrawalsCount)
                        .previousBalance(previousBalance)
                        .currentBalance(currentBalance)
                        .finalPayableAmount(currentBalance) // Full amount is payable
                        .build();
                
                // Set bank details if available
                if (bankDetail != null) {
                    report.setBankDetail(bankDetail);
                    report.setAccountName(bankDetail.getAccountName());
                    report.setAccountNumber(bankDetail.getAccountNumber());
                    report.setBankName(bankDetail.getBankName());
                    report.setIfscCode(bankDetail.getIfscCode());
                }
                
                // Save the report
                monthlyRevenueReportRepository.save(report);
                reportsGenerated++;
                
                log.info("Generated report for district: {}, franchisee: {}, amount: {}", 
                        district.getId(), district.getUser().getId(), franchiseeCommission);
                
            } catch (Exception e) {
                log.error("Error generating report for district: " + district.getId(), e);
            }
        }
        
        log.info("Completed monthly revenue report generation. Generated {} reports", reportsGenerated);
        return reportsGenerated;
    }

    @Override
    public MonthlyRevenueReportDto getReportById(Long reportId) {
        MonthlyRevenueReport report = monthlyRevenueReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Monthly revenue report not found with ID: " + reportId));
        return mapToDto(report);
    }
    
    @Override
    public List<MonthlyRevenueReportDto> getReportsByFranchisee(Long franchiseeId) {
        List<MonthlyRevenueReport> reports = monthlyRevenueReportRepository.findByFranchiseeId(franchiseeId);
        return reports.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    @Override
    public List<MonthlyRevenueReportDto> getReportsByFranchiseeDistrict(Long franchiseeDistrictId) {
        List<MonthlyRevenueReport> reports = monthlyRevenueReportRepository.findByFranchiseeDistrictId(franchiseeDistrictId);
        return reports.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    @Override
    public List<MonthlyRevenueReportDto> getReportsByYearAndMonth(Integer year, Integer month) {
        if (year == null && month == null) {
            // Return all reports if no year and month specified
            List<MonthlyRevenueReport> reports = monthlyRevenueReportRepository.findAll();
            return reports.stream().map(this::mapToDto).collect(Collectors.toList());
        } else if (month == null) {
            // Find by year only
            List<MonthlyRevenueReport> reports = monthlyRevenueReportRepository.findAll().stream()
                    .filter(r -> r.getYear().equals(year))
                    .collect(Collectors.toList());
            return reports.stream().map(this::mapToDto).collect(Collectors.toList());
        } else {
            // Find by year and month
            List<MonthlyRevenueReport> reports = monthlyRevenueReportRepository.findByYearAndMonth(year, month);
            return reports.stream().map(this::mapToDto).collect(Collectors.toList());
        }
    }
    
    @Override
    public List<MonthlyRevenueReportDto> getReportsByStatus(ReportStatus status) {
        List<MonthlyRevenueReport> reports = monthlyRevenueReportRepository.findByReportStatus(status);
        return reports.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public MonthlyRevenueReportDto processReport(Long reportId, ReportStatus status, String adminComments,
                                              String paymentReference, String paymentMethod, LocalDate paymentDate,
                                              String transactionType, String transactionId, String accountNumber,
                                              String ifscCode, String bankName, String proofUrl, Long adminId) {
        MonthlyRevenueReport report = monthlyRevenueReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Monthly revenue report not found with ID: " + reportId));
        
        // Get the franchisee district
        FranchiseeDistrict franchiseeDistrict = report.getFranchiseeDistrict();
        
        // Only process if status is changing to PAID
        boolean isPaid = status == ReportStatus.PAID && report.getReportStatus() != ReportStatus.PAID;
        
        // Update report status and payment details
        report.setReportStatus(status);
        report.setAdminComments(adminComments);
        report.setPaymentReference(paymentReference);
        report.setPaymentMethod(paymentMethod);
        report.setPaymentDate(paymentDate);
        report.setProcessedById(adminId);
        report.setProcessedAt(LocalDateTime.now());
        
        // Update bank details if provided
        if (accountNumber != null && !accountNumber.isEmpty()) {
            report.setAccountNumber(accountNumber);
        }
        if (ifscCode != null && !ifscCode.isEmpty()) {
            report.setIfscCode(ifscCode);
        }
        if (bankName != null && !bankName.isEmpty()) {
            report.setBankName(bankName);
        }
        
        // If status is PAID, update franchisee district balance
        if (isPaid) {
            // Get current balance
            BigDecimal currentBalance = franchiseeDistrict.getAvailableBalance();
            if (currentBalance == null) {
                currentBalance = BigDecimal.ZERO;
            }
            
            // Deduct the payment amount from the balance
            BigDecimal paymentAmount = report.getFinalPayableAmount();
            BigDecimal newBalance = currentBalance.subtract(paymentAmount);
            
            // Update the district balance
            franchiseeDistrict.setAvailableBalance(newBalance);
            
            // Create a payment transaction record in the withdrawal history
            try {
                // Create a withdrawal history entry
                Map<String, Object> entry = new HashMap<>();
                entry.put("type", "MONTHLY_PAYMENT");
                entry.put("amount", paymentAmount);
                entry.put("date", LocalDateTime.now().toString());
                entry.put("status", "PAID");
                entry.put("reference", paymentReference);
                entry.put("reportId", reportId);
                entry.put("previousBalance", currentBalance);
                entry.put("newBalance", newBalance);
                entry.put("transactionType", transactionType);
                entry.put("transactionId", transactionId);
                entry.put("proofUrl", proofUrl);
                entry.put("processedBy", adminId);
                
                // Get current history and append the new entry
                String currentHistory = franchiseeDistrict.getWithdrawalHistory();
                List<Map<String, Object>> historyList;
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
                
                if (currentHistory == null || currentHistory.isEmpty() || "[]".equals(currentHistory)) {
                    historyList = new ArrayList<>();
                } else {
                    historyList = objectMapper.readValue(currentHistory, 
                            new com.fasterxml.jackson.core.type.TypeReference<List<Map<String, Object>>>() {});
                }
                
                historyList.add(entry);
                String updatedHistory = objectMapper.writeValueAsString(historyList);
                franchiseeDistrict.setWithdrawalHistory(updatedHistory);
                
                // Save the updated district
                franchiseeDistrictRepository.save(franchiseeDistrict);
                
                log.info("Monthly report payment processed. Report ID: {}, Amount: {}, New Balance: {}",
                        reportId, paymentAmount, newBalance);
            } catch (Exception e) {
                log.error("Error updating withdrawal history for report payment: {}", e.getMessage(), e);
                // Continue with report update even if history update fails
            }
        }
        
        // Save the updated report
        MonthlyRevenueReport updatedReport = monthlyRevenueReportRepository.save(report);
        
        return mapToDto(updatedReport);
    }
    
    @Override
    public List<MonthlyRevenueReportDto> getReportSummaryForFranchisee(Long franchiseeId) {
        List<MonthlyRevenueReport> reports = monthlyRevenueReportRepository.findByFranchiseeId(franchiseeId);
        return reports.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    @Override
    public List<MonthlyRevenueReportDto> getReportSummaryForFranchiseeDistrict(Long franchiseeDistrictId) {
        List<MonthlyRevenueReport> reports = monthlyRevenueReportRepository.findByFranchiseeDistrictId(franchiseeDistrictId);
        return reports.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    @Override
    public List<MonthlyRevenueReportDto> getReportSummaryForAllFranchisees() {
        List<MonthlyRevenueReport> reports = monthlyRevenueReportRepository.findAll();
        return reports.stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    private MonthlyRevenueReportDto mapToDto(MonthlyRevenueReport report) {
        MonthlyRevenueReportDto dto = MonthlyRevenueReportDto.builder()
                .id(report.getId())
                .year(report.getYear())
                .month(report.getMonth())
                .generatedAt(report.getGeneratedAt())
                .reportStatus(report.getReportStatus().name())
                .franchiseeId(report.getFranchisee().getId())
                .franchiseeName(report.getFranchiseeName())
                .businessName(report.getBusinessName())
                .franchiseeDistrictId(report.getFranchiseeDistrict().getId())
                .districtId(report.getDistrictId())
                .districtName(report.getDistrictName())
                .state(report.getState())
                .totalRevenue(report.getTotalRevenue())
                .franchiseeCommission(report.getFranchiseeCommission())
                .adminShare(report.getAdminShare())
                .totalSubscriptions(report.getTotalSubscriptions())
                .newSubscriptions(report.getNewSubscriptions())
                .renewedSubscriptions(report.getRenewedSubscriptions())
                .paymentDueDate(report.getPaymentDueDate())
                .paymentDate(report.getPaymentDate())
                .paymentReference(report.getPaymentReference())
                .paymentMethod(report.getPaymentMethod())
                .adminComments(report.getAdminComments())
                .bankDetailId(report.getBankDetail() != null ? report.getBankDetail().getId() : null)
                .accountName(report.getAccountName())
                .accountNumber(report.getAccountNumber())
                .bankName(report.getBankName())
                .ifscCode(report.getIfscCode())
                .emergencyWithdrawalsAmount(report.getEmergencyWithdrawalsAmount())
                .emergencyWithdrawalsCount(report.getEmergencyWithdrawalsCount())
                .previousBalance(report.getPreviousBalance())
                .currentBalance(report.getCurrentBalance())
                .finalPayableAmount(report.getFinalPayableAmount())
                .updatedAt(report.getUpdatedAt())
                .processedById(report.getProcessedById())
                .processedAt(report.getProcessedAt())
                .build();
        
        // Add processed by name if admin ID is available
        if (report.getProcessedById() != null) {
            userRepository.findById(report.getProcessedById()).ifPresent(admin -> {
                dto.setProcessedByName(admin.getName());
            });
        }
        
        return dto;
    }
} 