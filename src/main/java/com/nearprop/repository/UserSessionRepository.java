package com.nearprop.repository;

import com.nearprop.entity.User;
import com.nearprop.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    List<UserSession> findByUserIdAndActive(Long userId, boolean active);
    
    @Query("SELECT s FROM UserSession s JOIN FETCH s.user WHERE s.sessionId = :sessionId AND s.active = :active")
    Optional<UserSession> findBySessionIdAndActive(String sessionId, boolean active);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false WHERE s.user.id = :userId AND s.sessionId != :currentSessionId")
    void deactivateOtherSessions(Long userId, String currentSessionId);
    
    @Modifying
    @Query("UPDATE UserSession s SET s.active = false WHERE s.expiresAt < :now")
    void deactivateExpiredSessions(LocalDateTime now);
    
    @Query("SELECT s FROM UserSession s WHERE s.user.id = :userId AND s.active = true ORDER BY s.createdAt DESC")
    List<UserSession> findActiveSessionsByUserId(Long userId);

    @Modifying
    @Query("UPDATE UserSession s SET s.active = false WHERE s.user.id = :userId")
    void deactivateAllUserSessions(Long userId);
    
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_sessions WHERE user_id = :userId", nativeQuery = true)
    void deleteAllByUserId(Long userId);

    void deleteByUser(User user);

} 