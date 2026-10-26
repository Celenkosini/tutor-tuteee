package com.proximtecproduct.tutor_tuteee.admin;

import com.proximtecproduct.tutor_tuteee.payment.PaymentRepository;
import com.proximtecproduct.tutor_tuteee.session.SessionRepository;
import com.proximtecproduct.tutor_tuteee.tutor.TutorProfile;
import com.proximtecproduct.tutor_tuteee.tutor.TutorProfileRepository;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import com.proximtecproduct.tutor_tuteee.auth.Role;






@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private TutorProfileRepository tutorRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private PaymentRepository paymentRepository;

    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminService(tutorRepository, userRepository, sessionRepository, paymentRepository);
    }

    @Test
    void getPendingTutors_ShouldReturnUnverifiedTutors() {
        TutorProfile tutor1 = new TutorProfile();
        TutorProfile tutor2 = new TutorProfile();
        List<TutorProfile> expectedTutors = Arrays.asList(tutor1, tutor2);

        when(tutorRepository.findByIsVerifiedFalse()).thenReturn(expectedTutors);

        List<TutorProfile> actualTutors = adminService.getPendingTutors();

        assertThat(actualTutors).isEqualTo(expectedTutors);
        verify(tutorRepository).findByIsVerifiedFalse();
    }

    @Test
    void updateTutorVerification_ShouldUpdateTutorStatus() {
        Long userId = 1L;
        TutorProfile tutor = new TutorProfile();
        AdminTutorVerificationRequest request = new AdminTutorVerificationRequest();
        // If needed, set the verification value via a setter:
        request.setVerified(true);

        when(tutorRepository.findByUserId(userId)).thenReturn(Optional.of(tutor));

        adminService.updateTutorVerification(userId, request);

        assertThat(tutor.isVerified()).isTrue();
        verify(tutorRepository).save(tutor);
    }

    @Test
    void updateTutorVerification_WhenTutorNotFound_ShouldThrowException() {
        Long userId = 1L;
        AdminTutorVerificationRequest request = new AdminTutorVerificationRequest();
        // Set the verified value using a setter if available
        // request.setVerified(true);

        when(tutorRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            adminService.updateTutorVerification(userId, request)
        );
    }

    @Test
    void getMetrics_ShouldReturnCorrectMetrics() {
        when(userRepository.count()).thenReturn(100L);
        when(userRepository.countByRole(Role.TUTOR)).thenReturn(20L);
        when(sessionRepository.count()).thenReturn(50L);
        when(paymentRepository.sumAmountCents()).thenReturn(10000L);
        when(paymentRepository.sumPlatformFeeCents()).thenReturn(1000L);

        AdminMetricsResponse metrics = adminService.getMetrics();

        assertThat(metrics.getTotalUsers()).isEqualTo(100L);
        assertThat(metrics.getTotalTutors()).isEqualTo(20L);
        assertThat(metrics.getTotalSessions()).isEqualTo(50L);
        assertThat(metrics.getTotalRevenueCents()).isEqualTo(10000L);
        assertThat(metrics.getTotalPlatformFeesCents()).isEqualTo(1000L);
    }
}