package com.job.job_studio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.job.job_studio.entity.AssessmentResult;
import com.job.job_studio.entity.StudentInfo;
import com.job.job_studio.mapper.AssessmentResultMapper;
import com.job.job_studio.service.AssessmentResultService;
import com.job.job_studio.service.StudentInfoService; // 需要注入 StudentInfoService 来检查学生是否存在
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssessmentResultServiceImpl extends ServiceImpl<AssessmentResultMapper, AssessmentResult> implements AssessmentResultService {

    private final AssessmentResultMapper assessmentResultMapper;
    private final StudentInfoService studentInfoService;
    // 假设问卷的跳转路径是固定的，方便前端直接重定向
    private static final String ASSESSMENT_REDIRECT_URL = "/assessment/new";

    @Autowired
    public AssessmentResultServiceImpl(AssessmentResultMapper assessmentResultMapper, StudentInfoService studentInfoService) {
        this.assessmentResultMapper = assessmentResultMapper;
        this.studentInfoService = studentInfoService;
    }

    @Override
    public Map<String, Object> checkAssessmentStatus(Long studentId) {
        Map<String, Object> result = new HashMap<>();
        result.put("studentId", studentId);
        result.put("isComplete", false); // 默认不完整

        // 1. 检查学生是否存在
        StudentInfo studentInfo = studentInfoService.getByStudentId(studentId);
        boolean studentExists = studentInfo!= null;
        result.put("dataExists", studentExists);

        if (!studentExists) {
            // 如果学生不存在，直接返回，isComplete 仍为 false
            result.put("redirectUrl", null);
            return result;
        }

        // 2. 检查是否存在完整的评测记录 (is_complete = true)
        QueryWrapper<AssessmentResult> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .eq("is_complete", true)
                .orderByDesc("assessment_date")
                .last("LIMIT 1");

        AssessmentResult latestComplete = assessmentResultMapper.selectOne(wrapper);

        if (latestComplete!= null) {
            // 找到完整记录，返回 true
            result.put("isComplete", true);
            result.put("redirectUrl", null);
        } else {
            // 未找到完整记录，需要进行评测，提供跳转URL
            result.put("isComplete", false);
            // 问卷跳转的安全性强化：实际项目应返回一个短时有效的Token [1]
            result.put("redirectUrl", ASSESSMENT_REDIRECT_URL + "?studentId=" + studentId);
        }

        return result;
    }

    @Override
    public AssessmentResult getLatestCompleteAssessment(Long studentId) {
        // 获取最新的完整记录，用于前端展示
        QueryWrapper<AssessmentResult> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .eq("is_complete", true)
                .orderByDesc("assessment_date")
                .last("LIMIT 1");

        return assessmentResultMapper.selectOne(wrapper);
    }
}