package com.proximtecproduct.tutor_tuteee.tutor;

import org.springframework.data.jpa.repository.*;
 import org.springframework.data.repository.query.Param;
import java.util.List; import java.util.Optional;

public interface TutorProfileRepository extends JpaRepository<TutorProfile,Long> {
  Optional<TutorProfile> findByUserEmail(String email);
  Optional<TutorProfile> findByUserId(Long userId);
  List<TutorProfile> findByIsVerifiedFalse();


  @Query("""
    SELECT t FROM TutorProfile t
    WHERE (:subject IS NULL OR t.subjects LIKE %:subject%)
      AND (:city IS NULL OR t.city = :city)
      AND (:minRate IS NULL OR t.hourlyRateCents >= :minRate)
      AND (:maxRate IS NULL OR t.hourlyRateCents <= :maxRate)
      AND t.isVerified = true
    """)
  List<TutorProfile> search(@Param("subject") String subject, @Param("city") String city,
                            @Param("minRate") Integer minRate, @Param("maxRate") Integer maxRate);
}


