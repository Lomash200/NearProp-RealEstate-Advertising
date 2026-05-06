package com.nearprop.dto.admin;

import com.nearprop.entity.Role;
import com.nearprop.notification.NotificationTarget;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;
@Builder
@Data
public class AdminNotificationRequest {

    private String title;
    private String body;
    private String imageUrl;

    private NotificationTarget sendTo;

    // Optional filters

    private String state;
    private Long districtId;
    private List<Long> userIds;
    private Set<Role> roles;


}
