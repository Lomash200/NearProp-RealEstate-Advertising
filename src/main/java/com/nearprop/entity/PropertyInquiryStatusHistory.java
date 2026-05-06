package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "property_inquiry_status_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyInquiryStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private PropertyInquiry inquiry;

    @Enumerated(EnumType.STRING)
    private PropertyInquiry.InquiryStatus status;

    @Column(length = 1000)
    private String comment;

    private Long updatedBy;

    @CreationTimestamp
    private LocalDateTime updatedAt;
} 