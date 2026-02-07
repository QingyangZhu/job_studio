package com.job.job_studio.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String role;
    private Long studentId;
    private boolean assessmentCompleted;
}