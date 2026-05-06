package com.nearprop.service.impl;

import com.nearprop.dto.PropertySummaryDto;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.dto.chat.*;
import com.nearprop.entity.*;
import com.nearprop.entity.ChatAttachment.AttachmentType;
import com.nearprop.entity.ChatMessage.MessageStatus;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.exception.UnauthorizedException;
import com.nearprop.repository.*;
import com.nearprop.service.ChatService;
import com.nearprop.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatServiceImpl implements ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatAttachmentRepository chatAttachmentRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final FileStorageService fileStorageService;
    private final MessageReportRepository messageReportRepository;

    @Override
    @Transactional
    public ChatRoomDto createChatRoom(CreateChatRoomRequest request, Long currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
                
        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + request.getPropertyId()));
                
        // Determine buyer and seller
        User buyer = currentUser;
        User seller;
        
        if (request.getSellerId() != null) {
            seller = userRepository.findById(request.getSellerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + request.getSellerId()));
        } else {
            seller = property.getOwner();
        }
        
        // Check if chat room already exists
        ChatRoom existingChatRoom = chatRoomRepository
                .findByPropertyIdAndBuyerIdAndSellerId(property.getId(), buyer.getId(), seller.getId())
                .orElse(null);
                
        if (existingChatRoom != null) {
            return mapToChatRoomDto(existingChatRoom, currentUserId);
        }
        
        // Create new chat room
        String title = request.getTitle();
        if (title == null || title.isBlank()) {
            title = property.getTitle();
        }
        
        ChatRoom chatRoom = ChatRoom.builder()
                .property(property)
                .buyer(buyer)
                .seller(seller)
                .title(title)
                .build();
                
        // Add participants
        chatRoom.addParticipant(buyer);
        chatRoom.addParticipant(seller);
        
        ChatRoom savedChatRoom = chatRoomRepository.save(chatRoom);
        
        return mapToChatRoomDto(savedChatRoom, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "chat-rooms", key = "#chatRoomId + '_' + #currentUserId")
    public ChatRoomDto getChatRoom(Long chatRoomId, Long currentUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        // Check if user is a participant
        if (!chatRoom.isParticipant(User.builder().id(currentUserId).build())) {
            throw new UnauthorizedException("You are not authorized to access this chat room");
        }
        
        return mapToChatRoomDto(chatRoom, currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "chat-rooms", key = "'user_' + #userId")
    public List<ChatRoomDto> getUserChatRooms(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findUserChatRooms(userId);
        return chatRooms.stream()
                .map(chatRoom -> mapToChatRoomDto(chatRoom, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatRoomDto> getUserChatRooms(Long userId, Pageable pageable) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findUserChatRooms(userId, pageable);
        return chatRooms.map(chatRoom -> mapToChatRoomDto(chatRoom, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDto> getPropertyChatRooms(Long propertyId, Long currentUserId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByPropertyIdAndUserId(propertyId, currentUserId);
        return chatRooms.stream()
                .map(chatRoom -> mapToChatRoomDto(chatRoom, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatMessageDto sendMessage(Long chatRoomId, SendMessageRequest request, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + senderId));
                
        // Check if user is a participant
        if (!chatRoom.isParticipant(sender)) {
            throw new UnauthorizedException("You are not authorized to send messages in this chat room");
        }
        
        // Create and save message
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(request.getContent())
                .status(MessageStatus.SENT)
                .build();
                
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        // Add attachments if any
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            List<ChatAttachment> attachments = chatAttachmentRepository.findAllById(request.getAttachmentIds());
            for (ChatAttachment attachment : attachments) {
                attachment.setMessage(savedMessage);
                savedMessage.getAttachments().add(attachment);
            }
            savedMessage = chatMessageRepository.save(savedMessage);
        }
        
        // Update chat room last message timestamp
        chatRoom.setLastMessageAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
        
        return mapToChatMessageDto(savedMessage, senderId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDto> getChatMessages(Long chatRoomId, Long currentUserId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        // Check if user is a participant
        if (!chatRoom.isParticipant(User.builder().id(currentUserId).build())) {
            throw new UnauthorizedException("You are not authorized to view messages in this chat room");
        }
        
        Page<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId, pageable);
        return messages.map(message -> mapToChatMessageDto(message, currentUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getRecentMessages(Long chatRoomId, Long currentUserId, int limit) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        // Check if user is a participant
        if (!chatRoom.isParticipant(User.builder().id(currentUserId).build())) {
            throw new UnauthorizedException("You are not authorized to view messages in this chat room");
        }
        
        Page<ChatMessage> messages = chatMessageRepository.findLatestMessages(chatRoomId, PageRequest.of(0, limit));
        return messages.map(message -> mapToChatMessageDto(message, currentUserId)).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getUnreadMessages(Long chatRoomId, Long currentUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        // Check if user is a participant
        if (!chatRoom.isParticipant(User.builder().id(currentUserId).build())) {
            throw new UnauthorizedException("You are not authorized to view messages in this chat room");
        }
        
        List<ChatMessage> unreadMessages = chatMessageRepository.findUnreadMessages(chatRoomId, currentUserId);
        return unreadMessages.stream()
                .map(message -> mapToChatMessageDto(message, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public int markMessagesAsRead(Long chatRoomId, Long currentUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        // Check if user is a participant
        if (!chatRoom.isParticipant(User.builder().id(currentUserId).build())) {
            throw new UnauthorizedException("You are not authorized to update messages in this chat room");
        }
        
        return chatMessageRepository.updateStatusForAllMessages(chatRoomId, currentUserId, MessageStatus.READ);
    }

    @Override
    @Transactional
    public ChatMessageDto updateMessageStatus(Long messageId, MessageStatus status, Long currentUserId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
                
        // Check if user is a participant in the chat room
        if (!message.getChatRoom().isParticipant(User.builder().id(currentUserId).build())) {
            throw new UnauthorizedException("You are not authorized to update this message");
        }
        
        chatMessageRepository.updateMessageStatus(messageId, status);
        
        // Reload the message to get the updated status
        message = chatMessageRepository.findById(messageId).orElseThrow();
        
        return mapToChatMessageDto(message, currentUserId);
    }

    @Override
    @Transactional
    public ChatAttachmentDto uploadAttachment(Long chatRoomId, MultipartFile file, Long currentUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        // Check if user is a participant
        if (!chatRoom.isParticipant(User.builder().id(currentUserId).build())) {
            throw new UnauthorizedException("You are not authorized to upload attachments in this chat room");
        }
        
        try {
            String contentType = file.getContentType();
            AttachmentType type = determineAttachmentType(contentType);
            
            // Upload file to storage service
            String fileName = file.getOriginalFilename();
            String fileUrl = fileStorageService.storeFile(file, "chat-attachments");
            
            // Create attachment entity (without message initially, will be linked when sending message)
            ChatAttachment attachment = ChatAttachment.builder()
                    .fileName(fileName)
                    .fileUrl(fileUrl)
                    .contentType(contentType)
                    .fileSize(file.getSize())
                    .type(type)
                    .build();
                    
            // For images, generate thumbnail and get dimensions
            if (type == AttachmentType.IMAGE) {
                // Generate thumbnail (implementation depends on your file storage service)
                String thumbnailUrl = fileStorageService.generateThumbnail(fileUrl);
                attachment.setThumbnailUrl(thumbnailUrl);
                
                // In a real implementation, you would get image dimensions here
                attachment.setWidth(800); // Example placeholder
                attachment.setHeight(600); // Example placeholder
            }
            
            ChatAttachment savedAttachment = chatAttachmentRepository.save(attachment);
            return mapToChatAttachmentDto(savedAttachment);
            
        } catch (IOException e) {
            log.error("Failed to upload attachment", e);
            throw new RuntimeException("Failed to upload attachment: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatAttachmentDto> getChatAttachments(Long chatRoomId, Long currentUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        // Check if user is a participant
        if (!chatRoom.isParticipant(User.builder().id(currentUserId).build())) {
            throw new UnauthorizedException("You are not authorized to view attachments in this chat room");
        }
        
        List<ChatAttachment> attachments = chatAttachmentRepository.findImagesByChatRoomId(chatRoomId);
        return attachments.stream()
                .map(this::mapToChatAttachmentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAttachment(Long attachmentId, Long currentUserId) {
        ChatAttachment attachment = chatAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + attachmentId));
                
        // Check if user is a participant in the chat room
        ChatRoom chatRoom = attachment.getChatRoom();
        if (!chatRoom.isParticipant(User.builder().id(currentUserId).build())) {
            throw new UnauthorizedException("You are not authorized to delete this attachment");
        }
        
        // Check if user is the uploader
        if (!attachment.getUploader().getId().equals(currentUserId)) {
            throw new UnauthorizedException("You can only delete attachments you uploaded");
        }
        
        // Delete the file
        try {
            fileStorageService.deleteFile(attachment.getFilePath());
            if (attachment.getThumbnailPath() != null) {
                fileStorageService.deleteFile(attachment.getThumbnailPath());
            }
        } catch (IOException e) {
            log.error("Error deleting attachment file: {}", e.getMessage());
        }
        
        // Delete from database
        chatAttachmentRepository.delete(attachment);
    }
    
    // Helper methods
    private ChatRoomDto mapToChatRoomDto(ChatRoom chatRoom, Long currentUserId) {
        // Count unread messages
        long unreadCount = chatMessageRepository.countUnreadMessages(chatRoom.getId(), currentUserId);
        
        // Get last message if any
        List<ChatMessage> recentMessages = chatMessageRepository.findLatestMessages(chatRoom.getId(), PageRequest.of(0, 1)).getContent();
        ChatMessageDto lastMessage = recentMessages.isEmpty() ? null : 
                                    mapToChatMessageDto(recentMessages.get(0), currentUserId);
        
        return ChatRoomDto.builder()
                .id(chatRoom.getId())
                .title(chatRoom.getTitle())
                .property(mapToPropertySummaryDto(chatRoom.getProperty()))
                .buyer(mapToUserSummaryDto(chatRoom.getBuyer()))
                .seller(mapToUserSummaryDto(chatRoom.getSeller()))
                .createdAt(chatRoom.getCreatedAt())
                .lastMessageAt(chatRoom.getLastMessageAt())
                .lastMessage(lastMessage)
                .unreadCount(unreadCount)
                .isParticipant(chatRoom.isParticipant(User.builder().id(currentUserId).build()))
                .build();
    }
    
    private ChatMessageDto mapToChatMessageDto(ChatMessage message, Long currentUserId) {
        return ChatMessageDto.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .sender(mapToUserSummaryDto(message.getSender()))
                .content(message.getContent())
                .status(message.getStatus())
                .createdAt(message.getCreatedAt())
                .deliveredAt(message.getDeliveredAt())
                .readAt(message.getReadAt())
                .isMine(message.getSender().getId().equals(currentUserId))
                .attachments(message.getAttachments().stream()
                        .map(this::mapToChatAttachmentDto)
                        .collect(Collectors.toList()))
                .build();
    }
    
    private ChatAttachmentDto mapToChatAttachmentDto(ChatAttachment attachment) {
        return ChatAttachmentDto.builder()
                .id(attachment.getId())
                .messageId(attachment.getMessage() != null ? attachment.getMessage().getId() : null)
                .fileName(attachment.getFileName())
                .fileUrl(attachment.getFileUrl())
                .contentType(attachment.getContentType())
                .fileSize(attachment.getFileSize())
                .type(attachment.getType())
                .width(attachment.getWidth())
                .height(attachment.getHeight())
                .thumbnailUrl(attachment.getThumbnailUrl())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
    
    private PropertySummaryDto mapToPropertySummaryDto(Property property) {
        String thumbnail = property.getImages() != null && !property.getImages().isEmpty() 
                ? property.getImages().get(0) 
                : null;
                
        return PropertySummaryDto.builder()
                .id(property.getId())
                .title(property.getTitle())
                .address(property.getAddress())
                .district(property.getDistrictName())
                .type(property.getType())
                .status(property.getStatus())
                .price(property.getPrice())
                .thumbnail(thumbnail)
                .build();
    }
    
    private UserSummaryDto mapToUserSummaryDto(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getMobileNumber())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }
    
    private AttachmentType determineAttachmentType(String contentType) {
        if (contentType == null) {
            return AttachmentType.DOCUMENT;
        }
        
        if (contentType.startsWith("image/")) {
            return AttachmentType.IMAGE;
        } else if (contentType.startsWith("video/")) {
            return AttachmentType.VIDEO;
        } else if (contentType.startsWith("audio/")) {
            return AttachmentType.AUDIO;
        } else {
            return AttachmentType.DOCUMENT;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatRoomDto> getAllChatRooms(Pageable pageable) {
        Page<ChatRoom> chatRooms = chatRoomRepository.findAll(pageable);
        return chatRooms.map(chatRoom -> mapToChatRoomDto(chatRoom, null));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChatRoomDto> getAllChatRoomsWithoutPagination(String sortBy, Sort.Direction direction) {
        Sort sort = Sort.by(direction, sortBy);
        List<ChatRoom> chatRooms = chatRoomRepository.findAll(sort);
        return chatRooms.stream()
                .map(chatRoom -> mapToChatRoomDto(chatRoom, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatRoomDto addParticipantToChatRoom(Long chatRoomId, Long userId, Long currentUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
                
        // Check if the current user is a participant or admin
        boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);
                
        if (!isAdmin && !chatRoom.isParticipant(currentUser)) {
            throw new UnauthorizedException("You are not authorized to add participants to this chat room");
        }
        
        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                
        // Add participant if not already in the room
        if (!chatRoom.isParticipant(userToAdd)) {
            chatRoom.addParticipant(userToAdd);
            chatRoom = chatRoomRepository.save(chatRoom);
        }
        
        return mapToChatRoomDto(chatRoom, currentUserId);
    }

    @Override
    @Transactional
    public ChatRoomDto removeParticipantFromChatRoom(Long chatRoomId, Long userId, Long currentUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
                
        // Only admins can remove participants
        boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);
                
        if (!isAdmin) {
            throw new UnauthorizedException("Only administrators can remove participants from chat rooms");
        }
        
        User userToRemove = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                
        // Cannot remove the buyer or seller
        if (chatRoom.getBuyer().getId().equals(userId) || chatRoom.getSeller().getId().equals(userId)) {
            throw new IllegalArgumentException("Cannot remove the buyer or seller from the chat room");
        }
        
        // Remove participant
        chatRoom.removeParticipant(userToRemove);
        chatRoom = chatRoomRepository.save(chatRoom);
        
        return mapToChatRoomDto(chatRoom, currentUserId);
    }

    @Override
    @Transactional
    public ChatRoomDto updateChatRoomStatus(Long chatRoomId, String status, Long currentUserId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
                
        // Check if the current user is the seller, buyer, or admin
        boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);
        boolean isSeller = chatRoom.getSeller().getId().equals(currentUserId);
        
        if (!isAdmin && !isSeller) {
            throw new UnauthorizedException("Only the seller or an administrator can update the chat room status");
        }
        
        // Update status
        chatRoom.setStatus(status);
        chatRoom = chatRoomRepository.save(chatRoom);
        
        return mapToChatRoomDto(chatRoom, currentUserId);
    }

    @Override
    @Transactional
    public ChatMessageDto sendAdminMessage(Long chatRoomId, SendMessageRequest request, Long adminId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + adminId));
                
        // Log all roles for debugging
        System.out.println("DEBUG: User ID: " + adminId + " has roles: " + admin.getRoles());
        
        for (Role role : admin.getRoles()) {
            System.out.println("DEBUG: Role: " + role + " equals ADMIN: " + (role == Role.ADMIN));
            System.out.println("DEBUG: Role name: " + role.getName());
        }
        
        // Check if the user is an admin
        boolean isAdmin = admin.getRoles().contains(Role.ADMIN);
        System.out.println("DEBUG: User isAdmin check result: " + isAdmin);
                
        if (!isAdmin) {
            throw new UnauthorizedException("Only administrators can send admin messages");
        }
        
        // Add admin as participant if not already in the room
        if (!chatRoom.isParticipant(admin)) {
            chatRoom.addParticipant(admin);
            chatRoom = chatRoomRepository.save(chatRoom);
        }
        
        // Create and save message
        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(admin)
                .content(request.getContent())
                .status(MessageStatus.SENT)
                .isAdminMessage(true)
                .build();
                
        // Handle parent message if it's a reply
        if (request.getParentMessageId() != null) {
            ChatMessage parentMessage = chatMessageRepository.findById(request.getParentMessageId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent message not found with id: " + request.getParentMessageId()));
            message.setParentMessage(parentMessage);
        }
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        // Add attachments if any
        if (request.getAttachmentIds() != null && !request.getAttachmentIds().isEmpty()) {
            List<ChatAttachment> attachments = chatAttachmentRepository.findAllById(request.getAttachmentIds());
            for (ChatAttachment attachment : attachments) {
                attachment.setMessage(savedMessage);
                savedMessage.getAttachments().add(attachment);
            }
            savedMessage = chatMessageRepository.save(savedMessage);
        }
        
        // Update chat room last message timestamp
        chatRoom.setLastMessageAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);
        
        return mapToChatMessageDto(savedMessage, adminId);
    }

    @Override
    @Transactional(readOnly = true)
    public MessageThreadDto getMessageThread(Long messageId, Long currentUserId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
                
        // Check if user is a participant in the chat room
        if (!message.getChatRoom().isParticipant(User.builder().id(currentUserId).build())) {
            throw new UnauthorizedException("You are not authorized to view this message thread");
        }
        
        // Get all replies to this message
        List<ChatMessage> replies = chatMessageRepository.findByParentMessageId(messageId);
        
        return MessageThreadDto.builder()
                .message(mapToChatMessageDto(message, currentUserId))
                .replies(replies.stream()
                        .map(reply -> mapToChatMessageDto(reply, currentUserId))
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDto> getChatMessages(Long chatRoomId, Long currentUserId, Pageable pageable, boolean includeReplies) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        // Check if user is a participant
        if (!chatRoom.isParticipant(User.builder().id(currentUserId).build())) {
            throw new UnauthorizedException("You are not authorized to view messages in this chat room");
        }
        
        Page<ChatMessage> messages;
        if (includeReplies) {
            messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId, pageable);
        } else {
            messages = chatMessageRepository.findByChatRoomIdAndParentMessageIsNullOrderByCreatedAtAsc(chatRoomId, pageable);
        }
        
        return messages.map(message -> mapToChatMessageDto(message, currentUserId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getAllChatMessagesWithoutPagination(Long chatRoomId, Long currentUserId, boolean includeReplies) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat room not found with id: " + chatRoomId));
                
        // Check if user is a participant
        if (!chatRoom.isParticipant(User.builder().id(currentUserId).build())) {
            throw new UnauthorizedException("You are not authorized to view messages in this chat room");
        }
        
        List<ChatMessage> messages;
        if (includeReplies) {
            messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId);
        } else {
            messages = chatMessageRepository.findByChatRoomIdAndParentMessageIdIsNullOrderByCreatedAtAsc(chatRoomId);
        }
        
        return messages.stream()
                .map(message -> mapToChatMessageDto(message, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatMessageDto editMessage(Long messageId, String content, Long currentUserId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
                
        // Check if user is the sender of the message
        if (!message.getSender().getId().equals(currentUserId)) {
            throw new UnauthorizedException("You can only edit your own messages");
        }
        
        // Check if message is older than 24 hours (optional: you can add a time limit for editing)
        if (message.getCreatedAt().plusHours(24).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Messages can only be edited within 24 hours of sending");
        }
        
        message.setContent(content);
        message.setEdited(true);
        message.setEditedAt(LocalDateTime.now());
        
        ChatMessage updatedMessage = chatMessageRepository.save(message);
        return mapToChatMessageDto(updatedMessage, currentUserId);
    }

    @Override
    @Transactional
    public void deleteMessage(Long messageId, Long currentUserId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
                
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + currentUserId));
                
        // Check if user is the sender of the message or an admin
        boolean isAdmin = user.getRoles().contains(Role.ADMIN);
                
        if (!message.getSender().getId().equals(currentUserId) && !isAdmin) {
            throw new UnauthorizedException("You can only delete your own messages");
        }
        
        // Delete any attachments
        for (ChatAttachment attachment : message.getAttachments()) {
            try {
                fileStorageService.deleteFile(attachment.getFilePath());
                if (attachment.getThumbnailPath() != null) {
                    fileStorageService.deleteFile(attachment.getThumbnailPath());
                }
            } catch (IOException e) {
                log.error("Error deleting attachment file: {}", e.getMessage());
            }
        }
        
        // Delete the message
        chatMessageRepository.delete(message);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDto> getAllMessages(Pageable pageable, String status) {
        Page<ChatMessage> messages;
        
        if (status != null && !status.isEmpty()) {
            messages = chatMessageRepository.findByStatus(MessageStatus.valueOf(status), pageable);
        } else {
            messages = chatMessageRepository.findAll(pageable);
        }
        
        return messages.map(message -> mapToChatMessageDto(message, null));
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDto> getAllMessagesWithoutPagination(String status) {
        List<ChatMessage> messages;
        if (status != null && !status.isEmpty()) {
            MessageStatus messageStatus = MessageStatus.valueOf(status.toUpperCase());
            messages = chatMessageRepository.findByStatusOrderByCreatedAtDesc(messageStatus);
        } else {
            messages = chatMessageRepository.findAll();
        }
        return messages.stream()
                .map(message -> mapToChatMessageDto(message, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long reportMessage(Long messageId, ReportMessageRequest request, Long reporterId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
                
        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + reporterId));
                
        // Check if user is a participant in the chat room
        if (!message.getChatRoom().isParticipant(reporter)) {
            throw new UnauthorizedException("You are not authorized to report this message");
        }
        
        // Create a message report record (assuming you have a MessageReport entity)
        MessageReport report = MessageReport.builder()
                .message(message)
                .reporter(reporter)
                .reason(request.getReason())
                .description(request.getDescription())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
                
        MessageReport savedReport = messageReportRepository.save(report);
        
        // Update message status
        message.setReported(true);
        chatMessageRepository.save(message);
        
        return savedReport.getId();
    }

    @Override
    @Transactional
    public void processReportedMessage(Long messageId, ProcessReportRequest request, Long adminId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
                
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + adminId));
                
        // Check if user is an admin
        boolean isAdmin = admin.getRoles().contains(Role.ADMIN);
                
        if (!isAdmin) {
            throw new UnauthorizedException("Only administrators can process reported messages");
        }
        
        // Update all reports for this message
        List<MessageReport> reports = messageReportRepository.findByMessageId(messageId);
        for (MessageReport report : reports) {
            report.setStatus("PROCESSED");
            report.setAdminNote(request.getNote());
            report.setProcessedBy(admin);
            report.setProcessedAt(LocalDateTime.now());
            messageReportRepository.save(report);
        }
        
        // Handle based on action
        String action = request.getAction();
        if ("REMOVE".equals(action)) {
            // Delete the message
            deleteMessage(messageId, adminId);
        } else if ("WARN".equals(action)) {
            // Mark message as warned
            message.setWarned(true);
            chatMessageRepository.save(message);
        } else {
            // IGNORE action - just mark as processed
            message.setReported(false);
            chatMessageRepository.save(message);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDto> searchMessages(String query, Pageable pageable) {
        // Search in message content
        Page<ChatMessage> messages;
        
        if (query != null && !query.trim().isEmpty()) {
            // Search by content first
            messages = chatMessageRepository.findByContentContainingIgnoreCase(query, pageable);
        } else {
            // If no query, just return all messages
            messages = chatMessageRepository.findAll(pageable);
        }
        
        return messages.map(message -> mapToChatMessageDto(message, null));
    }
} 