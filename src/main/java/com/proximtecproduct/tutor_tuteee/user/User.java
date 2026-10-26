package com.proximtecproduct.tutor_tuteee.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.proximtecproduct.tutor_tuteee.auth.SubscriptionTier;
import com.proximtecproduct.tutor_tuteee.auth.Role;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role role;

    /**
     * Public URL exposed by backend
     * Example:
     * http://localhost:8080/api/users/photos/uuid.jpg
     */
    @Column(length = 500)
    private String profileImageUrl;

    /**
     * Stored filename on disk (optional but recommended)
     * Example:
     * 3f9c2a8d-avatar.jpg
     */
    @Column(length = 255)
    private String profileImageName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SubscriptionTier subscriptionTier = SubscriptionTier.FREE;

    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    @Builder.Default
    private boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
