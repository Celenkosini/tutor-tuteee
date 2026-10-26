package com.proximtecproduct.tutor_tuteee.massage;

// message/controller/MessageController.java

import com.proximtecproduct.tutor_tuteee.common.ApiResponse;

import jakarta.validation.Valid; import org.springframework.http.ResponseEntity; 
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/threads")
public class MessageController {
  private final MessageService service; public MessageController(MessageService s){ this.service=s; }

  @PostMapping public ResponseEntity<ApiResponse<Thread>> create(@RequestParam Long sessionId){
    return ResponseEntity.ok(ApiResponse.ok(service.ensureThread(sessionId)));
  }
  @GetMapping("/{sessionId}/messages") public ResponseEntity<ApiResponse<List<Message>>> list(@PathVariable Long sessionId){
    return ResponseEntity.ok(ApiResponse.ok(service.list(sessionId)));
  }
  @PostMapping("/{sessionId}/messages") public ResponseEntity<ApiResponse<Message>> send(Authentication auth, @PathVariable Long sessionId, @Valid @RequestBody MessageRequest req){
    req.setSessionId(sessionId); return ResponseEntity.ok(ApiResponse.ok(service.send(auth.getName(), req)));
  }
}

