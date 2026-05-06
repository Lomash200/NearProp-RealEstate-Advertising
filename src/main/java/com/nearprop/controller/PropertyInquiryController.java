package com.nearprop.controller;

import com.nearprop.dto.PropertyInquiryDto;
import com.nearprop.service.PropertyInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
public class PropertyInquiryController {
    private final PropertyInquiryService propertyInquiryService;

    @PostMapping
    public ResponseEntity<PropertyInquiryDto> createInquiry(@RequestBody PropertyInquiryDto dto) {
        return ResponseEntity.ok(propertyInquiryService.createInquiry(dto));
    }

    @GetMapping
    public ResponseEntity<List<PropertyInquiryDto>> getInquiries(Authentication authentication, @RequestParam(required = false) Long districtId) {
        // Example: check role from authentication (pseudo-code, adapt to your security setup)
        boolean isAdmin = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return ResponseEntity.ok(propertyInquiryService.getAllInquiriesForAdmin());
        } else {
            // Franchise: must provide districtId
            if (districtId == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(propertyInquiryService.getAllInquiriesForFranchise(districtId));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<PropertyInquiryDto> updateInquiryStatus(@PathVariable Long id, @RequestBody PropertyInquiryDto.StatusHistoryDto statusDto, Authentication authentication) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.ok(propertyInquiryService.updateInquiryStatus(id, statusDto, userId));
    }

    private Long extractUserId(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof com.nearprop.entity.User) {
            return ((com.nearprop.entity.User) principal).getId();
        } else if (principal instanceof com.nearprop.security.UserPrincipal) {
            return ((com.nearprop.security.UserPrincipal) principal).getId();
        } else if (principal instanceof String) {
            try {
                return Long.valueOf((String) principal);
            } catch (NumberFormatException e) {
                // fallback to authentication.getName()
            }
        }
        // fallback: try authentication.getName()
        try {
            return Long.valueOf(authentication.getName());
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to extract userId from authentication principal: " + principal);
        }
    }
} 