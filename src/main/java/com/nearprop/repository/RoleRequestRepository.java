package com.nearprop.repository;

import com.nearprop.entity.Role;
import com.nearprop.entity.RoleRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRequestRepository extends JpaRepository<RoleRequest, Long> {
    Page<RoleRequest> findByStatus(RoleRequest.Status status, Pageable pageable);
    
    List<RoleRequest> findByUserId(Long userId);
    
    List<RoleRequest> findByUserIdAndRequestedRole(Long userId, Role requestedRole);
    
    @Query("SELECT r FROM RoleRequest r WHERE r.user.id = :userId AND r.requestedRole = :role AND r.status = 'PENDING'")
    Optional<RoleRequest> findPendingRequest(Long userId, Role role);
    
    @Query("SELECT COUNT(r) > 0 FROM RoleRequest r WHERE r.user.id = :userId AND r.requestedRole = :role AND r.status = 'APPROVED'")
    boolean hasApprovedRequest(Long userId, Role role);
    
    @Query("SELECT r FROM RoleRequest r WHERE r.status = 'PENDING' ORDER BY r.createdAt ASC")
    List<RoleRequest> findPendingRequests();
    
    @Query("SELECT COUNT(r) FROM RoleRequest r WHERE r.user.id = :userId")
    Long countByUserId(Long userId);
    
    @Query("SELECT COUNT(r) FROM RoleRequest r WHERE r.user.id = :userId AND r.status = 'PENDING'")
    Long countByUserIdAndStatusPending(Long userId);
} 