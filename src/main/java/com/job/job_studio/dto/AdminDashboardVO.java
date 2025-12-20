package com.job.job_studio.dto;

import lombok.Data;
import java.util.Map;

@Data
public class AdminDashboardVO {
    // 顶部卡片数据
    private Long totalStudents;       // 在读学生总数
    private Long totalAlumni;         // 校友总数
    private Long assessmentCount;     // 已完成评测数
    private Double completionRate;    // 评测完成率

    // 图表数据
    private Map<String, Long> studentMajorDistribution; // 学生专业分布
    private Map<String, Long> alumniGraduationYearDistribution; // 校友毕业年份分布
}