package com.nearprop.service;

import com.nearprop.config.EmailConfig;
import com.nearprop.dto.admin.AdminNotificationRequest;
import com.nearprop.entity.*;
import com.nearprop.notification.NotificationTarget;
import com.nearprop.repository.AdminNotificationRepository;
import com.nearprop.repository.DistrictRepository;
import com.nearprop.repository.NotificationRepository;
import com.nearprop.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.nearprop.entity.AdminNotification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import com.nearprop.service.S3Service;
import com.nearprop.config.AwsConfig;



@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    // ---------- EXISTING DEPENDENCIES ----------
    private final EmailConfig emailConfig;
    private final JavaMailSender mailSender;
    private final DigitalSmsService digitalSmsService;
    private final FcmService fcmService;

    // ---------- NEW (ADMIN NOTIFICATION) ----------
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final DistrictRepository districtRepository;

    // ======================================================
    // =============== ADMIN PUSH NOTIFICATION ===============
    // ======================================================
    private final AdminNotificationRepository adminNotificationRepository;

    private final S3Service s3Service;
    private final AwsConfig awsConfig;



    /**
     * 🔐 ADMIN → Global / Role / State / District / User notifications
     * SAFE: Existing system unaffected
     */
    // @Async
    @Transactional
    public AdminNotification sendAdminNotification(
            AdminNotificationRequest req,
            MultipartFile image
    ) {

        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = s3Service.uploadFile(
                        image.getBytes(),
                        "notifications/admin/" + image.getOriginalFilename(),
                        awsConfig.getS3().getBucket(),
                        image.getContentType()
                );
            } catch (IOException e) {
                log.error("Image upload failed", e);
            }
        }
        AdminNotification adminNotification = AdminNotification.builder()
                .title(req.getTitle())
                .body(req.getBody())
                .imageUrl(imageUrl)
                .sendTo(req.getSendTo())
                .state(req.getState())
                .districtId(req.getDistrictId())
                .roles(new HashSet<>(req.getRoles()))
                .build();

        // save + reference
        AdminNotification savedAdminNotification =
                adminNotificationRepository.save(adminNotification);
        List<User> users = switch (req.getSendTo()) {

            case ALL -> userRepository.findAll();

            case ROLE -> {
                List<User> list = new ArrayList<>();
                for (Role role : req.getRoles()) {
                    list.addAll(userRepository.findDistinctByRolesContaining(role));
                }
                yield list;
            }

            case STATE -> {
                List<District> districts =
                        districtRepository.findByState(req.getState());

                List<Long> districtIds =
                        districts.stream().map(District::getId).toList();

                yield userRepository.findByDistrictIdIn(districtIds);
            }

            case DISTRICT ->
                    userRepository.findByDistrictId(req.getDistrictId());

            case USERS ->
                    userRepository.findAllById(req.getUserIds());
        };

        // 🔥 STEP 4: user notifications + FCM
        for (User user : users) {

            Notification notification = Notification.builder()
                    .title(req.getTitle())
                    .body(req.getBody())
                    .imageUrl(imageUrl)
                    .user(user)
                    .build();

            notificationRepository.save(notification);

            if (StringUtils.hasText(user.getFcmToken())) {
                try {
                    fcmService.sendNotificationWithImage(
                            user.getFcmToken(),
                            req.getTitle(),
                            req.getBody(),
                            imageUrl
                    );
                } catch (Exception e) {
                    log.error("Push failed for user {}", user.getId(), e);
                }
            }
        }

        log.info("Admin notification sent to {} users", users.size());


        return savedAdminNotification;
    }


    // ======================================================
    // ================= EXISTING FEATURES ==================
    // ======================================================

    @Async
    public void sendSmsOtp(String mobileNumber, String otp) {
        sendSms(mobileNumber,
                String.format("Your NearProp verification code is: %s", otp));
    }

    @Async
    public void sendEmailOtp(String email, String otp) {
        sendEmail(email,
                "NearProp - Email Verification",
                "Your verification code is: " + otp);
    }

    @Async
    public void sendWelcomeEmail(String email, String name) {
        sendEmail(email,
                "Welcome to NearProp!",
                "Dear " + name + ",\n\nWelcome to NearProp!");
    }

    @Async
    public void sendSms(String to, String message) {
        try {
            digitalSmsService.sendSms(to, message);
        } catch (Exception e) {
            log.error("SMS failed", e);
        }
    }

    @Async
    public void sendEmail(String to, String subject, String body) {
        try {
            if (isEmailConfigured()) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper =
                        new MimeMessageHelper(message, true);

                helper.setFrom(emailConfig.getUsername());
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(body);

                mailSender.send(message);
            } else {
                log.info("MOCK EMAIL to {} | {}", to, subject);
            }
        } catch (MessagingException e) {
            log.error("Email failed", e);
        }
    }

    @Async
    public void sendPushNotification(String fcmToken, String title, String body) {
        if (!StringUtils.hasText(fcmToken)) return;
        try {
            fcmService.sendNotification(fcmToken, title, body);
        } catch (Exception e) {
            log.error("Push failed", e);
        }
    }

    // ======================================================
    // ================= DISTRICT TOPIC PUSH =================
    // ======================================================

    @Async
    public void notifyDistrictUsers(String district, String title, String body) {
        String topic = buildDistrictTopic(district);
        try {
            fcmService.sendToTopic(topic, title, body);
        } catch (Exception e) {
            log.error("District push failed", e);
        }
    }

    @Async
    public void notifyDistrictPropertyAdded(String district, Long propertyId) {

        Map<String, String> data = Map.of(
                "type", "PROPERTY",
                "id", String.valueOf(propertyId)
        );

        String topic = buildDistrictTopic(district);

        try {
            fcmService.sendToTopicWithData(
                    topic,
                    "New Property Available 🏠",
                    "A new property has been added in your area",
                    data
            );
        } catch (Exception e) {
            log.error("Property push failed", e);
        }
    }

    // ======================================================

    private String buildDistrictTopic(String district) {
        return "district_" + district.trim()
                .toLowerCase()
                .replaceAll("\\s+", "_");
    }

    private boolean isEmailConfigured() {
        return StringUtils.hasText(emailConfig.getUsername())
                && StringUtils.hasText(emailConfig.getPassword());
    }
    public Map<String, Object> getSmsBalanceStatus() {
        return digitalSmsService.getSmsBalanceStatus();
    }
    @Async
    public void notifyDistrictPlanPublished(String district, Long planId) {

        if (district == null || district.isBlank()) {
            log.warn("District is null/blank, skipping plan notification");
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("type", "PLAN");
        data.put("id", String.valueOf(planId));

        String topic = "district_" +
                district.trim()
                        .toLowerCase()
                        .replaceAll("\\s+", "_");

        try {
            fcmService.sendToTopicWithData(
                    topic,
                    "New Subscription Plan 🆕",
                    "A new subscription plan is available in your area",
                    data
            );
            log.info("Plan notification sent to topic {}", topic);
        } catch (Exception e) {
            log.error("Plan notification failed for topic {}", topic, e);
        }
    }


    @Async
    public void notifyPropertyAddedFiltered(Long districtId, Long propertyId) {

        log.info("🔔 Property notification started | districtId={}, propertyId={}", districtId, propertyId);

        List<String> tokens = userRepository.findPropertyNotificationTokens(
                districtId,
                Subscription.SubscriptionStatus.ACTIVE
        );

        log.info("👥 Found {} eligible users with FCM tokens", tokens.size());

        if (tokens.isEmpty()) {
            log.warn("⚠️ No tokens found, skipping notification");
            return;
        }

        Map<String, String> data = new HashMap<>();
        data.put("type", "PROPERTY");
        data.put("propertyId", String.valueOf(propertyId));

        for (String token : tokens) {
            try {
                fcmService.sendNotificationWithData(
                        token,
                        "New Property Alert! 🏠",
                        "A new property has been listed in your area",
                        data
                );
            } catch (Exception e) {
                log.error("❌ Failed to send property notification to token: {}", token, e);
            }
        }
    }



    @Async
    public void sendSubscriptionActivationNotification(
            String fcmToken,
            Long planId,
            String planName
    ) {
        if (!org.springframework.util.StringUtils.hasText(fcmToken)) {
            log.warn("FCM Token missing, skipping subscription notification");
            return;
        }

        try {
            // 🔥 DATA PAYLOAD (REDIRECT KEY)
            Map<String, String> data = new HashMap<>();
            data.put("type", "PLAN");
            data.put("planId", String.valueOf(planId));

            fcmService.sendNotificationWithData(
                    fcmToken,
                    "Plan Activated! 💳",
                    "Your " + planName + " subscription is now active. Enjoy NearProp premium features!",
                    data
            );

            log.info("📤 Subscription activation notification sent | planId={}", planId);

        } catch (Exception e) {
            log.error("❌ Failed to send subscription notification", e);
        }
    }



}
