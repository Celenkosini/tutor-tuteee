package com.proximtecproduct.tutor_tuteee.session;

// session/dto/SessionRequestDto.java
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data public class SessionRequest {
  @NotNull private Long tutorId;
  @NotBlank private String subject;
  private String description;
  @NotNull private LocationType locationType;
  @NotNull private LocalDateTime scheduledAt;
  @NotNull private Integer durationMinutes; // minutes
}

