package com.nearprop.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nearprop.config.AwsConfig;
import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.admin.AdminNotificationRequest;
import com.nearprop.entity.AdminNotification;
import com.nearprop.entity.Role;
import com.nearprop.notification.NotificationTarget;
import com.nearprop.repository.AdminNotificationRepository;
import com.nearprop.service.NotificationService;
import com.nearprop.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/admin/notifications")
@RequiredArgsConstructor
public class AdminNotificationController {

    private final NotificationService notificationService;
    private final AdminNotificationRepository adminNotificationRepository;
    private final S3Service s3Service;
    private final AwsConfig awsConfig;

    // 🔹 POST – create notification
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> sendNotification(

            @RequestParam String title,
            @RequestParam String body,
            @RequestParam String sendTo,

            @RequestParam(required = false) String roles,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) Long districtId,
            @RequestParam(required = false) String userIds,

            @RequestPart(value = "image", required = false) MultipartFile image
    ) {

        AdminNotificationRequest request = AdminNotificationRequest.builder()
                .title(title)
                .body(body)
                .sendTo(NotificationTarget.valueOf(sendTo)) // ⭐ IMPORTANT
                .state(state)
                .districtId(districtId)
                .roles(parseRoles(roles))
                .userIds(parseUserIds(userIds))
                .build();

        AdminNotification saved =
                notificationService.sendAdminNotification(request, image);

        return ApiResponse.success(
                "Notification sent successfully",
                saved
        );

    }



    private Set<Role> parseRoles(String roles) {
        if (roles == null || roles.isBlank() || roles.equals("[]")) {
            return Set.of();
        }

        return Arrays.stream(
                        roles.replace("[", "")
                                .replace("]", "")
                                .split(",")
                )
                .map(String::trim)
                .map(Role::valueOf)
                .collect(Collectors.toSet());
    }

    private List<Long> parseUserIds(String userIds) {
        if (userIds == null || userIds.isBlank()) return List.of();
        return Arrays.stream(userIds.replace("[", "")
                        .replace("]", "")
                        .split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Long::valueOf)
                .toList();
    }



    // 🔹 GET – admin history
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> getAdminNotifications(
            @RequestParam(required = false) NotificationTarget sendTo,
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Long districtId
    ) {

        List<AdminNotification> list;

        if (districtId != null) {
            list = adminNotificationRepository.findByDistrictId(districtId);
        } else if (role != null) {
            list = adminNotificationRepository.findByRolesContaining(role);
        } else if (sendTo != null) {
            list = adminNotificationRepository.findBySendTo(sendTo);
        } else {
            list = adminNotificationRepository.findAll();
        }

        return ApiResponse.success("Admin notification history", list);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> deleteAdminNotificationById(@PathVariable Long id) {

        adminNotificationRepository.deleteById(id);

        return ApiResponse.success(
                "Admin notification deleted successfully",
                null
        );
    }
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> deleteAllAdminNotifications() {

        adminNotificationRepository.deleteAll();

        return ApiResponse.success(
                "All admin notifications deleted successfully",
                null
        );
    }

}
