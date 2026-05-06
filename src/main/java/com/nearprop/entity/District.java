package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "districts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"properties"})
@ToString(exclude = {"properties"})
public class District {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String state;
    
    @Column(nullable = false)
    private String city;
    
    private String pincode;
    
    @Column(name = "revenue_share_percentage", nullable = false)
    private BigDecimal revenueSharePercentage;
    
    // Geographical coordinates for the district center
    private BigDecimal latitude;
    private BigDecimal longitude;
    
    // Radius of the district in kilometers
    private Double radiusKm;
    
    @Column(nullable = false)
    private boolean active;
    
    @OneToMany(mappedBy = "district")
    private Set<Property> properties = new HashSet<>();
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
} 