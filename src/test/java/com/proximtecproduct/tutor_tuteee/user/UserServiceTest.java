package com.proximtecproduct.tutor_tuteee.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;



import com.proximtecproduct.tutor_tuteee.auth.Role;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserService class using JUnit 5 and Mockito.
 * This class mocks the UserRepository to test the service's business logic in isolation.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private final Long testUserId = 1L;
    private final String testUserEmail = "testuser@example.com";

    /**
     * Sets up a mock user object before each test.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(testUserId);
        testUser.setEmail(testUserEmail);
        testUser.setRole(Role.TUTOR);
    }

    @Test
    void findByEmail_withExistingEmail_returnsUser() {
        // Given: The userRepository returns a user when the email exists
        when(userRepository.findByEmail(testUserEmail)).thenReturn(Optional.of(testUser));

        // When: The service method is called
        Optional<User> result = userService.findByEmail(testUserEmail);

        // Then: The result should not be empty and contain the test user
        assertTrue(result.isPresent());
        assertEquals(testUserEmail, result.get().getEmail());
        verify(userRepository, times(1)).findByEmail(testUserEmail);
    }

    @Test
    void findByEmail_withNonExistingEmail_returnsEmptyOptional() {
        // Given: The userRepository returns an empty optional for a non-existing email
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When: The service method is called
        Optional<User> result = userService.findByEmail("nonexistent@example.com");

        // Then: The result should be empty
        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByEmail(anyString());
    }

    @Test
    void getUserById_withExistingId_returnsUser() {
        // Given: The userRepository finds a user with the given ID
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        // When: The service method is called
        User result = userService.getUserById(testUserId);

        // Then: The correct user is returned
        assertNotNull(result);
        assertEquals(testUserId, result.getId());
        verify(userRepository, times(1)).findById(testUserId);
    }

    @Test
    void getUserById_withNonExistingId_throwsRuntimeException() {
        // Given: The userRepository returns an empty optional for a non-existing ID
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then: Calling the method should throw a RuntimeException
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(999L));
        assertEquals("User not found with ID: 999", exception.getMessage());
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void emailExists_withExistingEmail_returnsTrue() {
        // Given: The userRepository confirms the email exists
        when(userRepository.existsByEmail(testUserEmail)).thenReturn(true);

        // When/Then: The service method should return true
        assertTrue(userService.emailExists(testUserEmail));
        verify(userRepository, times(1)).existsByEmail(testUserEmail);
    }

    @Test
    void emailExists_withNonExistingEmail_returnsFalse() {
        // Given: The userRepository confirms the email does not exist
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // When/Then: The service method should return false
        assertFalse(userService.emailExists("nonexistent@example.com"));
        verify(userRepository, times(1)).existsByEmail(anyString());
    }

    @Test
    void getUsersByRole_returnsListOfUsers() {
        // Given: The userRepository returns a list of users for a specific role
        List<User> userList = List.of(testUser);
        when(userRepository.findByRole(Role.TUTOR)).thenReturn(userList);

        // When: The service method is called
        List<User> result = userService.getUsersByRole(Role.TUTOR);

        // Then: The returned list should not be empty and contain the test user
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testUser, result.get(0));
        verify(userRepository, times(1)).findByRole(Role.TUTOR);
    }

    @Test
    void saveUser_savesAndReturnsUser() {
        // Given: The userRepository returns the saved user
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When: The service method is called
        User savedUser = userService.saveUser(testUser);

        // Then: The returned user should be the same as the input user
        assertNotNull(savedUser);
        assertEquals(testUser, savedUser);
        verify(userRepository, times(1)).save(testUser);
    }
}