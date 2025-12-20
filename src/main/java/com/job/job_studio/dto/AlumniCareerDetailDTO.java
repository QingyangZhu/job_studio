package com.job.job_studio.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

/**
 * 校友职业详情 DTO (包含岗位要求)
 */
@Data
public class AlumniCareerDetailDTO {
    private Long careerId;
    private Long alumniId;
    private String companyName;
    private String industry;
    private String jobTitle;
    private String workCity;
    private LocalDate jobStartDate;
    private LocalDate jobEndDate;

    // 岗位能力素养要求，使用 Map 存储 KSQ 分数
    private Map<String, Integer> ksqRequirements;
}