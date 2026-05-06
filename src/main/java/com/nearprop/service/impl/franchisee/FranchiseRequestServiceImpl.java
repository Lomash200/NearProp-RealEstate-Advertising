package com.nearprop.service.impl.franchisee;

import com.nearprop.dto.UserSummaryDto;
import com.nearprop.dto.franchisee.CreateFranchiseRequestDto;
import com.nearprop.dto.franchisee.FranchiseRequestDto;
import com.nearprop.entity.FranchiseRequest;
import com.nearprop.entity.FranchiseRequest.RequestStatus;
import com.nearprop.entity.FranchiseeDistrict;
import com.nearprop.entity.Role;
import com.nearprop.entity.User;
import com.nearprop.enums.PermissionUser;
import com.nearprop.exception.BadRequestException;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.UserRepository;
import com.nearprop.repository.franchisee.FranchiseRequestRepository;
import com.nearprop.repository.franchisee.FranchiseeDistrictRepository;
import com.nearprop.service.S3Service;
import com.nearprop.service.SubAdminPermissionService;
import com.nearprop.service.franchisee.DistrictJsonService;
import com.nearprop.service.franchisee.FranchiseRequestService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import com.nearprop.config.AwsConfig;

@Service
@RequiredArgsConstructor
@Slf4j
public class FranchiseRequestServiceImpl implements FranchiseRequestService {

    private final FranchiseRequestRepository franchiseRequestRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final DistrictJsonService districtJsonService;
    private final FranchiseeDistrictRepository franchiseeDistrictRepository;
    private final AwsConfig awsConfig;
    private final SubAdminPermissionService subAdminPermissionService;


    private static final BigDecimal DEFAULT_REVENUE_SHARE_PERCENTAGE = new BigDecimal("50.00");
    private static final String DOCUMENT_PATH = "franchisee/documents/";
    
    @Override
    @Transactional
    public FranchiseRequestDto submitRequest(CreateFranchiseRequestDto requestDto, List<MultipartFile> documents, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Check if user already has a request for this district
       /* if (franchiseRequestRepository.findByUserIdAndDistrictId(userId, requestDto.getDistrictId()).isPresent()) {
            throw new BadRequestException("You have already submitted a request for this district");
        }*/

	// Check if user already has a request for this district
        if (franchiseeDistrictRepository.existsActiveFranchiseeForUserAndDistrict(userId, requestDto.getDistrictId())) {
            throw new BadRequestException("You have already submitted a request for this district");
        }
        
        // Check if district exists
        DistrictJsonService.DistrictData district = districtJsonService.getDistrictById(requestDto.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + requestDto.getDistrictId()));
        
        // Check if district is already assigned
       /* if (isDistrictAssigned(requestDto.getDistrictId())) {
            throw new BadRequestException("This district already has a franchisee assigned");
        }*/
        
