package com.nearprop.controller;

import com.nearprop.dto.*;
import com.nearprop.dto.admin.CreateSubscriptionPlanDto;
import com.nearprop.dto.franchisee.*;
import com.nearprop.advertisement.dto.AdvertisementDto;
import com.nearprop.advertisement.dto.CreateAdvertisementDto;
import com.nearprop.advertisement.service.AdvertisementAnalyticsService;
import com.nearprop.advertisement.service.AdvertisementService;
import com.nearprop.entity.*;
import com.nearprop.enums.PermissionUser;
import com.nearprop.entity.FranchiseRequest.RequestStatus;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.UserRepository;
import com.nearprop.repository.franchisee.FranchiseRequestRepository;
import com.nearprop.service.*;
import com.nearprop.service.admin.SubscriptionPlanAdminService;
import com.nearprop.service.analytics.AnalyticsService;
import com.nearprop.service.franchisee.FranchiseRequestService;
import com.nearprop.service.franchisee.FranchiseeDistrictService;
import com.nearprop.service.franchisee.WithdrawalRequestService;
import com.twilio.http.Response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/permissions")
@RequiredArgsConstructor
@Slf4j
public class PermissionController {

    private final PropertyUpdateRequestService updateRequestService;
    private final S3Service s3Service;
    private final WithdrawalRequestService withdrawalRequestService;
    private final SubAdminPermissionService permissionService;
    private final FranchiseRequestRepository franchiseRequestRepository;
    private final PropertyService propertyService;
    private final AdvertisementService advertisementService;
    private final AdvertisementAnalyticsService analyticsService;
    private final UserManagementService userManagementService;
    private final FranchiseRequestService franchiseRequestService;
    private final FranchiseeDistrictService franchiseeDistrictService;
    private final UserRepository userRepository;
    private final SubscriptionPlanAdminService planAdminService;


    // ADMIN -------------

    // Assign permissions to SubAdmin
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> assignPermissions(@RequestBody AssignPermissionDto dto) {
        try {
            permissionService.assignPermissions(dto);
            return ResponseEntity.ok(ApiResponse.success("Permissions assigned successfully"));
        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            if (errorMessage.contains("SubAdmin not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("SubAdmin not found with ID: " + dto.getSubAdminId()));
            } else if (errorMessage.contains("Permissions already assigned")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ApiResponse.error(errorMessage));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An unexpected error occurred: " + errorMessage));
        }
    }

    // Get permissions for SubAdmin
    @GetMapping("/{subAdminId}")  //
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SubAdminPermission>>> getPermissions(@PathVariable Long subAdminId) {
        List<SubAdminPermission> permissions = permissionService.getPermissions(subAdminId);
        return ResponseEntity.ok(ApiResponse.success("Permissions retrieved successfully", permissions));
    }

    // Update permissions of SubAdmin
    @PutMapping("/update")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> updatePermissions(@Valid @RequestBody AssignPermissionDto dto) {
        try {
            permissionService.updatePermissions(dto);
            return ResponseEntity.ok(ApiResponse.success("Permissions updated successfully"));
        } catch (RuntimeException e) {
            if (e.getMessage().equals("SubAdmin not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("SubAdmin not found with ID: " + dto.getSubAdminId()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong: " + e.getMessage()));
        }
    }


    // get All subAdmin By PermissionUser
    @GetMapping("/subadmin/by-module")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<SubAdminWithActionsDto>>> getSubAdminsByModule(
            @RequestParam PermissionUser module) {
        List<SubAdminWithActionsDto> subAdmins = permissionService.getSubAdminsWithActionsByModule(module);
        return ResponseEntity.ok(ApiResponse.success(
                "Subadmins retrieved successfully for module: " + module, subAdmins));
    }

    // Get all sub-admins with their permissions
    @GetMapping("/subadmins")
    @PreAuthorize("hasAnyRole('ADMIN','SUBADMIN')")


    public ResponseEntity<ApiResponse<List<SubAdminDetailWithPermissionsDto>>> getAllSubAdmins() {
        log.info("Admin requested all SubAdmins at {}", java.time.LocalDateTime.now());
        List<SubAdminDetailWithPermissionsDto> subAdmins = permissionService.getAllSubAdminsWithPermissions();
        if (subAdmins == null || subAdmins.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("No SubAdmins found", subAdmins));
        }
        return ResponseEntity.ok(ApiResponse.success("All SubAdmins retrieved successfully", subAdmins));
    }


