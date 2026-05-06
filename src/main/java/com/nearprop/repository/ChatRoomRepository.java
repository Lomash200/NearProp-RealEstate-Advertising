package com.nearprop.repository;

import com.nearprop.entity.ChatRoom;
import com.nearprop.entity.Property;
import com.nearprop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    
    Optional<ChatRoom> findByPropertyIdAndBuyerIdAndSellerId(Long propertyId, Long buyerId, Long sellerId);
    
    @Query("SELECT c FROM ChatRoom c WHERE c.buyer.id = :userId OR c.seller.id = :userId ORDER BY c.lastMessageAt DESC NULLS LAST")
    List<ChatRoom> findUserChatRooms(Long userId);
    
    @Query("SELECT c FROM ChatRoom c WHERE c.buyer.id = :userId OR c.seller.id = :userId ORDER BY c.lastMessageAt DESC NULLS LAST")
    Page<ChatRoom> findUserChatRooms(Long userId, Pageable pageable);
    
    @Query("SELECT c FROM ChatRoom c WHERE c.property.id = :propertyId AND (c.buyer.id = :userId OR c.seller.id = :userId)")
    List<ChatRoom> findByPropertyIdAndUserId(Long propertyId, Long userId);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ChatRoom c WHERE c.id = :chatRoomId AND (c.buyer.id = :userId OR c.seller.id = :userId)")
    boolean isUserParticipant(Long chatRoomId, Long userId);
    
    @Query("SELECT c FROM ChatRoom c JOIN c.participants p WHERE p.id = :userId ORDER BY c.lastMessageAt DESC NULLS LAST")
    List<ChatRoom> findChatRoomsByParticipantId(Long userId);
    
    @Query("SELECT COUNT(c) FROM ChatRoom c WHERE c.buyer.id = :buyerId OR c.seller.id = :sellerId")
    Long countByBuyerIdOrSellerId(Long buyerId, Long sellerId);
} 