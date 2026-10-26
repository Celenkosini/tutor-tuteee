package com.proximtecproduct.tutor_tuteee.tutor;

// tutor/service/TutorProfileService.java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.proximtecproduct.tutor_tuteee.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TutorProfileService {

    private final TutorProfileRepository repo;
    private final UserRepository users;
    private final ObjectMapper om;

    public TutorProfileService(TutorProfileRepository r, UserRepository u, ObjectMapper om){
       this.repo = r; 
       this.users = u; 
       this.om = om;
    }

    public TutorProfile upsert(String email, TutorProfileDto dto){
        var user = users.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var profile = repo.findByUserEmail(email)
                        .orElse(TutorProfile.builder().user(user).build());
        
        // --- ALIGNMENT WITH NEW FIELD ---
        profile.setProfileImageUrl(dto.getProfileImageUrl()); // <--- SET THE NEW FIELD
        // ---------------------------------
        
        profile.setBio(dto.getBio());
        profile.setHourlyRateCents(dto.getHourlyRateCents());
        profile.setCity(dto.getCity()); 
        profile.setCountry(dto.getCountry());

        try {
            profile.setSubjects(om.writeValueAsString(dto.getSubjects()));
            profile.setAvailabilityJson(om.writeValueAsString(dto.getAvailability()));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JSON");
        }
        
        return repo.save(profile);
    }

    public List<TutorProfile> search(TutorSearchCriteria c){
        return repo.search(c.getSubject(), c.getCity(), c.getMinRate(), c.getMaxRate());
    } 

    public boolean profileExists(String email) {
        return repo.findByUserEmail(email).isPresent();
    }

    public TutorProfile getById(Long id){ 
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Tutor not found")); 
    }
}