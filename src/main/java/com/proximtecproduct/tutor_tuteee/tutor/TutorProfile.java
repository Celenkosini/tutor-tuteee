package com.proximtecproduct.tutor_tuteee.tutor;

import com.proximtecproduct.tutor_tuteee.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tutors")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
public class TutorProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name="user_id", unique=true)
    private User user;

    // --- NEW FIELD FOR IMAGE ---
    private String profileImageUrl;
    // ---------------------------

    private String bio;

    @Column(columnDefinition = "TEXT")
    private String subjects; // JSON or CSV

    private Integer hourlyRateCents;
    private String city;
    private String country;

    private boolean isVerified = false;
    private Double ratingAvg = 0.0;
    private Integer ratingCount = 0;

    @Column(columnDefinition = "TEXT")
    private String availabilityJson;
}