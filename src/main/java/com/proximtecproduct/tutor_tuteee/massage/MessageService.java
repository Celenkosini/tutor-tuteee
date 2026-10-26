package com.proximtecproduct.tutor_tuteee.massage;

// message/service/MessageService.java

import com.proximtecproduct.tutor_tuteee.common.BusinessException;
import com.proximtecproduct.tutor_tuteee.session.SessionRepository;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; import java.util.List;

@Service
public class MessageService {
  private final ThreadRepository threads; private final MessageRepository messages; private final SessionRepository sessions; private final UserRepository users;
  public MessageService(ThreadRepository t, MessageRepository m, SessionRepository s, UserRepository u){ this.threads=t; this.messages=m; this.sessions=s; this.users=u; }

  @Transactional
  public Thread ensureThread(Long sessionId){
    return threads.findBySessionId(sessionId).orElseGet(()->{
      var s = sessions.findById(sessionId).orElseThrow(()->new BusinessException("Session not found"));
      return threads.save(Thread.builder().session(s).build());
    });
  }

  @Transactional
  public Message send(String senderEmail, MessageRequest req){
    var s = sessions.findById(req.getSessionId()).orElseThrow(()->new BusinessException("Session not found"));
    var sender = users.findByEmail(senderEmail).orElseThrow(()->new BusinessException("User not found"));
    if(!sender.getId().equals(s.getTutee().getId()) && !sender.getId().equals(s.getTutor().getId())) throw new BusinessException("Not a participant");
    var receiver = sender.getId().equals(s.getTutee().getId()) ? s.getTutor() : s.getTutee();
    var th = ensureThread(s.getId());
    return messages.save(Message.builder().thread(th).sender(sender).receiver(receiver).content(req.getContent()).build());
  }

  public List<Message> list(Long sessionId){
    var th = threads.findBySessionId(sessionId).orElseThrow(()->new BusinessException("Thread not found"));
    return messages.findByThreadIdOrderByCreatedAtAsc(th.getId());
  }
}

