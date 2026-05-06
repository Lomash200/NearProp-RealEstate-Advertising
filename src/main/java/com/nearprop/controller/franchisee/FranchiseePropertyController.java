package com.nearprop.controller.franchisee;

import com.nearprop.dto.FranchiseePropertyFormRequest;
import com.nearprop.dto.FranchiseePropertyRequest;
import com.nearprop.dto.PropertyDto;
import com.nearprop.dto.PropertyFormDto;
import com.nearprop.entity.User;
import com.nearprop.service.franchisee.FranchiseePropertyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller for franchisee property operations
 */
@RestController
@RequestMapping("/franchisee/properties")
@PreAuthorize("hasRole('FRANCHISEE')")
@RequiredArgsConstructor
public class FranchiseePropertyController {

    private final FranchiseePropertyService franchiseePropertyService;

    /**
     * Create a property on behalf of another user
     * @param request The property request with owner's ID
     * @param currentUser The current franchisee user
     * @return The created property
     */
    @PostMapping
    public ResponseEntity<PropertyDto> createPropertyOnBehalf(
            @Valid @RequestBody FranchiseePropertyRequest request,
            @AuthenticationPrincipal User currentUser) {
        PropertyDto property = franchiseePropertyService.createPropertyOnBehalf(
                request.getOwnerPermanentId(),
                request.getPropertyDetails(),
                currentUser.getId()
        );
        return new ResponseEntity<>(property, HttpStatus.CREATED);
    }

    /**
     * Create a property using form data on behalf of another user
     * @param ownerPermanentId The permanent ID of the owner
     * @param propertyFormDto The property form data
     * @param images The property images
     * @param videoFile The property video
     * @param currentUser The current franchisee user
     * @return The created property
     */
    @PostMapping(value = "/form", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PropertyDto> createPropertyFormOnBehalf(
            @RequestParam("ownerPermanentId") String ownerPermanentId,
            @Valid @ModelAttribute PropertyFormDto propertyFormDto,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "video", required = false) MultipartFile videoFile,
            @AuthenticationPrincipal User currentUser) {
        PropertyDto property = franchiseePropertyService.createPropertyFormOnBehalf(
                ownerPermanentId,
                propertyFormDto,
                images,
                videoFile,
                currentUser.getId()
        );
        return new ResponseEntity<>(property, HttpStatus.CREATED);
    }

    /**
     * Validate a user's permanent ID
     * @param permanentId The permanent ID to validate
     * @return True if valid, false otherwise
     */
    @GetMapping("/validate-id/{permanentId}")
    public ResponseEntity<Boolean> validateUserId(@PathVariable String permanentId) {
        boolean isValid = franchiseePropertyService.validateUserId(permanentId);
        return ResponseEntity.ok(isValid);
    }
} 