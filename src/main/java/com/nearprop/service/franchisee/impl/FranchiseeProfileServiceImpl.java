package com.nearprop.service.franchisee.impl;

import com.nearprop.dto.UserDto;
import com.nearprop.dto.franchisee.DistrictDto;
import com.nearprop.dto.franchisee.FranchiseeDistrictDto;
import com.nearprop.dto.franchisee.FranchiseeDistrictRevenueDto;
import com.nearprop.dto.franchisee.FranchiseeProfileDto;
import com.nearprop.dto.franchisee.FranchiseeBankDetailDto;
import com.nearprop.entity.FranchiseRequest;
import com.nearprop.entity.FranchiseeDistrict;
import com.nearprop.entity.DistrictRevenue;
import com.nearprop.entity.DistrictRevenue.PaymentStatus;
import com.nearprop.entity.DistrictRevenue.RevenueType;
import com.nearprop.entity.User;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.franchisee.FranchiseRequestRepository;
import com.nearprop.repository.franchisee.FranchiseeDistrictRepository;
import com.nearprop.repository.franchisee.DistrictRevenueRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.DocumentService;
import com.nearprop.service.franchisee.DistrictService;
import com.nearprop.service.franchisee.FranchiseeBankDetailService;
import com.nearprop.service.franchisee.FranchiseeDistrictService;
import com.nearprop.service.franchisee.FranchiseeProfileService;
import com.nearprop.service.franchisee.WithdrawalRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import com.nearprop.dto.FranchiseeBankDetailDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FranchiseeProfileServiceImpl implements FranchiseeProfileService {

    private final UserRepository userRepository;
    private final FranchiseeDistrictRepository franchiseeDistrictRepository;
    private final FranchiseRequestRepository franchiseRequestRepository;
    private final FranchiseeBankDetailService bankDetailService;
    private final DistrictService districtService;
    private final DocumentService documentService;
    private final DistrictRevenueRepository districtRevenueRepository;
    private final WithdrawalRequestService withdrawalRequestService;
    
    private UserDto mapUserToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
    
    private String getFirstName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] names = fullName.split("\\s+");
        return names[0];
    }
    
    private String getLastName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "";
        }
        String[] names = fullName.split("\\s+");
        if (names.length > 1) {
            StringBuilder lastName = new StringBuilder();
            for (int i = 1; i < names.length; i++) {
                if (i > 1) {
                    lastName.append(" ");
                }
                lastName.append(names[i]);
            }
            return lastName.toString();
        }
        return "";
    }
    
    private String getPhone(User user) {
        return user.getMobileNumber();
    }
    
    private String getProfilePictureUrl(User user) {
        return user.getProfileImageUrl();
    }

    /*@Override
    @Transactional(readOnly = true)
    public FranchiseeProfileDto getCompleteProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        FranchiseRequest request = franchiseRequestRepository.findApprovedByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No approved franchise request found for user: " + userId));
        
        List<FranchiseeDistrict> districts = franchiseeDistrictRepository.findByUserId(userId);
        
        // Calculate overall totals
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal withdrawnAmount = BigDecimal.ZERO;
        int totalTransactions = 0;
        int completedTransactions = 0;
        
        List<FranchiseeDistrictDto> districtDtos = new ArrayList<>();
        List<FranchiseeDistrictRevenueDto> districtRevenues = new ArrayList<>();
        
        for (FranchiseeDistrict district : districts) {
            // Get revenue details for this district
            List<DistrictRevenue> allRevenues = districtRevenueRepository.findByFranchiseeDistrictId(district.getId());
    
            List<DistrictRevenue> paidRevenues = districtRevenueRepository.findByFranchiseeDistrictAndPaymentStatus(
                district, PaymentStatus.PAID);
            List<DistrictRevenue> withdrawals = districtRevenueRepository.findByFranchiseeDistrictAndRevenueType(
                district, RevenueType.WITHDRAWAL);
            
            // Calculate district totals
                
            BigDecimal districtWithdrawnAmount = withdrawals.stream()
                .map(DistrictRevenue::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .abs(); // Make positive since withdrawals are stored as negative
            
            // Update overall totals
            totalRevenue = totalRevenue.add(district.getTotalRevenue() != null ? district.getTotalRevenue() : BigDecimal.ZERO);
            totalCommission = totalCommission.add(district.getTotalCommission() != null ? district.getTotalCommission() : BigDecimal.ZERO);
            withdrawnAmount = withdrawnAmount.add(districtWithdrawnAmount);
            totalTransactions += allRevenues.size();
            completedTransactions += paidRevenues.size();
            
            // Create district revenue DTO
            FranchiseeDistrictRevenueDto revenueDto = FranchiseeDistrictRevenueDto.builder()
                .districtId(district.getDistrictId())
                .districtName(district.getDistrictName())
                .state(district.getState())
                .totalRevenue(district.getTotalRevenue())
                .totalCommission(district.getTotalCommission())
                .withdrawnAmount(districtWithdrawnAmount)
                .totalTransactions(allRevenues.size())
                .completedTransactions(paidRevenues.size())
                .totalSubscribers(districtRevenueRepository.countSubscribersByFranchiseeDistrict(district.getId()).intValue())
                .activeSubscribers((int) paidRevenues.stream()
                    .filter(r -> r.getRevenueType() == RevenueType.SUBSCRIPTION_PAYMENT)
                    .count())
                .startDate(district.getStartDate())
                .lastTransactionDate(allRevenues.isEmpty() ? null : 
                    allRevenues.stream()
                        .map(DistrictRevenue::getTransactionDate)
                        .max(LocalDateTime::compareTo)
                        .orElse(null))
                .build();
            
            districtRevenues.add(revenueDto);
            
            // Create basic district DTO
            FranchiseeDistrictDto districtDto = FranchiseeDistrictDto.builder()
                .id(district.getId())
                .districtId(district.getDistrictId())
                .districtName(district.getDistrictName())
                .state(district.getState())
                .totalRevenue(district.getTotalRevenue())
                .totalCommission(district.getTotalCommission())
                .totalTransactions(district.getTotalTransactions())
                .startDate(district.getStartDate())
                .endDate(district.getEndDate())
                .build();
            
            districtDtos.add(districtDto);
        }
        
        // Build and return the complete profile
        return FranchiseeProfileDto.builder()
                .id(userId)
                .userDetails(mapUserToDto(user))
                .businessName(request.getBusinessName())
                .businessAddress(request.getBusinessAddress())
                .businessRegistrationNumber(request.getBusinessRegistrationNumber())
                .gstNumber(request.getGstNumber())
                .panNumber(request.getPanNumber())
                .aadharNumber(request.getAadharNumber())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .yearsOfExperience(request.getYearsOfExperience())
                .bankDetails(bankDetailService.getPrimaryBankDetail(userId))
                .assignedDistricts(districtDtos)
                .totalRevenue(totalRevenue)
                .totalCommission(totalCommission)
                .withdrawnAmount(withdrawnAmount)
                .availableBalance(withdrawalRequestService.getTotalAvailableBalanceForFranchisee(userId))
                .totalProperties(0) // You may want to implement property counting
                .totalTransactions(totalTransactions)
                .completedTransactions(completedTransactions)
                .districtRevenues(districtRevenues)
                .isVerified(true)
                .joinDate(request.getApprovedAt())
                .build();
    }*/
  
   @Override
    @Transactional(readOnly = true)
    public FranchiseeProfileDto getCompleteProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

       FranchiseRequest request =
               franchiseRequestRepository
                       .findFirstByUserIdAndStatusOrderByUpdatedAtDesc(
                               userId, FranchiseRequest.RequestStatus.APPROVED)
                       .orElseThrow(() -> new ResourceNotFoundException(
                               "No approved franchise request found for user: " + userId));


       List<FranchiseeDistrict> districts = franchiseeDistrictRepository.findByUserId(userId);

        // Calculate overall totals
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal withdrawnAmount = BigDecimal.ZERO;
        int totalTransactions = 0;
        int completedTransactions = 0;

        List<FranchiseeDistrictDto> districtDtos = new ArrayList<>();
        List<FranchiseeDistrictRevenueDto> districtRevenues = new ArrayList<>();

        for (FranchiseeDistrict district : districts) {
            // Get revenue details for this district
            List<DistrictRevenue> allRevenues = districtRevenueRepository.findByFranchiseeDistrictId(district.getId());

            List<DistrictRevenue> paidRevenues = districtRevenueRepository.findByFranchiseeDistrictAndPaymentStatus(
                    district, PaymentStatus.PAID);
            List<DistrictRevenue> withdrawals = districtRevenueRepository.findByFranchiseeDistrictAndRevenueType(
                    district, RevenueType.WITHDRAWAL);

            // Calculate district totals
            BigDecimal districtWithdrawnAmount = withdrawals.stream()
                    .map(DistrictRevenue::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .abs();

            // Update overall totals
            totalRevenue = totalRevenue.add(district.getTotalRevenue() != null ? district.getTotalRevenue() : BigDecimal.ZERO);
            totalCommission = totalCommission.add(district.getTotalCommission() != null ? district.getTotalCommission() : BigDecimal.ZERO);
            withdrawnAmount = withdrawnAmount.add(districtWithdrawnAmount);
            totalTransactions += allRevenues.size();
            completedTransactions += paidRevenues.size();

            // Create district revenue DTO
            FranchiseeDistrictRevenueDto revenueDto = FranchiseeDistrictRevenueDto.builder()
                    .districtId(district.getDistrictId())
                    .districtName(district.getDistrictName())
                    .state(district.getState())
                    .totalRevenue(district.getTotalRevenue())
                    .totalCommission(district.getTotalCommission())
                    .withdrawnAmount(districtWithdrawnAmount)
                    .totalTransactions(allRevenues.size())
                    .completedTransactions(paidRevenues.size())
                    .totalSubscribers(districtRevenueRepository.countSubscribersByFranchiseeDistrict(district.getId()).intValue())
                    .activeSubscribers((int) paidRevenues.stream()
                            .filter(r -> r.getRevenueType() == RevenueType.SUBSCRIPTION_PAYMENT)
                            .count())
                    .startDate(district.getStartDate())
                    .lastTransactionDate(allRevenues.isEmpty() ? null :
                            allRevenues.stream()
                                    .map(DistrictRevenue::getTransactionDate)
                                    .max(LocalDateTime::compareTo)
                                    .orElse(null))
                    .build();

            districtRevenues.add(revenueDto);

            // Create basic district DTO
            FranchiseeDistrictDto districtDto = FranchiseeDistrictDto.builder()
                    .id(district.getId())
                    .districtId(district.getDistrictId())
                    .districtName(district.getDistrictName())
                    .state(district.getState())
                    .totalRevenue(district.getTotalRevenue())
                    .totalCommission(district.getTotalCommission())
                    .totalTransactions(district.getTotalTransactions())
                    .startDate(district.getStartDate())
                    .endDate(district.getEndDate())
                    .build();

            districtDtos.add(districtDto);
        }

        //  Make bank details optional (return null if not found)
        FranchiseeBankDetailDto primaryBank = bankDetailService.getPrimaryBankDetail(userId); // This method now returns null if not found

        // Build and return the complete profile
        return FranchiseeProfileDto.builder()
                .id(userId)
                .userDetails(mapUserToDto(user))
                .businessName(request.getBusinessName())
                .businessAddress(request.getBusinessAddress())
                .businessRegistrationNumber(request.getBusinessRegistrationNumber())
                .gstNumber(request.getGstNumber())
                .panNumber(request.getPanNumber())
                .aadharNumber(request.getAadharNumber())
                .contactEmail(request.getContactEmail())
                .contactPhone(request.getContactPhone())
                .yearsOfExperience(request.getYearsOfExperience())
                .bankDetails(primaryBank)  // ✅ Optional now
                .assignedDistricts(districtDtos)
                .totalRevenue(totalRevenue)
                .totalCommission(totalCommission)
                .withdrawnAmount(withdrawnAmount)
                .availableBalance(withdrawalRequestService.getTotalAvailableBalanceForFranchisee(userId))
                .totalProperties(0) // You may want to implement property counting
                .totalTransactions(totalTransactions)
                .completedTransactions(completedTransactions)
                .districtRevenues(districtRevenues)
                .isVerified(true)
                .joinDate(request.getApprovedAt())
                .build();
    } 

}