    // get subAdmin By Id
    @GetMapping("/subadmin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubAdminDetailWithPermissionsDto>> getSubAdminById(@PathVariable Long id) {
        log.info("Fetching SubAdmin details for ID: {}", id);

        try {
            SubAdminDetailWithPermissionsDto subAdmin = permissionService.getSubAdminDetailsWithPermissions(id);
            return ResponseEntity.ok(ApiResponse.success("SubAdmin retrieved successfully", subAdmin));
        } catch (Exception e) {
            log.error("Error fetching SubAdmin by ID: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to fetch SubAdmin: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/delete-subadmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSubAdmin(@PathVariable Long id) {
        try {
            String message = permissionService.deleteSubAdminById(id);
            return ResponseEntity.ok().body(message);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(404).body(ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Something went wrong: " + e.getMessage());
        }
    }

    // Delete permissions for a specific module
    @DeleteMapping("/{subAdminId}/module/{module}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deletePermissionsByModule(
            @PathVariable Long subAdminId,
            @PathVariable PermissionUser module) {
        try {
            String message = permissionService.deletePermissionsByModule(subAdminId, module);
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("SubAdmin not found with ID: " + subAdminId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Something went wrong: " + e.getMessage()));
        }
    }


    // PROPERTY --------------


    // property Permission
    @GetMapping("/property/{propertyId}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).PROPERTY, T(com.nearprop.enums.Action).VIEW)")
    public ResponseEntity<ApiResponse> getPropertyBySubAdmin(@PathVariable Long propertyId) {
        PropertyDto property = propertyService.getProperty(propertyId);
        return ResponseEntity.ok(ApiResponse.success("Property retrieved successfully", property));
    }

//    @DeleteMapping("/property/{propertyId}")
//    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).PROPERTY, T(com.nearprop.enums.Action).DELETE)")
//    public ResponseEntity<ApiResponse> deletePropertyBySubAdmin(@PathVariable Long propertyId,
//                                                                @AuthenticationPrincipal User currentUser) {
//        //propertyService.deletePropertybySubAdmin(propertyId, currentUser.getId());
//        propertyService.adminDeleteProperty(propertyId);
//        return ResponseEntity.ok(ApiResponse.success("Property deleted successfully"));
//    }

    @DeleteMapping("/property/{id}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).PROPERTY, T(com.nearprop.enums.Action).DELETE)")
    public ResponseEntity<?> deleteProperty(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        propertyService.hardDelete(id, user.getId());
        return ResponseEntity.ok("Deleted");
    }

    @PostMapping(value = "/form1", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).PROPERTY, T(com.nearprop.enums.Action).CREATE)")
    public ResponseEntity<PropertyDto> createPropertyBySubAdmin(@Valid @ModelAttribute PropertyFormDto propertyFormDto,
                                                                @RequestParam(value = "images", required = false) List<MultipartFile> images,
                                                                @RequestParam(value = "video", required = false) MultipartFile videoFile,
                                                                @AuthenticationPrincipal User currentUser) {
        PropertyDto createdProperty = propertyService.createPropertyFromFormBySubAdmin(propertyFormDto, images, videoFile,
                currentUser.getId());
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

//    @PutMapping("property/{id}")
//    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).PROPERTY,T(com.nearprop.enums.Action).UPDATE")
//    public ResponseEntity<PropertyDto> updatePropertyBySubAdmin(@PathVariable("id") Long propertyId,
//                                                                @Valid @RequestBody CreatePropertyDto propertyDto, @AuthenticationPrincipal User currentUser) {
//        PropertyDto updatedProperty = propertyService.updateProperty(propertyId, propertyDto, currentUser.getId());
//        return ResponseEntity.ok(updatedProperty);
//    }

    @PutMapping(value = "property/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).PROPERTY, T(com.nearprop.enums.Action).UPDATE)")
    public ResponseEntity<PropertyDto> updatePropertyBySubAdmin(
            @PathVariable("id") Long propertyId,
            @Valid @ModelAttribute PropertyFormDto propertyFormDto,  // Changed to @ModelAttribute
            @RequestParam(value = "images", required = false) List<MultipartFile> images,  // Changed key name
            @RequestParam(value = "video", required = false) MultipartFile videoFile,
            @AuthenticationPrincipal User currentUser) {

        PropertyDto updatedProperty = propertyService.updatePropertyFromFormBySubAdmin(propertyId, propertyFormDto, images, videoFile, currentUser.getId());
        return ResponseEntity.ok(updatedProperty);
    }

    @GetMapping("/getAllProperty")
    @PreAuthorize("hasRole('SUBADMIN') and @subAdminPermissionService.hasPermissionForAnyAction(principal,T(com.nearprop.enums.PermissionUser).PROPERTY)")
    public ResponseEntity<ApiResponse<List<PropertyDto>>> getAllPropertiesBySubAdmin(
            @RequestParam(required = false) Double latitude, @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radius) {
        List<PropertyDto> properties = propertyService.getAllPropertiesWithoutPagination(latitude, longitude, radius);
        return ResponseEntity.ok(ApiResponse.success("Properties retrieved successfully", properties));
    }

    // Now property update api's request to reject or approve

    /**
     * Get all pending property update requests (admin only)
     */
    @GetMapping("/admin/pending")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).PROPERTY, T(com.nearprop.enums.Action).VIEW)")
    public ResponseEntity<Page<PropertyUpdateRequestDto>> getPendingUpdateRequests(Pageable pageable) {
        Page<PropertyUpdateRequestDto> requests = updateRequestService.getUpdateRequestsByStatus(
                PropertyUpdateRequest.RequestStatus.PENDING, pageable);

        return ResponseEntity.ok(requests);
    }

    /**
     * Review a property update request (admin only)
     */
    @PostMapping("/admin/review")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).PROPERTY, T(com.nearprop.enums.Action).UPDATE)")
    public ResponseEntity<PropertyUpdateRequestDto> reviewUpdateRequest(
            @RequestBody ReviewPropertyUpdateRequestDto reviewDto,
            @AuthenticationPrincipal User currentUser) {

        // Set reviewer type to ADMIN
        reviewDto.setReviewerType(ReviewPropertyUpdateRequestDto.ReviewerType.ADMIN);

        PropertyUpdateRequestDto reviewedRequest = updateRequestService.reviewUpdateRequest(
                reviewDto, currentUser.getId());

        return ResponseEntity.ok(reviewedRequest);
    }

