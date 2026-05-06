package com.nearprop.service.franchisee;

import com.nearprop.dto.franchisee.FranchiseeBankDetailDto;

import java.util.List;

public interface FranchiseeBankDetailService {
    
    /**
     * Add a new bank detail for a franchisee
     */
    FranchiseeBankDetailDto addBankDetail(FranchiseeBankDetailDto bankDetailDto, Long userId);
    
    /**
     * Get all bank details for a franchisee
     */
    List<FranchiseeBankDetailDto> getBankDetailsByUserId(Long userId);
    
    /**
     * Get bank detail by ID
     */
    FranchiseeBankDetailDto getBankDetailById(Long id, Long userId);
    
    /**
     * Update a bank detail
     */
    FranchiseeBankDetailDto updateBankDetail(Long id, FranchiseeBankDetailDto bankDetailDto, Long userId);
    
    /**
     * Delete a bank detail
     */
    void deleteBankDetail(Long id, Long userId);
    
    /**
     * Set a bank detail as primary
     */
    FranchiseeBankDetailDto setPrimaryBankDetail(Long id, Long userId);
    
    /**
     * Get primary bank detail for a franchisee
     */
    FranchiseeBankDetailDto getPrimaryBankDetail(Long userId);
    
    /**
     * Verify a bank detail (admin only)
     */
    FranchiseeBankDetailDto verifyBankDetail(Long id, Long adminId);
}
