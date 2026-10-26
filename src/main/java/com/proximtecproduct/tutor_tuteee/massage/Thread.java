package com.proximtecproduct.tutor_tuteee.massage;

// message/entity/Thread.java

import com.proximtecproduct.tutor_tuteee.session.Session;
import jakarta.persistence.*; import lombok.*;
@Entity @Table(name="threads")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Thread { @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @OneToOne @JoinColumn(name="session_id", unique=true) private Session session; }

