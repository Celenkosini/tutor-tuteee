package com.proximtecproduct.tutor_tuteee.subscription;

import com.proximtecproduct.tutor_tuteee.user.User;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public String upgradeSubscription(UpgradeSubscription request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Subscription subscription = subscriptionRepository.findByUser(user)
                .orElse(Subscription.builder()
                        .user(user)
                        .startDate(LocalDateTime.now())
                        .build());

        subscription.setTier(request.getNewTier());
        subscription.setPaymentStatus(SubscriptionPaymentStatus.PAID);
        subscription.setStartDate(LocalDateTime.now());
        subscription.setEndDate(LocalDateTime.now().plusMonths(1));

        subscriptionRepository.save(subscription);

        user.setSubscriptionTier(com.proximtecproduct.tutor_tuteee.auth.SubscriptionTier.valueOf(request.getNewTier().name()));
        userRepository.save(user);

        return "Subscription upgraded to " + request.getNewTier();
    }

    public Subscription getUserSubscription(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Subscription not found"));
    }
}
