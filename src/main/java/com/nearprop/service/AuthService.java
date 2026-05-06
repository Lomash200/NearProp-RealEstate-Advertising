//package com.nearprop.service;
//
//import com.nearprop.dto.auth.LoginRequest;
//import com.nearprop.dto.auth.RegisterRequest;
//import com.nearprop.dto.auth.TokenResponse;
//import com.nearprop.dto.auth.VerifyOtpRequest;
//import com.nearprop.dto.auth.RegisterResponse;
//import com.nearprop.entity.Role;
//import com.nearprop.entity.User;
//import com.nearprop.entity.UserSession;
//import com.nearprop.entity.OtpType;
//import com.nearprop.exception.AuthException;
//import com.nearprop.repository.UserRepository;
//import com.nearprop.repository.UserSessionRepository;
//import com.nearprop.repository.OtpRepository;
//import com.nearprop.security.JwtUtil;
//import com.nearprop.config.JwtConfig;
//import com.nearprop.util.IdGenerator;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.flywaydb.core.internal.util.StringUtils;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//import java.util.Set;
//import java.util.UUID;
//import com.nearprop.service.FcmService;
//
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class AuthService {
//    private final UserRepository userRepository;
//    private final UserSessionRepository userSessionRepository;
//    private final OtpService otpService;
//    private final NotificationService notificationService;
//    private final EmailService emailService;
//    private final JwtUtil jwtUtil;
//    private final JwtConfig jwtConfig;
//    private final OtpRepository otpRepository;
//
//    //  Updated dummy variables (with clear names)
//    private static final String DUMMY_MOBILE = "+919012345678";
//    private static final String DUMMY_MOBILE_WITHOUT91 = "9012345678";
//    private static final String DUMMY_OTP = "123456";
//    private final FcmService fcmService;
//
//
//    @Transactional
//    public RegisterResponse register(RegisterRequest request) {
//        log.debug("Processing registration request for mobile: {}", request.getMobileNumber());
//
//        User existingUser = userRepository.findByMobileNumber(request.getMobileNumber()).orElse(null);
//        User existingEmailUser = (request.getEmail() != null)
//                ? userRepository.findByEmail(request.getEmail()).orElse(null)
//                : null;
//
//        boolean mobileExists = existingUser != null;
//        boolean emailExists = existingEmailUser != null;
//        boolean mobileVerified = mobileExists && existingUser.isMobileVerified();
//        boolean emailVerified = emailExists && existingEmailUser != null && existingEmailUser.isEmailVerified();
//
//        String mobileOtp = null;
//        String emailOtp = null;
//        String message = "";
//
//        if (mobileExists && !mobileVerified) {
//            mobileOtp = otpService.generateOtp(request.getMobileNumber(), OtpType.MOBILE);
//            message = "Mobile number is registered but not verified. OTP resent.";
//        }
//        if (emailExists && !emailVerified) {
//            emailOtp = otpService.generateOtp(request.getEmail(), OtpType.EMAIL);
//            if (!message.isEmpty()) {
//                message += " Email is registered but not verified. OTP resent.";
//            } else {
//                message = "Email is registered but not verified. OTP resent.";
//            }
//        }
//        if ((mobileExists && mobileVerified) && (request.getEmail() == null || (emailExists && emailVerified))) {
//            throw new AuthException("Mobile number and email already registered and verified");
//        }
//        if (mobileExists || emailExists) {
//            return RegisterResponse.builder().success(false).message(message).mobileVerified(mobileVerified)
//                    .emailVerified(emailVerified).mobileOtp(mobileOtp).emailOtp(emailOtp).build();
//        }
//
//        // Generate appropriate permanent ID based on role
//        String permanentId;
//        if (request.getRoles() != null && request.getRoles().contains(Role.SELLER)) {
//            permanentId = IdGenerator.generateSellerId();
//        } else if (request.getRoles() != null && request.getRoles().contains(Role.ADVISOR)) {
//            permanentId = IdGenerator.generatePropertyAdvisorId();
//        } else if (request.getRoles() != null && request.getRoles().contains(Role.DEVELOPER)) {
//            permanentId = IdGenerator.generateDeveloperId();
//        } else {
//            permanentId = IdGenerator.generateUserId();
//        }
//
//        User user = User.builder().name(request.getName()).mobileNumber(request.getMobileNumber())
//                .email(request.getEmail()).address(request.getAddress()).district(request.getDistrict())
//                .roles(request.getRoles() != null ? request.getRoles() : Set.of(Role.USER)).mobileVerified(false)
//                .emailVerified(false).permanentId(permanentId).build();
//
//        userRepository.save(user);
//        log.debug("User created successfully with ID: {} and permanent ID: {}", user.getId(), user.getPermanentId());
//
//        mobileOtp = otpService.generateOtp(request.getMobileNumber(), OtpType.MOBILE);
//        if (request.getEmail() != null) {
//            emailOtp = otpService.generateOtp(request.getEmail(), OtpType.EMAIL);
//        }
//        return RegisterResponse.builder().success(true)
//                .message("Registration successful. Please verify your mobile number and email.").mobileVerified(false)
//                .emailVerified(false).mobileOtp(mobileOtp).emailOtp(emailOtp).build();
//    }
//
//    @Transactional
//    public TokenResponse login(LoginRequest request) {
//        log.debug("Processing login request for identifier: {}", request.getMobileNumber());
//
//        String mobile = request.getMobileNumber().trim();
//
//        //  Handle both dummy numbers
//        if (mobile.equals(DUMMY_MOBILE) || mobile.equals(DUMMY_MOBILE_WITHOUT91)) {
//            log.info("Dummy login request detected for: {}", mobile);
//
//            // Ensure dummy user exists or create it once
//            userRepository.findByMobileNumber(DUMMY_MOBILE).orElseGet(() -> {
//                User user = User.builder().name("Dummy User").mobileNumber(DUMMY_MOBILE).roles(Set.of(Role.USER))
//                        .mobileVerified(true).emailVerified(true)
//                        .permanentId("DUMMY-" + System.currentTimeMillis()).build();
//                return userRepository.save(user);
//            });
//
//            return TokenResponse.builder().message("OTP sent successfully (Dummy User)").otp(DUMMY_OTP).build();
//        }
//
//        //  Normal user flow
//        User user = userRepository.findByMobileNumber(mobile)
//                .orElseThrow(() -> new AuthException("User not found"));
//        String generatedOtp = otpService.generateOtp(mobile, OtpType.MOBILE);
//        return TokenResponse.builder().message("OTP sent successfully").otp(generatedOtp).build();
//    }
//
//    @Transactional
//    public TokenResponse verifyOtp(VerifyOtpRequest request) {
//
//        log.info("OTP verification started | identifier={} | type={} | device={}",
//                request.getIdentifier(),
//                request.getType(),
//                request.getDeviceInfo());
//
//        try {
//            String identifier = request.getIdentifier().trim();
//            String otpCode = request.getCode();
//            OtpType otpType = request.getType();
//
//            log.debug("Parsed OTP request | identifier={} | otpType={}", identifier, otpType);
//
//            // ✅ Handle dummy numbers
//            if ((identifier.equals(DUMMY_MOBILE) || identifier.equals(DUMMY_MOBILE_WITHOUT91))
//                    && DUMMY_OTP.equals(otpCode)
//                    && otpType == OtpType.MOBILE) {
//
//                log.info("Dummy OTP verified successfully | identifier={}", identifier);
//
//                User dummyUser = userRepository.findByMobileNumber(DUMMY_MOBILE).orElse(null);
//
//                if (dummyUser == null) {
//                    dummyUser = User.builder()
//                            .name("Dummy User")
//                            .mobileNumber(DUMMY_MOBILE)
//                            .roles(Set.of(Role.USER))
//                            .mobileVerified(true)
//                            .emailVerified(true)
//                            .permanentId("DUMMY-" + System.currentTimeMillis())
//                            .build();
//
//                    dummyUser = userRepository.save(dummyUser);
//                }
//
//
//                // 🔔 SAVE FCM TOKEN (DUMMY USER ALSO)
//                if (StringUtils.hasText(request.getFcmToken())) {
//                    dummyUser.setFcmToken(request.getFcmToken());
//                    userRepository.save(dummyUser);
//
//                    log.info("FCM token saved for dummy user {} : {}",
//                            dummyUser.getId(), request.getFcmToken());
//                }
//
//
//                log.debug("Dummy user loaded | userId={}", dummyUser.getId());
//
//                String token = generateToken(dummyUser, request.getDeviceInfo(), "");
//
//                log.info("Token generated for dummy user | userId={} | tokenLength={}",
//                        dummyUser.getId(), token.length());
//
//                return TokenResponse.builder()
//                        .token(token)
//                        .message("Login successful (Dummy User)")
//                        .userId(dummyUser.getId())
//                        .roles(dummyUser.getRoles())
//                        .name(dummyUser.getName())
//                        .email(dummyUser.getEmail())
//                        .mobileNumber(dummyUser.getMobileNumber())
//                        .permanentId(dummyUser.getPermanentId())
//                        .build();
//            }
//
//            // ✅ Normal OTP flow
//            if (otpType == OtpType.MOBILE) {
//                log.info("Verifying mobile OTP | mobile={}", identifier);
//                verifyMobileOtp(identifier, otpCode);
//                log.info("Mobile OTP verified successfully | mobile={}", identifier);
//
//            } else if (otpType == OtpType.EMAIL) {
//                log.info("Verifying email OTP | email={}", identifier);
//                verifyEmailOtp(identifier, otpCode);
//                log.info("Email OTP verified successfully | email={}", identifier);
//
//                return TokenResponse.builder()
//                        .message("Email verified successfully")
//                        .build();
//            } else {
//                log.warn("Invalid OTP type received | type={}", otpType);
//                throw new AuthException("Invalid OTP type: " + otpType);
//            }
//
//            User user = userRepository.findByMobileNumber(identifier)
//                    .orElseThrow(() -> {
//                        log.warn("User not found after OTP verification | mobile={}", identifier);
//                        return new AuthException("User not found with mobile: " + identifier);
//                        // 🔔 Save FCM token
//                        if (org.springframework.util.StringUtils.hasText(request.getFcmToken())) {
//                            user.setFcmToken(request.getFcmToken());
//                            userRepository.save(user);
//
//                            log.info("FCM token saved for user {} : {}", user.getId(), request.getFcmToken());
//                        }
//
//                        // 📍 Subscribe user to DISTRICT topic
//                        if (org.springframework.util.StringUtils.hasText(user.getDistrict())
//                                && org.springframework.util.StringUtils.hasText(user.getFcmToken())) {
//
//                            String districtTopic = "district_" + user.getDistrict()
//                                    .trim()
//                                    .toLowerCase()
//                                    .replaceAll("\\s+", "_");
//
//                            fcmService.subscribeToTopic(user.getFcmToken(), districtTopic);
//
//                            log.info("User {} subscribed to district topic {}", user.getId(), districtTopic);
//                        }
//
//                        // 📦 Subscribe user to PLAN CATEGORY topics (role wise)
//                        if (org.springframework.util.StringUtils.hasText(user.getFcmToken())) {
//                            for (Role role : user.getRoles()) {
//                                String planTopic = "plan_" + role.name().toLowerCase();
//                                fcmService.subscribeToTopic(user.getFcmToken(), planTopic);
//
//                                log.info("User {} subscribed to plan topic {}", user.getId(), planTopic);
//                            }
//                        }
//
//
//
//                    });
//
//            log.debug("User loaded after OTP verification | userId={}", user.getId());
//
//            String token = generateToken(user, request.getDeviceInfo(), "");
//
//            log.info("Login successful | userId={} | tokenLength={}",
//                    user.getId(), token);
//
//            return TokenResponse.builder()
//                    .token(token)
//                    .message("Login successful")
//                    .userId(user.getId())
//                    .roles(user.getRoles())
//                    .name(user.getName())
//                    .email(user.getEmail())
//                    .mobileNumber(user.getMobileNumber())
//                    .permanentId(user.getPermanentId())
//                    .build();
//
//        } catch (Exception e) {
//            log.error("OTP verification failed | identifier={} | reason={}",
//                    request.getIdentifier(), e.getMessage(), e);
//            throw new AuthException("OTP Verification Error: " + e.getMessage());
//        }
//    }
//
//
////	@Transactional
////	public TokenResponse verifyOtp(VerifyOtpRequest request) {
////		try {
////			String identifier = request.getIdentifier().trim();
////			String otpCode = request.getCode();
////			OtpType otpType = request.getType();
////
////			//  Handle both dummy numbers for OTP verify
////			if ((identifier.equals(DUMMY_MOBILE) || identifier.equals(DUMMY_MOBILE_WITHOUT91))
////					&& DUMMY_OTP.equals(otpCode) && otpType == OtpType.MOBILE) {
////				log.info("Dummy user OTP verified for: {}", identifier);
////
////				User dummyUser = userRepository.findByMobileNumber(DUMMY_MOBILE)
////						.orElseGet(() -> {
////							User user = User.builder().name("Dummy User").mobileNumber(DUMMY_MOBILE)
////									.roles(Set.of(Role.USER)).mobileVerified(true).emailVerified(true)
////									.permanentId("DUMMY-" + System.currentTimeMillis()).build();
////							return userRepository.save(user);
////						});
////
////				String token = generateToken(dummyUser, request.getDeviceInfo(), "");
////
////				log.debug("ayushhhh",token);
////				return TokenResponse.builder().token(token).message("Login successful (Dummy User)")
////						.userId(dummyUser.getId()).roles(dummyUser.getRoles()).name(dummyUser.getName())
////						.email(dummyUser.getEmail()).mobileNumber(dummyUser.getMobileNumber())
////						.permanentId(dummyUser.getPermanentId()).build();
////			}
////
////			//  Normal users
////			if (otpType == OtpType.MOBILE) {
////				verifyMobileOtp(identifier, otpCode);
////			} else if (otpType == OtpType.EMAIL) {
////				verifyEmailOtp(identifier, otpCode);
////				return TokenResponse.builder().message("Email verified successfully").build();
////			} else {
////				throw new AuthException("Invalid OTP type: " + otpType);
////			}
////
////			User user = userRepository.findByMobileNumber(identifier)
////					.orElseThrow(() -> new AuthException("User not found with mobile: " + identifier));
////			String token = generateToken(user, request.getDeviceInfo(), "");
////			log.debug("Ayush : {}",token);
////			return TokenResponse.builder().token(token).message("Login successful").userId(user.getId())
////					.roles(user.getRoles()).name(user.getName()).email(user.getEmail())
////					.mobileNumber(user.getMobileNumber()).permanentId(user.getPermanentId()).build();
////
////		} catch (Exception e) {
////			log.error("Error in verifyOtp: {}", e.getMessage(), e);
////			throw new AuthException("OTP Verification Error: " + e.getMessage());
////		}
////	}
//
//    private void verifyMobileOtp(String mobileNumber, String otpCode) {
//        log.info("Verifying mobile OTP for mobile: {}, code: {}", mobileNumber, otpCode);
//
//        boolean isValid = otpService.verifyOtp(mobileNumber, otpCode, OtpType.MOBILE);
//        log.info("OTP verification result: {}", isValid);
//
//        if (!isValid) {
//            log.error("OTP verification failed for mobile: {}", mobileNumber);
//            throw new AuthException("Invalid OTP code for mobile: " + mobileNumber);
//        }
//
//        User user = userRepository.findByMobileNumber(mobileNumber)
//                .orElseThrow(() -> new AuthException("User not found with mobile: " + mobileNumber));
//
//        user.setMobileVerified(true);
//        userRepository.save(user);
//        log.info("User mobile verified successfully: {}", user.getId());
//    }
//
//    private void verifyEmailOtp(String email, String otpCode) {
//        boolean isValid = otpService.verifyOtp(email, otpCode, OtpType.EMAIL);
//        if (!isValid) {
//            throw new AuthException("Invalid OTP");
//        }
//
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new AuthException("User not found"));
//        user.setEmailVerified(true);
//        userRepository.save(user);
//    }
//
//    private String generateToken(User user, String deviceInfo, String ipAddress) {
//
//        log.info("Generating JWT token | userId={} | roles={}",
//                user.getId(), user.getRoles());
//
//        // 🔁 Deactivate old sessions (except franchisee)
//        if (!user.getRoles().contains(Role.FRANCHISEE)) {
//            log.info("Deactivating existing sessions | userId={}", user.getId());
//            userSessionRepository.deactivateAllUserSessions(user.getId());
//        } else {
//            log.debug("Session deactivation skipped (FRANCHISEE role) | userId={}", user.getId());
//        }
//
//        // ⏱️ Determine expiration time
//        boolean isLongTermUser = user.getRoles().stream()
//                .anyMatch(role ->
//                        role == Role.USER
//                                && !user.getRoles().contains(Role.SELLER)
//                                && !user.getRoles().contains(Role.ADVISOR)
//                                && !user.getRoles().contains(Role.DEVELOPER)
//                                && !user.getRoles().contains(Role.FRANCHISEE)
//                );
//
//        long expirationTimeMs = isLongTermUser
//                ? 100L * 365 * 24 * 60 * 60 * 1000   // ~100 years
//                : 7L * 24 * 60 * 60 * 1000;         // 7 days
//
//        log.debug("Token expiration resolved | userId={} | longTerm={} | expirationMs={}",
//                user.getId(), isLongTermUser, expirationTimeMs);
//
//        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expirationTimeMs / 1000);
//
//        // 🌐 Normalize device & IP
//        String finalDeviceInfo =
//                (deviceInfo != null && !deviceInfo.isEmpty()) ? deviceInfo : "Unknown Device";
//
//        String finalIpAddress =
//                (ipAddress != null && !ipAddress.isEmpty()) ? ipAddress : "0.0.0.0";
//
//        log.debug("Session metadata | userId={} | device={} | ip={}",
//                user.getId(), finalDeviceInfo, finalIpAddress);
//
//        // 🧾 Create session
//        UserSession session = UserSession.builder()
//                .sessionId(UUID.randomUUID().toString())
//                .user(user)
//                .deviceInfo(finalDeviceInfo)
//                .ipAddress(finalIpAddress)
//                .active(true)
//                .expiresAt(expiresAt)
//                .build();
//
//        userSessionRepository.save(session);
//
//        log.info("User session created | userId={} | sessionId={} | expiresAt={}",
//                user.getId(), session.getSessionId(), expiresAt);
//
//        // 🔐 Generate JWT
//        String token = jwtUtil.generateToken(user, session.getSessionId());
//
//        log.info("JWT generated successfully | userId={} | sessionId={} | tokenLength={}",
//                user.getId(), session.getSessionId(), token.length());
//
//        return token;
//    }
//
//    @Transactional
//    public void logout(String sessionId) {
//        log.debug("Processing logout for session: {}", sessionId);
//        userSessionRepository.findBySessionIdAndActive(sessionId, true).ifPresent(session -> {
//            session.setActive(false);
//            userSessionRepository.save(session);
//            log.debug("Session deactivated: {}", sessionId);
//        });
//    }
//
//    @Transactional
//    public void sendEmailVerification(String email) {
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new AuthException("User not found"));
//        if (user.isEmailVerified()) {
//            throw new AuthException("Email already verified");
//        }
//        otpService.generateOtp(email, OtpType.EMAIL);
//    }
//
//    @Transactional
//    public String addOrUpdateEmail(Long userId, String email) {
//        log.info("Adding/updating email for user {}: {}", userId, email);
//
//        Optional<User> existingEmailUser = userRepository.findByEmail(email);
//        if (existingEmailUser.isPresent() && !existingEmailUser.get().getId().equals(userId)) {
//            throw new AuthException("Email is already registered to another user");
//        }
//
//        User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));
//        user.setEmail(email);
//        user.setEmailVerified(false);
//        userRepository.save(user);
//
//        String otp = otpService.generateOtp(email, OtpType.EMAIL);
//        log.info("Email updated and verification OTP sent for user {}", userId);
//
//        return otp;
//    }
//
//    public String resendMobileOtp(String mobileNumber) {
//        User user = userRepository.findByMobileNumber(mobileNumber)
//                .orElseThrow(() -> new AuthException("User with this mobile number does not exist"));
//        return otpService.generateOtp(mobileNumber, OtpType.MOBILE);
//    }
//}


