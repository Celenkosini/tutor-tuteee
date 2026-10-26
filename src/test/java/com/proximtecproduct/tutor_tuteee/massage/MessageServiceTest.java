package com.proximtecproduct.tutor_tuteee.massage;

import com.proximtecproduct.tutor_tuteee.common.BusinessException;
import com.proximtecproduct.tutor_tuteee.session.Session;
import com.proximtecproduct.tutor_tuteee.session.SessionRepository;
import com.proximtecproduct.tutor_tuteee.user.User;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the MessageService class.
 * This class uses Mockito to mock dependencies and verify business logic.
 */
@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @InjectMocks
    private MessageService messageService;

    @Mock
    private ThreadRepository threads;
    @Mock
    private MessageRepository messages;
    @Mock
    private SessionRepository sessions;
    @Mock
    private UserRepository users;

    // Test data
    private User tutee;
    private User tutor;
    private Session session;
    private Thread thread;
    private MessageRequest messageRequest;

    @BeforeEach
    void setUp() {
        tutee = new User();
        tutee.setId(1L);
        tutee.setEmail("tutee@example.com");

        tutor = new User();
        tutor.setId(2L);
        tutor.setEmail("tutor@example.com");

        session = new Session();
        session.setId(100L);
        session.setTutee(tutee);
        session.setTutor(tutor);

        thread = new Thread();
        thread.setId(1L);
        thread.setSession(session);

        messageRequest = new MessageRequest();
        messageRequest.setSessionId(session.getId());
        messageRequest.setContent("Hello, how are you?");
    }

    /**
     * Test case for ensuring a thread exists when one already does.
     */
    @Test
    void ensureThread_withExistingThread_returnsExistingThread() {
        // Given
        when(threads.findBySessionId(session.getId())).thenReturn(Optional.of(thread));

        // When
        Thread result = messageService.ensureThread(session.getId());

        // Then
        assertEquals(thread.getId(), result.getId());
        verify(threads, never()).save(any(Thread.class));
    }

    /**
     * Test case for ensuring a thread exists by creating a new one.
     */
    @Test
    void ensureThread_withNewThread_createsAndReturnsNewThread() {
        // Given
        when(threads.findBySessionId(session.getId())).thenReturn(Optional.empty());
        when(sessions.findById(session.getId())).thenReturn(Optional.of(session));
        when(threads.save(any(Thread.class))).thenReturn(thread);

        // When
        Thread result = messageService.ensureThread(session.getId());

        // Then
        assertEquals(thread.getId(), result.getId());
        verify(threads).save(argThat(t -> t.getSession().getId().equals(session.getId())));
    }

    /**
     * Test case for ensuring a thread for a nonexistent session.
     */
    @Test
    void ensureThread_withNonexistentSession_throwsException() {
        // Given
        when(threads.findBySessionId(anyLong())).thenReturn(Optional.empty());
        when(sessions.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessException.class, () -> messageService.ensureThread(999L));
    }

    /**
     * Test case for sending a message from a tutee.
     */
    @Test
    void send_asTutee_sendsMessage() {
        // Given
        when(sessions.findById(session.getId())).thenReturn(Optional.of(session));
        when(users.findByEmail(tutee.getEmail())).thenReturn(Optional.of(tutee));
        when(threads.findBySessionId(session.getId())).thenReturn(Optional.of(thread));
        when(messages.save(any(Message.class))).thenReturn(new Message());

        // When
        Message result = messageService.send(tutee.getEmail(), messageRequest);

        // Then
        assertNotNull(result);
        verify(messages).save(argThat(m ->
                m.getSender().getId().equals(tutee.getId()) &&
                m.getReceiver().getId().equals(tutor.getId()) &&
                m.getContent().equals(messageRequest.getContent())
        ));
    }

    /**
     * Test case for sending a message from a tutor.
     */
    @Test
    void send_asTutor_sendsMessage() {
        // Given
        when(sessions.findById(session.getId())).thenReturn(Optional.of(session));
        when(users.findByEmail(tutor.getEmail())).thenReturn(Optional.of(tutor));
        when(threads.findBySessionId(session.getId())).thenReturn(Optional.of(thread));
        when(messages.save(any(Message.class))).thenReturn(new Message());

        // When
        Message result = messageService.send(tutor.getEmail(), messageRequest);

        // Then
        assertNotNull(result);
        verify(messages).save(argThat(m ->
                m.getSender().getId().equals(tutor.getId()) &&
                m.getReceiver().getId().equals(tutee.getId()) &&
                m.getContent().equals(messageRequest.getContent())
        ));
    }

    /**
     * Test case for sending a message from a non-participant.
     */
    @Test
    void send_asNonParticipant_throwsException() {
        // Given
        User nonParticipant = new User();
        nonParticipant.setId(99L);
        nonParticipant.setEmail("outsider@example.com");

        when(sessions.findById(session.getId())).thenReturn(Optional.of(session));
        when(users.findByEmail(nonParticipant.getEmail())).thenReturn(Optional.of(nonParticipant));

        // When / Then
        assertThrows(BusinessException.class, () -> messageService.send(nonParticipant.getEmail(), messageRequest));
        verify(messages, never()).save(any(Message.class));
    }

    /**
     * Test case for listing messages for a valid session.
     */
    @Test
    void list_withExistingThread_returnsMessages() {
        // Given
        Message m1 = new Message(); m1.setContent("Message 1");
        Message m2 = new Message(); m2.setContent("Message 2");
        when(threads.findBySessionId(session.getId())).thenReturn(Optional.of(thread));
        when(messages.findByThreadIdOrderByCreatedAtAsc(thread.getId())).thenReturn(List.of(m1, m2));

        // When
        List<Message> result = messageService.list(session.getId());

        // Then
        assertFalse(result.isEmpty());
        assertEquals(2, result.size());
        assertEquals("Message 1", result.get(0).getContent());
        verify(messages).findByThreadIdOrderByCreatedAtAsc(thread.getId());
    }

    /**
     * Test case for listing messages for a nonexistent thread.
     */
    @Test
    void list_withNonexistentThread_throwsException() {
        // Given
        when(threads.findBySessionId(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(BusinessException.class, () -> messageService.list(999L));
    }
}
