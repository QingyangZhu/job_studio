package com.job.job_studio.dto;

import lombok.Data;
import java.util.Map;
import java.util.List;

@Data
public class AssessmentSubmitDTO {
    // 1. 通用能力 (算法、软技能等)
    private Map<String, Integer> generalScores;

    // 2. 目标岗位列表
    private List<String> targetJobs;

    // 3. 专项技能评分 (Map<技能名, 分数>)
    private Map<String, Integer> specificScores;
}