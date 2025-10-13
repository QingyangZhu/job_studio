package com.job.job_studio.controller;

import com.job.job_studio.entity.CareerDetails;
import com.job.job_studio.entity.AlumniEvent;
import com.job.job_studio.service.CareerDetailsService;
import com.job.job_studio.service.AlumniEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs") // 定义基础路径
public class JobAnalysisController {

    private final CareerDetailsService careerDetailsService;
    private final AlumniEventService alumniEventService;

    @Autowired
    public JobAnalysisController(CareerDetailsService careerDetailsService,
                                 AlumniEventService alumniEventService) {
        this.careerDetailsService = careerDetailsService;
        this.alumniEventService = alumniEventService;
    }

    /**
     * GET /api/jobs/placements
     * 获取所有毕业生的就业去向概览，用于大屏地图和行业分布图。
     * @return 所有 CareerDetails 列表
     */
    @GetMapping("/placements")
    public ResponseEntity<List<CareerDetails>> getAllJobPlacements() {
        List<CareerDetails> placements = careerDetailsService.getAllJobPlacements();
        return ResponseEntity.ok(placements);
    }

    /**
     * GET /api/jobs/filter?industry=互联网
     * 根据行业名称筛选就业记录，支持岗位能力知识图谱的行业聚焦。
     * @param industry 行业名称（可选）
     * @return 匹配的就业记录列表
     */
    @GetMapping("/filter")
    public ResponseEntity<List<CareerDetails>> filterJobsByIndustry(@RequestParam(required = false) String industry) {
        List<CareerDetails> filteredJobs = careerDetailsService.getJobsByIndustry(industry);
        return ResponseEntity.ok(filteredJobs);
    }

    /**
     * GET /api/jobs/high_level_events
     * 获取所有国家级/高级别的特殊事件记录，用于突出优秀毕业生的成就。
     * @return 特定级别的事件列表
     */
    @GetMapping("/high_level_events")
    public ResponseEntity<List<AlumniEvent>> getHighLevelEvents() {
        // 查询 “国家级” 事件，用于前端展示成就墙
        List<AlumniEvent> events = alumniEventService.getEventsByLevel("国家级");
        return ResponseEntity.ok(events);
    }
}