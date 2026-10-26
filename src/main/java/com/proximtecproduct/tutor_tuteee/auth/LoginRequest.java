package com.proximtecproduct.tutor_tuteee.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class LoginRequest {
    @Email @NotBlank private String email;
    @NotBlank private String password;
}

