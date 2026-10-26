package com.proximtecproduct.tutor_tuteee.admin;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminTutorVerificationRequest {
    @NotNull
    private Boolean verified;
}

