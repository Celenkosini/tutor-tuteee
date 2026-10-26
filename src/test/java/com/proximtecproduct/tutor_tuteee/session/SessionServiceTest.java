package com.proximtecproduct.tutor_tuteee.session;

import com.proximtecproduct.tutor_tuteee.user.User;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


import com.proximtecproduct.tutor_tuteee.auth.Role;
import com.proximtecproduct.tutor_tuteee.auth.SubscriptionTier;
import com.proximtecproduct.tutor_tuteee.common.BusinessException;
import com.proximtecproduct.tutor_tuteee.tutor.TutorProfile;
import com.proximtecproduct.tutor_tuteee.tutor.TutorProfileRepository;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;


/**
 * Unit tests for the SessionService class.
 * This class uses Mockito to mock the dependencies and verify business logic.
 */
@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService;

    @Mock
    private SessionRepository sessions;
    @Mock
    private UserRepository users;
    @Mock
    private TutorProfileRepository tutors;

    // Test data
    private User tutee;
    private User tutorUser;
    private TutorProfile tutorProfile;
    private SessionRequest sessionRequest;

    @BeforeEach
    void setUp() {
        tutee = new User();
        tutee.setId(1L);
        tutee.setEmail("tutee@test.com");
        tutee.setRole(Role.TUTEE);
        tutee.setSubscriptionTier(SubscriptionTier.FREE);
        tutee.setName("Tutee User");

        tutorUser = new User();
        tutorUser.setId(2L);
        tutorUser.setEmail("tutor@test.com");
        tutorUser.setRole(Role.TUTOR);
        tutorUser.setName("Tutor User");

        tutorProfile = new TutorProfile();
        tutorProfile.setVerified(true);
        tutorProfile.setHourlyRateCents(30000); // R300

        sessionRequest = new SessionRequest();
        sessionRequest.setTutorId(tutorUser.getId());
        sessionRequest.setSubject("Maths");
        sessionRequest.setScheduledAt(LocalDateTime.now().plusDays(1));
        sessionRequest.setDurationMinutes(60);
    }

    /**
     * Test case for successful session creation with a FREE plan.
     */
    @Test
    void create_withValidRequestAndFreePlan_createsSession() {
        // Given
        when(users.findByEmail(tutee.getEmail())).thenReturn(Optional.of(tutee));
        when(users.findById(tutorUser.getId())).thenReturn(Optional.of(tutorUser));
        when(tutors.findByUserEmail(tutorUser.getEmail())).thenReturn(Optional.of(tutorProfile));
        when(sessions.countCreatedThisMonth(eq(tutee), any())).thenReturn(0L); // No sessions used this month
        
        Session savedSession = Session.builder()
                .id(100L)
                .tutee(tutee)
                .tutor(tutorUser)
                .priceCentsSnapshot(30000)
                .status(SessionStatus.REQUESTED)
                .build();
        when(sessions.save(any(Session.class))).thenReturn(savedSession);

        // When
        SessionDto result = sessionService.create(sessionRequest, tutee.getEmail());

        // Then
        assertNotNull(result);
        assertEquals(SessionStatus.REQUESTED, result.getStatus());
        assertEquals(30000, result.getPriceCents());
        verify(sessions).save(any(Session.class));
    }

    /**
     * Test case for when the monthly booking limit is reached.
     */
    @Test
    void create_withLimitReached_throwsBusinessException() {
        // Given
        tutee.setSubscriptionTier(SubscriptionTier.FREE);
        when(users.findByEmail(tutee.getEmail())).thenReturn(Optional.of(tutee));
        when(users.findById(tutorUser.getId())).thenReturn(Optional.of(tutorUser));
        when(tutors.findByUserEmail(tutorUser.getEmail())).thenReturn(Optional.of(tutorProfile));
        when(sessions.countCreatedThisMonth(eq(tutee), any())).thenReturn(1L); // FREE limit is 1

        // When / Then
        assertThrows(BusinessException.class, () -> sessionService.create(sessionRequest, tutee.getEmail()));
        verify(sessions, never()).save(any(Session.class));
    }

    /**
     * Test case for a user that cannot be found.
     */
    @Test
    void create_withNonexistentTutee_throwsBusinessException() {
        // Given
        when(users.findByEmail(anyString())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessException.class, () -> sessionService.create(sessionRequest, "nonexistent@test.com"));
    }
    
    /**
     * Test case for a tutor that is not verified.
     */
    @Test
    void create_withUnverifiedTutor_throwsBusinessException() {
        // Given
        tutorProfile.setVerified(false);
        when(users.findByEmail(tutee.getEmail())).thenReturn(Optional.of(tutee));
        when(users.findById(tutorUser.getId())).thenReturn(Optional.of(tutorUser));
        when(tutors.findByUserEmail(tutorUser.getEmail())).thenReturn(Optional.of(tutorProfile));

        // When / Then
        assertThrows(BusinessException.class, () -> sessionService.create(sessionRequest, tutee.getEmail()));
    }
    
    /**
     * Test case for a user with the TUTEE role getting their sessions.
     */
    @Test
   void getMine_forTutee_returnsTuteeSessions() {
        // Given
        // Create a proper Session object with initialized tutor and tutee users
        Session mockSession = Session.builder()
            .id(1L)
            .tutor(tutorUser)
            .tutee(tutee)
            .subject("Maths")
            .status(SessionStatus.REQUESTED)
            .build();
        
        when(users.findByEmail(tutee.getEmail())).thenReturn(Optional.of(tutee));
        when(sessions.findByTutee(tutee)).thenReturn(List.of(mockSession));

        // When
        List<SessionDto> result = sessionService.getMine(tutee.getEmail());

        // Then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Tutor User", result.get(0).getTutorName());
        assertEquals("Tutee User", result.get(0).getTuteeName());
        verify(sessions).findByTutee(tutee);
        verify(sessions, never()).findByTutor(any());
        verify(sessions, never()).findAll();
    }
    
    /**
     * Test case for updating a session's status as a tutor.
     */
    @Test
    void updateStatus_asTutor_updatesStatusToApproved() {
        // Given
        User tutor = new User();
        tutor.setId(10L);
        tutor.setEmail("tutor@example.com");
        tutor.setRole(Role.TUTOR);
        
        Session session = new Session();
        session.setId(1L);
        session.setTutor(tutor);
        session.setTutee(tutee);
        session.setStatus(SessionStatus.REQUESTED);

        StatusUpdateDto dto = new StatusUpdateDto();
        dto.setStatus(SessionStatus.APPROVED);
        
        when(users.findByEmail(tutor.getEmail())).thenReturn(Optional.of(tutor));
        when(sessions.findById(1L)).thenReturn(Optional.of(session));

        // When
        sessionService.updateStatus(1L, dto, tutor.getEmail());

        // Then
        assertEquals(SessionStatus.APPROVED, session.getStatus());
        verify(sessions).save(session);
    }
    
    /**
     * Test case for a tutor attempting an invalid status update.
     */
    @Test
   void updateStatus_asTutor_throwsExceptionForInvalidStatus() {
        // Given
        User tutor = new User();
        tutor.setId(10L);
        tutor.setEmail("tutor@example.com");
        tutor.setRole(Role.TUTOR);
        
        Session session = new Session();
        session.setId(1L);
        session.setTutor(tutor);
        
        StatusUpdateDto dto = new StatusUpdateDto();
        dto.setStatus(SessionStatus.CANCELLED); // Tutor cannot cancel
        
        when(users.findByEmail(tutor.getEmail())).thenReturn(Optional.of(tutor));
        when(sessions.findById(1L)).thenReturn(Optional.of(session));
        
        // When / Then
        assertThrows(BusinessException.class, () -> sessionService.updateStatus(1L, dto, tutor.getEmail()));
    }
}