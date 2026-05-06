package com.nearprop.repository;

import com.nearprop.entity.ReelInteraction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReelInteractionRepository extends JpaRepository<ReelInteraction, Long> {
    
    List<ReelInteraction> findByReelIdAndType(Long reelId, ReelInteraction.InteractionType type);
    
    Page<ReelInteraction> findByReelIdAndType(Long reelId, ReelInteraction.InteractionType type, Pageable pageable);
    
    @Query("SELECT COUNT(ri) FROM ReelInteraction ri WHERE ri.reel.id = :reelId AND ri.type = :type")
    Long countByReelIdAndType(@Param("reelId") Long reelId, @Param("type") ReelInteraction.InteractionType type);
    
    Optional<ReelInteraction> findByReelIdAndUserIdAndType(Long reelId, Long userId, ReelInteraction.InteractionType type);
    
    boolean existsByReelIdAndUserIdAndType(Long reelId, Long userId, ReelInteraction.InteractionType type);
    
    void deleteByReelIdAndUserIdAndType(Long reelId, Long userId, ReelInteraction.InteractionType type);
    
    List<ReelInteraction> findByReelId(Long reelId);
    
    @Query("SELECT ri FROM ReelInteraction ri WHERE ri.user.id = :userId AND ri.type = :type")
    Page<ReelInteraction> findByUserIdAndType(@Param("userId") Long userId, @Param("type") ReelInteraction.InteractionType type, Pageable pageable);
    
    @Query("SELECT ri.reel FROM ReelInteraction ri WHERE ri.user.id = :userId AND ri.type = 'SAVE'")
    Page<ReelInteraction> findSavedReelsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT COUNT(ri) FROM ReelInteraction ri WHERE ri.reel.id = :reelId AND ri.type = 'SAVE'")
    Long countSavesByReelId(@Param("reelId") Long reelId);
} 