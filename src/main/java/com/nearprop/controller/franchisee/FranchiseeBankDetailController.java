package com.nearprop.controller.franchisee;

import com.nearprop.dto.franchisee.FranchiseeBankDetailDto;
import com.nearprop.entity.User;
import com.nearprop.service.franchisee.FranchiseeBankDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/franchisee/bank-details")
@RequiredArgsConstructor
@Slf4j
public class FranchiseeBankDetailController {

    private final FranchiseeBankDetailService bankDetailService;
    
    @PostMapping
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<FranchiseeBankDetailDto> addBankDetail(
            @Valid @RequestBody FranchiseeBankDetailDto bankDetailDto,
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Adding bank detail for franchisee: {}", currentUser.getId());
        return ResponseEntity.ok(bankDetailService.addBankDetail(bankDetailDto, currentUser.getId()));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<List<FranchiseeBankDetailDto>> getBankDetails(
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Fetching all bank details for franchisee: {}", currentUser.getId());
        return ResponseEntity.ok(bankDetailService.getBankDetailsByUserId(currentUser.getId()));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<FranchiseeBankDetailDto> getBankDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Fetching bank detail {} for franchisee: {}", id, currentUser.getId());
        return ResponseEntity.ok(bankDetailService.getBankDetailById(id, currentUser.getId()));
    }
    
    @GetMapping("/primary")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<FranchiseeBankDetailDto> getPrimaryBankDetail(
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Fetching primary bank detail for franchisee: {}", currentUser.getId());
        return ResponseEntity.ok(bankDetailService.getPrimaryBankDetail(currentUser.getId()));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<FranchiseeBankDetailDto> updateBankDetail(
            @PathVariable Long id,
            @Valid @RequestBody FranchiseeBankDetailDto bankDetailDto,
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Updating bank detail {} for franchisee: {}", id, currentUser.getId());
        return ResponseEntity.ok(bankDetailService.updateBankDetail(id, bankDetailDto, currentUser.getId()));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<Map<String, String>> deleteBankDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Deleting bank detail {} for franchisee: {}", id, currentUser.getId());
        bankDetailService.deleteBankDetail(id, currentUser.getId());
        return ResponseEntity.ok(Map.of("message", "Bank detail successfully deleted"));
    }
    
    @PutMapping("/{id}/set-primary")
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<FranchiseeBankDetailDto> setPrimaryBankDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Setting bank detail {} as primary for franchisee: {}", id, currentUser.getId());
        return ResponseEntity.ok(bankDetailService.setPrimaryBankDetail(id, currentUser.getId()));
    }
    
    @PutMapping("/{id}/verify")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FranchiseeBankDetailDto> verifyBankDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Admin {} verifying bank detail: {}", currentUser.getId(), id);
        return ResponseEntity.ok(bankDetailService.verifyBankDetail(id, currentUser.getId()));
    }
}
