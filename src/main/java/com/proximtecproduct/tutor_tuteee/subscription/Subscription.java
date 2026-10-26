package com.proximtecproduct.tutor_tuteee.subscription;

import com.proximtecproduct.tutor_tuteee.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionTier tier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionPaymentStatus paymentStatus;

    @Column(nullable = false)
    private LocalDateTime startDate;

    private LocalDateTime endDate;

    public boolean isActive() {
        return paymentStatus == SubscriptionPaymentStatus.PAID &&
               (endDate == null || endDate.isAfter(LocalDateTime.now()));
    }
}
