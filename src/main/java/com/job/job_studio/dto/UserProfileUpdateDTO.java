package com.job.job_studio.dto;

import lombok.Data;

@Data
public class UserProfileUpdateDTO {
    private String email;     // 前端传的是 email
    private String phone;
    private String targetJob;
    private String github;    // 前端传的是 github
    private String bio;
}