package com.nearprop.service.franchisee.impl;

import com.nearprop.dto.franchisee.FranchiseeBankDetailDto;
import com.nearprop.entity.FranchiseeBankDetail;
import com.nearprop.entity.User;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.exception.UnauthorizedException;
import com.nearprop.repository.FranchiseeBankDetailRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.franchisee.FranchiseeBankDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FranchiseeBankDetailServiceImpl implements FranchiseeBankDetailService {

    private final FranchiseeBankDetailRepository bankDetailRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public FranchiseeBankDetailDto addBankDetail(FranchiseeBankDetailDto bankDetailDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Check if account number already exists for this user
        if (bankDetailRepository.existsByUserIdAndAccountNumber(userId, bankDetailDto.getAccountNumber())) {
            throw new IllegalArgumentException("Bank account with this account number already exists");
        }
        
        FranchiseeBankDetail bankDetail = mapToEntity(bankDetailDto);
        bankDetail.setUser(user);
        
        // If this is the first account or explicitly marked as primary
        boolean shouldBePrimary = bankDetailDto.isPrimary() || 
                                  !bankDetailRepository.findByUserIdAndIsPrimaryTrue(userId).isPresent();
        
        if (shouldBePrimary) {
            // If making this account primary, ensure other accounts are not primary
            bankDetailRepository.findByUserIdAndIsPrimaryTrue(userId)
                    .ifPresent(existingPrimary -> {
                        existingPrimary.setPrimary(false);
                        bankDetailRepository.save(existingPrimary);
                    });
            
            bankDetail.setPrimary(true);
        }
        
        bankDetail.setVerified(false); // New accounts are not verified by default
        
        FranchiseeBankDetail savedDetail = bankDetailRepository.save(bankDetail);
        log.info("Added new bank detail for user {}: {}", userId, savedDetail.getId());
        
        return mapToDto(savedDetail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FranchiseeBankDetailDto> getBankDetailsByUserId(Long userId) {
        return bankDetailRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FranchiseeBankDetailDto getBankDetailById(Long id, Long userId) {
        FranchiseeBankDetail bankDetail = bankDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank detail not found with id: " + id));
        
        // Ensure user can only access their own bank details
        if (!bankDetail.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to access this bank detail");
        }
        
        return mapToDto(bankDetail);
    }

    @Override
    @Transactional
    public FranchiseeBankDetailDto updateBankDetail(Long id, FranchiseeBankDetailDto bankDetailDto, Long userId) {
        FranchiseeBankDetail bankDetail = bankDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank detail not found with id: " + id));
        
        // Ensure user can only update their own bank details
        if (!bankDetail.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to update this bank detail");
        }
        
        // Don't allow updating account number for verified accounts
        if (bankDetail.isVerified() && !bankDetail.getAccountNumber().equals(bankDetailDto.getAccountNumber())) {
            throw new IllegalArgumentException("Cannot change account number for a verified account");
        }
        
        // Update fields
        bankDetail.setAccountName(bankDetailDto.getAccountName());
        bankDetail.setBankName(bankDetailDto.getBankName());
        bankDetail.setBranchName(bankDetailDto.getBranchName());
        bankDetail.setIfscCode(bankDetailDto.getIfscCode());
        bankDetail.setAccountType(bankDetailDto.getAccountType());
        bankDetail.setUpiId(bankDetailDto.getUpiId());
        
        // Only update account number if not verified
        if (!bankDetail.isVerified()) {
            bankDetail.setAccountNumber(bankDetailDto.getAccountNumber());
        }
        
        // Handle primary status change
        if (bankDetailDto.isPrimary() && !bankDetail.isPrimary()) {
            // If making this account primary, ensure other accounts are not primary
            bankDetailRepository.findByUserIdAndIsPrimaryTrue(userId)
                    .ifPresent(existingPrimary -> {
                        existingPrimary.setPrimary(false);
                        bankDetailRepository.save(existingPrimary);
                    });
            
            bankDetail.setPrimary(true);
        }
        
        FranchiseeBankDetail updatedDetail = bankDetailRepository.save(bankDetail);
        log.info("Updated bank detail for user {}: {}", userId, updatedDetail.getId());
        
        return mapToDto(updatedDetail);
    }

    @Override
    @Transactional
    public void deleteBankDetail(Long id, Long userId) {
        FranchiseeBankDetail bankDetail = bankDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank detail not found with id: " + id));
        
        // Ensure user can only delete their own bank details
        if (!bankDetail.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to delete this bank detail");
        }
        
        // Don't allow deleting the primary account if there are others
        if (bankDetail.isPrimary() && bankDetailRepository.findByUserId(userId).size() > 1) {
            throw new IllegalArgumentException("Cannot delete primary bank account. Set another account as primary first.");
        }
        
        bankDetailRepository.delete(bankDetail);
        log.info("Deleted bank detail for user {}: {}", userId, id);
    }

    @Override
    @Transactional
    public FranchiseeBankDetailDto setPrimaryBankDetail(Long id, Long userId) {
        FranchiseeBankDetail bankDetail = bankDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank detail not found with id: " + id));
        
        // Ensure user can only update their own bank details
        if (!bankDetail.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to update this bank detail");
        }
        
        // If already primary, nothing to do
        if (bankDetail.isPrimary()) {
            return mapToDto(bankDetail);
        }
        
        // Set current primary to not primary
        bankDetailRepository.findByUserIdAndIsPrimaryTrue(userId)
                .ifPresent(existingPrimary -> {
                    existingPrimary.setPrimary(false);
                    bankDetailRepository.save(existingPrimary);
                });
        
        // Set new primary
        bankDetail.setPrimary(true);
        FranchiseeBankDetail updatedDetail = bankDetailRepository.save(bankDetail);
        log.info("Set bank detail {} as primary for user {}", id, userId);
        
        return mapToDto(updatedDetail);
    }

   /* @Override
    @Transactional(readOnly = true)
    public FranchiseeBankDetailDto getPrimaryBankDetail(Long userId) {
        return bankDetailRepository.findByUserIdAndIsPrimaryTrue(userId)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("No primary bank detail found for user: " + userId));
    }*/

    @Override
    @Transactional(readOnly = true)
    public FranchiseeBankDetailDto getPrimaryBankDetail(Long userId) {
        return bankDetailRepository.findByUserIdAndIsPrimaryTrue(userId)
                .map(this::mapToDto)
                .orElse(null);
    }

    @Override
    @Transactional
    public FranchiseeBankDetailDto verifyBankDetail(Long id, Long adminId) {
        FranchiseeBankDetail bankDetail = bankDetailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank detail not found with id: " + id));
        
        bankDetail.setVerified(true);
        bankDetail.setVerifiedAt(LocalDateTime.now());
        bankDetail.setVerifiedBy(adminId);
        
        FranchiseeBankDetail verifiedDetail = bankDetailRepository.save(bankDetail);
        log.info("Bank detail {} verified by admin {}", id, adminId);
        
        return mapToDto(verifiedDetail);
    }

    private FranchiseeBankDetailDto mapToDto(FranchiseeBankDetail entity) {
        return FranchiseeBankDetailDto.builder()
                .id(entity.getId())
                .accountName(entity.getAccountName())
                .accountNumber(entity.getAccountNumber())
                .ifscCode(entity.getIfscCode())
                .bankName(entity.getBankName())
                .branchName(entity.getBranchName())
                .accountType(entity.getAccountType())
                .upiId(entity.getUpiId())
                .isPrimary(entity.isPrimary())
                .isVerified(entity.isVerified())
                .build();
    }

    private FranchiseeBankDetail mapToEntity(FranchiseeBankDetailDto dto) {
        return FranchiseeBankDetail.builder()
                .accountName(dto.getAccountName())
                .accountNumber(dto.getAccountNumber())
                .ifscCode(dto.getIfscCode())
                .bankName(dto.getBankName())
                .branchName(dto.getBranchName())
                .accountType(dto.getAccountType())
                .upiId(dto.getUpiId())
                .isPrimary(dto.isPrimary())
                .isVerified(false) // Always false for new entries
                .build();
    }
}
