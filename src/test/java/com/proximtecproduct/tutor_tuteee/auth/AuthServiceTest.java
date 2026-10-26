package com.proximtecproduct.tutor_tuteee.auth;

import com.proximtecproduct.tutor_tuteee.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);
        jwtUtil = mock(JwtUtil.class);
        authService = new AuthService(userRepository, passwordEncoder, authenticationManager, jwtUtil);
    }

    @Test
    void testLoginSuccess() {
        LoginRequest request = new LoginRequest("test@example.com", "password");
        User mockUser = User.builder()
                .email("test@example.com")
                .password("hashedPassword")
                .role(Role.TUTEE) // Change 'LEARNER' to the correct enum value if needed, e.g., 'TUTEE'
                .build();

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken("test@example.com")).thenReturn("mock-jwt-token");

        LoginResponse response = authService.login(request);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("test@example.com", "password"));

        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Login successful", response.getMessage());
    }

    @Test
    void testLoginUserNotFound() {
        LoginRequest request = new LoginRequest("notfound@example.com", "password");

    // Stub authenticationManager to return a valid Authentication object
    when(authenticationManager.authenticate(any()))
            .thenReturn(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    // Simulate no user found
    when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class, () -> {
        authService.login(request);
    });

    assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testRegisterSuccess() {
        RegisterRequest request = new RegisterRequest("John", "john@example.com", "123456", Role.TUTOR);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded-password");

        String message = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("John", savedUser.getName());
        assertEquals("john@example.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals(Role.TUTOR, savedUser.getRole());
        assertEquals(SubscriptionTier.FREE, savedUser.getSubscriptionTier());

        assertEquals("User registered successfully", message);
    }

    @Test
    void testRegisterFailsWhenEmailExists() {
        RegisterRequest request = new RegisterRequest("Jane", "jane@example.com", "secret", Role.TUTEE);
        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });

        assertEquals("Email already in use", ex.getMessage());
    }
}
