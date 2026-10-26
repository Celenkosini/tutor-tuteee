package com.proximtecproduct.tutor_tuteee.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import com.proximtecproduct.tutor_tuteee.common.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor

public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(authService.register(req)));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest req) {
    return ResponseEntity.ok(ApiResponse.ok(authService.login(req)));
  }

  // ✅ Fixed: No call to non-existing ApiResponse.error()
  @GetMapping("/me")
  public ResponseEntity<ApiResponse<?>> getCurrentUser(
      org.springframework.security.core.Authentication auth) {

    if (auth == null || !auth.isAuthenticated()) {
      // Manual ApiResponse error handling
      ApiResponse<String> response = new ApiResponse<>();
      response.setSuccess(false);
      response.setMessage("Not logged in");
      return ResponseEntity.status(401).body(response);
    }
    String email = auth.getName();
    var user = authService.findUserByEmail(email);
    
    var userInfo = Map.of(
        "username", user.getName(),
        "authorities", user.getRole(),
        "profileImageUrl", user.getProfileImageUrl()
    );

    return ResponseEntity.ok(ApiResponse.ok(userInfo));
  }
}
