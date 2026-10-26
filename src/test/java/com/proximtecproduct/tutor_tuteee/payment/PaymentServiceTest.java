package com.proximtecproduct.tutor_tuteee.payment;

import com.proximtecproduct.tutor_tuteee.common.BusinessException;
import com.proximtecproduct.tutor_tuteee.session.Session;
import com.proximtecproduct.tutor_tuteee.session.SessionRepository;
import com.proximtecproduct.tutor_tuteee.session.SessionStatus;
import com.proximtecproduct.tutor_tuteee.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the PaymentService class.
 * This class uses Mockito to mock dependencies and verify business logic.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository repo;
    @Mock
    private SessionRepository sessions;

    // Test data
    private Session approvedSession;
    private User tutee;
    private User tutor;

    @BeforeEach
    void setUp() {
        tutee = new User();
        tutee.setId(1L);
        tutee.setEmail("tutee@example.com");

        tutor = new User();
        tutor.setId(2L);
        tutor.setEmail("tutor@example.com");

        approvedSession = new Session();
        approvedSession.setId(100L);
        approvedSession.setStatus(SessionStatus.APPROVED);
        approvedSession.setPriceCentsSnapshot(10000); // R100.00
        approvedSession.setTutee(tutee);
        approvedSession.setTutor(tutor);
    }

    /**
     * Test case for creating a payment intent for an approved session.
     */
    @Test
    void intent_withApprovedSession_createsPaymentIntent() {
        // Given
        when(sessions.findById(approvedSession.getId())).thenReturn(Optional.of(approvedSession));
        when(repo.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Payment payment = paymentService.intent(approvedSession.getId(), tutee.getEmail());

        // Then
        assertNotNull(payment);
        assertEquals(approvedSession.getPriceCentsSnapshot().longValue(), payment.getAmountCents());
        assertEquals(500, payment.getPlatformFeeCents(), "Platform fee should be 5% of the total amount, rounded up.");
        assertEquals(PaymentStatus.PENDING, payment.getStatus());
        verify(sessions).findById(approvedSession.getId());
        verify(repo).save(any(Payment.class));
    }

    /**
     * Test case for attempting to create a payment intent for a session that does not exist.
     */
    @Test
    void intent_withNonexistentSession_throwsBusinessException() {
        // Given
        when(sessions.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessException.class, () -> paymentService.intent(999L, "tutee@example.com"));
        verify(repo, never()).save(any(Payment.class));
    }

    /**
     * Test case for attempting to create a payment intent for a session that is not approved.
     */
    @Test
    void intent_withNotApprovedSession_throwsBusinessException() {
        // Given
        Session unapprovedSession = new Session();
        unapprovedSession.setStatus(SessionStatus.REQUESTED);
        when(sessions.findById(unapprovedSession.getId())).thenReturn(Optional.of(unapprovedSession));

        // When / Then
        assertThrows(BusinessException.class, () -> paymentService.intent(unapprovedSession.getId(), "tutee@example.com"));
        verify(repo, never()).save(any(Payment.class));
    }

    /**
     * Test case for successfully capturing a payment.
     */
    @Test
    void capture_withExistingPayment_updatesStatusToCaptured() {
        // Given
        Payment pendingPayment = new Payment();
        pendingPayment.setId(1L);
        pendingPayment.setStatus(PaymentStatus.PENDING);
        
        when(repo.findById(pendingPayment.getId())).thenReturn(Optional.of(pendingPayment));
        when(repo.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Payment capturedPayment = paymentService.capture(pendingPayment.getId());

        // Then
        assertEquals(PaymentStatus.CAPTURED, capturedPayment.getStatus());
        verify(repo).findById(pendingPayment.getId());
        verify(repo).save(pendingPayment);
    }

    /**
     * Test case for attempting to capture a payment that does not exist.
     */
    @Test
    void capture_withNonexistentPayment_throwsBusinessException() {
        // Given
        when(repo.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessException.class, () -> paymentService.capture(999L));
        verify(repo, never()).save(any(Payment.class));
    }
}
