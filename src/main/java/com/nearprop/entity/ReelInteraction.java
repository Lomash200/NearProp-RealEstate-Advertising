package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "reel_interactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReelInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reel_id", nullable = false)
    private Reel reel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionType type;

    @Column(length = 1000)
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum InteractionType {
        LIKE, COMMENT, FOLLOW, VIEW, SHARE, SAVE
    }
    
    // Custom equals and hashCode to allow multiple comments from same user on same reel
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ReelInteraction that = (ReelInteraction) o;
        
        // For comments, only compare by ID
        if (type == InteractionType.COMMENT) {
            if (id == null) return false;
            return id.equals(that.id);
        }
        
        // For other interactions, keep unique by user and reel
        return reel.getId().equals(that.getReel().getId()) &&
               user.getId().equals(that.getUser().getId()) &&
               type == that.getType();
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        
        // For comments, only use ID
        if (type == InteractionType.COMMENT) {
            return result;
        }
        
        // For other interactions, maintain uniqueness by user, reel, type
        result = 31 * result + reel.getId().hashCode();
        result = 31 * result + user.getId().hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
} 