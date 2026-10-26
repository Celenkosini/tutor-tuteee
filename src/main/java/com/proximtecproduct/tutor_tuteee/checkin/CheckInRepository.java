package com.proximtecproduct.tutor_tuteee.checkin;

import com.proximtecproduct.tutor_tuteee.session.Session;
import com.proximtecproduct.tutor_tuteee.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    List<CheckIn> findBySession(Session session);
    List<CheckIn> findByUser(User user);
}
