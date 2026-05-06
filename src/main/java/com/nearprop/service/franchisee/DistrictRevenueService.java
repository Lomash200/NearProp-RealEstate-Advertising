package com.nearprop.service.franchisee;

import com.nearprop.dto.franchisee.DistrictRevenueDto;
import com.nearprop.dto.franchisee.FranchiseeRevenueStatsDto;
import com.nearprop.entity.DistrictRevenue.PaymentStatus;
import com.nearprop.entity.DistrictRevenue.RevenueType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface DistrictRevenueService {
    
    /**
     * Record a new revenue transaction for a district
     * 
     * @param districtId District ID
     * @param franchiseeDistrictId Franchisee district ID
     * @param revenueType Type of revenue
     * @param amount Total amount
     * @param description Description of the transaction
     * @param propertyId Optional property ID (for property-related revenue)
     * @param subscriptionId Optional subscription ID (for subscription-related revenue)
     * @return Created revenue record
     */
    DistrictRevenueDto recordRevenue(
            Long districtId, 
            Long franchiseeDistrictId, 
            RevenueType revenueType, 
            BigDecimal amount, 
            String description,
            Long propertyId,
            Long subscriptionId);
    
    /**
     * Record a new revenue transaction for a district with specified payment status
     * 
     * @param districtId District ID
     * @param franchiseeDistrictId Franchisee district ID
     * @param revenueType Type of revenue
     * @param amount Total amount
     * @param description Description of the transaction
     * @param initialStatus Initial payment status
     * @param subscriptionId Optional subscription ID (for subscription-related revenue)
     * @return Created revenue record
     */
    DistrictRevenueDto recordRevenue(
            Long districtId, 
            Long franchiseeDistrictId, 
            RevenueType revenueType, 
            BigDecimal amount, 
            String description,
            PaymentStatus initialStatus,
            Long subscriptionId);
    
    /**
     * Get a revenue record by ID
     * 
     * @param revenueId Revenue ID
     * @return Revenue details
     */
    DistrictRevenueDto getRevenue(Long revenueId);
    
    /**
     * Update payment status of a revenue record
     * 
     * @param revenueId Revenue ID
     * @param status New payment status
     * @param paymentReference Payment reference (for PAID status)
     * @return Updated revenue record
     */
    DistrictRevenueDto updatePaymentStatus(Long revenueId, PaymentStatus status, String paymentReference);
    
    /**
     * Update payment status of all revenue records associated with a subscription
     * 
     * @param subscriptionId Subscription ID
     * @param status New payment status
     * @param paymentReference Payment reference (for PAID status)
     */
    void updateSubscriptionPaymentStatus(Long subscriptionId, PaymentStatus status, String paymentReference);
    
    /**
     * Handle stuck pending transactions that are older than cutoff time
     * 
     * @param cutoffTime Cutoff time
     * @return Number of transactions processed
     */
    int handleStuckPendingTransactions(LocalDateTime cutoffTime);
    
    /**
     * Get revenue records by district ID
     * 
     * @param districtId District ID
     * @param pageable Pagination information
     * @return Page of revenue records
     */
    Page<DistrictRevenueDto> getRevenuesByDistrict(Long districtId, Pageable pageable);
    
    /**
     * Get revenue records by franchisee district ID
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @param pageable Pagination information
     * @return Page of revenue records
     */
    Page<DistrictRevenueDto> getRevenuesByFranchiseeDistrict(Long franchiseeDistrictId, Pageable pageable);
    
    /**
     * Get revenue records by franchisee user ID
     * 
     * @param franchiseeUserId Franchisee user ID
     * @param pageable Pagination information
     * @return Page of revenue records
     */
    Page<DistrictRevenueDto> getRevenuesByFranchiseeUser(Long franchiseeUserId, Pageable pageable);
    
    /**
     * Get revenue records by date range
     * 
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination information
     * @return Page of revenue records
     */
    Page<DistrictRevenueDto> getRevenuesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    /**
     * Get revenue records by type
     * 
     * @param revenueType Revenue type
     * @param pageable Pagination information
     * @return Page of revenue records
     */
    Page<DistrictRevenueDto> getRevenuesByType(RevenueType revenueType, Pageable pageable);
    
    /**
     * Get revenue records by payment status
     * 
     * @param status Payment status
     * @param pageable Pagination information
     * @return Page of revenue records
     */
    Page<DistrictRevenueDto> getRevenuesByPaymentStatus(PaymentStatus status, Pageable pageable);
    
    /**
     * Get revenue statistics for a franchisee
     * 
     * @param franchiseeUserId Franchisee user ID
     * @return Revenue statistics
     */
    FranchiseeRevenueStatsDto getFranchiseeRevenueStats(Long franchiseeUserId);
    
    /**
     * Get revenue statistics for a district
     * 
     * @param districtId District ID
     * @return Revenue statistics
     */
    FranchiseeRevenueStatsDto getDistrictRevenueStats(Long districtId);
    
    /**
     * Process pending payments to franchisees
     */
    void processPendingPayments();
}