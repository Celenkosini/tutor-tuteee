package com.proximtecproduct.tutor_tuteee.session;

// session/service/SessionService.java

import com.proximtecproduct.tutor_tuteee.auth.SubscriptionTier;
import com.proximtecproduct.tutor_tuteee.common.BusinessException;
 

import com.proximtecproduct.tutor_tuteee.tutor.TutorProfileRepository; 
import com.proximtecproduct.tutor_tuteee.user.UserRepository;
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional;
import java.time.*; import java.util.List;

@Service
public class SessionService {
  private final SessionRepository sessions; private final UserRepository users; private final TutorProfileRepository tutors;
  public SessionService(SessionRepository s, UserRepository u, TutorProfileRepository t){ this.sessions=s; this.users=u; this.tutors=t; }

  private int monthlyLimit(SubscriptionTier tier){
    return switch (tier){
      case FREE -> 1;
      case ENTRY -> 10;
      case PREMIUM -> Integer.MAX_VALUE;
    };
  }

  @Transactional
  public SessionDto create(SessionRequest req, String tuteeEmail){
    var tutee = users.findByEmail(tuteeEmail).orElseThrow(()->new BusinessException("Tutee not found"));
    var tutorUser = users.findById(req.getTutorId()).orElseThrow(()->new BusinessException("Tutor not found"));
    var profile = tutors.findByUserEmail(tutorUser.getEmail()).orElseThrow(()->new BusinessException("Tutor profile missing"));
    if(!profile.isVerified()) throw new BusinessException("Tutor not verified");

    // Freemium limits
    var startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
    long used = sessions.countCreatedThisMonth(tutee, startOfMonth);
    int limit = monthlyLimit(tutee.getSubscriptionTier());
    if(used >= limit) throw new BusinessException("Monthly booking limit reached for your plan");

    int priceCents = Math.toIntExact(Math.round(profile.getHourlyRateCents() * (req.getDurationMinutes()/60.0)));

    var entity = Session.builder()
        .tutor(tutorUser).tutee(tutee).subject(req.getSubject()).description(req.getDescription())
        .locationType(req.getLocationType()).scheduledAt(req.getScheduledAt()).durationMinutes(req.getDurationMinutes())
        .status(SessionStatus.REQUESTED).priceCentsSnapshot(priceCents).build();
    var saved = sessions.save(entity);
    return toDto(saved);
  }

  public List<SessionDto> getMine(String email){
    var me = users.findByEmail(email).orElseThrow(()->new BusinessException("User not found"));
    List<Session> list = switch (me.getRole()){
      case TUTEE -> sessions.findByTutee(me);
      case TUTOR -> sessions.findByTutor(me);
      case ADMIN -> sessions.findAll();
    };
    return list.stream().map(this::toDto).toList();
  }

  @Transactional
  public void updateStatus(Long id, StatusUpdateDto dto, String email){
    var me = users.findByEmail(email).orElseThrow(()->new BusinessException("User not found"));
    var s = sessions.findById(id).orElseThrow(()->new BusinessException("Session not found"));
    // Permissions: tutor can APPROVE/DECLINE/COMPLETE; tutee can CANCEL
    if(me.getRole()==com.proximtecproduct.tutor_tuteee.auth.Role.TUTOR && s.getTutor().getId().equals(me.getId())){
      if(dto.getStatus()==SessionStatus.APPROVED || dto.getStatus()==SessionStatus.DECLINED || dto.getStatus()==SessionStatus.COMPLETED){
        s.setStatus(dto.getStatus());
      } else throw new BusinessException("Tutor cannot set status: "+dto.getStatus());
    } else if(me.getRole()==com.proximtecproduct.tutor_tuteee.auth.Role.TUTEE && s.getTutee().getId().equals(me.getId())){
      if(dto.getStatus()==SessionStatus.CANCELLED) s.setStatus(SessionStatus.CANCELLED);
      else throw new BusinessException("Tutee can only cancel");
    } else if(me.getRole()!=com.proximtecproduct.tutor_tuteee.auth.Role.ADMIN){
      throw new BusinessException("Not allowed");
    }
    sessions.save(s);
  }

  private SessionDto toDto(Session s){
    return SessionDto.builder()
        .id(s.getId())
        .tutorName(s.getTutor().getName())
        .tuteeName(s.getTutee().getName())
        .subject(s.getSubject())
        .status(s.getStatus())
        .locationType(s.getLocationType())
        .scheduledAt(s.getScheduledAt())
        .durationMinutes(s.getDurationMinutes())
        .priceCents(s.getPriceCentsSnapshot())
        .build();
  }
}