    // Advertisement ---------------

    // get advertisement by Id
    @GetMapping("advertisement/{id}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).ADVERTISEMENT, T(com.nearprop.enums.Action).VIEW)")
    public ResponseEntity<AdvertisementDto> getAdvertisementBySubAdmin(@PathVariable Long id,
                                                                       HttpServletRequest request) {
        log.info("Fetching advertisement ID: {}", id);
        AdvertisementDto ad = advertisementService.getAdvertisement(id);

        // Record view
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referrer = request.getHeader("Referer");
        String userDistrict = request.getParameter("district"); // Get district from request param if available

        // Get user ID if authenticated
        User currentUser = (User) request.getAttribute("currentUser");
        Long userId = currentUser != null ? currentUser.getId() : null;

        // Record view asynchronously
        analyticsService.recordView(id, userId, ipAddress, userAgent, userDistrict, referrer);

        return ResponseEntity.ok(ad);
    }

    // delete advertisement by Id
    @DeleteMapping("/advertisement/{id}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).ADVERTISEMENT, T(com.nearprop.enums.Action).DELETE)")
    public ResponseEntity<ApiResponse> deleteAdvertisementBySubAdmin(@PathVariable Long id,
                                                                     @AuthenticationPrincipal User currentUser) {
        log.info("Deleting advertisement ID: {} by user ID: {}", id, currentUser.getId());
        advertisementService.deleteAdvertisementSubAdmin(id, currentUser.getId());
        log.info("Advertisement ID: {} deleted successfully", id);
        return ResponseEntity.ok(ApiResponse.success("Advertisement deleted successfully"));
    }

