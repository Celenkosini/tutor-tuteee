package com.proximtecproduct.tutor_tuteee.payment;

// payment/entity/Payment.java

import com.proximtecproduct.tutor_tuteee.session.Session; 
import com.proximtecproduct.tutor_tuteee.user.User;

import jakarta.persistence.*; 
import lombok.*; 

import java.time.LocalDateTime;

@Entity @Table(name="payments")
@Getter 
@Setter
 @Builder
@NoArgsConstructor 
@AllArgsConstructor
public class Payment {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne @JoinColumn(name="session_id") private Session session;
  @ManyToOne @JoinColumn(name="tutee_id") private User tutee;
  @ManyToOne @JoinColumn(name="tutor_id") private User tutor;

  private Long amountCents; private Long platformFeeCents;
  @Enumerated(EnumType.STRING) private PaymentStatus status = PaymentStatus.PENDING;
  private String providerRef; private LocalDateTime createdAt; @PrePersist void pre(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}
