package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "property_reviews")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private Integer rating;
    
    @Column(length = 1000)
    private String comment;
    
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ReviewLike> likes = new HashSet<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Helper method to add like
    public void addLike(User user) {
        ReviewLike like = new ReviewLike();
        like.setReview(this);
        like.setUser(user);
        likes.add(like);
    }
    
    // Helper method to remove like
    public void removeLike(User user) {
        likes.removeIf(like -> like.getUser().getId().equals(user.getId()));
    }
    
    // Helper method to check if user has liked
    public boolean isLikedBy(User user) {
        return likes.stream()
                .anyMatch(like -> like.getUser().getId().equals(user.getId()));
    }
    
    // Get like count
    public int getLikeCount() {
        return likes.size();
    }
} 