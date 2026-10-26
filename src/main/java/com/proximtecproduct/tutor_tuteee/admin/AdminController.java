package com.proximtecproduct.tutor_tuteee.admin;



import com.proximtecproduct.tutor_tuteee.tutor.TutorProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // GET /api/admin/tutors/pending
    @GetMapping("/tutors/pending")
    public ResponseEntity<List<TutorProfile>> getPendingTutors() {
        return ResponseEntity.ok(adminService.getPendingTutors());
    }

    // PUT /api/admin/tutors/{userId}/verify
    @PutMapping("/tutors/{userId}/verify")
    public ResponseEntity<Void> verifyTutor(
            @PathVariable Long userId,
            @RequestBody AdminTutorVerificationRequest request
    ) {
        adminService.updateTutorVerification(userId, request);
        return ResponseEntity.ok().build();
    }

    // GET /api/admin/metrics
    @GetMapping("/metrics")
    public ResponseEntity<AdminMetricsResponse> getMetrics() {
        return ResponseEntity.ok(adminService.getMetrics());
    }
}

