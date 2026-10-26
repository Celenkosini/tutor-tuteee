package com.proximtecproduct.tutor_tuteee.subscription;

import com.proximtecproduct.tutor_tuteee.auth.SubscriptionTier;
import com.proximtecproduct.tutor_tuteee.user.User;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setSubscriptionTier(SubscriptionTier.FREE);
    }

    @Test
    void testUpgradeSubscription_NewSubscription() {
        UpgradeSubscription request = new UpgradeSubscription();
        request.setUserId(1L);
        request.setNewTier(com.proximtecproduct.tutor_tuteee.subscription.SubscriptionTier.PREMIUM);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.save(any(User.class))).thenReturn(user);

        String result = subscriptionService.upgradeSubscription(request);

        assertEquals("Subscription upgraded to PREMIUM", result);
        assertEquals(SubscriptionTier.PREMIUM, user.getSubscriptionTier());

        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetUserSubscription_Found() {
        Subscription subscription = Subscription.builder()
                .user(user)
                .tier(com.proximtecproduct.tutor_tuteee.subscription.SubscriptionTier.ENTRY)
                .paymentStatus(SubscriptionPaymentStatus.PAID)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findByUser(user)).thenReturn(Optional.of(subscription));

        Subscription result = subscriptionService.getUserSubscription(1L);

        assertEquals(com.proximtecproduct.tutor_tuteee.subscription.SubscriptionTier.ENTRY, result.getTier());
    }

    @Test
    void testGetUserSubscription_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                subscriptionService.getUserSubscription(1L)
        );

        assertEquals("User not found", ex.getMessage());
    }
}
