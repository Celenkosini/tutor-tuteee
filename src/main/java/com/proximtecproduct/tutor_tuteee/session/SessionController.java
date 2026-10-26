package com.proximtecproduct.tutor_tuteee.session;

// session/controller/SessionController.java

import com.proximtecproduct.tutor_tuteee.common.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController 
@RequestMapping("/api/sessions")
public class SessionController {

  private final SessionService service;

  public SessionController(SessionService s){ 
     this.service=s;
  }

  @PostMapping 
  public ResponseEntity<ApiResponse<SessionDto>> create(Authentication auth, @Valid @RequestBody SessionRequest dto){
    return ResponseEntity.ok(ApiResponse.ok(service.create(dto, auth.getName())));
  }
  @GetMapping public ResponseEntity<ApiResponse<List<SessionDto>>> mine(Authentication auth){
    return ResponseEntity.ok(ApiResponse.ok(service.getMine(auth.getName())));
  }
  @PutMapping("/{id}/status") public ResponseEntity<ApiResponse<Void>> status(Authentication auth, @PathVariable Long id, @RequestBody StatusUpdateDto dto){
    service.updateStatus(id, dto, auth.getName()); return ResponseEntity.ok(ApiResponse.ok(null));
  }
}
