package com.nearprop.controller.franchisee;

import com.nearprop.dto.franchisee.FranchiseeProfileDto;
import com.nearprop.entity.User;
import com.nearprop.service.franchisee.FranchiseeProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/franchisee/profile")
@RequiredArgsConstructor
@Slf4j

public class FranchiseeProfileController {

    private final FranchiseeProfileService profileService;
    
    @GetMapping
    @PreAuthorize("hasRole('FRANCHISEE')")
    public ResponseEntity<FranchiseeProfileDto> getMyProfile(
            @AuthenticationPrincipal User currentUser) {
        
        log.info("Fetching complete profile for franchisee: {}", currentUser.getId());
        return ResponseEntity.ok(profileService.getCompleteProfile(currentUser.getId()));
    }
    
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FranchiseeProfileDto> getFranchiseeProfile(
            @PathVariable Long userId) {
        
        log.info("Admin fetching complete profile for franchisee: {}", userId);
        return ResponseEntity.ok(profileService.getCompleteProfile(userId));
    }
}
