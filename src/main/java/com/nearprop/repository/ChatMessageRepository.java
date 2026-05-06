package com.nearprop.repository;

import com.nearprop.entity.ChatMessage;
import com.nearprop.entity.ChatMessage.MessageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    Page<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId, Pageable pageable);
    
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatRoomId);
    
    Page<ChatMessage> findByChatRoomIdAndParentMessageIsNullOrderByCreatedAtAsc(Long chatRoomId, Pageable pageable);
    
    List<ChatMessage> findByChatRoomIdAndParentMessageIdIsNullOrderByCreatedAtAsc(Long chatRoomId);
    
    List<ChatMessage> findByParentMessageId(Long parentMessageId);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId AND m.createdAt > :since ORDER BY m.createdAt ASC")
    List<ChatMessage> findByChatRoomIdAndCreatedAtAfter(Long chatRoomId, LocalDateTime since);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId AND m.status = 'SENT' AND m.sender.id != :userId")
    Long countUnreadMessages(Long chatRoomId, Long userId);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId AND m.status = 'SENT' AND m.sender.id != :userId")
    List<ChatMessage> findUnreadMessages(Long chatRoomId, Long userId);
    
    @Modifying
    @Query("UPDATE ChatMessage m SET m.status = :status, m.deliveredAt = CASE WHEN :status = 'DELIVERED' THEN CURRENT_TIMESTAMP ELSE m.deliveredAt END, m.readAt = CASE WHEN :status = 'READ' THEN CURRENT_TIMESTAMP ELSE m.readAt END WHERE m.id = :messageId")
    void updateMessageStatus(Long messageId, MessageStatus status);
    
    @Modifying
    @Query("UPDATE ChatMessage m SET m.status = :status, m.deliveredAt = CASE WHEN :status = 'DELIVERED' THEN CURRENT_TIMESTAMP ELSE m.deliveredAt END, m.readAt = CASE WHEN :status = 'READ' THEN CURRENT_TIMESTAMP ELSE m.readAt END WHERE m.chatRoom.id = :chatRoomId AND m.sender.id != :userId AND m.status != 'READ'")
    int updateStatusForAllMessages(Long chatRoomId, Long userId, MessageStatus status);
    
    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId")
    Long countByChatRoomId(Long chatRoomId);
    
    @Query("SELECT m FROM ChatMessage m WHERE m.chatRoom.id = :chatRoomId ORDER BY m.createdAt DESC")
    Page<ChatMessage> findLatestMessages(Long chatRoomId, Pageable pageable);
    
    Page<ChatMessage> findByStatus(MessageStatus status, Pageable pageable);
    
    List<ChatMessage> findByStatusOrderByCreatedAtDesc(MessageStatus status);
    
    // Search by content
    Page<ChatMessage> findByContentContainingIgnoreCase(String query, Pageable pageable);
    
    // Find messages by date range
    Page<ChatMessage> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    // Find messages by sender
    Page<ChatMessage> findBySenderId(Long senderId, Pageable pageable);
    
    // Find messages by chat room and reported status
    Page<ChatMessage> findByChatRoomIdAndReportedTrue(Long chatRoomId, Pageable pageable);
    
    // Advanced search with multiple criteria
    @Query("SELECT m FROM ChatMessage m WHERE " +
           "(:content IS NULL OR LOWER(m.content) LIKE LOWER(CONCAT('%', :content, '%'))) AND " +
           "(:chatRoomId IS NULL OR m.chatRoom.id = :chatRoomId) AND " +
           "(:senderId IS NULL OR m.sender.id = :senderId) AND " +
           "(:isAdminMessage IS NULL OR m.isAdminMessage = :isAdminMessage) AND " +
           "(:isReported IS NULL OR m.reported = :isReported)")
    Page<ChatMessage> advancedSearch(
        String content, Long chatRoomId, Long senderId, Boolean isAdminMessage, Boolean isReported, 
        Pageable pageable);
} 