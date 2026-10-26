package com.proximtecproduct.tutor_tuteee.admin;


import com.proximtecproduct.tutor_tuteee.tutor.TutorProfile;
import com.proximtecproduct.tutor_tuteee.auth.Role;
import com.proximtecproduct.tutor_tuteee.payment.PaymentRepository;
import com.proximtecproduct.tutor_tuteee.session.SessionRepository;
import com.proximtecproduct.tutor_tuteee.tutor.TutorProfileRepository;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final TutorProfileRepository tutorRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PaymentRepository paymentRepository;

    public List<TutorProfile> getPendingTutors() {
        return tutorRepository.findByIsVerifiedFalse();
    }

    @Transactional
    public void updateTutorVerification(Long userId, AdminTutorVerificationRequest request) {
        TutorProfile tutor = tutorRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));
        tutor.setVerified(request.getVerified());
        tutorRepository.save(tutor);
    }

    public AdminMetricsResponse getMetrics() {
        long totalUsers = userRepository.count();
        long totalTutors = userRepository.countByRole(Role.TUTOR);
        long totalSessions = sessionRepository.count();
        long revenue = paymentRepository.sumAmountCents();
        long platformFees = paymentRepository.sumPlatformFeeCents();

        return new AdminMetricsResponse(totalUsers, totalTutors, totalSessions, revenue, platformFees);
    }
}

