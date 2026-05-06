package com.nearprop.dto.auth;

import com.nearprop.entity.OtpType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {
    @NotBlank(message = "Identifier (mobile/email) is required")
    private String identifier;

    @NotBlank(message = "OTP code is required")
    private String code;

    private OtpType type;

    private String deviceInfo;

    private String ipAddress;
    private String fcmToken;

} 