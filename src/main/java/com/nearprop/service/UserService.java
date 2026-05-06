//package com.nearprop.service;
//
//import com.nearprop.dto.UserDetailDto;
//import com.nearprop.dto.UserDto;
//import com.nearprop.entity.Role;
//import com.nearprop.entity.User;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface UserService {
//
//    /**
//     * Find a user by username
//     * @param username The username
//     * @return The user if found
//     */
//    Optional<User> findByUsername(String username);
//
//    /**
//     * Get current user
//     * @return The current user
//     */
//    User getCurrentUser();
//
//    /**
//     * Get user details by ID
//     * @param userId The user ID
//     * @return The user details
//     */
//    UserDto getUserDetails(Long userId);
//
//    /**
//     * Get all users
//     * @return List of all users
//     */
//    List<UserDto> getAllUsers();
//    List<UserDetailDto> getPublicDevelopers();
//
//    List<UserDetailDto> getPublicAdvisors();
//    List<UserDto> getUsersByRole(Role role);
//
//}

package com.nearprop.service;

import com.nearprop.dto.UserDetailDto;
import com.nearprop.dto.UserDto;
import com.nearprop.entity.Role;
import com.nearprop.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    /**
     * Find a user by username
     * @param username The username
     * @return The user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Get current user
     * @return The current user
     */
    User getCurrentUser();

    /**
     * Get user details by ID
     * @param userId The user ID
     * @return The user details
     */
    UserDto getUserDetails(Long userId);

    /**
     * Get all users
     * @return List of all users
     */
    List<UserDto> getAllUsers();

    /**
     * Get public developers with active PROFILE subscription
     * @return List of developers
     */
    List<UserDetailDto> getPublicDevelopers();

    /**
     * Get public advisors with active PROFILE subscription
     * @return List of advisors
     */
    List<UserDetailDto> getPublicAdvisors();

    /**
     * Get users by role (all users, not filtered by subscription)
     * @param role The role
     * @return List of users
     */
    List<UserDto> getUsersByRole(Role role);
}