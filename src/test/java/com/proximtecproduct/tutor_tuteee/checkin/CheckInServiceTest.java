package com.proximtecproduct.tutor_tuteee.checkin;

import com.proximtecproduct.tutor_tuteee.session.Session;
import com.proximtecproduct.tutor_tuteee.session.SessionRepository;
import com.proximtecproduct.tutor_tuteee.user.User;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckInServiceTest {

    @Mock
    private CheckInRepository checkInRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CheckInService checkInService;

    private Session session;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        session = Session.builder().id(1L).build();
        user = User.builder().id(1L).build();
    }

    @Test
    void checkIn_ShouldSaveAndReturnCheckIn() {
        CheckInRequest request = new CheckInRequest();
        request.setSessionId(1L);
        request.setUserId(1L);

        CheckIn expectedCheckIn = CheckIn.builder()
                .id(1L)
                .session(session)
                .user(user)
                .checkInTime(LocalDateTime.now())
                .build();

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(checkInRepository.save(any(CheckIn.class))).thenReturn(expectedCheckIn);

        CheckIn result = checkInService.checkIn(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(checkInRepository, times(1)).save(any(CheckIn.class));
    }

    @Test
    void getCheckInsBySession_ShouldReturnListOfCheckIns() {
        CheckIn checkIn = CheckIn.builder().id(1L).session(session).user(user).build();
        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(checkInRepository.findBySession(session)).thenReturn(Arrays.asList(checkIn));

        List<CheckIn> result = checkInService.getCheckInsBySession(1L);

        assertEquals(1, result.size());
        assertEquals(session, result.get(0).getSession());
    }

    @Test
    void getCheckInsByUser_ShouldReturnListOfCheckIns() {
        CheckIn checkIn = CheckIn.builder().id(1L).session(session).user(user).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(checkInRepository.findByUser(user)).thenReturn(Arrays.asList(checkIn));

        List<CheckIn> result = checkInService.getCheckInsByUser(1L);

        assertEquals(1, result.size());
        assertEquals(user, result.get(0).getUser());
    }
}
