package com.job.job_studio.controller;

import com.job.job_studio.entity.AlumniInfo;
import com.job.job_studio.entity.AcademicPerformance;
import com.job.job_studio.entity.AlumniEvent;
import com.job.job_studio.service.AlumniInfoService;
import com.job.job_studio.service.AcademicPerformanceService;
import com.job.job_studio.service.AlumniEventService;
import com.job.job_studio.service.AlumniTimelineService; // 引入新的 Service
import com.job.job_studio.vo.AlumniTimelineVO; // 引入新的 VO
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alumni") //
public class AlumniDataController {

    private final AlumniInfoService alumniInfoService;
    private final AcademicPerformanceService academicPerformanceService;
    private final AlumniEventService alumniEventService;
    private final AlumniTimelineService alumniTimelineService; // 新增注入

    @Autowired
    public AlumniDataController(AlumniInfoService alumniInfoService,
                                AcademicPerformanceService academicPerformanceService,
                                AlumniEventService alumniEventService,
                                AlumniTimelineService alumniTimelineService) { // 更新构造器
        this.alumniInfoService = alumniInfoService;
        this.academicPerformanceService = academicPerformanceService;
        this.alumniEventService = alumniEventService;
        this.alumniTimelineService = alumniTimelineService; // 赋值
    }

    /**
     * GET /api/v1/alumni/all
     * 获取所有校友的基本信息列表，用于大屏总览或下拉选择框。
     * @return 包含所有校友基本信息的列表
     */
    @GetMapping("/all")
    public ResponseEntity<List<AlumniInfo>> getAllAlumni() {
        List<AlumniInfo> alumniList = alumniInfoService.getAllAlumniInfo();
        return ResponseEntity.ok(alumniList);
    }

    /**
     * GET /api/v1/alumni/{alumniId}
     * 根据校友ID获取该校友的完整生涯轨迹数据（基本信息 + GPA + 事件）。
     * @param alumniId 校友ID
     * @return 包含该校友所有数据的聚合对象
     */
    @GetMapping("/{alumniId}")
    public ResponseEntity<Map<String, Object>> getFullAlumniData(@PathVariable Long alumniId) {
        // 1. 获取基本信息
        AlumniInfo info = alumniInfoService.getById(alumniId);
        if (info == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. 获取 GPA 曲线数据
        List<AcademicPerformance> gpaList = academicPerformanceService.getPerformanceByAlumniId(alumniId);

        // 3. 获取特殊事件数据 (竞赛、干部、实习等)
        List<AlumniEvent> eventList = alumniEventService.getEventsByAlumniId(alumniId);

        // 4. 聚合数据返回
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("info", info);
        responseData.put("gpaCurve", gpaList);
        responseData.put("events", eventList);

        return ResponseEntity.ok(responseData);
    }

    /**
     * GET /api/v1/alumni/{alumniId}/timeline
     * 接口功能：获取校友的多维度成长曲线数据（GPA线、持续区域、里程碑标记）。
     * @param alumniId 校友ID
     * @return D3.js 可视化所需的 AlumniTimelineVO JSON 结构。
     */
    @GetMapping("/{alumniId}/timeline")
    public ResponseEntity<AlumniTimelineVO> getAlumniTimeline(@PathVariable Long alumniId) {
        AlumniTimelineVO timelineData = alumniTimelineService.getAlumniTimelineData(alumniId);

        if (timelineData == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(timelineData);
    }
}