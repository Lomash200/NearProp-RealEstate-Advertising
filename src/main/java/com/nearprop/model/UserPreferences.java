package com.nearprop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity to store user preferences including language settings
 */
@Entity
@Table(name = "user_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferences {
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private com.nearprop.entity.User user;
    
    @Column(name = "language")
    private String language;
    
    @Column(name = "email_notifications")
    private boolean emailNotifications;
    
    @Column(name = "sms_notifications")
    private boolean smsNotifications;
    
    @Column(name = "app_notifications")
    private boolean appNotifications;
    
    @Column(name = "temperature_unit")
    private String temperatureUnit;
    
    @Column(name = "distance_unit")
    private String distanceUnit;
    
    @Column(name = "currency")
    private String currency;
    
    @Column(name = "date_format")
    private String dateFormat;
    
    @Column(name = "time_format")
    private String timeFormat;
    
    @Column(name = "theme")
    private String theme;
    
    @Column(name = "dashboard_view")
    private String dashboardView;
    
    @Column(name = "property_view_type")
    private String propertyViewType;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
} 