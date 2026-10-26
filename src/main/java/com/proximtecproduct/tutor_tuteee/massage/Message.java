package com.proximtecproduct.tutor_tuteee.massage;

// message/entity/Message.java

import com.proximtecproduct.tutor_tuteee.user.User;
import jakarta.persistence.*; 
import lombok.*; 
import java.time.LocalDateTime;

@Entity @Table(name="messages")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Message {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @ManyToOne @JoinColumn(name="thread_id") private Thread thread;
  @ManyToOne @JoinColumn(name="sender_id") private User sender;
  @ManyToOne @JoinColumn(name="receiver_id") private User receiver;
  @Column(length=4000) private String content;
  private LocalDateTime createdAt; @PrePersist void pre(){ if(createdAt==null) createdAt=LocalDateTime.now(); }
}

