package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.entity.Otp;
import com.nearprop.entity.OtpType;
import com.nearprop.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/v1/debug")
@RequiredArgsConstructor
@Slf4j
@Profile("!production") // Disable in production
public class DebugController {

    private final OtpRepository otpRepository;

    @GetMapping("/otps")
    public ResponseEntity<ApiResponse<List<Otp>>> getOtps(@RequestParam String mobileNumber) {
        log.info("Fetching OTPs for mobile number: {}", mobileNumber);
        List<Otp> otps = otpRepository.findByMobileNumber(mobileNumber);
        return ResponseEntity.ok(ApiResponse.success("OTPs found", otps));
    }
    
    @PostMapping("/verify-otp-test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testOtpVerification(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String code = request.get("code");
        String typeStr = request.get("type");
        OtpType type = OtpType.valueOf(typeStr);
        
        log.info("Debug OTP verification for: {}, code: {}, type: {}", identifier, code, type);
        
        List<Otp> otps = type == OtpType.MOBILE ? 
            otpRepository.findByMobileNumber(identifier) : 
            otpRepository.findByEmail(identifier);
        
        Otp latestOtp = otps.isEmpty() ? null : 
            otps.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .findFirst()
                .orElse(null);
        
        Map<String, Object> response = Map.of(
            "otpsFound", otps.size(),
            "latestOtp", latestOtp != null ? Map.of(
                "id", latestOtp.getId(),
                "code", latestOtp.getCode(),
                "verified", latestOtp.isVerified(),
                "blocked", latestOtp.isBlocked(),
                "createdAt", latestOtp.getCreatedAt(),
                "expiresAt", latestOtp.getExpiresAt()
            ) : null,
            "codeMatches", latestOtp != null && latestOtp.getCode().equals(code),
            "isValid", latestOtp != null && !latestOtp.isVerified() && !latestOtp.isBlocked()
        );
        
        return ResponseEntity.ok(ApiResponse.success("OTP verification debug results", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugLogin(@RequestBody Map<String, String> request) {
        String mobileNumber = request.get("mobileNumber");
        String deviceInfo = request.get("deviceInfo");
        
        log.info("Debug login for mobile: {}", mobileNumber);
        
        // Generate OTP
        String code = String.valueOf(100000 + new java.security.SecureRandom().nextInt(900000));
        
        // Create new OTP
        Otp otp = Otp.builder()
                .identifier(mobileNumber)
                .mobileNumber(mobileNumber)
                .type(OtpType.MOBILE)
                .code(code)
                .attempts(0)
                .verified(false)
                .blocked(false)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .createdAt(LocalDateTime.now())
                .build();
        
        otpRepository.save(otp);
        log.info("Generated debug OTP for {}: {}", mobileNumber, code);
        
        Map<String, Object> response = Map.of(
            "otp", code,
            "message", "OTP sent successfully"
        );
        
        return ResponseEntity.ok(ApiResponse.success("Debug login successful", response));
    }
    
    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugVerifyOtp(@RequestBody Map<String, String> request) {
        String identifier = request.get("identifier");
        String code = request.get("code");
        String deviceInfo = request.get("deviceInfo");
        
        log.info("Debug verify OTP for: {}, code: {}", identifier, code);
        
        // Find the latest OTP
        List<Otp> otps = otpRepository.findByMobileNumber(identifier);
        
        if (otps.isEmpty()) {
            log.error("No OTPs found for: {}", identifier);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("No OTPs found"));
        }
        
        Otp latestOtp = otps.stream()
            .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
            .findFirst()
            .orElse(null);
            
        if (latestOtp == null) {
            log.error("No valid OTP found for: {}", identifier);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("No valid OTP found"));
        }
        
        if (!latestOtp.getCode().equals(code)) {
            log.error("OTP code mismatch. Expected: {}, Actual: {}", latestOtp.getCode(), code);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Invalid OTP code"));
        }
        
        if (latestOtp.isVerified() || latestOtp.isBlocked()) {
            log.error("OTP is already verified or blocked");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("OTP is already used or blocked"));
        }
        
        // Mark as verified
        latestOtp.setVerified(true);
        latestOtp.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(latestOtp);
        log.info("OTP verified successfully");
        
        // Create a dummy token
        String token = "debug_token_" + UUID.randomUUID().toString();
        
        Map<String, Object> response = Map.of(
            "token", token,
            "message", "Login successful"
        );
        
        return ResponseEntity.ok(ApiResponse.success("Debug verification successful", response));
    }
} 