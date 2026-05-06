package com.nearprop.service.franchisee;

import com.nearprop.dto.franchisee.CreateWithdrawalRequestDto;
import com.nearprop.dto.franchisee.WithdrawalRequestDto;
import com.nearprop.dto.franchisee.WithdrawalRequestResponseDto;
import com.nearprop.dto.franchisee.WithdrawalHistoryDto;
import com.nearprop.entity.FranchiseeWithdrawalRequest.WithdrawalStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface WithdrawalRequestService {
    
    /**
     * Create a new withdrawal request from a franchisee
     * 
     * @param requestDto Withdrawal request details
     * @param franchiseeId The franchisee making the request
     * @return Created withdrawal request
     */
    WithdrawalRequestDto createWithdrawalRequest(CreateWithdrawalRequestDto requestDto, Long franchiseeId);
    
    /**
     * Get a specific withdrawal request by ID
     * 
     * @param requestId Withdrawal request ID
     * @return Withdrawal request details
     */
    WithdrawalRequestDto getWithdrawalRequest(Long requestId);
    
    /**
     * Get all withdrawal requests for a specific franchisee
     * 
     * @param franchiseeId Franchisee ID
     * @return List of withdrawal requests
     */
    List<WithdrawalRequestDto> getWithdrawalRequestsByFranchisee(Long franchiseeId);
    
    /**
     * Get all withdrawal requests by status
     * 
     * @param status Withdrawal status
     * @return List of withdrawal requests
     */
    List<WithdrawalRequestDto> getWithdrawalRequestsByStatus(WithdrawalStatus status);
    
    /**
     * Process a withdrawal request (admin only)
     * 
     * @param requestId Withdrawal request ID
     * @param responseDto Response details (status, comments, payment reference)
     * @param adminId Admin user ID
     * @return Updated withdrawal request
     */
    WithdrawalRequestDto processWithdrawalRequest(Long requestId, WithdrawalRequestResponseDto responseDto, Long adminId);
    
    /**
     * Calculate the available balance for emergency withdrawal for a franchisee district
     * (30% of pending commission from subscriptions)
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @return Available balance for withdrawal
     */
    BigDecimal calculateAvailableWithdrawalBalance(Long franchiseeDistrictId);
    
    /**
     * Get the total amount withdrawn (approved and paid) for a franchisee district since a specified date
     * 
     * @param franchiseeDistrictId Franchisee district ID
     * @param startDate The start date for withdrawal history
     * @return Total withdrawn amount
     */
    BigDecimal getTotalWithdrawnAmount(Long franchiseeDistrictId, LocalDateTime startDate);

    /**
     * Get the total available balance for a franchisee user across all their districts
     * This is the consistent method that should be used by all APIs
     * 
     * @param franchiseeUserId Franchisee user ID
     * @return Total available balance
     */
    BigDecimal getTotalAvailableBalanceForFranchisee(Long franchiseeUserId);

    /**
     * Generate monthly revenue reports for all franchisees
     * This should be run at the end of each month
     * 
     * @return Number of reports generated
     */
    int generateMonthlyRevenueReports();
    
    /**
     * Get all withdrawal requests
     * 
     * @return List of all withdrawal requests
     */
    List<WithdrawalRequestDto> getAllWithdrawalRequests();
    
    /**
     * Get withdrawal history for a franchisee
     * 
     * @param franchiseeId Franchisee ID
     * @return Withdrawal history
     */
    WithdrawalHistoryDto getWithdrawalHistory(Long franchiseeId);
    
    /**
     * Get withdrawal history for a district
     * 
     * @param districtId District ID
     * @return Withdrawal history
     */
    WithdrawalHistoryDto getWithdrawalHistoryForDistrict(Long districtId);
}