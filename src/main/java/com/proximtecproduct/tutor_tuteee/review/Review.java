package com.proximtecproduct.tutor_tuteee.review;

// review/entity/Review.java
import com.proximtecproduct.tutor_tuteee.session.Session;
 import com.proximtecproduct.tutor_tuteee.user.User;

import jakarta.persistence.*;

import lombok.*; 

import java.time.LocalDateTime;

@Entity
 @Table(name="reviews")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Review {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne @JoinColumn(name="session_id") private Session session;
  @ManyToOne @JoinColumn(name="tutee_id") private User tutee;
  @ManyToOne @JoinColumn(name="tutor_id") private User tutor;
  private int rating; @Column(length=2000) private String comment;
  private LocalDateTime createdAt; @PrePersist void pre(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}

