package com.job.job_studio.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.job.job_studio.entity.AssessmentResult;
import java.util.Map;

public interface AssessmentResultService extends IService<AssessmentResult> {

    /**
     * 【核心接口】能力数据状态检查（KBL-1）
     * 检查学生ID是否存在、数据是否完整，用于前端决策跳转。
     * @param studentId 学号
     * @return Map包含: studentId, dataExists, isComplete, redirectUrl (如果需要)
     */
    Map<String, Object> checkAssessmentStatus(Long studentId);

    /**
     * 获取最新的完整能力评测结果，用于可视化展示。
     * @param studentId 学号
     * @return 完整评测记录
     */
    AssessmentResult getLatestCompleteAssessment(Long studentId);
}