package com.nearprop.service;

import com.nearprop.dto.role.ProcessRoleRequestDto;
import com.nearprop.dto.role.RoleRequestDto;
import com.nearprop.entity.Role;
import com.nearprop.entity.RoleRequest;
import com.nearprop.entity.User;
import com.nearprop.exception.AuthException;
import com.nearprop.repository.RoleRequestRepository;
import com.nearprop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final UserRepository userRepository;
    private final RoleRequestRepository roleRequestRepository;
    private final RoleEmailService roleEmailService;

    @Transactional
    public void addRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.getRoles().add(role);
        userRepository.save(user);
        log.info("Added role {} to user {}", role, userId);
    }

    @Transactional
    public void removeRole(Long userId, Role role) {
        if (role == Role.USER) {
            throw new IllegalArgumentException("Cannot remove USER role");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.getRoles().remove(role);
        userRepository.save(user);
        log.info("Removed role {} from user {}", role, userId);
    }

    public boolean hasRole(Long userId, Role role) {
        return userRepository.findById(userId)
                .map(user -> user.getRoles().contains(role))
                .orElse(false);
    }

    @Transactional
    public RoleRequest requestRole(Long userId, RoleRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (user.getRoles().contains(requestDto.getRole())) {
            throw new IllegalArgumentException("User already has this role");
        }
        
        if (roleRequestRepository.findPendingRequest(userId, requestDto.getRole()).isPresent()) {
            throw new IllegalArgumentException("A pending request for this role already exists");
        }
        
        // Create the role request with APPROVED status directly
        RoleRequest roleRequest = RoleRequest.builder()
                .user(user)
                .requestedRole(requestDto.getRole())
                .reason(requestDto.getReason())
                .documentUrls(requestDto.getDocumentUrls())
                .status(RoleRequest.Status.APPROVED)
                .createdAt(LocalDateTime.now())
                .processedAt(LocalDateTime.now()) // Set processed time to now
                .adminComment("Auto-approved by system")
                .build();
        
        // Add the role to the user directly
        user.getRoles().add(requestDto.getRole());
        userRepository.save(user);
        
        RoleRequest savedRequest = roleRequestRepository.save(roleRequest);
        log.info("Auto-approved role request for user {} to become {}", 
                userId, requestDto.getRole());
        
        // Send approval notification email
        try {
            roleEmailService.sendRoleRequestApprovedNotification(savedRequest);
            log.info("Role request approval notification email sent for request ID: {}", savedRequest.getId());
        } catch (Exception e) {
            log.error("Error sending role request approval notification: {}", e.getMessage(), e);
        }
        
        return savedRequest;
    }

    @Transactional
    public RoleRequest processRoleRequest(Long requestId, ProcessRoleRequestDto processDto) {
        RoleRequest request = roleRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Role request not found"));
        
        if (request.getStatus() != RoleRequest.Status.PENDING) {
            throw new IllegalArgumentException("Request has already been processed");
        }
        
        request.setStatus(processDto.getApproved() ? RoleRequest.Status.APPROVED : RoleRequest.Status.REJECTED);
        request.setAdminComment(processDto.getComment());
        request.setProcessedAt(LocalDateTime.now());
        request.setProcessedBy(processDto.getProcessedBy());
        
        if (processDto.getApproved()) {
            User user = request.getUser();
            user.getRoles().add(request.getRequestedRole());
            userRepository.save(user);
            log.info("Approved role request {} for user {} to become {}", 
                    requestId, request.getUser().getId(), request.getRequestedRole());
        } else {
            log.info("Rejected role request {} for user {} to become {}", 
                    requestId, request.getUser().getId(), request.getRequestedRole());
        }
        
        RoleRequest savedRequest = roleRequestRepository.save(request);
        
        // Send email notification based on approval status
        try {
            if (processDto.getApproved()) {
                roleEmailService.sendRoleRequestApprovedNotification(savedRequest);
                log.info("Role request approval notification email sent");
            } else {
                roleEmailService.sendRoleRequestRejectedNotification(savedRequest);
                log.info("Role request rejection notification email sent");
            }
        } catch (Exception e) {
            log.error("Error sending role request processed notification: {}", e.getMessage(), e);
        }
        
        return savedRequest;
    }

    public Page<RoleRequest> getPendingRequests(Pageable pageable) {
        return roleRequestRepository.findByStatus(RoleRequest.Status.PENDING, pageable);
    }

    public List<RoleRequest> getUserRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found"));
        return roleRequestRepository.findByUserId(userId);
    }
} 