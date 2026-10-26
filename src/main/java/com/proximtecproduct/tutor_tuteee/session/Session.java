package com.proximtecproduct.tutor_tuteee.session;

// session/entity/Session.java

import com.proximtecproduct.tutor_tuteee.user.*;
import jakarta.persistence.*;
 import lombok.*;
 import java.time.LocalDateTime;

@Entity
 @Table(name="tutoring_sessions")
@Getter 
@Setter 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class Session {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne @JoinColumn(name="tutor_id", nullable=false) private User tutor;
  @ManyToOne @JoinColumn(name="tutee_id", nullable=false) private User tutee;

  @Column(nullable=false) private String subject;
  @Column(columnDefinition="TEXT") private String description;

  @Enumerated(EnumType.STRING) @Column(nullable=false) private LocationType locationType;
  @Column(nullable=false) private LocalDateTime scheduledAt;
  @Column(nullable=false) private Integer durationMinutes;

  @Enumerated(EnumType.STRING) @Column(nullable=false) private SessionStatus status = SessionStatus.REQUESTED;

  private Integer priceCentsSnapshot; // calculated from tutor rate
  private LocalDateTime createdAt;
  @PrePersist void pre(){ if(createdAt==null) createdAt = LocalDateTime.now(); }
}