        // Save request first to get requestId
        FranchiseRequest request = FranchiseRequest.builder()
                .user(user)
                .districtId(requestDto.getDistrictId())
                .districtName(district.getName())
                .state(district.getState())
                .businessName(requestDto.getBusinessName())
                .businessAddress(requestDto.getBusinessAddress())
                .businessRegistrationNumber(requestDto.getBusinessRegistrationNumber())
                .gstNumber(requestDto.getGstNumber())
                .panNumber(requestDto.getPanNumber())
                .aadharNumber(requestDto.getAadharNumber())
                .contactEmail(requestDto.getContactEmail())
                .contactPhone(requestDto.getContactPhone())
                .yearsOfExperience(requestDto.getYearsOfExperience())
                .status(RequestStatus.PENDING)
                .build();
        FranchiseRequest savedRequest = franchiseRequestRepository.save(request);
        // Now upload documents to S3
        List<String> documentUrls = new ArrayList<>();
        if (documents != null && !documents.isEmpty()) {
            for (MultipartFile document : documents) {
                try {
                    String sanitizedUserName = user.getName().replaceAll("[^a-zA-Z0-9]", "-").toLowerCase();
                    String s3Key = String.format("franchisee/documents/%d_%s/%d/%s",
                            user.getId(), sanitizedUserName, savedRequest.getId(), document.getOriginalFilename());
                    String s3Url = s3Service.uploadFile(document.getBytes(), s3Key, awsConfig.getS3().getBucket(), document.getContentType());
                    documentUrls.add(s3Url);
                } catch (IOException e) {
                    log.error("Error uploading document to S3", e);
                    throw new BadRequestException("Failed to upload document: " + document.getOriginalFilename());
                }
            }
        }
        savedRequest.setDocumentIds(String.join(",", documentUrls));
        FranchiseRequest updatedRequest = franchiseRequestRepository.save(savedRequest);
        return mapToDto(updatedRequest);
    }

    @Override
    public FranchiseRequestDto getRequest(Long requestId) {
        FranchiseRequest request = franchiseRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise request not found with ID: " + requestId));
        return mapToDto(request);
    }

    @Override
    public Page<FranchiseRequestDto> getRequestsByStatus(RequestStatus status, Pageable pageable) {
        return franchiseRequestRepository.findByStatus(status, pageable).map(this::mapToDto);
    }

    @Override
    public Page<FranchiseRequestDto> getUserRequests(Long userId, Pageable pageable) {
        return franchiseRequestRepository.findByUserId(userId, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional
    public FranchiseRequestDto approveRequest(Long requestId, String comments, String endDate, Long adminId) {
        FranchiseRequest request = franchiseRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise request not found with ID: " + requestId));
        
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + adminId));
        
        // Validate admin role
        if (!admin.getRoles().contains(Role.ADMIN)) {
            throw new BadRequestException("Only administrators can approve franchise requests");
        }
        
        // Check if request is pending
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be approved");
        }
        
        // Check if district is already assigned
        if (isDistrictAssigned(request.getDistrictId())) {
            throw new BadRequestException("This district already has a franchisee assigned");
        }
        
        // Approve the request
        request.setStatus(RequestStatus.APPROVED);
        request.setAdminComments(comments);
        request.setReviewedBy(adminId);
        request.setReviewedAt(LocalDateTime.now());
        
        FranchiseRequest savedRequest = franchiseRequestRepository.save(request);
        
        // Create franchisee district assignment
        User franchisee = request.getUser();
        
        // Add ROLE_FRANCHISEE to user if not present
        if (!franchisee.getRoles().contains(Role.FRANCHISEE)) {
            Set<Role> roles = new HashSet<>(franchisee.getRoles());
            roles.add(Role.FRANCHISEE);
            franchisee.setRoles(roles);
            userRepository.save(franchisee);
        }
        
        // Get district data
        DistrictJsonService.DistrictData district = districtJsonService.getDistrictById(request.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + request.getDistrictId()));
        
        // Parse endDate if provided, otherwise default to 1 year from now
        LocalDateTime endDateTime;
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                LocalDate parsedDate = LocalDate.parse(endDate);
                endDateTime = parsedDate.atTime(23, 59, 59); // Set to end of day
            } catch (Exception e) {
                throw new BadRequestException("Invalid endDate format. Use yyyy-MM-dd format (e.g., 2025-12-31)");
            }
        } else {
            endDateTime = LocalDateTime.now().plusYears(1);
        }
        
        FranchiseeDistrict franchiseeDistrict = new FranchiseeDistrict();
        franchiseeDistrict.setUser(franchisee);
        franchiseeDistrict.setDistrictId(district.getSerialNumber());
        franchiseeDistrict.setDistrictName(district.getName());
        franchiseeDistrict.setState(district.getState());
        franchiseeDistrict.setFranchiseRequest(request);
        franchiseeDistrict.setStartDate(LocalDateTime.now());
        franchiseeDistrict.setEndDate(endDateTime);
        franchiseeDistrict.setActive(true);
        franchiseeDistrict.setRevenueSharePercentage(DEFAULT_REVENUE_SHARE_PERCENTAGE);
        franchiseeDistrict.setTotalProperties(0);
        franchiseeDistrict.setTotalTransactions(0);
        franchiseeDistrict.setTotalRevenue(BigDecimal.ZERO);
        franchiseeDistrict.setTotalCommission(BigDecimal.ZERO);
        franchiseeDistrict.setStatus(FranchiseeDistrict.FranchiseeStatus.ACTIVE);
        franchiseeDistrict.setOfficeAddress(request.getBusinessAddress());
        franchiseeDistrict.setContactPhone(request.getContactPhone());
        franchiseeDistrict.setContactEmail(request.getContactEmail());
        
        franchiseeDistrictRepository.save(franchiseeDistrict);
        
        return mapToDto(savedRequest);
    }

    @Override
    @Transactional
    public FranchiseRequestDto approveRequestBySubAdmin(Long requestId, String comments, String endDate, Long subAdminId) {
        FranchiseRequest request = franchiseRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise request not found with ID: " + requestId));

        User subAdmin = userRepository.findById(subAdminId)
                .orElseThrow(() -> new ResourceNotFoundException("SubAdmin not found with ID: " + subAdminId));

        //  Validate SUBADMIN role & permission
        if (!(subAdmin.getRoles().contains(Role.SUBADMIN) &&
                subAdminPermissionService.hasPermissionForAnyAction(subAdmin, PermissionUser.FRANCHISEE))) {
            throw new BadRequestException("Only SubAdmins with FRANCHISEE permission can approve franchise requests");
        }

        //  Check if request is pending
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be approved");
        }

        //  Check if district is already assigned
        if (isDistrictAssigned(request.getDistrictId())) {
            throw new BadRequestException("This district already has a franchisee assigned");
        }

        //  Approve the request
        request.setStatus(RequestStatus.APPROVED);
        request.setAdminComments(comments);
        request.setReviewedBy(subAdminId);
        request.setReviewedAt(LocalDateTime.now());

        FranchiseRequest savedRequest = franchiseRequestRepository.save(request);

        //  Create franchisee district assignment
        User franchisee = request.getUser();

        // Add ROLE_FRANCHISEE if not already assigned
        if (!franchisee.getRoles().contains(Role.FRANCHISEE)) {
            Set<Role> roles = new HashSet<>(franchisee.getRoles());
            roles.add(Role.FRANCHISEE);
            franchisee.setRoles(roles);
            userRepository.save(franchisee);
        }

        //  Get district details
        DistrictJsonService.DistrictData district = districtJsonService.getDistrictById(request.getDistrictId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + request.getDistrictId()));

        //  Parse endDate or set default
        LocalDateTime endDateTime;
        if (endDate != null && !endDate.trim().isEmpty()) {
            try {
                LocalDate parsedDate = LocalDate.parse(endDate);
                endDateTime = parsedDate.atTime(23, 59, 59);
            } catch (Exception e) {
                throw new BadRequestException("Invalid endDate format. Use yyyy-MM-dd format (e.g., 2025-12-31)");
            }
        } else {
            endDateTime = LocalDateTime.now().plusYears(1);
        }

        FranchiseeDistrict franchiseeDistrict = new FranchiseeDistrict();
        franchiseeDistrict.setUser(franchisee);
        franchiseeDistrict.setDistrictId(district.getSerialNumber());
        franchiseeDistrict.setDistrictName(district.getName());
        franchiseeDistrict.setState(district.getState());
        franchiseeDistrict.setFranchiseRequest(request);
        franchiseeDistrict.setStartDate(LocalDateTime.now());
        franchiseeDistrict.setEndDate(endDateTime);
        franchiseeDistrict.setActive(true);
        franchiseeDistrict.setRevenueSharePercentage(DEFAULT_REVENUE_SHARE_PERCENTAGE);
        franchiseeDistrict.setTotalProperties(0);
        franchiseeDistrict.setTotalTransactions(0);
        franchiseeDistrict.setTotalRevenue(BigDecimal.ZERO);
        franchiseeDistrict.setTotalCommission(BigDecimal.ZERO);
        franchiseeDistrict.setStatus(FranchiseeDistrict.FranchiseeStatus.ACTIVE);
        franchiseeDistrict.setOfficeAddress(request.getBusinessAddress());
        franchiseeDistrict.setContactPhone(request.getContactPhone());
        franchiseeDistrict.setContactEmail(request.getContactEmail());

        franchiseeDistrictRepository.save(franchiseeDistrict);

        return mapToDto(savedRequest);
    }



    @Override
    @Transactional
    public FranchiseRequestDto rejectRequest(Long requestId, String comments, Long adminId) {
        FranchiseRequest request = franchiseRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise request not found with ID: " + requestId));
        
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found with ID: " + adminId));
        
        // Validate admin role
        if (!admin.getRoles().contains(Role.ADMIN)) {
            throw new BadRequestException("Only administrators can reject franchise requests");
        }
        
        // Check if request is pending
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be rejected");
        }
        
        // Reject the request
        request.setStatus(RequestStatus.REJECTED);
        request.setAdminComments(comments);
        request.setReviewedBy(adminId);
        request.setReviewedAt(LocalDateTime.now());
        
        FranchiseRequest savedRequest = franchiseRequestRepository.save(request);
        return mapToDto(savedRequest);
    }

    @Override
    @Transactional
    public FranchiseRequestDto rejectRequestBySubAdmin(Long requestId, String comments, Long subAdminId) {
        FranchiseRequest request = franchiseRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise request not found with ID: " + requestId));

        User subAdmin = userRepository.findById(subAdminId)
                .orElseThrow(() -> new ResourceNotFoundException("SubAdmin not found with ID: " + subAdminId));

        //  Allow only Admin OR SubAdmin with FRANCHISEE permission
        boolean isAdmin = subAdmin.getRoles().contains(Role.ADMIN);
        boolean hasFranchiseePermission = subAdminPermissionService.hasPermissionForAnyAction(subAdmin, PermissionUser.FRANCHISEE);

        if (!isAdmin && !hasFranchiseePermission) {
            throw new BadRequestException("You don't have permission to reject this request");
        }

        //  Check if request is pending
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be rejected");
        }

        //  Reject the request
        request.setStatus(RequestStatus.REJECTED);
        request.setAdminComments(comments);
        request.setReviewedBy(subAdminId);
        request.setReviewedAt(LocalDateTime.now());

        FranchiseRequest savedRequest = franchiseRequestRepository.save(request);
        return mapToDto(savedRequest);
    }



    @Override
    public boolean isDistrictAssigned(Long districtId) {
        return franchiseRequestRepository.existsApprovedRequestByDistrictId(districtId);
    }

    @Override
    public List<FranchiseRequestDto> getRequestsByDistrict(Long districtId) {
        return franchiseRequestRepository.findByDistrictId(districtId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Map<RequestStatus, Long> getRequestStatistics() {
        Map<RequestStatus, Long> stats = new HashMap<>();
        for (RequestStatus status : RequestStatus.values()) {
            stats.put(status, franchiseRequestRepository.countByStatus(status));
        }
        return stats;
    }

    @Override
    @Transactional
    public void cancelRequest(Long requestId, Long userId) {
        FranchiseRequest request = franchiseRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Franchise request not found with ID: " + requestId));
        
        // Check if request belongs to user
        if (!request.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only cancel your own franchise requests");
        }
        
        // Check if request is pending
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be cancelled");
        }
        
        // Delete the request
        franchiseRequestRepository.delete(request);
    }
    
    private FranchiseRequestDto mapToDto(FranchiseRequest request) {
        FranchiseRequestDto dto = FranchiseRequestDto.builder()
                .id(request.getId())
                .user(mapToUserSummaryDto(request.getUser()))
                .districtId(request.getDistrictId())
                .districtName(request.getDistrictName())
                .state(request.getState())
                .businessName(request.getBusinessName())
                .businessAddress(request.getBusinessAddress())
                .businessRegistrationNumber(request.getBusinessRegistrationNumber())
                .gstNumber(request.getGstNumber())
                .panNumber(request.getPanNumber())
                .aadharNumber(request.getAadharNumber())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .yearsOfExperience(request.getYearsOfExperience())
                .status(request.getStatus())
                .adminComments(request.getAdminComments())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .reviewedAt(request.getReviewedAt())
                .build();
        
        // Set document URLs
        if (request.getDocumentIds() != null && !request.getDocumentIds().isEmpty()) {
            List<String> documentUrls = Arrays.stream(request.getDocumentIds().split(","))
                    .collect(Collectors.toList());
            dto.setDocumentUrls(documentUrls);
        }
        
        // Set reviewer if present
        if (request.getReviewedBy() != null) {
            userRepository.findById(request.getReviewedBy()).ifPresent(reviewer -> {
                dto.setReviewedBy(mapToUserSummaryDto(reviewer));
            });
        }
        
        return dto;
    }
    
    private String getFileUrl(String documentId) {
        // This is a placeholder - implement based on your FileStorageService implementation
        return "https://storage-url/" + documentId;
    }
    
    private UserSummaryDto mapToUserSummaryDto(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getMobileNumber())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }
} 
