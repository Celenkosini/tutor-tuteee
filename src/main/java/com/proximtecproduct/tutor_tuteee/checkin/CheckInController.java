package com.proximtecproduct.tutor_tuteee.checkin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
public class CheckInController {

    private final CheckInService checkInService;

    @PostMapping
    public ResponseEntity<CheckIn> createCheckIn(@RequestBody CheckInRequest request) {
        return ResponseEntity.ok(checkInService.checkIn(request));
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<CheckIn>> getBySession(@PathVariable Long sessionId) {
        return ResponseEntity.ok(checkInService.getCheckInsBySession(sessionId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CheckIn>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(checkInService.getCheckInsByUser(userId));
    }
}
