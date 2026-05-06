package com.nearprop.repository.franchisee;

import com.nearprop.entity.DistrictRevenue;
import com.nearprop.entity.FranchiseeDistrict;
import com.nearprop.entity.DistrictRevenue.PaymentStatus;
import com.nearprop.entity.DistrictRevenue.RevenueType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DistrictRevenueRepository extends JpaRepository<DistrictRevenue, Long> {
    
    List<DistrictRevenue> findByDistrictId(Long districtId);
    
    List<DistrictRevenue> findByFranchiseeDistrictId(Long franchiseeDistrictId);
    
    List<DistrictRevenue> findByRevenueType(RevenueType revenueType);
    
    List<DistrictRevenue> findByPaymentStatus(PaymentStatus paymentStatus);
    
    @Query("SELECT dr FROM DistrictRevenue dr WHERE dr.transactionDate BETWEEN :startDate AND :endDate")
    List<DistrictRevenue> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT dr FROM DistrictRevenue dr WHERE dr.franchiseeDistrict.user.id = :franchiseeUserId")
    Page<DistrictRevenue> findByFranchiseeUserId(Long franchiseeUserId, Pageable pageable);
    
    @Query("SELECT dr FROM DistrictRevenue dr WHERE dr.districtId = :districtId AND dr.paymentStatus = :status")
    List<DistrictRevenue> findByDistrictIdAndPaymentStatus(Long districtId, PaymentStatus status);
    
    @Query("SELECT SUM(dr.amount) FROM DistrictRevenue dr WHERE dr.districtId = :districtId")
    BigDecimal sumTotalRevenueByDistrict(Long districtId);
    
    @Query("SELECT SUM(dr.franchiseeCommission) FROM DistrictRevenue dr WHERE dr.franchiseeDistrict.id = :franchiseeDistrictId")
    BigDecimal sumTotalCommissionByFranchiseeDistrict(Long franchiseeDistrictId);
    
    /**
     * Sum all franchisee commissions for a specific franchisee district ID, regardless of payment status
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @return Total commission amount
     */
    @Query("SELECT SUM(dr.franchiseeCommission) FROM DistrictRevenue dr " +
           "WHERE dr.franchiseeDistrict.id = :franchiseeDistrictId " +
           "AND dr.paymentStatus != 'CANCELLED'")
    BigDecimal sumAllCommissionsByFranchiseeDistrictId(Long franchiseeDistrictId);
    
    @Query("SELECT SUM(dr.franchiseeCommission) FROM DistrictRevenue dr " +
           "WHERE dr.franchiseeDistrict.user.id = :franchiseeUserId " +
           "AND dr.paymentStatus = :status")
    BigDecimal sumCommissionByFranchiseeUserIdAndStatus(Long franchiseeUserId, PaymentStatus status);
    
    @Query("SELECT dr FROM DistrictRevenue dr WHERE dr.propertyId = :propertyId")
    List<DistrictRevenue> findByPropertyId(Long propertyId);
    
    @Query("SELECT dr FROM DistrictRevenue dr WHERE dr.subscriptionId = :subscriptionId")
    List<DistrictRevenue> findBySubscriptionId(Long subscriptionId);
    
    @Query("SELECT SUM(dr.franchiseeCommission) FROM DistrictRevenue dr " +
           "WHERE dr.franchiseeDistrict.id = :franchiseeDistrictId " +
           "AND dr.revenueType = 'SUBSCRIPTION_PAYMENT' " +
           "AND dr.paymentStatus = :status")
    BigDecimal sumSubscriptionCommissionByFranchiseeDistrictAndStatus(Long franchiseeDistrictId, PaymentStatus status);
    
    @Query("SELECT COUNT(DISTINCT dr.subscriptionId) FROM DistrictRevenue dr " +
           "WHERE dr.franchiseeDistrict.id = :franchiseeDistrictId " +
           "AND dr.revenueType = 'SUBSCRIPTION_PAYMENT'")
    Long countSubscribersByFranchiseeDistrict(Long franchiseeDistrictId);
    
    @Query("SELECT SUM(dr.franchiseeCommission) FROM DistrictRevenue dr " +
           "WHERE dr.franchiseeDistrict.id = :franchiseeDistrictId " +
           "AND dr.revenueType = 'SUBSCRIPTION_PAYMENT' " +
           "AND dr.paymentStatus = :status " +
           "AND dr.transactionDate BETWEEN :startDate AND :endDate")
    BigDecimal sumSubscriptionCommissionByFranchiseeDistrictAndDateRange(
            Long franchiseeDistrictId, 
            PaymentStatus status, 
            LocalDateTime startDate, 
            LocalDateTime endDate);
    
    List<DistrictRevenue> findByFranchiseeDistrictAndRevenueType(
        FranchiseeDistrict franchiseeDistrict, 
        RevenueType revenueType);
    
    List<DistrictRevenue> findByFranchiseeDistrictAndPaymentStatus(
        FranchiseeDistrict franchiseeDistrict, 
        PaymentStatus paymentStatus);
    
    @Query("SELECT dr FROM DistrictRevenue dr " +
           "WHERE dr.paymentStatus = 'PENDING' " +
           "AND dr.transactionDate <= :cutoffTime")
    List<DistrictRevenue> findStuckPendingTransactions(LocalDateTime cutoffTime);

    /**
     * Find all district revenue entries for a specific franchisee district within a date range
     * 
     * @param franchiseeDistrict Franchisee district
     * @param startDate Start date
     * @param endDate End date
     * @return List of district revenue entries
     */
    List<DistrictRevenue> findByFranchiseeDistrictAndTransactionDateBetween(
        FranchiseeDistrict franchiseeDistrict,
        LocalDateTime startDate,
        LocalDateTime endDate);
}