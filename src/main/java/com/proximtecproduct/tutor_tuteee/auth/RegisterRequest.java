package com.proximtecproduct.tutor_tuteee.auth;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role; // LEARNER, TUTOR, PARENT, ADMIN
}

