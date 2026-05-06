package com.nearprop.repository;

import com.nearprop.entity.AdminNotification;
import com.nearprop.entity.Role;
import com.nearprop.notification.NotificationTarget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminNotificationRepository
        extends JpaRepository<AdminNotification, Long> {
    List<AdminNotification> findBySendTo(NotificationTarget sendTo);

    List<AdminNotification> findByDistrictId(Long districtId);

    List<AdminNotification> findByRolesContaining(Role role);

    List<AdminNotification> findByState(String state);

}
