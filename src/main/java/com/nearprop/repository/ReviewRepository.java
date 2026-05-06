package com.nearprop.repository;

import com.nearprop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByPropertyId(Long propertyId, Pageable pageable);
    
    Page<Review> findByUserId(Long userId, Pageable pageable);
    
    // This method is used to check if a user has already reviewed a property
    // but now we allow multiple reviews from same user
    Optional<Review> findByPropertyIdAndUserId(Long propertyId, Long userId);
    
    // Find all reviews by property and user
    List<Review> findAllByPropertyIdAndUserId(Long propertyId, Long userId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property.id = :propertyId")
    Double getAverageRatingForProperty(Long propertyId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.property.id = :propertyId")
    Long getReviewCountForProperty(Long propertyId);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.user.id = :userId")
    Long countByUserId(Long userId);
} 