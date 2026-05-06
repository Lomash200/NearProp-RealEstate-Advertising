package com.nearprop.dto;

import com.nearprop.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String permanentId;
    private String name;
    private String email;
    private String mobileNumber;
    private String username;
    private String phoneNumber;
    private String profileImageUrl;
    private Set<Role> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isFollowing;
    private Double latitude;
    private Double longitude;
    private Long districtId;
    private String district;
    private String state;
    
    public String getUsername() {
        return mobileNumber;
    }
    
    public String getPhoneNumber() {
        return mobileNumber;
    }
} 