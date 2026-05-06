package com.nearprop.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nearprop.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TokenResponse {
    private String token;
    private String refreshToken;
    private String message;
    private Long userId;
    private String permanentId;
    private Set<Role> roles;
    private String name;
    private String email;
    private String mobileNumber;
    private String otp; // Only used for development/testing
} 