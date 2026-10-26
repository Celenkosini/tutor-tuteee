package com.proximtecproduct.tutor_tuteee.review;

// review/service/ReviewService.java

import com.proximtecproduct.tutor_tuteee.auth.SubscriptionTier;
import com.proximtecproduct.tutor_tuteee.common.BusinessException;
import com.proximtecproduct.tutor_tuteee.session.SessionStatus; 
import com.proximtecproduct.tutor_tuteee.session.SessionRepository;
import com.proximtecproduct.tutor_tuteee.tutor.TutorProfileRepository;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
  private final ReviewRepository reviews; private final SessionRepository sessions; private final UserRepository users; private final TutorProfileRepository tutors;
  public ReviewService(ReviewRepository r, SessionRepository s, UserRepository u, TutorProfileRepository t){ this.reviews=r; this.sessions=s; this.users=u; this.tutors=t; }

  @Transactional
  public Review create(String tuteeEmail, ReviewRequest req){
    var tutee = users.findByEmail(tuteeEmail).orElseThrow(()->new BusinessException("User not found"));
    if(tutee.getSubscriptionTier()==SubscriptionTier.FREE) throw new BusinessException("Upgrade required to post reviews");
    var session = sessions.findById(req.getSessionId()).orElseThrow(()->new BusinessException("Session not found"));
    if(!session.getTutee().getId().equals(tutee.getId())) throw new BusinessException("Not your session");
    if(session.getStatus()!=SessionStatus.COMPLETED) throw new BusinessException("Can review only completed sessions");
    var review = reviews.save(Review.builder().session(session).tutor(session.getTutor()).tutee(tutee).rating(req.getRating()).comment(req.getComment()).build());
    // update tutor aggregates
    var profile = tutors.findByUserEmail(session.getTutor().getEmail()).orElseThrow();
    int count = profile.getRatingCount()==null?0:profile.getRatingCount(); double avg = profile.getRatingAvg()==null?0.0:profile.getRatingAvg();
    double newAvg = ((avg*count)+req.getRating())/(count+1);
    profile.setRatingAvg(newAvg); profile.setRatingCount(count+1); tutors.save(profile);
    return review;
  }
}

