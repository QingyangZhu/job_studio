package com.job.job_studio.controller;

import com.job.job_studio.service.JobGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jobs") // 建议统一 API 前缀
public class JobGraphController {

    private final JobGraphService jobGraphService;

    @Autowired
    public JobGraphController(JobGraphService jobGraphService) {
        this.jobGraphService = jobGraphService;
    }

    /**
     * GET /api/jobs/list
     * 获取所有可用的岗位名称列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<String>> getJobRoles() {
        List<String> jobs = jobGraphService.getAllJobRoles();
        return ResponseEntity.ok(jobs);
    }

    /**
     * GET /api/jobs/graph
     * 根据岗位名称和学生ID获取图谱数据
     */
    @GetMapping("/graph")
    public ResponseEntity<Map<String, Object>> getJobKnowledgeGraph(
            @RequestParam String jobRole,
            @RequestParam(required = false) Long studentId) {

        Map<String, Object> graphData = jobGraphService.generateForceGraphData(jobRole, studentId);
        return ResponseEntity.ok(graphData);
    }
}