package com.proximtecproduct.tutor_tuteee.review;

// review/controller/ReviewController.java

import com.proximtecproduct.tutor_tuteee.common.ApiResponse;

import jakarta.validation.Valid; 
import org.springframework.http.ResponseEntity; 
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/reviews")
public class ReviewController {
  private final ReviewService service; public ReviewController(ReviewService s){ this.service=s; }
  @PostMapping public ResponseEntity<ApiResponse<Review>> create(Authentication auth, @Valid @RequestBody ReviewRequest req){
    return ResponseEntity.ok(ApiResponse.ok(service.create(auth.getName(), req)));
  }
}

