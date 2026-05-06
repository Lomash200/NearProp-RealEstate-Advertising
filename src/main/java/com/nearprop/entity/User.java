package com.nearprop.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@ToString(exclude = {"roles", "password", "favorites"})
@ToString(exclude = {"roles", "password", "favorites", "subscriptions"})

@EqualsAndHashCode(exclude = {"roles", "favorites"}, callSuper = false)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String mobileNumber;

    @Column(unique = true)
    private String email;

    @Column(length = 128)
    private String password;

    @Column(unique = true)
    private String permanentId;
    
    private String address;
    private String district;
    
    @Column(length = 512)
    private String profileImageUrl;



    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    private boolean mobileVerified;
    private boolean emailVerified;
    
    // Aadhaar verification fields for future implementation
    @Column(unique = true)
    private String aadhaarNumber;
    
    private boolean aadhaarVerified;
    
    @Column(length = 512)
    private String aadhaarDocumentUrl;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime lastLoginAt;

    @ManyToMany
    @JoinTable(
        name = "user_favorites",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "property_id")
    )
    @Builder.Default
    @JsonIgnore
    private Set<Property> favorites = new HashSet<>();
    
    private Double latitude;
    private Double longitude;
    private Long districtId;
    
    // Explicitly define getId method
    public Long getId() {
        return this.id;
    }
    
    // Explicitly define getRoles method
    public Set<Role> getRoles() {
        return this.roles;
    }
    
    // Add username method - fall back to email if available, otherwise use mobile number
    public String getUsername() {
        return email != null ? email : mobileNumber;
    }
    
     //Get profile image URL with default if not set
    public String getProfileImageUrl() {
        return profileImageUrl != null ? profileImageUrl :
            "";
    }


    @Column(name = "fcm_token", length = 512)
    private String fcmToken;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions = new ArrayList<>();



} 