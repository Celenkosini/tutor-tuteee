package com.proximtecproduct.tutor_tuteee.massage;

// message/dto/MessageRequest.java
import jakarta.validation.constraints.*; import lombok.Data;
@Data public class MessageRequest { @NotNull private Long sessionId; @NotBlank private String content; }

