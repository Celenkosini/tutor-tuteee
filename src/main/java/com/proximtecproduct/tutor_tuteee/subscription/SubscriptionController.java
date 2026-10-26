package com.proximtecproduct.tutor_tuteee.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/upgrade")
    public ResponseEntity<String> upgrade(@RequestBody UpgradeSubscription request) {
        return ResponseEntity.ok(subscriptionService.upgradeSubscription(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Subscription> getUserSubscription(@PathVariable Long userId) {
        return ResponseEntity.ok(subscriptionService.getUserSubscription(userId));
    }
}
