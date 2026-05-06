package com.nearprop.service;

import com.nearprop.dto.chat.ChatAnalyticsDto;
import com.nearprop.dto.chat.ChatMessageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;

public interface ChatAnalyticsService {
    /**
     * Get global chat statistics
     */
    ChatAnalyticsDto getGlobalStatistics();
    
    /**
     * Get daily message counts within a date range
     */
    Map<String, Object> getDailyMessageCounts(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Get top active users by message count
     */
    Map<String, Object> getTopActiveUsers(int limit);
    
    /**
     * Get top active chat rooms by message count
     */
    Map<String, Object> getTopActiveRooms(int limit);
    
    /**
     * Advanced search for messages with multiple criteria
     */
    Page<ChatMessageDto> searchMessages(
            String content, Long chatRoomId, Long userId, Boolean isAdminMessage, Boolean isReported, 
            Pageable pageable);
    
    /**
     * Get statistics on reported messages
     */
    Map<String, Object> getReportedMessageStatistics();
} 