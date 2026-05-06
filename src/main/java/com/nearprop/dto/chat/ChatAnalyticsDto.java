package com.nearprop.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatAnalyticsDto {
    // General statistics
    private Long totalMessages;
    private Long totalRooms;
    private Long activeRooms;
    private Long totalUsers;
    private Long activeUsers;
    
    // Message statistics
    private Long messagesLast24Hours;
    private Long messagesLastWeek;
    private Long messagesLastMonth;
    private Double averageMessagesPerRoom;
    private Double averageMessagesPerUser;
    
    // Response metrics
    private Double averageResponseTime; // in minutes
    private Long totalUnreadMessages;
    
    // Admin statistics
    private Long totalAdminMessages;
    private Long reportedMessages;
    private Long resolvedReports;
    private Long pendingReports;
    
    // Activity timing
    private LocalDateTime mostActiveHour;
    private LocalDateTime mostActiveDay;
    
    // Chat health metrics
    private Double averageSessionLength; // in minutes
    private Double messageDeliverySuccess; // percentage
} 