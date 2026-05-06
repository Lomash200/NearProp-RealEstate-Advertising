package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.auth.LoginRequest;
import com.nearprop.dto.auth.RegisterRequest;
import com.nearprop.dto.auth.TokenResponse;
import com.nearprop.dto.auth.VerifyOtpRequest;
import com.nearprop.dto.role.RoleRequestDto;
import com.nearprop.dto.auth.RegisterResponse;
import com.nearprop.entity.OtpType;
import com.nearprop.entity.RoleRequest;
import com.nearprop.entity.User;
import com.nearprop.exception.AuthException;
import com.nearprop.service.AuthService;
import com.nearprop.service.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final RoleService roleService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received registration request for mobile: {}, email: {}", request.getMobileNumber(), request.getEmail());
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response.getMessage(), response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received login request for mobile: {}", request.getMobileNumber());
        TokenResponse response = authService.login(request);
        
        ApiResponse<TokenResponse> apiResponse = ApiResponse.success("OTP sent successfully", response);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<TokenResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Received OTP verification request for: {}, code: {}, type: {}", 
                request.getIdentifier(), request.getCode(), request.getType());
        
        try {
            TokenResponse response = authService.verifyOtp(request);
            log.info("OTP verification successful for: {}", request.getIdentifier());
            log.info("OTP verification dhdbjdbjddjb for: {}", response);

            ApiResponse<TokenResponse> apiResponse = ApiResponse.success("OTP verified successfully", response);
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            log.error("Error verifying OTP: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        log.info("Received email verification request for: {}", email);
        
        VerifyOtpRequest verifyRequest = VerifyOtpRequest.builder()
                .identifier(email)
                .code(otp)
                .type(OtpType.EMAIL)
                .build();
        
        authService.verifyOtp(verifyRequest);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully"));
    }
    
    @PostMapping("/request-role")
    public ResponseEntity<ApiResponse<RoleRequest>> requestRole(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody RoleRequestDto request) {
        log.info("Received role request for role {}", request.getRole());
        
        if (user == null) {
            log.error("Unauthorized access to request-role endpoint");
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Authentication required to request a role"));
        }
        
        log.info("Processing role request from user {} for role {}", user.getId(), request.getRole());
        RoleRequest roleRequest = roleService.requestRole(user.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Role request submitted successfully", roleRequest));
    }
    
    @PostMapping("/send-email-verification")
    public ResponseEntity<ApiResponse<Void>> sendEmailVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("Sending email verification OTP to: {}", email);
        authService.sendEmailVerification(email);
        return ResponseEntity.ok(ApiResponse.success("Verification email sent successfully"));
    }
    
    @PostMapping("/add-update-email")
    public ResponseEntity<ApiResponse<Map<String, String>>> addOrUpdateEmail(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> request) {
        
        if (user == null) {
            log.error("Unauthorized access to add-update-email endpoint");
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error("Authentication required"));
        }
        
        String email = request.get("email");
        log.info("Received request to add/update email for user {}: {}", user.getId(), email);
        
        if (email == null || email.isEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.error("Email is required"));
        }
        
        String otp = authService.addOrUpdateEmail(user.getId(), email);
        Map<String, String> response = Map.of(
            "email", email,
            "emailOtp", otp
        );
        
        return ResponseEntity.ok(ApiResponse.success("Email updated and verification OTP sent", response));
    }
    
    // logout api
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        log.info("Received logout request");
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @PostMapping("/resend-mobile-otp")
    public ResponseEntity<ApiResponse<Map<String, String>>> resendMobileOtp(@RequestBody Map<String, String> request) {
        String mobileNumber = request.get("mobileNumber");
        log.info("Resending OTP for mobile: {}", mobileNumber);
        String otp = authService.resendMobileOtp(mobileNumber);
        Map<String, String> response = Map.of("mobileOtp", otp);
        
        ApiResponse<Map<String, String>> apiResponse = ApiResponse.success("OTP resent successfully", response);
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/request-delete-data")
    public ResponseEntity<ApiResponse<Map<String, String>>> requestDeleteData() {
        Map<String, String> response = Map.of(
            "code", "REQUEST_DELETE_DATA_SUCCESS"
        );
        return ResponseEntity.ok(ApiResponse.success("Request to delete data sent to admin", response));
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 