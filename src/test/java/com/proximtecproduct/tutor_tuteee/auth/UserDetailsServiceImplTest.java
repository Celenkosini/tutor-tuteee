package com.proximtecproduct.tutor_tuteee.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import com.proximtecproduct.tutor_tuteee.user.User;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the UserDetailsServiceImpl class.
 * This class uses Mockito to mock the UserRepository, allowing for
 * isolated testing of the loadUserByUsername method.
 */
@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private UserRepository userRepository;

    private User testUser;
    private final String testEmail = "test@example.com";
    private final Role testRole = Role.TUTOR;

    /**
     * Set up a mock User object for use in tests.
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail(testEmail);
        testUser.setPassword("password123");
        testUser.setRole(testRole);
    }

    /**
     * Test case for a successful user lookup.
     * Verifies that the correct UserDetails object is returned when a user exists.
     */
    @Test
    void loadUserByUsername_withExistingUser_returnsUserDetails() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(testEmail);

        // Then
        assertNotNull(userDetails);
        assertEquals(testEmail, userDetails.getUsername());
        assertEquals(testUser.getPassword(), userDetails.getPassword());
        
        // Check authorities
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        assertFalse(authorities.isEmpty());
        assertTrue(authorities.contains(new SimpleGrantedAuthority(testRole.name())));

        // Verify that the findByEmail method was called once
        verify(userRepository, times(1)).findByEmail(testEmail);
    }

    /**
     * Test case for when a user is not found.
     * Verifies that a UsernameNotFoundException is thrown.
     */
    @Test
    void loadUserByUsername_withNonexistentUser_throwsUsernameNotFoundException() {
        // Given
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When/Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(testEmail)
        );

        // Verify the exception message
        assertEquals("User not found with email: " + testEmail, exception.getMessage());
        
        // Verify that the findByEmail method was called once
        verify(userRepository, times(1)).findByEmail(testEmail);
    }
}