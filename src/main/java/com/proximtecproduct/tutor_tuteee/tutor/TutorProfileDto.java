package com.proximtecproduct.tutor_tuteee.tutor;

import lombok.*;

@Getter
@Setter
public class TutorProfileDto {
    // --- NEW FIELD ADDED ---
    private String profileImageUrl;
    // -------------------------

    private String bio;
    private java.util.List<String> subjects;
    private Integer hourlyRateCents;
    private String city;
    private String country;
    private java.util.List<String> locationTypes; // optional
    private java.util.List<AvailabilitySlot> availability;

    @Getter @Setter
    public static class AvailabilitySlot {
        public String day;
        public String from;
        public String to;
    }
}