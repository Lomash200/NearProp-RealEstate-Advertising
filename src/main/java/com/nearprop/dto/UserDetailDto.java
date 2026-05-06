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
public class UserDetailDto {
    private Long id;
    private String permanentId;
    private String name;
    private String email;
    private String mobileNumber;
    private String address;
    private String district;
   // private Long districtId;
    private String state;
    private String profileImageUrl;
    private Set<Role> roles;
    private boolean mobileVerified;
    private boolean emailVerified;
    private String aadhaarNumber;
    private boolean aadhaarVerified;
    private String aadhaarDocumentUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    
    // Statistics
    private Long propertyCount;
    private Long activePropertyCount;
    private Long pendingPropertyCount;
    private Long reelCount;
    private Long favoriteCount;
    private Long reviewCount;
    private Long subscriptionCount;
    private Long activeSubscriptionCount;
    private Long chatRoomCount;
    private Long advertisementCount;
    private Long roleRequestCount;
    private Long pendingRoleRequestCount;
} 