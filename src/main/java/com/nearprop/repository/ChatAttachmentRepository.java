package com.nearprop.repository;

import com.nearprop.entity.ChatAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatAttachmentRepository extends JpaRepository<ChatAttachment, Long> {
    
    List<ChatAttachment> findByMessageId(Long messageId);
    
    @Query("SELECT a FROM ChatAttachment a WHERE a.message.chatRoom.id = :chatRoomId AND a.type = 'IMAGE' ORDER BY a.createdAt DESC")
    List<ChatAttachment> findImagesByChatRoomId(Long chatRoomId);
    
    @Query("SELECT a FROM ChatAttachment a WHERE a.message.sender.id = :userId ORDER BY a.createdAt DESC")
    List<ChatAttachment> findByUserId(Long userId);
} 