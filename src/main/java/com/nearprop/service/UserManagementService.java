package com.nearprop.service;

import com.nearprop.dto.SubAdminDetailWithPermissionsDto;
import com.nearprop.dto.UserDetailDto;
import com.nearprop.entity.Role;

import java.util.List;

public interface UserManagementService {
    
    /**
     * Get all users with detailed statistics
     * @return List of all users with their statistics
     */
    List<UserDetailDto> getAllUsersWithDetails();
    
    /**
     * Get users by role with detailed statistics
     * @param role The role to filter by
     * @return List of users with the specified role and their statistics
     */
    List<UserDetailDto> getUsersByRoleWithDetails(Role role);
    
    /**
     * Get user details with statistics by user ID
     * @param userId The user ID
     * @return User details with statistics
     */
    UserDetailDto getUserDetailsWithStatistics(Long userId);

    UserDetailDto getUserDetailById(Long id);

    List<UserDetailDto> getUsersByRoleHaveSubscription(String advisior);

    List<UserDetailDto> getUsersByRoleHaveDeveloperSubscription(String developer);
}