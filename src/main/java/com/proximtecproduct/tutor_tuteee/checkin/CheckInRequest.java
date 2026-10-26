package com.proximtecproduct.tutor_tuteee.checkin;

import lombok.Data;

@Data
public class CheckInRequest {
    private Long sessionId;
    private Long userId;
}
