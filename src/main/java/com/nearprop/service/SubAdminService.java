package com.nearprop.service;

import java.util.List;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nearprop.advertisement.repository.AdvertisementRepository;
import com.nearprop.dto.CreateSubAdminDto;
import com.nearprop.dto.SubAdminAnalyticDto;
import com.nearprop.dto.admin.CreateAdminDto;
import com.nearprop.entity.Role;
import com.nearprop.entity.User;
import com.nearprop.exception.AuthException;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.repository.ReelRepository;
import com.nearprop.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubAdminService {

	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final PropertyRepository propertyRepository;
	private final UserRepository userRepository;
	private final ReelRepository reelRepository;
	private final AdvertisementRepository advertisementRepository;

	public SubAdminAnalyticDto getSubAdminAnalytics() {
		log.info("Fetching analytics data for sub-admin dashboard");
		long totalProperties = propertyRepository.count();
		long totalUser = userRepository.count();
		long totalReel = reelRepository.count();
		long totalAdvertisement = advertisementRepository.count();
		long totalSeller = userRepository.countSellerUsers();
		long totalAdvisior = userRepository.countAdvisorUsers();
		long totalSubAdmin = userRepository.countSubadminUsers();
		long totalFranchisee = userRepository.countFranchiseeUsers();
		long totalDeveloper = userRepository.countDeveloperUsers();
		long totalAdmin = userRepository.countAdminUsers();
		return SubAdminAnalyticDto.builder().totalProperties(totalProperties).totalUser(totalUser).totalReel(totalReel)
				.totalAdvertisement(totalAdvertisement).totalSeller(totalSeller).totalAdvisor(totalAdvisior)
				.totalSubAdmin(totalSubAdmin).totalFranchisee(totalFranchisee).totalDeveloper(totalDeveloper)
				.totalAdmin(totalAdmin).build();
	}

	@Transactional
	public User createSubAdmin(CreateSubAdminDto subAdminDto) {
		log.info("Creating new admin user with name: {} and mobile: {}", subAdminDto.getName(),
				subAdminDto.getMobileNumber());

		// Check if mobile number already exists
		if (userRepository.existsByMobileNumber(subAdminDto.getMobileNumber())) {
			log.warn("Mobile number already registered: {}", subAdminDto.getMobileNumber());
			throw new AuthException("Mobile number already registered");
		}

		// Check if email already exists
		if (userRepository.existsByEmail(subAdminDto.getEmail())) {
			log.warn("Email already registered: {}", subAdminDto.getEmail());
			throw new AuthException("Email already registered");
		}

		// Create the Subadmin user with both USER and ADMIN roles
		User SubadminUser = User.builder().name(subAdminDto.getName()).mobileNumber(subAdminDto.getMobileNumber())
				.email(subAdminDto.getEmail()).address(subAdminDto.getAddress()).district(subAdminDto.getDistrict())
				.roles(Set.of(Role.USER, Role.SUBADMIN))
				.mobileVerified(true) // Admin users are pre-verified
				.emailVerified(true) // Admin users are pre-verified
				.build();

		User savedAdmin = userRepository.save(SubadminUser);
		log.info("Admin user created successfully with ID: {}", savedAdmin.getId());

		// Send welcome email to the admin
		emailService.sendWelcomeEmail(subAdminDto.getEmail(), subAdminDto.getName());

		return savedAdmin;
	}

}
