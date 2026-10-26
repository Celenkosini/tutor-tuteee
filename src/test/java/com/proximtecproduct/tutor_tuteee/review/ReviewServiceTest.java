package com.proximtecproduct.tutor_tuteee.review;

import com.proximtecproduct.tutor_tuteee.auth.SubscriptionTier;
import com.proximtecproduct.tutor_tuteee.common.BusinessException;
import com.proximtecproduct.tutor_tuteee.session.Session;
import com.proximtecproduct.tutor_tuteee.session.SessionRepository;
import com.proximtecproduct.tutor_tuteee.session.SessionStatus;
import com.proximtecproduct.tutor_tuteee.tutor.TutorProfile;
import com.proximtecproduct.tutor_tuteee.tutor.TutorProfileRepository;
import com.proximtecproduct.tutor_tuteee.user.User;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ReviewService class.
 * This class uses Mockito to mock dependencies and verify business logic.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private ReviewRepository reviews;
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
    private Session completedSession;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        // Mock Tutee and Tutor users
        tutee = new User();
        tutee.setId(1L);
        tutee.setEmail("tutee@example.com");
        tutee.setSubscriptionTier(SubscriptionTier.ENTRY); // An upgrade is required
        
        tutorUser = new User();
        tutorUser.setId(2L);
        tutorUser.setEmail("tutor@example.com");
        
        // Mock Tutor Profile for rating aggregation
        tutorProfile = new TutorProfile();
        tutorProfile.setRatingAvg(4.0);
        tutorProfile.setRatingCount(2);

        // Mock a completed session
        completedSession = new Session();
        completedSession.setId(100L);
        completedSession.setTutee(tutee);
        completedSession.setTutor(tutorUser);
        completedSession.setStatus(SessionStatus.COMPLETED);

        // Mock a review request
        reviewRequest = new ReviewRequest();
        reviewRequest.setSessionId(completedSession.getId());
        reviewRequest.setRating(5);
        reviewRequest.setComment("Great session!");
    }

    /**
     * Test case for a successful review creation and rating aggregation.
     */
    @Test
    void create_withValidRequest_createsReviewAndUpdatesTutorRating() {
        // Given
        when(users.findByEmail(tutee.getEmail())).thenReturn(Optional.of(tutee));
        when(sessions.findById(reviewRequest.getSessionId())).thenReturn(Optional.of(completedSession));
        when(tutors.findByUserEmail(tutorUser.getEmail())).thenReturn(Optional.of(tutorProfile));
        when(reviews.save(any(Review.class))).thenReturn(new Review());
        when(tutors.save(any(TutorProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        reviewService.create(tutee.getEmail(), reviewRequest);

        // Then
        // Verify review was saved with correct details
        ArgumentCaptor<Review> reviewCaptor = ArgumentCaptor.forClass(Review.class);
        verify(reviews).save(reviewCaptor.capture());
        Review savedReview = reviewCaptor.getValue();
        assertEquals(reviewRequest.getRating(), savedReview.getRating());
        assertEquals(reviewRequest.getComment(), savedReview.getComment());
        assertEquals(completedSession.getId(), savedReview.getSession().getId());
        assertEquals(tutee.getId(), savedReview.getTutee().getId());

        // Verify tutor profile was updated with new average and count
        ArgumentCaptor<TutorProfile> profileCaptor = ArgumentCaptor.forClass(TutorProfile.class);
        verify(tutors).save(profileCaptor.capture());
        TutorProfile updatedProfile = profileCaptor.getValue();
        assertEquals(4.333333333333333, updatedProfile.getRatingAvg(), 0.001); // (4*2 + 5)/3
        assertEquals(3, updatedProfile.getRatingCount());
    }

    /**
     * Test case for creating a review with a FREE subscription tier.
     */
    @Test
    void create_withFreeSubscription_throwsBusinessException() {
        // Given
        tutee.setSubscriptionTier(SubscriptionTier.FREE);
        when(users.findByEmail(tutee.getEmail())).thenReturn(Optional.of(tutee));

        // When / Then
        assertThrows(BusinessException.class, () -> reviewService.create(tutee.getEmail(), reviewRequest));
        verify(reviews, never()).save(any(Review.class));
    }

    /**
     * Test case for creating a review for a non-existent session.
     */
    @Test
    void create_withNonexistentSession_throwsBusinessException() {
        // Given
        when(users.findByEmail(tutee.getEmail())).thenReturn(Optional.of(tutee));
        when(sessions.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessException.class, () -> reviewService.create(tutee.getEmail(), reviewRequest));
        verify(reviews, never()).save(any(Review.class));
    }

    /**
     * Test case for a user reviewing a session they were not a participant of.
     */
   @Test
void create_withWrongUser_throwsBusinessException() {
    // Given
    User wrongUser = new User();
    wrongUser.setId(99L);
    wrongUser.setEmail("wrong@example.com");
    
    // Stubbing 1 (Only keep the stubbings that are actually executed before the exception)
    when(users.findByEmail(wrongUser.getEmail())).thenReturn(Optional.of(wrongUser));
    
    // The sessions.findById() stubbing is REMOVED as the BusinessException
    // is thrown before the service method reaches this call.
    
    // When / Then
    assertThrows(BusinessException.class, () -> reviewService.create(wrongUser.getEmail(), reviewRequest));
    verify(reviews, never()).save(any(Review.class));
}

    /**
     * Test case for reviewing a session that is not in a COMPLETED status.
     */
    @Test
    void create_withIncompleteSession_throwsBusinessException() {
        // Given
        completedSession.setStatus(SessionStatus.APPROVED);
        when(users.findByEmail(tutee.getEmail())).thenReturn(Optional.of(tutee));
        when(sessions.findById(reviewRequest.getSessionId())).thenReturn(Optional.of(completedSession));

        // When / Then
        assertThrows(BusinessException.class, () -> reviewService.create(tutee.getEmail(), reviewRequest));
        verify(reviews, never()).save(any(Review.class));
    }
}