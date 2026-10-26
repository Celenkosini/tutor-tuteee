package com.proximtecproduct.tutor_tuteee.payment;

// payment/controller/PaymentController.java

import com.proximtecproduct.tutor_tuteee.common.ApiResponse;

import org.springframework.http.ResponseEntity; 
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/payments")
public class PaymentController {
  private final PaymentService service; public PaymentController(PaymentService s){ this.service=s; }

  @PostMapping("/session/{sessionId}/intent") public ResponseEntity<ApiResponse<Payment>> intent(Authentication auth, @PathVariable Long sessionId){
    return ResponseEntity.ok(ApiResponse.ok(service.intent(sessionId, auth.getName())));
  }
  @PostMapping("/{id}/capture") public ResponseEntity<ApiResponse<Payment>> capture(@PathVariable Long id){
    return ResponseEntity.ok(ApiResponse.ok(service.capture(id)));
  }
}

