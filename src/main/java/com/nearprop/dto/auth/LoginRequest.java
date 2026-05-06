package com.nearprop.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "Mobile number is required")
    private String mobileNumber;

// Axhbdb
// Axhbdb
// Axhbdb
// Axhbdb

    private String deviceInfo;
    private String ipAddress;
} 