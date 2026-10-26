package com.proximtecproduct.tutor_tuteee.tutor;

// tutor/controller/TutorProfileController.java
import com.proximtecproduct.tutor_tuteee.common.ApiResponse;
import org.springframework.http.ResponseEntity;
 import org.springframework.security.core.Authentication; 
 import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController 
@RequestMapping("/api/tutors")
public class TutorProfileController {

  private final TutorProfileService service;

  public TutorProfileController(TutorProfileService s){
     this.service=s; 
}

  @PostMapping("/profile") 
  public ResponseEntity<ApiResponse<TutorProfile>> upsert(Authentication auth, 
                                                            @RequestBody TutorProfileDto dto){
    return ResponseEntity.ok(ApiResponse.ok(service.upsert(auth.getName(), dto)));
  }

  @GetMapping 
  public ResponseEntity<ApiResponse<List<TutorProfile>>> search(
      @RequestParam(required=false) String subject, @RequestParam(required=false) String city,
      @RequestParam(required=false) Integer minRate, @RequestParam(required=false) Integer maxRate){
    return ResponseEntity.ok(ApiResponse.ok(service.search(new TutorSearchCriteria(){{
      setSubject(subject); setCity(city); setMinRate(minRate); setMaxRate(maxRate);
    }})));
  }

  @GetMapping("/{id}") public ResponseEntity<ApiResponse<TutorProfile>> get(@PathVariable Long id){
    return ResponseEntity.ok(ApiResponse.ok(service.getById(id)));
  }

  @GetMapping("/profile/exists") 
  public ResponseEntity<ApiResponse<Boolean>> checkProfileExists(Authentication auth){
    // Use auth.getName() which is the email
    return ResponseEntity.ok(ApiResponse.ok(service.profileExists(auth.getName())));
  }
}

