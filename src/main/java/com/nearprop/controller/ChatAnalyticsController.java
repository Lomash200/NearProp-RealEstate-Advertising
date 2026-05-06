package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.chat.ChatAnalyticsDto;
import com.nearprop.dto.chat.ChatMessageDto;
import com.nearprop.service.ChatAnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/chat/analytics")
@RequiredArgsConstructor
@Slf4j
public class ChatAnalyticsController {

    private final ChatAnalyticsService chatAnalyticsService;
    
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ChatAnalyticsDto>> getGlobalStats() {
        ChatAnalyticsDto stats = chatAnalyticsService.getGlobalStatistics();
        return ResponseEntity.ok(ApiResponse.success("Chat analytics retrieved successfully", stats));
    }
    
    @GetMapping("/stats/daily")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDailyMessageCounts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        // Default to last 30 days if not specified
        if (startDate == null) {
            startDate = LocalDateTime.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }
        
        Map<String, Object> dailyStats = chatAnalyticsService.getDailyMessageCounts(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Daily message counts retrieved", dailyStats));
    }
    
    @GetMapping("/stats/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTopActiveUsers(
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> userStats = chatAnalyticsService.getTopActiveUsers(limit);
        return ResponseEntity.ok(ApiResponse.success("Top active users retrieved", userStats));
    }
    
    @GetMapping("/stats/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTopActiveRooms(
            @RequestParam(defaultValue = "10") int limit) {
        
        Map<String, Object> roomStats = chatAnalyticsService.getTopActiveRooms(limit);
        return ResponseEntity.ok(ApiResponse.success("Top active rooms retrieved", roomStats));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ChatMessageDto>> searchMessages(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long chatRoomId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Boolean isAdminMessage,
            @RequestParam(required = false) Boolean isReported,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ChatMessageDto> messages = chatAnalyticsService.searchMessages(
                query, chatRoomId, userId, isAdminMessage, isReported, pageRequest);
        
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/reported/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReportStats() {
        Map<String, Object> reportStats = chatAnalyticsService.getReportedMessageStatistics();
        return ResponseEntity.ok(ApiResponse.success("Reported message statistics retrieved", reportStats));
    }
} 