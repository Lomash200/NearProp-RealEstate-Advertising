package com.nearprop.repository;

import com.nearprop.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByMobileNumberAndVerifiedFalseAndBlockedFalse(String mobileNumber);
    
    Optional<Otp> findByEmailAndVerifiedFalseAndBlockedFalse(String email);

    @Query("SELECT o FROM Otp o WHERE o.mobileNumber = :identifier AND o.verified = false AND o.blocked = false " +
           "AND o.createdAt > :cutoffTime")
    Optional<Otp> findValidOtpByMobileNumber(String identifier, LocalDateTime cutoffTime);

    @Query("SELECT o FROM Otp o WHERE o.email = :identifier AND o.verified = false AND o.blocked = false " +
           "AND o.createdAt > :cutoffTime")
    Optional<Otp> findValidOtpByEmail(String identifier, LocalDateTime cutoffTime);

    @Modifying
    @Query("DELETE FROM Otp o WHERE o.createdAt < :cutoffTime")
    void deleteByCreatedAtBefore(LocalDateTime cutoffTime);

    @Query(value = "SELECT * FROM otps WHERE mobile_number = :mobileNumber ORDER BY created_at DESC", nativeQuery = true)
    List<Otp> findByMobileNumber(String mobileNumber);
    
    @Query(value = "SELECT * FROM otps WHERE email = :email ORDER BY created_at DESC", nativeQuery = true)
    List<Otp> findByEmail(String email);

    @Query(value = "SELECT * FROM otps WHERE mobile_number = :mobileNumber AND code = :code", nativeQuery = true)
    Optional<Otp> findByMobileNumberAndCode(String mobileNumber, String code);
    
    @Query(value = "SELECT * FROM otps WHERE email = :email AND code = :code", nativeQuery = true)
    Optional<Otp> findByEmailAndCode(String email, String code);
} 