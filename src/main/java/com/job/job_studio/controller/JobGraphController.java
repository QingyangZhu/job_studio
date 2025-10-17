package com.job.job_studio.controller;

import com.job.job_studio.service.JobGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/jobs")
public class JobGraphController {

    private final JobGraphService jobGraphService;

    @Autowired
    public JobGraphController(JobGraphService jobGraphService) {
        this.jobGraphService = jobGraphService;
    }

    /**
     * GET /api/v1/jobs/graph?jobRole=前端开发工程师&studentId=22010101
     * 接口功能：获取目标岗位的知识图谱数据结构，并根据用户ID计算推荐路径。
     * @param jobRole 目标岗位名称（Query Parameter）
     * @param studentId 学生ID（Query Parameter，用于个性化推荐）
     * @return ECharts Force-Directed Graph 兼容的 JSON 数据。
     */
    @GetMapping("/graph")
    public ResponseEntity<Map<String, Object>> getJobKnowledgeGraph(
            @RequestParam String jobRole,
            @RequestParam Long studentId) {

        // 调用 Service 模拟计算图谱和最短路径
        Map<String, Object> graphData = jobGraphService.generateForceGraphData(jobRole, studentId);

        return ResponseEntity.ok(graphData);
    }
}