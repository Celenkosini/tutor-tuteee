package com.proximtecproduct.tutor_tuteee.session;

import com.proximtecproduct.tutor_tuteee.user.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "session_learners")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionLearner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;

    @ManyToOne
    @JoinColumn(name = "learner_id", nullable = false)
    private User learner;
}
