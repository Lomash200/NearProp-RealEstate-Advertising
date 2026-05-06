package com.nearprop.entity;

import com.nearprop.notification.NotificationTarget;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "admin_notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(length = 512)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private NotificationTarget sendTo;

    private String state;
    private Long districtId;


    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(
            name = "admin_notification_roles",
            joinColumns = @JoinColumn(name = "admin_notification_id")
    )
    @Column(name = "role")
    private Set<Role> roles;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
