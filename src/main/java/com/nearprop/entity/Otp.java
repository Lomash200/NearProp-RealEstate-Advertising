package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String identifier;

    @Column(nullable = false)
    private String mobileNumber;

    @Column
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OtpType type;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private boolean verified;

    @Column(nullable = false)
    private int attempts;

    @Column(nullable = false)
    private boolean blocked;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime verifiedAt;

    @Column
    private LocalDateTime expiresAt;

    public boolean isValid() {
        return !blocked && attempts < 3 && !verified && 
               expiresAt.isAfter(LocalDateTime.now());
    }

    public void incrementAttempts() {
        attempts++;
        if (attempts >= 3) {
            blocked = true;
        }
    }
} 