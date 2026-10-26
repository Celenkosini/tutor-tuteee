package com.proximtecproduct.tutor_tuteee.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class AdminMetricsResponse {
    private long totalUsers;
    private long totalTutors;
    private long totalSessions;
    private long totalRevenueCents;
    private long totalPlatformFeesCents;

    

    
}

