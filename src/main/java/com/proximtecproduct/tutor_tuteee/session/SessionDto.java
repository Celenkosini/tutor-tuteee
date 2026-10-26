package com.proximtecproduct.tutor_tuteee.session;

// session/dto/SessionDto.java
import lombok.Builder; 
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
 @Builder 
 public class SessionDto {
  private Long id; 
  private String tutorName; 
  private String tuteeName; 
  private String subject; 
  private SessionStatus status;
  private LocationType locationType; 
  private LocalDateTime scheduledAt; 
  private Integer durationMinutes; 
  private Integer priceCents;
}
