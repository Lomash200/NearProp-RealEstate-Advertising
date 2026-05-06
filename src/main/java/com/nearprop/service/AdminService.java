package com.nearprop.service;

import com.nearprop.dto.admin.CreateAdminDto;
import com.nearprop.entity.Role;
import com.nearprop.entity.User;
import com.nearprop.exception.AuthException;
import com.nearprop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    /**
     * Creates a new admin user in the system
     * This is a secured operation that should only be performed by superadmins
     * 
     * @param adminDto the admin details
     * @return the created admin user
     */
    @Transactional
    public User createAdmin(CreateAdminDto adminDto) {
        log.info("Creating new admin user with name: {} and mobile: {}", adminDto.getName(), adminDto.getMobileNumber());
        
        // Check if mobile number already exists
        if (userRepository.existsByMobileNumber(adminDto.getMobileNumber())) {
            log.warn("Mobile number already registered: {}", adminDto.getMobileNumber());
            throw new AuthException("Mobile number already registered");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(adminDto.getEmail())) {
            log.warn("Email already registered: {}", adminDto.getEmail());
            throw new AuthException("Email already registered");
        }
        
        // Create the admin user with both USER and ADMIN roles
        User adminUser = User.builder()
                .name(adminDto.getName())
                .mobileNumber(adminDto.getMobileNumber())
                .email(adminDto.getEmail())
                .address(adminDto.getAddress())
                .district(adminDto.getDistrict())
                .password(passwordEncoder.encode(adminDto.getPassword()))
                .roles(Set.of(Role.USER, Role.ADMIN))
                .mobileVerified(true)  // Admin users are pre-verified
                .emailVerified(true)   // Admin users are pre-verified
                .build();
        
        User savedAdmin = userRepository.save(adminUser);
        log.info("Admin user created successfully with ID: {}", savedAdmin.getId());
        
        // Send welcome email to the admin
        emailService.sendWelcomeEmail(adminDto.getEmail(), adminDto.getName());
        
        return savedAdmin;
    }
} 