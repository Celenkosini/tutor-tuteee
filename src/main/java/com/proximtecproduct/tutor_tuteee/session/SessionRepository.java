package com.proximtecproduct.tutor_tuteee.session;

// session/repo/SessionRepository.java

 import com.proximtecproduct.tutor_tuteee.user.User;
import org.springframework.data.jpa.repository.*; 
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime; import java.util.List;

public interface SessionRepository extends JpaRepository<Session,Long> {
  @Query("SELECT s FROM Session s WHERE s.tutee = :u ORDER BY s.scheduledAt DESC") List<Session> findByTutee(@Param("u") User u);
  @Query("SELECT s FROM Session s WHERE s.tutor = :u ORDER BY s.scheduledAt DESC") List<Session> findByTutor(@Param("u") User u);

  @Query("""
    SELECT COUNT(s) FROM Session s
    WHERE s.tutee = :tutee AND s.createdAt >= :monthStart
  """)
  long countCreatedThisMonth(@Param("tutee") User tutee, @Param("monthStart") LocalDateTime monthStart);
}

