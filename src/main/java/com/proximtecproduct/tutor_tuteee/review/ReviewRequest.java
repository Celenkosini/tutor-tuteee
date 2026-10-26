package com.proximtecproduct.tutor_tuteee.review;

// review/dto/ReviewRequest.java

import jakarta.validation.constraints.*; import lombok.Data;
@Data public class ReviewRequest { @NotNull private Long sessionId; @Min(1) @Max(5) private int rating; private String comment; }

