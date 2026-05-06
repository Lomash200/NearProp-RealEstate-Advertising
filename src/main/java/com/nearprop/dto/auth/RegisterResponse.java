package com.nearprop.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {
    private boolean success;
    private String message;
    private boolean mobileVerified;
    private boolean emailVerified;
    private String mobileOtp;
    private String emailOtp;
} 