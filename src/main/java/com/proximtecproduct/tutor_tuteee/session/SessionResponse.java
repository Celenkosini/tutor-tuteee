package com.proximtecproduct.tutor_tuteee.session;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SessionResponse {
    private Long id;
    private String tutorName;
    private String subject;
    private SessionStatus status;
    private LocationType locationType;
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;

    public static SessionResponse fromEntity(Session session) {
        return SessionResponse.builder()
                .id(session.getId())
                .tutorName(session.getTutor().getName())
                .subject(session.getSubject())
                .status(session.getStatus())
                .locationType(session.getLocationType())
                .scheduledAt(session.getScheduledAt())
                .durationMinutes(session.getDurationMinutes())
                .build();
    }
}
