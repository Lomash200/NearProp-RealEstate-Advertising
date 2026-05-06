package com.nearprop.repository.franchisee;

import com.nearprop.entity.FranchiseeWithdrawalRequest;
import com.nearprop.entity.FranchiseeWithdrawalRequest.WithdrawalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.nearprop.entity.FranchiseeDistrict;


@Repository
public interface WithdrawalRequestRepository extends JpaRepository<FranchiseeWithdrawalRequest, Long> {
    
    List<FranchiseeWithdrawalRequest> findByFranchiseeDistrictId(Long franchiseeDistrictId);
    
    List<FranchiseeWithdrawalRequest> findByStatus(WithdrawalStatus status);
    
    @Query("SELECT w FROM FranchiseeWithdrawalRequest w WHERE w.franchiseeDistrict.user.id = :franchiseeId")
    List<FranchiseeWithdrawalRequest> findByFranchiseeId(Long franchiseeId);
    
    @Query("SELECT COUNT(w) FROM FranchiseeWithdrawalRequest w WHERE w.franchiseeDistrict.id = :franchiseeDistrictId AND w.status = 'PENDING'")
    Long countPendingRequestsByFranchiseeDistrictId(Long franchiseeDistrictId);
    
    @Query("SELECT SUM(w.requestedAmount) FROM FranchiseeWithdrawalRequest w WHERE w.franchiseeDistrict.id = :franchiseeDistrictId AND w.status = 'PAID' AND w.createdAt >= :startDate")
    BigDecimal sumWithdrawalAmountsSinceDate(Long franchiseeDistrictId, LocalDateTime startDate);
    
    @Query("SELECT w FROM FranchiseeWithdrawalRequest w WHERE w.franchiseeDistrict.id = :franchiseeDistrictId " +
           "AND w.status IN ('APPROVED', 'PAID') " +
           "AND w.createdAt >= :startDate")
    List<FranchiseeWithdrawalRequest> findPaidRequestsSinceDate(Long franchiseeDistrictId, LocalDateTime startDate);

    /**
     * Calculate the sum of approved and paid withdrawal amounts for a franchisee district within a date range
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @param startDate Start date
     * @param endDate End date
     * @return Sum of approved withdrawal amounts
     */
    @Query("SELECT SUM(w.requestedAmount) FROM FranchiseeWithdrawalRequest w WHERE w.franchiseeDistrict.id = :franchiseeDistrictId " +
           "AND w.status IN ('APPROVED', 'PAID') " +
           "AND w.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal sumApprovedWithdrawalAmountsBetweenDates(Long franchiseeDistrictId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Calculate the sum of all approved and paid withdrawal amounts for a franchisee district
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @return Sum of approved withdrawal amounts
     */
    @Query("SELECT SUM(w.requestedAmount) FROM FranchiseeWithdrawalRequest w WHERE w.franchiseeDistrict.id = :franchiseeDistrictId " +
           "AND w.status IN ('APPROVED', 'PAID')")
    BigDecimal sumApprovedWithdrawalAmountsByFranchiseeDistrictId(Long franchiseeDistrictId);

    /**
     * Find all withdrawal requests for a specific franchisee district within a date range
     * 
     * @param franchiseeDistrict Franchisee district
     * @param startDate Start date
     * @param endDate End date
     * @return List of withdrawal requests
     */
    List<FranchiseeWithdrawalRequest> findByFranchiseeDistrictAndCreatedAtBetween(
        FranchiseeDistrict franchiseeDistrict,
        LocalDateTime startDate,
        LocalDateTime endDate);
        
    /**
     * Find all withdrawal requests ordered by created date descending
     * 
     * @return List of all withdrawal requests
     */
    @Query("SELECT w FROM FranchiseeWithdrawalRequest w ORDER BY w.createdAt DESC")
    List<FranchiseeWithdrawalRequest> findAllOrderByCreatedAtDesc();
} 