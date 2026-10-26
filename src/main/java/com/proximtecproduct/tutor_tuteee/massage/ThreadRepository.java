package com.proximtecproduct.tutor_tuteee.massage;

// message/repo/ThreadRepository.java

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ThreadRepository extends JpaRepository<Thread,Long> { 
    Optional<Thread> findBySessionId(Long sessionId); }

