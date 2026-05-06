package com.nearprop.service.franchisee.impl;

import com.nearprop.dto.franchisee.MonthlyRevenueReportDto;
import com.nearprop.entity.FranchiseeDistrict;
import com.nearprop.entity.MonthlyRevenueReport;
import com.nearprop.entity.MonthlyRevenueReport.ReportStatus;
import com.nearprop.entity.Role;
import com.nearprop.entity.User;
import com.nearprop.repository.MonthlyRevenueReportRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.repository.franchisee.FranchiseeDistrictRepository;
import com.nearprop.service.franchisee.MockReportGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MockReportGenerationServiceImpl implements MockReportGenerationService {

    private final MonthlyRevenueReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FranchiseeDistrictRepository franchiseeDistrictRepository;
    private final Random random = new Random();

    @Override
    @Transactional
    public MonthlyRevenueReportDto generateMockReportForFranchisee(Long franchiseeId, Integer year, Integer month) {
        log.info("Generating mock report for franchisee {} for {}/{}", franchiseeId, year, month);

        User franchisee = userRepository.findById(franchiseeId)
                .orElseThrow(() -> new IllegalArgumentException("Franchisee not found with ID: " + franchiseeId));

        final Integer reportYear = (year == null) ? LocalDate.now().getYear() : year;
        final Integer reportMonth = (month == null) ? LocalDate.now().getMonthValue() : month;

        // Check if report already exists for this franchisee, year and month
        reportRepository.findByYearAndMonthAndFranchiseeId(reportYear, reportMonth, franchiseeId)
                .ifPresent(report -> {
                    throw new IllegalStateException("Report already exists for franchisee " + franchiseeId +
                            " for " + reportYear + "/" + reportMonth);
                });

        // Generate random report data
        LocalDateTime generatedAt = LocalDateTime.now();
        YearMonth yearMonth = YearMonth.of(reportYear, reportMonth);
        int daysInMonth = yearMonth.lengthOfMonth();

        // Get first district for this franchisee
        List<FranchiseeDistrict> districts = franchiseeDistrictRepository.findByUserId(franchiseeId);
        if (districts.isEmpty()) {
            throw new IllegalStateException("No districts found for franchisee: " + franchiseeId);
        }

        FranchiseeDistrict district = districts.get(0);

        // Generate random metrics
        int totalListings = randomRange(50, 200);
        int activeListings = randomRange(20, totalListings - 10);
        int soldListings = randomRange(5, totalListings - activeListings);
        int pendingListings = totalListings - activeListings - soldListings;
        
        int totalSubscriptions = randomRange(10, 50);
        int activeSubscriptions = randomRange(5, totalSubscriptions - 2);
        int expiredSubscriptions = totalSubscriptions - activeSubscriptions;
        
        BigDecimal totalRevenue = BigDecimal.valueOf(random.nextDouble() * 100000).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal franchiseeRevenue = totalRevenue.multiply(BigDecimal.valueOf(0.7)).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal platformRevenue = totalRevenue.subtract(franchiseeRevenue).setScale(2, BigDecimal.ROUND_HALF_UP);
        
        BigDecimal walletBalance = BigDecimal.valueOf(random.nextDouble() * 50000).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal pendingWithdrawal = BigDecimal.valueOf(random.nextDouble() * 10000).setScale(2, BigDecimal.ROUND_HALF_UP);

        MonthlyRevenueReport report = MonthlyRevenueReport.builder()
                .year(reportYear)
                .month(reportMonth)
                .generatedAt(generatedAt)
                .reportStatus(ReportStatus.PENDING)
                .franchisee(franchisee)
                .franchiseeName(franchisee.getName())
                .businessName("Business " + franchiseeId)
                .franchiseeDistrict(district)
                .districtId(district.getDistrictId())
                .districtName(district.getDistrictName())
                .state(district.getState())
                .totalRevenue(totalRevenue)
                .franchiseeCommission(franchiseeRevenue)
                .adminShare(platformRevenue)
                .totalSubscriptions(totalSubscriptions)
                .newSubscriptions(randomRange(1, totalSubscriptions))
                .renewedSubscriptions(randomRange(1, totalSubscriptions))
                .paymentDueDate(yearMonth.atEndOfMonth().plusDays(10))
                .previousBalance(BigDecimal.valueOf(randomRange(5000, 10000)))
                .currentBalance(BigDecimal.valueOf(randomRange(10000, 20000)))
                .finalPayableAmount(BigDecimal.valueOf(randomRange(5000, 20000)))
                .build();

        MonthlyRevenueReport savedReport = reportRepository.save(report);
        log.info("Saved mock report with ID: {}", savedReport.getId());
        return convertToDto(savedReport);
    }

    @Override
    @Transactional
    public List<MonthlyRevenueReportDto> generateMockReportsForAllFranchisees(Integer year, Integer month) {
        log.info("Generating mock reports for all franchisees for {}/{}", year, month);
        final Integer reportYear = (year == null) ? LocalDate.now().getYear() : year;
        final Integer reportMonth = (month == null) ? LocalDate.now().getMonthValue() : month;

        List<User> allUsers = userRepository.findAll();
        List<User> franchisees = allUsers.stream()
            .filter(u -> u.getRoles() != null && u.getRoles().contains(Role.FRANCHISEE))
            .collect(Collectors.toList());
        List<MonthlyRevenueReportDto> reports = new ArrayList<>();

        for (User franchisee : franchisees) {
            try {
                reports.add(generateMockReportForFranchisee(franchisee.getId(), reportYear, reportMonth));
            } catch (Exception e) {
                log.warn("Failed to generate mock report for franchisee {}: {}", franchisee.getId(), e.getMessage());
            }
        }
        return reports;
    }

    @Override
    @Transactional
    public MonthlyRevenueReportDto generateMockReportForFranchiseeDistrict(Long franchiseeDistrictId, Integer year, Integer month) {
        log.info("Generating mock report for franchisee district {} for {}/{}", franchiseeDistrictId, year, month);
        
        FranchiseeDistrict district = franchiseeDistrictRepository.findById(franchiseeDistrictId)
                .orElseThrow(() -> new IllegalArgumentException("Franchisee district not found with ID: " + franchiseeDistrictId));
        
        User franchisee = district.getUser();
        
        final Integer reportYear = (year == null) ? LocalDate.now().getYear() : year;
        final Integer reportMonth = (month == null) ? LocalDate.now().getMonthValue() : month;

        // Check if report already exists for this district, year and month
        reportRepository.findByYearAndMonthAndFranchiseeDistrictId(reportYear, reportMonth, franchiseeDistrictId)
                .ifPresent(report -> {
                    throw new IllegalStateException("Report already exists for district " + franchiseeDistrictId +
                            " for " + reportYear + "/" + reportMonth);
                });

        // Generate random report data
        LocalDateTime generatedAt = LocalDateTime.now();
        YearMonth yearMonth = YearMonth.of(reportYear, reportMonth);
        int daysInMonth = yearMonth.lengthOfMonth();

        // Generate random metrics
        int totalListings = randomRange(20, 100);
        int activeListings = randomRange(10, totalListings - 5);
        int soldListings = randomRange(2, totalListings - activeListings);
        int pendingListings = totalListings - activeListings - soldListings;
        
        int totalSubscriptions = randomRange(5, 30);
        int activeSubscriptions = randomRange(2, totalSubscriptions - 1);
        int expiredSubscriptions = totalSubscriptions - activeSubscriptions;
        
        BigDecimal totalRevenue = BigDecimal.valueOf(random.nextDouble() * 50000).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal franchiseeRevenue = totalRevenue.multiply(BigDecimal.valueOf(0.7)).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal platformRevenue = totalRevenue.subtract(franchiseeRevenue).setScale(2, BigDecimal.ROUND_HALF_UP);
        
        BigDecimal walletBalance = BigDecimal.valueOf(random.nextDouble() * 20000).setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal pendingWithdrawal = BigDecimal.valueOf(random.nextDouble() * 5000).setScale(2, BigDecimal.ROUND_HALF_UP);

        MonthlyRevenueReport report = MonthlyRevenueReport.builder()
                .year(reportYear)
                .month(reportMonth)
                .generatedAt(generatedAt)
                .reportStatus(ReportStatus.PENDING)
                .franchisee(franchisee)
                .franchiseeName(franchisee.getName())
                .businessName("Business " + franchisee.getId())
                .franchiseeDistrict(district)
                .districtId(district.getDistrictId())
                .districtName(district.getDistrictName())
                .state(district.getState())
                .totalRevenue(totalRevenue)
                .franchiseeCommission(franchiseeRevenue)
                .adminShare(platformRevenue)
                .totalSubscriptions(totalSubscriptions)
                .newSubscriptions(randomRange(1, totalSubscriptions))
                .renewedSubscriptions(randomRange(1, totalSubscriptions))
                .paymentDueDate(yearMonth.atEndOfMonth().plusDays(10))
                .previousBalance(BigDecimal.valueOf(randomRange(5000, 10000)))
                .currentBalance(BigDecimal.valueOf(randomRange(10000, 20000)))
                .finalPayableAmount(BigDecimal.valueOf(randomRange(5000, 20000)))
                .build();

        MonthlyRevenueReport savedReport = reportRepository.save(report);
        log.info("Saved mock report with ID: {}", savedReport.getId());
        return convertToDto(savedReport);
    }

    @Override
    @Transactional
    public List<MonthlyRevenueReportDto> generateMockReportsForFranchiseeDistricts(Long franchiseeId, Integer year, Integer month) {
        log.info("Generating mock reports for all districts of franchisee {} for {}/{}", franchiseeId, year, month);
        
        User franchisee = userRepository.findById(franchiseeId)
                .orElseThrow(() -> new IllegalArgumentException("Franchisee not found with ID: " + franchiseeId));
        
        List<FranchiseeDistrict> districts = franchiseeDistrictRepository.findByUserId(franchiseeId);
        if (districts.isEmpty()) {
            throw new IllegalStateException("No districts found for franchisee: " + franchiseeId);
        }
        
        final Integer reportYear = (year == null) ? LocalDate.now().getYear() : year;
        final Integer reportMonth = (month == null) ? LocalDate.now().getMonthValue() : month;
        
        List<MonthlyRevenueReportDto> reports = new ArrayList<>();
        
        for (FranchiseeDistrict district : districts) {
            try {
                reports.add(generateMockReportForFranchiseeDistrict(district.getId(), reportYear, reportMonth));
            } catch (Exception e) {
                log.warn("Failed to generate mock report for district {}: {}", district.getId(), e.getMessage());
            }
        }
        
        return reports;
    }
    
    // Helper method to convert entity to DTO
    private MonthlyRevenueReportDto convertToDto(MonthlyRevenueReport report) {
        return MonthlyRevenueReportDto.builder()
                .id(report.getId())
                .year(report.getYear())
                .month(report.getMonth())
                .generatedAt(report.getGeneratedAt())
                .reportStatus(report.getReportStatus().name())
                .franchiseeId(report.getFranchisee().getId())
                .franchiseeName(report.getFranchiseeName())
                .businessName(report.getBusinessName())
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
                .previousBalance(report.getPreviousBalance())
                .currentBalance(report.getCurrentBalance())
                .finalPayableAmount(report.getFinalPayableAmount())
                .build();
    }
    
    // Helper method to generate random integer in range
    private int randomRange(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
} 