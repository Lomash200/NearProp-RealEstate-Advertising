package com.nearprop.service.franchisee.impl;

import com.nearprop.dto.franchisee.DistrictRevenueDto;
import com.nearprop.dto.franchisee.FranchiseeRevenueStatsDto;
import com.nearprop.entity.DistrictRevenue;
import com.nearprop.entity.DistrictRevenue.PaymentStatus;
import com.nearprop.entity.DistrictRevenue.RevenueType;
import com.nearprop.entity.FranchiseeDistrict;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.franchisee.DistrictRevenueRepository;
import com.nearprop.repository.franchisee.FranchiseeDistrictRepository;
import com.nearprop.service.franchisee.DistrictRevenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistrictRevenueServiceImpl implements DistrictRevenueService {

    private final DistrictRevenueRepository districtRevenueRepository;
    private final FranchiseeDistrictRepository franchiseeDistrictRepository;
    
    // Set 50% revenue share for subscription payments
    private static final BigDecimal SUBSCRIPTION_REVENUE_SHARE = new BigDecimal("50.00");

    @Override
    @Transactional
    public DistrictRevenueDto recordRevenue(
            Long districtId, 
            Long franchiseeDistrictId, 
            RevenueType revenueType, 
            BigDecimal amount, 
            String description,
            Long propertyId,
            Long subscriptionId) {
        
        return recordRevenue(districtId, franchiseeDistrictId, revenueType, amount, description, PaymentStatus.PENDING, subscriptionId);
    }
    
    @Override
    @Transactional
    public DistrictRevenueDto recordRevenue(
            Long districtId, 
            Long franchiseeDistrictId, 
            RevenueType revenueType, 
            BigDecimal amount, 
            String description,
            PaymentStatus initialStatus,
            Long subscriptionId) {
        
        // Validate franchisee district
        FranchiseeDistrict franchiseeDistrict = franchiseeDistrictRepository.findById(franchiseeDistrictId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchisee district not found with ID: " + franchiseeDistrictId));
        
        // Ensure district ID matches
        if (!franchiseeDistrict.getDistrictId().equals(districtId)) {
            throw new IllegalArgumentException("District ID does not match the franchisee district");
        }
        
        // Calculate franchisee share based on revenue type
        BigDecimal franchiseeSharePercentage;
        
        // For subscription payments, use fixed 50% share
        if (revenueType == RevenueType.SUBSCRIPTION_PAYMENT) {
            franchiseeSharePercentage = SUBSCRIPTION_REVENUE_SHARE;
        } else {
            // For other revenue types, use the configured share percentage
            franchiseeSharePercentage = franchiseeDistrict.getRevenueSharePercentage();
        }
        
        BigDecimal franchiseeCommission = amount.multiply(franchiseeSharePercentage).divide(new BigDecimal(100));
        BigDecimal companyRevenue = amount.subtract(franchiseeCommission);
        
        DistrictRevenue revenue = DistrictRevenue.builder()
                .districtId(districtId)
                .districtName(franchiseeDistrict.getDistrictName())
                .state(franchiseeDistrict.getState())
                .franchiseeDistrict(franchiseeDistrict)
                .revenueType(revenueType)
                .amount(amount)
                .franchiseeCommission(franchiseeCommission)
                .companyRevenue(companyRevenue)
                .description(description)
                .subscriptionId(subscriptionId)
                .paymentStatus(initialStatus != null ? initialStatus : PaymentStatus.PENDING)
                .transactionDate(LocalDateTime.now())
                .build();
        
        DistrictRevenue savedRevenue = districtRevenueRepository.save(revenue);
        
        // Update district totals if payment is confirmed
        if (savedRevenue.getPaymentStatus() == PaymentStatus.PAID) {
            updateDistrictTotals(franchiseeDistrict, savedRevenue);
        }
        
        return mapToDto(savedRevenue);
    }

    @Override
    @Transactional(readOnly = true)
    public DistrictRevenueDto getRevenue(Long revenueId) {
        DistrictRevenue revenue = districtRevenueRepository.findById(revenueId)
                .orElseThrow(() -> new ResourceNotFoundException("Revenue record not found with ID: " + revenueId));
        return mapToDto(revenue);
    }

    @Override
    @Transactional
    public DistrictRevenueDto updatePaymentStatus(Long revenueId, PaymentStatus status, String paymentReference) {
        DistrictRevenue revenue = districtRevenueRepository.findById(revenueId)
                .orElseThrow(() -> new ResourceNotFoundException("Revenue record not found with ID: " + revenueId));
        
        revenue.setPaymentStatus(status);
        
        if (status == PaymentStatus.PAID) {
            revenue.setPaymentReference(paymentReference);
            revenue.setPaymentDate(LocalDateTime.now());
        }
        
        DistrictRevenue updatedRevenue = districtRevenueRepository.save(revenue);
        return mapToDto(updatedRevenue);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictRevenueDto> getRevenuesByDistrict(Long districtId, Pageable pageable) {
        List<DistrictRevenue> revenues = districtRevenueRepository.findByDistrictId(districtId);
        List<DistrictRevenueDto> dtos = revenues.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictRevenueDto> getRevenuesByFranchiseeDistrict(Long franchiseeDistrictId, Pageable pageable) {
        List<DistrictRevenue> revenues = districtRevenueRepository.findByFranchiseeDistrictId(franchiseeDistrictId);
        List<DistrictRevenueDto> dtos = revenues.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictRevenueDto> getRevenuesByFranchiseeUser(Long franchiseeUserId, Pageable pageable) {
        return districtRevenueRepository.findByFranchiseeUserId(franchiseeUserId, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictRevenueDto> getRevenuesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        List<DistrictRevenue> revenues = districtRevenueRepository.findByDateRange(startDate, endDate);
        List<DistrictRevenueDto> dtos = revenues.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictRevenueDto> getRevenuesByType(RevenueType revenueType, Pageable pageable) {
        List<DistrictRevenue> revenues = districtRevenueRepository.findByRevenueType(revenueType);
        List<DistrictRevenueDto> dtos = revenues.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictRevenueDto> getRevenuesByPaymentStatus(PaymentStatus status, Pageable pageable) {
        List<DistrictRevenue> revenues = districtRevenueRepository.findByPaymentStatus(status);
        List<DistrictRevenueDto> dtos = revenues.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());
        
        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }

    @Override
    @Transactional(readOnly = true)
    public FranchiseeRevenueStatsDto getFranchiseeRevenueStats(Long franchiseeUserId) {
        // Get all franchisee district IDs for this user
        List<FranchiseeDistrict> franchiseeDistricts = franchiseeDistrictRepository.findByUserId(franchiseeUserId);
        
        if (franchiseeDistricts.isEmpty()) {
            return createEmptyRevenueStats();
        }
        
        BigDecimal totalPaid = districtRevenueRepository.sumCommissionByFranchiseeUserIdAndStatus(
                franchiseeUserId, PaymentStatus.PAID);
        
        BigDecimal totalPending = districtRevenueRepository.sumCommissionByFranchiseeUserIdAndStatus(
                franchiseeUserId, PaymentStatus.PENDING);
        
        // Calculate total revenue as sum of paid and pending
        BigDecimal totalCommission = (totalPaid != null ? totalPaid : BigDecimal.ZERO)
                .add(totalPending != null ? totalPending : BigDecimal.ZERO);
        
        // Since we don't have a direct method to count transactions, we'll estimate
        long transactionCount = franchiseeDistricts.stream()
                .mapToLong(fd -> districtRevenueRepository.findByFranchiseeDistrictId(fd.getId()).size())
                .sum();
        
        return FranchiseeRevenueStatsDto.builder()
                .franchiseeUserId(franchiseeUserId)
                .totalRevenue(totalCommission)
                .totalCommission(totalCommission)
                .paidCommission(totalPaid != null ? totalPaid : BigDecimal.ZERO)
                .pendingCommission(totalPending != null ? totalPending : BigDecimal.ZERO)
                .totalTransactions((int) transactionCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FranchiseeRevenueStatsDto getDistrictRevenueStats(Long districtId) {
        // Calculate stats
        BigDecimal totalRevenue = districtRevenueRepository.sumTotalRevenueByDistrict(districtId);
        
        List<DistrictRevenue> paidRevenues = districtRevenueRepository.findByDistrictIdAndPaymentStatus(
                districtId, PaymentStatus.PAID);
        
        
        BigDecimal totalPaid = paidRevenues.stream()
                .map(DistrictRevenue::getFranchiseeCommission)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long transactionCount = districtRevenueRepository.findByDistrictId(districtId).size();
        
        return FranchiseeRevenueStatsDto.builder()
                .districtId(districtId)
                .paidCommission(totalPaid)
                .totalTransactions((int) transactionCount)
                .build();
    }

    @Override
    @Transactional
    public void processPendingPayments() {
        // In a real implementation, this would integrate with a payment processor
        // For now, just log that we're processing payments
        System.out.println("Processing pending payments at " + LocalDateTime.now());
        
        // Find all pending payments
        List<DistrictRevenue> pendingPayments = districtRevenueRepository.findByPaymentStatus(PaymentStatus.PENDING);
        
        // Process each payment (in a real system, this would call a payment API)
        for (DistrictRevenue payment : pendingPayments) {
            // Simulate payment processing
            payment.setPaymentStatus(PaymentStatus.PAID);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentReference("AUTO-" + System.currentTimeMillis());
            
            districtRevenueRepository.save(payment);
        }
    }
    
    @Override
    @Transactional
    public void updateSubscriptionPaymentStatus(Long subscriptionId, PaymentStatus status, String paymentReference) {
        List<DistrictRevenue> revenues = districtRevenueRepository.findBySubscriptionId(subscriptionId);
        if (revenues.isEmpty()) {
            throw new ResourceNotFoundException("No revenue records found for subscription: " + subscriptionId);
        }
        
        for (DistrictRevenue revenue : revenues) {
            if (revenue.getPaymentStatus() != PaymentStatus.PAID) {
                revenue.setPaymentStatus(status);
                revenue.setPaymentReference(paymentReference);
                if (status == PaymentStatus.PAID) {
                    revenue.setPaymentDate(LocalDateTime.now());
                    // Update district totals when payment is confirmed
                    updateDistrictTotals(revenue.getFranchiseeDistrict(), revenue);
                }
                districtRevenueRepository.save(revenue);
            }
        }
    }
    
    @Override
    @Transactional
    public int handleStuckPendingTransactions(LocalDateTime cutoffTime) {
        List<DistrictRevenue> stuckTransactions = districtRevenueRepository.findStuckPendingTransactions(cutoffTime);
        
        int count = 0;
        for (DistrictRevenue revenue : stuckTransactions) {
            // Check if related subscription is confirmed
            if (revenue.getSubscriptionId() != null) {
                // For now, we'll just log these transactions
                count++;
            }
        }
        return count;
    }
    
    private DistrictRevenueDto mapToDto(DistrictRevenue revenue) {
        return DistrictRevenueDto.builder()
                .id(revenue.getId())
                .districtId(revenue.getDistrictId())
                .districtName(revenue.getDistrictName())
                .state(revenue.getState())
                .revenueType(revenue.getRevenueType())
                .amount(revenue.getAmount())
                .franchiseeCommission(revenue.getFranchiseeCommission())
                .companyRevenue(revenue.getCompanyRevenue())
                .description(revenue.getDescription())
                .propertyId(revenue.getPropertyId())
                .subscriptionId(revenue.getSubscriptionId())
                .paymentStatus(revenue.getPaymentStatus())
                .paymentReference(revenue.getPaymentReference())
                .transactionDate(revenue.getTransactionDate())
                .paymentDate(revenue.getPaymentDate())
                .build();
    }
    
    private FranchiseeRevenueStatsDto createEmptyRevenueStats() {
        return FranchiseeRevenueStatsDto.builder()
                .totalRevenue(BigDecimal.ZERO)
                .totalCommission(BigDecimal.ZERO)
                .paidCommission(BigDecimal.ZERO)
                .totalTransactions(0)
                .build();
    }
    
    private void updateDistrictTotals(FranchiseeDistrict district, DistrictRevenue revenue) {
        // Initialize fields if null
        if (district.getTotalRevenue() == null) {
            district.setTotalRevenue(BigDecimal.ZERO);
        }
        if (district.getTotalCommission() == null) {
            district.setTotalCommission(BigDecimal.ZERO);
        }
        if (district.getTotalTransactions() == null) {
            district.setTotalTransactions(0);
        }
        
        // Update totals
        district.setTotalRevenue(district.getTotalRevenue().add(revenue.getAmount()));
        district.setTotalCommission(district.getTotalCommission().add(revenue.getFranchiseeCommission()));
        district.setTotalTransactions(district.getTotalTransactions() + 1);
        franchiseeDistrictRepository.save(district);
    }
}