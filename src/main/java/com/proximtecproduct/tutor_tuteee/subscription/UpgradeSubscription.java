package com.proximtecproduct.tutor_tuteee.subscription;

import lombok.Data;

@Data
public class UpgradeSubscription {
    private Long userId;
    private SubscriptionTier newTier;
}