package com.nearprop.service;

import com.nearprop.dto.auth.LoginRequest;
import com.nearprop.dto.auth.RegisterRequest;
import com.nearprop.dto.auth.TokenResponse;
import com.nearprop.dto.auth.VerifyOtpRequest;
import com.nearprop.dto.auth.RegisterResponse;
import com.nearprop.entity.Role;
import com.nearprop.entity.User;
import com.nearprop.entity.UserSession;
import com.nearprop.entity.OtpType;
import com.nearprop.exception.AuthException;
import com.nearprop.repository.UserRepository;
import com.nearprop.repository.UserSessionRepository;
import com.nearprop.repository.OtpRepository;
import com.nearprop.security.JwtUtil;
import com.nearprop.config.JwtConfig;
import com.nearprop.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import com.nearprop.service.FcmService;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    private final OtpService otpService;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final OtpRepository otpRepository;
    private final FcmService fcmService;

    private static final String DUMMY_MOBILE = "+919012345678";
    private static final String DUMMY_MOBILE_WITHOUT91 = "9012345678";
    private static final String DUMMY_OTP = "123456";


    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.debug("Processing registration request for mobile: {}", request.getMobileNumber());

        User existingUser = userRepository.findByMobileNumber(request.getMobileNumber()).orElse(null);
        User existingEmailUser = (request.getEmail() != null)
                ? userRepository.findByEmail(request.getEmail()).orElse(null)
                : null;

        boolean mobileExists = existingUser != null;
        boolean emailExists = existingEmailUser != null;
        boolean mobileVerified = mobileExists && existingUser.isMobileVerified();
        boolean emailVerified = emailExists && existingEmailUser != null && existingEmailUser.isEmailVerified();

        String mobileOtp = null;
        String emailOtp = null;
        String message = "";

        if (mobileExists && !mobileVerified) {
            mobileOtp = otpService.generateOtp(request.getMobileNumber(), OtpType.MOBILE);
            message = "Mobile number is registered but not verified. OTP resent.";
        }
        if (emailExists && !emailVerified) {
            emailOtp = otpService.generateOtp(request.getEmail(), OtpType.EMAIL);
            if (!message.isEmpty()) {
                message += " Email is registered but not verified. OTP resent.";
            } else {
                message = "Email is registered but not verified. OTP resent.";
            }
        }
        if ((mobileExists && mobileVerified) && (request.getEmail() == null || (emailExists && emailVerified))) {
            throw new AuthException("Mobile number and email already registered and verified");
        }
        if (mobileExists || emailExists) {
            return RegisterResponse.builder().success(false).message(message).mobileVerified(mobileVerified)
                    .emailVerified(emailVerified).mobileOtp(mobileOtp).emailOtp(emailOtp).build();
        }

        String permanentId;
        if (request.getRoles() != null && request.getRoles().contains(Role.SELLER)) {
            permanentId = IdGenerator.generateSellerId();
        } else if (request.getRoles() != null && request.getRoles().contains(Role.ADVISOR)) {
            permanentId = IdGenerator.generatePropertyAdvisorId();
        } else if (request.getRoles() != null && request.getRoles().contains(Role.DEVELOPER)) {
            permanentId = IdGenerator.generateDeveloperId();
        } else {
            permanentId = IdGenerator.generateUserId();
        }

        User user = User.builder().name(request.getName()).mobileNumber(request.getMobileNumber())
                .email(request.getEmail()).address(request.getAddress()).district(request.getDistrict())
                .roles(request.getRoles() != null ? request.getRoles() : Set.of(Role.USER)).mobileVerified(false)
                .emailVerified(false).permanentId(permanentId).build();

        userRepository.save(user);
        log.debug("User created successfully with ID: {} and permanent ID: {}", user.getId(), user.getPermanentId());

        mobileOtp = otpService.generateOtp(request.getMobileNumber(), OtpType.MOBILE);
        if (request.getEmail() != null) {
            emailOtp = otpService.generateOtp(request.getEmail(), OtpType.EMAIL);
        }
        return RegisterResponse.builder().success(true)
                .message("Registration successful. Please verify your mobile number and email.").mobileVerified(false)
                .emailVerified(false).mobileOtp(mobileOtp).emailOtp(emailOtp).build();
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        log.debug("Processing login request for identifier: {}", request.getMobileNumber());

        String mobile = request.getMobileNumber().trim();

        if (mobile.equals(DUMMY_MOBILE) || mobile.equals(DUMMY_MOBILE_WITHOUT91)) {
            log.info("Dummy login request detected for: {}", mobile);

            userRepository.findByMobileNumber(DUMMY_MOBILE).orElseGet(() -> {
                User user = User.builder().name("Dummy User").mobileNumber(DUMMY_MOBILE).roles(Set.of(Role.USER))
                        .mobileVerified(true).emailVerified(true)
                        .permanentId("DUMMY-" + System.currentTimeMillis()).build();
                return userRepository.save(user);
            });

            return TokenResponse.builder().message("OTP sent successfully (Dummy User)").otp(DUMMY_OTP).build();
        }

        User user = userRepository.findByMobileNumber(mobile)
                .orElseThrow(() -> new AuthException("User not found"));
        String generatedOtp = otpService.generateOtp(mobile, OtpType.MOBILE);
        return TokenResponse.builder().message("OTP sent successfully").otp(generatedOtp).build();
    }

    @Transactional
    public TokenResponse verifyOtp(VerifyOtpRequest request) {
        log.info("OTP verification started | identifier={} | type={}  | token={}", request.getIdentifier(), request.getType(), request.getFcmToken());

        try {
            String identifier = request.getIdentifier().trim();
            String otpCode = request.getCode();
            OtpType otpType = request.getType();

            // Handle Dummy Login logic
            if ((identifier.equals(DUMMY_MOBILE) || identifier.equals(DUMMY_MOBILE_WITHOUT91))
                    && DUMMY_OTP.equals(otpCode) && otpType == OtpType.MOBILE) {

                User dummyUser = userRepository.findByMobileNumber(DUMMY_MOBILE).orElse(null);
                if (dummyUser == null) {
                    dummyUser = User.builder()
                            .name("Dummy User").mobileNumber(DUMMY_MOBILE).roles(Set.of(Role.USER))
                            .mobileVerified(true).emailVerified(true).permanentId("DUMM-" + System.currentTimeMillis())
                            .build();
                    dummyUser = userRepository.save(dummyUser);
                }

                if (StringUtils.hasText(request.getFcmToken())) {
                    dummyUser.setFcmToken(request.getFcmToken());


                    log.debug(
                            "Setting FCM token for dummyUser | userId={} | fromdemo ={} | from refcmToken={}",
                            dummyUser.getId(),
                            dummyUser.getFcmToken(),
                            request.getFcmToken()
                    );

                    userRepository.save(dummyUser);
                }

                String token = generateToken(dummyUser, request.getDeviceInfo(), "");
                return createTokenResponse(dummyUser, token, "Login successful (Dummy User)");
            }

            // Normal OTP validation
            if (otpType == OtpType.MOBILE) {
                verifyMobileOtp(identifier, otpCode);
            } else if (otpType == OtpType.EMAIL) {
                verifyEmailOtp(identifier, otpCode);
                return TokenResponse.builder().message("Email verified successfully").build();
            } else {
                throw new AuthException("Invalid OTP type: " + otpType);
            }

            // Fetch User after successful verification
            User user = userRepository.findByMobileNumber(identifier)
                    .orElseThrow(() -> new AuthException("User not found with mobile: " + identifier));

            // Save FCM token and handle topic subscriptions
            if (StringUtils.hasText(request.getFcmToken())) {
                user.setFcmToken(request.getFcmToken());
                userRepository.save(user);
                log.info("FCM token saved for user {}", user.getId());

                // District topic subscription
//                if (StringUtils.hasText(user.getDistrict())) {
//                    String districtTopic = "district_" + user.getDistrict().trim().toLowerCase().replaceAll("\\s+", "_");
//                    fcmService.subscribeToTopic(user.getFcmToken(), districtTopic);
//                }

                // Plan topic subscription (Role-wise)
//                for (Role role : user.getRoles()) {
//                    String planTopic = "plan_" + role.name().toLowerCase();
//                    fcmService.subscribeToTopic(user.getFcmToken(), planTopic);
//                }
            }

            String token = generateToken(user, request.getDeviceInfo(), "");
            return createTokenResponse(user, token, "Login successful");

        } catch (Exception e) {
            log.error("OTP verification failed: {}", e.getMessage());
            throw new AuthException("OTP Verification Error: " + e.getMessage());
        }
    }

    private TokenResponse createTokenResponse(User user, String token, String message) {
        return TokenResponse.builder()
                .token(token)
                .message(message)
                .userId(user.getId())
                .roles(user.getRoles())
                .name(user.getName())
                .email(user.getEmail())
                .mobileNumber(user.getMobileNumber())
                .permanentId(user.getPermanentId())
                .build();
    }

    private void verifyMobileOtp(String mobileNumber, String otpCode) {
        boolean isValid = otpService.verifyOtp(mobileNumber, otpCode, OtpType.MOBILE);
        if (!isValid) {
            throw new AuthException("Invalid OTP code for mobile: " + mobileNumber);
        }

        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new AuthException("User not found with mobile: " + mobileNumber));

        user.setMobileVerified(true);
        userRepository.save(user);
    }

    private void verifyEmailOtp(String email, String otpCode) {
        boolean isValid = otpService.verifyOtp(email, otpCode, OtpType.EMAIL);
        if (!isValid) {
            throw new AuthException("Invalid OTP");
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AuthException("User not found"));
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    private String generateToken(User user, String deviceInfo, String ipAddress) {
        if (!user.getRoles().contains(Role.FRANCHISEE)) {
            userSessionRepository.deactivateAllUserSessions(user.getId());
        }

        boolean isLongTermUser = user.getRoles().stream()
                .anyMatch(role -> role == Role.USER
                        && !user.getRoles().contains(Role.SELLER)
                        && !user.getRoles().contains(Role.ADVISOR)
                        && !user.getRoles().contains(Role.DEVELOPER)
                        && !user.getRoles().contains(Role.FRANCHISEE));

        long expirationTimeMs = isLongTermUser
                ? 100L * 365 * 24 * 60 * 60 * 1000
                : 7L * 24 * 60 * 60 * 1000;

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expirationTimeMs / 1000);
        String finalDeviceInfo = (deviceInfo != null && !deviceInfo.isEmpty()) ? deviceInfo : "Unknown Device";
        String finalIpAddress = (ipAddress != null && !ipAddress.isEmpty()) ? ipAddress : "0.0.0.0";

        UserSession session = UserSession.builder()
                .sessionId(UUID.randomUUID().toString())
                .user(user)
                .deviceInfo(finalDeviceInfo)
                .ipAddress(finalIpAddress)
                .active(true)
                .expiresAt(expiresAt)
                .build();

        userSessionRepository.save(session);
        return jwtUtil.generateToken(user, session.getSessionId());
    }

    @Transactional
    public void logout(String sessionId) {
        userSessionRepository.findBySessionIdAndActive(sessionId, true).ifPresent(session -> {
            session.setActive(false);
            userSessionRepository.save(session);
        });
    }

    @Transactional
    public void sendEmailVerification(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AuthException("User not found"));
        if (user.isEmailVerified()) {
            throw new AuthException("Email already verified");
        }
        otpService.generateOtp(email, OtpType.EMAIL);
    }

    @Transactional
    public String addOrUpdateEmail(Long userId, String email) {
        Optional<User> existingEmailUser = userRepository.findByEmail(email);
        if (existingEmailUser.isPresent() && !existingEmailUser.get().getId().equals(userId)) {
            throw new AuthException("Email is already registered to another user");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new AuthException("User not found"));
        user.setEmail(email);
        user.setEmailVerified(false);
        userRepository.save(user);

        return otpService.generateOtp(email, OtpType.EMAIL);
    }

    public String resendMobileOtp(String mobileNumber) {
        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new AuthException("User with this mobile number does not exist"));
        return otpService.generateOtp(mobileNumber, OtpType.MOBILE);
    }
}
