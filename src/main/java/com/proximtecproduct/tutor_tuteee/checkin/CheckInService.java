package com.proximtecproduct.tutor_tuteee.checkin;

import com.proximtecproduct.tutor_tuteee.session.Session;
import com.proximtecproduct.tutor_tuteee.session.SessionRepository;
import com.proximtecproduct.tutor_tuteee.user.User;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public CheckIn checkIn(CheckInRequest request) {
        Session session = sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        CheckIn checkIn = CheckIn.builder()
                .session(session)
                .user(user)
                .checkInTime(LocalDateTime.now())
                .build();

        return checkInRepository.save(checkIn);
    }

    public List<CheckIn> getCheckInsBySession(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        return checkInRepository.findBySession(session);
    }

    public List<CheckIn> getCheckInsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return checkInRepository.findByUser(user);
    }
}
