package com.proximtecproduct.tutor_tuteee.massage;

// message/repo/MessageRepository.java

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MessageRepository extends JpaRepository<Message,Long> { 
    List<Message> findByThreadIdOrderByCreatedAtAsc(Long threadId); 
}