    // create advertisement
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).ADVERTISEMENT, T(com.nearprop.enums.Action).CREATE)")
    public ResponseEntity<AdvertisementDto> createAdvertisementBySubAdmin(
            @Valid @ModelAttribute CreateAdvertisementDto dto, @AuthenticationPrincipal User currentUser) {
        log.info("Creating advertisement request received from user ID: {}", currentUser.getId());
        AdvertisementDto createdAd = advertisementService.createAdvertisement(dto, currentUser.getId());
        log.info("Advertisement created with ID: {}", createdAd.getId());
        return new ResponseEntity<>(createdAd, HttpStatus.CREATED);
    }

    // update advertisement by Id
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).ADVERTISEMENT, T(com.nearprop.enums.Action).UPDATE)")
    public ResponseEntity<AdvertisementDto> updateAdvertisementBysubAdmin(@PathVariable Long id,
                                                                          @Valid @ModelAttribute CreateAdvertisementDto dto, @AuthenticationPrincipal User currentUser) {
        log.info("Updating advertisement ID: {} request received from user ID: {}", id, currentUser.getId());
        AdvertisementDto updatedAd = advertisementService.updateAdvertisement(id, dto, currentUser.getId());
        log.info("Advertisement updated with ID: {}", updatedAd.getId());
        return ResponseEntity.ok(updatedAd);
    }

    // get All Advertisement
    @GetMapping("/allAdvertisement")
    @PreAuthorize("hasRole('SUBADMIN') and @subAdminPermissionService.hasPermissionForAnyAction(principal, T(com.nearprop.enums.PermissionUser).ADVERTISEMENT)")
    public ResponseEntity<List<AdvertisementDto>> getAllAdvertisementsBySubAdmin(
            @AuthenticationPrincipal User currentUser) {
        log.info("Fetching all advertisements for SubAdmin user ID: {}", currentUser.getId());

        // Service layer call
        List<AdvertisementDto> advertisements = advertisementService.getAllAdvertisements();

        log.info("Total advertisements fetched: {}", advertisements.size());
        return ResponseEntity.ok(advertisements);
    }

    // Franchisee ---------------

    // get Franchisee By ID
    @GetMapping("/franchisee/{requestId}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).FRANCHISEE, T(com.nearprop.enums.Action).VIEW)")
    public ResponseEntity<?> getFranchiseeByIdBySubAdmin(@AuthenticationPrincipal User user,
                                                         @PathVariable Long requestId) {
        return ResponseEntity.ok(franchiseRequestService.getRequest(requestId));
    }

    // create Franchisee Request
    @PostMapping(value = "/form-franchisee", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).FRANCHISEE, T(com.nearprop.enums.Action).CREATE)")
    public ResponseEntity<FranchiseRequestDto> submitRequestBySubAdmin(@RequestParam("districtId") Long districtId,
                                                                       @RequestParam("businessName") String businessName, @RequestParam("businessAddress") String businessAddress,
                                                                       @RequestParam(value = "businessRegistrationNumber", required = false) String businessRegistrationNumber,
                                                                       @RequestParam(value = "gstNumber", required = false) String gstNumber,
                                                                       @RequestParam("panNumber") String panNumber, @RequestParam("aadharNumber") String aadharNumber,
                                                                       @RequestParam("contactEmail") String contactEmail, @RequestParam("contactPhone") String contactPhone,
                                                                       @RequestParam("yearsOfExperience") Integer yearsOfExperience,
                                                                       @RequestParam(value = "documents", required = false) List<MultipartFile> documents,
                                                                       @AuthenticationPrincipal User currentUser) {

        log.info("Received franchise request submission for district: {}, business: {}", districtId, businessName);
        if (documents != null) {
            log.info("Received {} documents", documents.size());
            for (MultipartFile doc : documents) {
                log.info("Document: {}, size: {}, content type: {}", doc.getOriginalFilename(), doc.getSize(),
                        doc.getContentType());
            }
        } else {
            log.info("No documents received");
        }

        // Create DTO from individual parts
        CreateFranchiseRequestDto requestDto = CreateFranchiseRequestDto.builder().districtId(districtId)
                .businessName(businessName).businessAddress(businessAddress)
                .businessRegistrationNumber(businessRegistrationNumber).gstNumber(gstNumber).panNumber(panNumber)
                .aadharNumber(aadharNumber).contactEmail(contactEmail).contactPhone(contactPhone)
                .yearsOfExperience(yearsOfExperience).build();

        return ResponseEntity.ok(franchiseRequestService.submitRequest(requestDto, documents, currentUser.getId()));
    }

    // delete Franchise By Id
    @DeleteMapping("franchisee/{franchiseeId}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).FRANCHISEE, T(com.nearprop.enums.Action).DELETE)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteFranchisee(@PathVariable Long franchiseeId,
                                                                             @RequestParam(required = true) String reason) {

        log.info("Admin request to delete franchisee with ID: {}, reason: {}", franchiseeId, reason);

        try {
            Map<String, Object> result = franchiseeDistrictService.deleteFranchisee(franchiseeId, reason);

            return ResponseEntity.ok(ApiResponse.success("Franchisee deleted successfully", result));
        } catch (Exception e) {
            log.error("Error deleting franchisee with ID: {}", franchiseeId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete franchisee: " + e.getMessage()));
        }
    }

    // update Franchisee By Id
    @PutMapping("franchisee/{id}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).FRANCHISEE, T(com.nearprop.enums.Action).UPDATE)")
    public ResponseEntity<FranchiseeDistrictDto> updateFranchiseeDistrict(@PathVariable("id") Long franchiseeDistrictId,
                                                                          @Valid @RequestBody Map<String, Object> updateFields) {
        try {
            log.info("Updating franchisee district with ID: {}, fields: {}", franchiseeDistrictId, updateFields);
            FranchiseeDistrictDto franchiseeDistrict = franchiseeDistrictService
                    .updateFranchiseeDistrictFields(franchiseeDistrictId, updateFields);
            return ResponseEntity.ok(franchiseeDistrict);
        } catch (ResourceNotFoundException e) {
            log.error("Failed to update franchisee district: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating franchisee district: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // approve request By ID
    @PutMapping("franchisee/{id}/approve")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).FRANCHISEE, T(com.nearprop.enums.Action).UPDATE)")
    public ResponseEntity<FranchiseRequestDto> approveRequestBySubAdmin(@PathVariable("id") Long requestId,
                                                                        @RequestParam(required = false) String comments, @RequestParam(required = false) String endDate,
                                                                        @AuthenticationPrincipal User currentUser) {

        return ResponseEntity.ok(
                franchiseRequestService.approveRequestBySubAdmin(requestId, comments, endDate, currentUser.getId()));
    }

    // cancel request By ID
    @PutMapping("franchisee/{id}/reject")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).FRANCHISEE, T(com.nearprop.enums.Action).UPDATE)")
    public ResponseEntity<FranchiseRequestDto> rejectRequestBySubAdmin(@PathVariable("id") Long requestId,
                                                                       @RequestParam String comments, @AuthenticationPrincipal User currentUser) {

        return ResponseEntity
                .ok(franchiseRequestService.rejectRequestBySubAdmin(requestId, comments, currentUser.getId()));
    }

    // get requst by Status
    @GetMapping("franchisee/status/{status}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).FRANCHISEE, T(com.nearprop.enums.Action).VIEW)")
    public ResponseEntity<Page<FranchiseRequestDto>> getRequestsByStatus(@PathVariable RequestStatus status,
                                                                         @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
                                                                         @RequestParam(defaultValue = "createdAt") String sortBy,
                                                                         @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, sortDirection, sortBy);

        return ResponseEntity.ok(franchiseRequestService.getRequestsByStatus(status, pageable));
    }

    //    @PreAuthorize("hasRole('SUBADMIN') and @subAdminPermissionService.hasPermissionForAnyAction(principal,T(com.nearprop.enums.PermissionUser).SUBsCRIPTION)")

    //   Subscription ---------------

    @PostMapping("subscription/create-subs")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).SUBSCRIPTION, T(com.nearprop.enums.Action).CREATE)")
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> createPlan(
            @Valid @RequestBody CreateSubscriptionPlanDto createDto) {
        log.info("Creating new subscription plan with name: {}", createDto.getName());
        SubscriptionPlanDto planDto = planAdminService.createSubscriptionPlan(createDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Subscription plan created successfully", planDto));
    }



    @GetMapping("subscription/get-all")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).SUBSCRIPTION, T(com.nearprop.enums.Action).VIEW)")
    public ResponseEntity<ApiResponse<Page<SubscriptionPlanDto>>> getAllPlans(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        log.info("Fetching all subscription plans");
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SubscriptionPlanDto> plans = planAdminService.getAllSubscriptionPlans(pageable);
        return ResponseEntity.ok(ApiResponse.success("Subscription plans retrieved successfully", plans));
    }



    @GetMapping("subscription/{id}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).SUBSCRIPTION, T(com.nearprop.enums.Action).VIEW)")
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> getPlanById(@PathVariable Long id) {
        log.info("Fetching subscription plan with ID: {}", id);
        SubscriptionPlanDto planDto = planAdminService.getSubscriptionPlanById(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan retrieved successfully", planDto));
    }

    @GetMapping("subscription/type/{type}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).SUBSCRIPTION, T(com.nearprop.enums.Action).VIEW)")
    public ResponseEntity<ApiResponse<List<SubscriptionPlanDto>>> getPlansByType(@PathVariable SubscriptionPlan.PlanType type) {
        log.info("Fetching subscription plans of type: {}", type);
        List<SubscriptionPlanDto> plans = planAdminService.getSubscriptionPlansByType(type);
        return ResponseEntity.ok(ApiResponse.success("Subscription plans retrieved successfully", plans));
    }

    @PutMapping("subscription/{id}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).SUBSCRIPTION, T(com.nearprop.enums.Action).UPDATE)")
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> updatePlan(
            @PathVariable Long id,
            @Valid @RequestBody CreateSubscriptionPlanDto updateDto) {
        log.info("Updating subscription plan with ID: {}", id);
        SubscriptionPlanDto planDto = planAdminService.updateSubscriptionPlan(id, updateDto);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan updated successfully", planDto));
    }

    @PutMapping("subscription/{id}/activate")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).SUBSCRIPTION, T(com.nearprop.enums.Action).UPDATE)")
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> activatePlan(@PathVariable Long id) {
        log.info("Activating subscription plan with ID: {}", id);
        SubscriptionPlanDto planDto = planAdminService.setSubscriptionPlanActiveStatus(id, true);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan activated successfully", planDto));
    }

    @PutMapping("subscription/{id}/deactivate")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).SUBSCRIPTION, T(com.nearprop.enums.Action).UPDATE)")
    public ResponseEntity<ApiResponse<SubscriptionPlanDto>> deactivatePlan(@PathVariable Long id) {
        log.info("Deactivating subscription plan with ID: {}", id);
        SubscriptionPlanDto planDto = planAdminService.setSubscriptionPlanActiveStatus(id, false);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan deactivated successfully", planDto));
    }

    @DeleteMapping("subscription/{id}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal,T(com.nearprop.enums.PermissionUser).SUBSCRIPTION, T(com.nearprop.enums.Action).DELETE)")
    public ResponseEntity<ApiResponse<Void>> deletePlan(@PathVariable Long id) {
        log.info("Deleting subscription plan with ID: {}", id);
        planAdminService.deleteSubscriptionPlan(id);
        return ResponseEntity.ok(ApiResponse.success("Subscription plan deleted successfully"));
    }


    // Withdraw Operation of SubAdmin-------------------------
    // get all Withdrawal
    @GetMapping("/admin/all")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).WITHDRAW, T(com.nearprop.enums.Action).VIEW)")
    public ResponseEntity<List<WithdrawalRequestDto>> getAllWithdrawalRequestsForAdmin() {
        List<WithdrawalRequestDto> requests = withdrawalRequestService.getAllWithdrawalRequests();
        return ResponseEntity.ok(requests);
    }

    // get all withdraw by status
    @GetMapping("/admin/status/{status}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).WITHDRAW, T(com.nearprop.enums.Action).VIEW)")
    public ResponseEntity<List<WithdrawalRequestDto>> getWithdrawalRequestsByStatus(
            @PathVariable FranchiseeWithdrawalRequest.WithdrawalStatus status) {
        List<WithdrawalRequestDto> response = withdrawalRequestService.getWithdrawalRequestsByStatus(status);
        return ResponseEntity.ok(response);
    }

    // send money Api

    @PutMapping("/admin/process/{id}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).WITHDRAW, T(com.nearprop.enums.Action).UPDATE)")
    public ResponseEntity<WithdrawalRequestDto> processWithdrawalRequest(
            @PathVariable("id") Long requestId,
            @RequestParam("status") String status,
            @RequestParam(value = "adminComments", required = false) String adminComments,
            @RequestParam(value = "paymentReference", required = false) String paymentReference,
            @RequestParam(value = "accountNumber", required = false) String accountNumber,
            @RequestParam(value = "ifscCode", required = false) String ifscCode,
            @RequestParam(value = "bankName", required = false) String bankName,
            @RequestParam(value = "mobileNumber", required = false) String mobileNumber,
            @RequestParam(value = "transactionType", required = false) String transactionType,
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @RequestParam(value = "paymentScreenshot", required = false) MultipartFile paymentScreenshot,
            @AuthenticationPrincipal User currentUser) {

        log.info("Admin processing withdrawal request {} with status {}", requestId, status);

        String screenshotUrl = null;
        if (paymentScreenshot != null && !paymentScreenshot.isEmpty()) {
            // Save screenshot to S3 with improved path structure
            screenshotUrl = s3Service.uploadWithdrawalScreenshot(requestId, paymentScreenshot);
            log.info("Screenshot uploaded for withdrawal request {}: {}", requestId, screenshotUrl);
        }

        WithdrawalRequestResponseDto responseDto = new WithdrawalRequestResponseDto();
        responseDto.setStatus(FranchiseeWithdrawalRequest.WithdrawalStatus.valueOf(status));
        responseDto.setAdminComments(adminComments);
        responseDto.setPaymentReference(paymentReference);
        responseDto.setAccountNumber(accountNumber);
        responseDto.setIfscCode(ifscCode);
        responseDto.setBankName(bankName);
        responseDto.setMobileNumber(mobileNumber);
        responseDto.setScreenshotUrl(screenshotUrl);
        responseDto.setTransactionType(transactionType);
        responseDto.setTransactionId(transactionId);

        WithdrawalRequestDto response = withdrawalRequestService.processWithdrawalRequest(
                requestId, responseDto, currentUser.getId());

        log.info("Withdrawal request {} processed with status {} by admin {}",
                requestId, status, currentUser.getId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/history/franchisee/{franchiseeId}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).WITHDRAW, T(com.nearprop.enums.Action).VIEW)")
    public ResponseEntity<WithdrawalHistoryDto> getWithdrawalHistoryForFranchisee(@PathVariable Long franchiseeId) {
        log.info("Admin getting withdrawal history for franchisee: {}", franchiseeId);
        WithdrawalHistoryDto history = withdrawalRequestService.getWithdrawalHistory(franchiseeId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/admin/history/district/{districtId}")
    @PreAuthorize("@subAdminPermissionService.hasPermission(principal, T(com.nearprop.enums.PermissionUser).WITHDRAW, T(com.nearprop.enums.Action).VIEW)")
    public ResponseEntity<WithdrawalHistoryDto> getWithdrawalHistoryForDistrictAdmin(@PathVariable Long districtId) {
        log.info("Admin getting withdrawal history for district: {}", districtId);
        WithdrawalHistoryDto history = withdrawalRequestService.getWithdrawalHistoryForDistrict(districtId);
        return ResponseEntity.ok(history);
    }


}

