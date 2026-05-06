package com.nearprop.service.impl;

import com.nearprop.dto.chat.ChatAnalyticsDto;
import com.nearprop.dto.chat.ChatMessageDto;
import com.nearprop.entity.ChatMessage;
import com.nearprop.entity.ChatRoom;
import com.nearprop.repository.ChatMessageRepository;
import com.nearprop.repository.ChatRoomRepository;
import com.nearprop.repository.MessageReportRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.ChatAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatAnalyticsServiceImpl implements ChatAnalyticsService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final MessageReportRepository messageReportRepository;
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    @Transactional(readOnly = true)
    public ChatAnalyticsDto getGlobalStatistics() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        LocalDateTime lastWeek = now.minusWeeks(1);
        LocalDateTime lastMonth = now.minusMonths(1);
        
        // Get total counts
        Long totalMessages = chatMessageRepository.count();
        Long totalRooms = chatRoomRepository.count();
        
        // Count active rooms (had message in last week)
        int activeRoomsCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT chat_room_id) FROM chat_messages WHERE created_at > ?",
                Integer.class, lastWeek);
        
        // Count active users (sent a message in last week)
        int activeUsersCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT sender_id) FROM chat_messages WHERE created_at > ?",
                Integer.class, lastWeek);
        
        // Messages in different time periods
        int last24HoursMessages = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_messages WHERE created_at > ?",
                Integer.class, yesterday);
                
        int lastWeekMessages = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_messages WHERE created_at > ?",
                Integer.class, lastWeek);
                
        int lastMonthMessages = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_messages WHERE created_at > ?",
                Integer.class, lastMonth);
        
        // Admin statistics
        int totalAdminMessages = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_messages WHERE is_admin_message = true",
                Integer.class);
                
        // Report statistics
        int reportedMessagesCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_messages WHERE is_reported = true",
                Integer.class);
                
        int pendingReportsCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM message_reports WHERE status = 'PENDING'",
                Integer.class);
                
        int resolvedReportsCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM message_reports WHERE status = 'PROCESSED'",
                Integer.class);
        
        // User and room counts
        Long totalUsers = userRepository.count();
        
        // Calculate averages
        double averageMessagesPerRoom = totalRooms > 0 ? (double) totalMessages / totalRooms : 0;
        double averageMessagesPerUser = totalUsers > 0 ? (double) totalMessages / totalUsers : 0;
        
        // Get unread messages
        int totalUnreadMessages = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM chat_messages WHERE status = 'SENT'",
                Integer.class);
        
        // Most active hour and day (simplified approach)
        LocalDateTime mostActiveHour = jdbcTemplate.queryForObject(
                "SELECT date_trunc('hour', created_at) as hour FROM chat_messages " +
                "GROUP BY hour ORDER BY COUNT(*) DESC LIMIT 1",
                LocalDateTime.class);
                
        LocalDateTime mostActiveDay = jdbcTemplate.queryForObject(
                "SELECT date_trunc('day', created_at) as day FROM chat_messages " +
                "GROUP BY day ORDER BY COUNT(*) DESC LIMIT 1",
                LocalDateTime.class);
        
        return ChatAnalyticsDto.builder()
                .totalMessages(totalMessages)
                .totalRooms(totalRooms)
                .activeRooms((long) activeRoomsCount)
                .totalUsers(totalUsers)
                .activeUsers((long) activeUsersCount)
                .messagesLast24Hours((long) last24HoursMessages)
                .messagesLastWeek((long) lastWeekMessages)
                .messagesLastMonth((long) lastMonthMessages)
                .averageMessagesPerRoom(averageMessagesPerRoom)
                .averageMessagesPerUser(averageMessagesPerUser)
                .totalUnreadMessages((long) totalUnreadMessages)
                .totalAdminMessages((long) totalAdminMessages)
                .reportedMessages((long) reportedMessagesCount)
                .pendingReports((long) pendingReportsCount)
                .resolvedReports((long) resolvedReportsCount)
                .mostActiveHour(mostActiveHour)
                .mostActiveDay(mostActiveDay)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDailyMessageCounts(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map<String, Object>> dailyCounts = jdbcTemplate.queryForList(
                "SELECT date_trunc('day', created_at) as day, COUNT(*) as count FROM chat_messages " +
                "WHERE created_at BETWEEN ? AND ? GROUP BY day ORDER BY day",
                startDate, endDate);
                
        // Format dates for JSON serialization
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        List<Map<String, Object>> formattedResults = dailyCounts.stream()
                .map(row -> {
                    Map<String, Object> formattedRow = new HashMap<>();
                    formattedRow.put("date", ((LocalDateTime) row.get("day")).format(formatter));
                    formattedRow.put("count", row.get("count"));
                    return formattedRow;
                })
                .collect(Collectors.toList());
                
        Map<String, Object> result = new HashMap<>();
        result.put("startDate", startDate.format(formatter));
        result.put("endDate", endDate.format(formatter));
        result.put("data", formattedResults);
        result.put("totalMessages", formattedResults.stream()
                .mapToLong(row -> ((Number) row.get("count")).longValue())
                .sum());
                
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTopActiveUsers(int limit) {
        List<Map<String, Object>> topUsers = jdbcTemplate.queryForList(
                "SELECT u.id, u.name, COUNT(m.id) as message_count FROM users u " +
                "JOIN chat_messages m ON u.id = m.sender_id " +
                "GROUP BY u.id, u.name ORDER BY message_count DESC LIMIT ?",
                limit);
                
        Map<String, Object> result = new HashMap<>();
        result.put("limit", limit);
        result.put("users", topUsers);
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getTopActiveRooms(int limit) {
        List<Map<String, Object>> topRooms = jdbcTemplate.queryForList(
                "SELECT cr.id, cr.title, p.id as property_id, p.title as property_title, " + 
                "COUNT(m.id) as message_count FROM chat_rooms cr " +
                "JOIN chat_messages m ON cr.id = m.chat_room_id " +
                "JOIN properties p ON cr.property_id = p.id " +
                "GROUP BY cr.id, cr.title, p.id, p.title ORDER BY message_count DESC LIMIT ?",
                limit);
                
        Map<String, Object> result = new HashMap<>();
        result.put("limit", limit);
        result.put("rooms", topRooms);
        
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDto> searchMessages(String content, Long chatRoomId, Long userId, Boolean isAdminMessage, Boolean isReported, Pageable pageable) {
        // Use advanced search with multiple criteria
        Page<ChatMessage> messages = chatMessageRepository.advancedSearch(
                content, chatRoomId, userId, isAdminMessage, isReported, pageable);
        
        return messages.map(message -> mapToChatMessageDto(message));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getReportedMessageStatistics() {
        // Get report counts by reason
        List<Map<String, Object>> reportsByReason = jdbcTemplate.queryForList(
                "SELECT reason, COUNT(*) as count FROM message_reports GROUP BY reason ORDER BY count DESC");
        
        // Get report counts by status
        List<Map<String, Object>> reportsByStatus = jdbcTemplate.queryForList(
                "SELECT status, COUNT(*) as count FROM message_reports GROUP BY status");
        
        // Get average resolution time
        Double avgResolutionTimeHours = jdbcTemplate.queryForObject(
                "SELECT AVG(EXTRACT(EPOCH FROM (processed_at - created_at))/3600) " +
                "FROM message_reports WHERE processed_at IS NOT NULL",
                Double.class);
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalReports", messageReportRepository.count());
        result.put("reportsByReason", reportsByReason);
        result.put("reportsByStatus", reportsByStatus);
        result.put("averageResolutionTimeHours", avgResolutionTimeHours);
        
        return result;
    }
    
    // Helper method to map ChatMessage entity to ChatMessageDto
    private ChatMessageDto mapToChatMessageDto(ChatMessage message) {
        return ChatMessageDto.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .content(message.getContent())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .deliveredAt(message.getDeliveredAt())
                .readAt(message.getReadAt())
                .isAdminMessage(message.isAdminMessage())
                .edited(message.isEdited())
                .editedAt(message.getEditedAt())
                .reported(message.isReported())
                .warned(message.isWarned())
                .build();
    }
} 