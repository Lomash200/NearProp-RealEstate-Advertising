package com.nearprop.service;

import com.nearprop.dto.chat.*;
import com.nearprop.entity.ChatMessage.MessageStatus;
import com.nearprop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatService {
    ChatRoomDto createChatRoom(CreateChatRoomRequest request, Long currentUserId);
    ChatRoomDto getChatRoom(Long chatRoomId, Long currentUserId);
    List<ChatRoomDto> getUserChatRooms(Long userId);
    Page<ChatRoomDto> getUserChatRooms(Long userId, Pageable pageable);
    List<ChatRoomDto> getPropertyChatRooms(Long propertyId, Long currentUserId);
    ChatMessageDto sendMessage(Long chatRoomId, SendMessageRequest request, Long senderId);
    Page<ChatMessageDto> getChatMessages(Long chatRoomId, Long currentUserId, Pageable pageable);
    Page<ChatMessageDto> getChatMessages(Long chatRoomId, Long currentUserId, Pageable pageable, boolean includeReplies);
    List<ChatMessageDto> getAllChatMessagesWithoutPagination(Long chatRoomId, Long currentUserId, boolean includeReplies);
    List<ChatMessageDto> getRecentMessages(Long chatRoomId, Long currentUserId, int limit);
    List<ChatMessageDto> getUnreadMessages(Long chatRoomId, Long currentUserId);
    int markMessagesAsRead(Long chatRoomId, Long currentUserId);
    ChatMessageDto updateMessageStatus(Long messageId, MessageStatus status, Long currentUserId);
    ChatAttachmentDto uploadAttachment(Long chatRoomId, MultipartFile file, Long currentUserId);
    List<ChatAttachmentDto> getChatAttachments(Long chatRoomId, Long currentUserId);
    void deleteAttachment(Long attachmentId, Long currentUserId);
    Page<ChatRoomDto> getAllChatRooms(Pageable pageable);
    List<ChatRoomDto> getAllChatRoomsWithoutPagination(String sortBy, Sort.Direction direction);
    ChatRoomDto addParticipantToChatRoom(Long chatRoomId, Long userId, Long currentUserId);
    ChatRoomDto removeParticipantFromChatRoom(Long chatRoomId, Long userId, Long currentUserId);
    ChatRoomDto updateChatRoomStatus(Long chatRoomId, String status, Long currentUserId);
    ChatMessageDto sendAdminMessage(Long chatRoomId, SendMessageRequest request, Long adminId);
    MessageThreadDto getMessageThread(Long messageId, Long currentUserId);
    ChatMessageDto editMessage(Long messageId, String content, Long currentUserId);
    void deleteMessage(Long messageId, Long currentUserId);
    Page<ChatMessageDto> getAllMessages(Pageable pageable, String status);
    List<ChatMessageDto> getAllMessagesWithoutPagination(String status);
    Long reportMessage(Long messageId, ReportMessageRequest request, Long reporterId);
    void processReportedMessage(Long messageId, ProcessReportRequest request, Long adminId);
    User getUserById(Long userId);
    Page<ChatMessageDto> searchMessages(String query, Pageable pageable);
} 