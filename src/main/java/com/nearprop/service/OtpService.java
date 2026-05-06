package com.nearprop.service;

import com.nearprop.entity.Otp;
import com.nearprop.entity.OtpType;
import com.nearprop.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {
    private final OtpRepository otpRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public String generateOtp(String identifier, OtpType type) {
        String code = generateRandomCode();

        // Invalidate any existing OTPs
        Optional<Otp> existingOtp = type == OtpType.MOBILE ?
                otpRepository.findByMobileNumberAndVerifiedFalseAndBlockedFalse(identifier) :
                otpRepository.findByEmailAndVerifiedFalseAndBlockedFalse(identifier);

        existingOtp.ifPresent(otp -> {
            otp.setVerified(true);
            otpRepository.save(otp);
        });

        // Create new OTP
        Otp otp = Otp.builder()
                .identifier(identifier)
                .type(type)
                .code(code)
                .attempts(0)
                .verified(false)
                .blocked(false)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        if (type == OtpType.MOBILE) {
            otp.setMobileNumber(identifier);
            // Format the mobile number with +91 prefix if it doesn't have a country code
            String formattedMobile = identifier.startsWith("+") ? identifier : "+91" + identifier;
            // Use the standard message format required by Digital SMS API
            String message = "Your Rudraashwi verification code is: " + code + ". Do not share this code with anyone. It is valid for 10 minutes.";
            notificationService.sendSms(formattedMobile, message);
        } else {
            otp.setEmail(identifier);
            // Set a placeholder for mobile number to satisfy not-null constraint
            otp.setMobileNumber("EMAIL_" + identifier);
            emailService.sendOtpEmail(identifier, code);
        }

        otpRepository.save(otp);
        log.info("Generated OTP for {}: {}", identifier, code);
        return code;
    }

    @Transactional
    public boolean verifyOtp(String identifier, String code, OtpType type) {
        log.info("Verifying OTP for identifier: {}, code: {}, type: {}", identifier, code, type);
        
        // Normalize identifier (in case it's sent as a number)
        String normalized = identifier == null ? null : identifier.trim();
        // Bypass for test mobile/otp
        if (type == OtpType.MOBILE && ("1234598760".equals(normalized))) {
            log.info("Bypassing OTP validation for test mobile {} (any code allowed)", normalized);
            return true;
        }
        if (type == OtpType.MOBILE && (("0000000000".equals(normalized) && "000000".equals(code)) || ("8888888888".equals(normalized) && "123456".equals(code)))) {
            log.info("Bypassing OTP verification for test mobile {} and code {}", normalized, code);
            return true;
        }

        // Get all OTPs for the identifier
        Optional<Otp> otpOptional = type == OtpType.MOBILE ? 
            otpRepository.findByMobileNumberAndVerifiedFalseAndBlockedFalse(identifier) : 
            otpRepository.findByEmailAndVerifiedFalseAndBlockedFalse(identifier);
        
        if (otpOptional.isEmpty()) {
            log.warn("No valid unverified OTPs found for {}", identifier);
            return false;
        }
        
        Otp otp = otpOptional.get();
        
        log.info("Found active OTP: id={}, code={}, verified={}, blocked={}, expiresAt={}", 
                otp.getId(), otp.getCode(), otp.isVerified(), 
                otp.isBlocked(), otp.getExpiresAt());
        
        // Check if OTP matches
        if (!otp.getCode().equals(code)) {
            log.warn("OTP code mismatch. Expected: {}, Actual: {}", otp.getCode(), code);
            return false;
        }
        
        // Check if OTP is expired
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("OTP has expired. Expires at: {}, Current time: {}", 
                    otp.getExpiresAt(), LocalDateTime.now());
            return false;
        }
        
        // Mark as verified
        otp.setVerified(true);
        otp.setVerifiedAt(LocalDateTime.now());
        otpRepository.save(otp);
        
        log.info("OTP verified successfully for {}", identifier);
        
        return true;
    }

    private String generateRandomCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    @Scheduled(cron = "0 */5 * * * *") // Run every 5 minutes
    @Transactional
    public void cleanupOldOtps() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        otpRepository.deleteByCreatedAtBefore(cutoff);
        log.info("Cleaned up OTPs older than {}", cutoff);
    }
} 