package com.nearprop.controller.franchisee;

import com.nearprop.dto.franchisee.CreateFranchiseRequestDto;
import com.nearprop.dto.franchisee.FranchiseRequestDto;
import com.nearprop.entity.User;
import com.nearprop.service.franchisee.FranchiseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/franchisee/request-form")
@RequiredArgsConstructor
public class FranchiseRequestFormController {

    private final FranchiseRequestService franchiseRequestService;
    
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FranchiseRequestDto> submitRequest(
            @RequestParam("districtId") Long districtId,
            @RequestParam("businessName") String businessName,
            @RequestParam("businessAddress") String businessAddress,
            @RequestParam(value = "businessRegistrationNumber", required = false) String businessRegistrationNumber,
            @RequestParam(value = "gstNumber", required = false) String gstNumber,
            @RequestParam("panNumber") String panNumber,
            @RequestParam("aadharNumber") String aadharNumber,
            @RequestParam("contactEmail") String contactEmail,
            @RequestParam("contactPhone") String contactPhone,
            @RequestParam("yearsOfExperience") Integer yearsOfExperience,
            @RequestParam(value = "documents", required = false) List<MultipartFile> documents,
            @AuthenticationPrincipal User currentUser) {
        
        // Create DTO from individual parts
        CreateFranchiseRequestDto requestDto = CreateFranchiseRequestDto.builder()
                .districtId(districtId)
                .businessName(businessName)
                .businessAddress(businessAddress)
                .businessRegistrationNumber(businessRegistrationNumber)
                .gstNumber(gstNumber)
                .panNumber(panNumber)
                .aadharNumber(aadharNumber)
                .contactEmail(contactEmail)
                .contactPhone(contactPhone)
                .yearsOfExperience(yearsOfExperience)
                .build();
        
        return ResponseEntity.ok(franchiseRequestService.submitRequest(requestDto, documents, currentUser.getId()));
    }
} 