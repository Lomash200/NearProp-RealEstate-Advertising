package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chat_rooms", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"property_id", "buyer_id", "seller_id"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
    
    @Column(nullable = false)
    private String title;
    
    @Column(name = "status")
    @Builder.Default
    private String status = "ACTIVE";
    
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @OrderBy("createdAt ASC")
    private List<ChatMessage> messages = new ArrayList<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    @Column
    private LocalDateTime lastMessageAt;
    
    @ManyToMany
    @JoinTable(
        name = "chat_room_participants",
        joinColumns = @JoinColumn(name = "chat_room_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @Builder.Default
    private Set<User> participants = new HashSet<>();
    
    // Helper methods
    public void addMessage(ChatMessage message) {
        messages.add(message);
        message.setChatRoom(this);
        lastMessageAt = LocalDateTime.now();
    }
    
    public void addParticipant(User user) {
        participants.add(user);
    }
    
    public void removeParticipant(User user) {
        participants.removeIf(participant -> participant.getId().equals(user.getId()));
    }
    
    public boolean isParticipant(User user) {
        if (user == null || user.getId() == null) {
            return false;
        }
        
        for (User participant : participants) {
            if (participant.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }
} 