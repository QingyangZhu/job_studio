package com.job.job_studio.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.job.job_studio.dto.AssessmentSubmitDTO; // 确保你有这个类
import com.job.job_studio.entity.AssessmentResult;
import com.job.job_studio.entity.StudentExperience;
import com.job.job_studio.entity.StudentInfo;
import com.job.job_studio.service.AssessmentResultService;
import com.job.job_studio.service.StudentExperienceService;
import com.job.job_studio.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 【修改点1】为了解决 403 问题并符合前后端分离规范，建议加上 /api 前缀
// 请确保 SecurityConfig 中放行了 "/api/students/**"
@RestController
@RequestMapping("/students")
public class StudentAssessmentController {

    private final StudentInfoService studentInfoService;
    private final AssessmentResultService assessmentResultService;
    private final StudentExperienceService studentExperienceService;

    // 【修改点2】引入 Jackson 工具类
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public StudentAssessmentController(StudentInfoService studentInfoService,
                                       AssessmentResultService assessmentResultService,
                                       StudentExperienceService studentExperienceService) {
        this.studentInfoService = studentInfoService;
        this.assessmentResultService = assessmentResultService;
        this.studentExperienceService = studentExperienceService;
    }

    // ================== 原有查询接口 (保持不变) ==================

    /**
     * 获取所有在读学生的简要列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<StudentInfo>> getStudentList() {
        List<StudentInfo> students = studentInfoService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    /**
     * 检查该学生的能力评测数据是否完整
     */
    @GetMapping("/{studentId}/status")
    public ResponseEntity<Map<String, Object>> checkAssessmentStatus(@PathVariable Long studentId) {
        Map<String, Object> status = assessmentResultService.checkAssessmentStatus(studentId);
        if (!(Boolean) status.getOrDefault("dataExists", true)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(status);
    }

    /**
     * 获取学生完整的、最新的评测数据和实践经历
     */
    @GetMapping("/{studentId}/profile")
    public ResponseEntity<Map<String, Object>> getStudentProfile(@PathVariable Long studentId) {
        // 1. 获取最新的完整能力评测结果
        AssessmentResult assessment = assessmentResultService.getLatestCompleteAssessment(studentId);
        // 2. 获取学生的基础信息 (注意：确保你的 Service 层有 getByStudentId 方法，或者使用 getById)
        // 这里为了兼容你的原代码，保留 getByStudentId。如果报错，请改为 getById(studentId)
        StudentInfo info = studentInfoService.getById(studentId);

        // 3. 获取实践经历
        List<StudentExperience> experiences = studentExperienceService.getExperiencesByStudentId(studentId);

        if (info == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> profile = new HashMap<>();
        profile.put("info", info);
        profile.put("assessment", assessment);
        profile.put("experiences", experiences);

        return ResponseEntity.ok(profile);
    }

    // ================== 原有管理接口 (保持不变) ==================

    @PostMapping
    public ResponseEntity<String> addStudent(@RequestBody StudentInfo student) {
        boolean success = studentInfoService.save(student);
        return success ? ResponseEntity.ok("新增成功") : ResponseEntity.badRequest().body("新增失败");
    }

    @PutMapping
    public ResponseEntity<String> updateStudent(@RequestBody StudentInfo student) {
        boolean success = studentInfoService.updateById(student);
        return success ? ResponseEntity.ok("更新成功") : ResponseEntity.badRequest().body("更新失败");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        boolean success = studentInfoService.removeById(id);
        return success ? ResponseEntity.ok("删除成功") : ResponseEntity.badRequest().body("删除失败");
    }

    /**
     * [Admin] 获取指定学生的测评详情
     */
    @GetMapping("/{studentId}/assessment/detail")
    public ResponseEntity<AssessmentResult> getAssessmentDetail(@PathVariable Long studentId) {
        QueryWrapper<AssessmentResult> query = new QueryWrapper<>();
        query.eq("student_id", studentId)
                .orderByDesc("create_time") // 建议改为按 create_time 排序，如果没有该字段则用 assessment_date
                .last("LIMIT 1");

        AssessmentResult result = assessmentResultService.getOne(query);

        if (result == null) {
            result = new AssessmentResult();
            result.setStudentId(studentId);
            result.setIsComplete(false);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * [Admin] 管理员保存/更新学生的测评数据（保持原样，用于后台手动录入）
     */
    @PostMapping("/{studentId}/assessment/save")
    public ResponseEntity<String> saveAssessment(
            @PathVariable Long studentId,
            @RequestBody AssessmentResult result) {

        result.setStudentId(studentId);

        // 兼容性处理：同时设置两个时间
        if (result.getCreateTime() == null) result.setCreateTime(LocalDateTime.now());
        if (result.getAssessmentDate() == null) result.setAssessmentDate(LocalDate.now());

        result.setIsComplete(true);

        boolean success = assessmentResultService.saveOrUpdate(result);

        if (success) {
            return ResponseEntity.ok("测评数据保存成功");
        } else {
            return ResponseEntity.badRequest().body("保存失败");
        }
    }

    // ================== 【核心修改】学生端提交接口 ==================

    /**
     * 学生端提交测评（升级版）
     * 接收 DTO，自动计算 JSON 和分数，并保存到数据库
     */
    @PostMapping("/{studentId}/assessment/submit")
    public ResponseEntity<?> submitAssessment(
            @PathVariable Long studentId,
            @RequestBody AssessmentSubmitDTO dto) { // 【注意】这里参数改为了 DTO

        try {
            // 1. 数据处理：合并通用能力和专项技能
            Map<String, Integer> allSkills = new HashMap<>();
            if (dto.getGeneralScores() != null) allSkills.putAll(dto.getGeneralScores());
            if (dto.getSpecificScores() != null) allSkills.putAll(dto.getSpecificScores());

            // 2. 转换为 JSON
            String skillJson = objectMapper.writeValueAsString(allSkills);

            // 3. 计算综合分
            double avgScore = allSkills.values().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            // 4. 处理目标岗位
            String targetJobsStr = dto.getTargetJobs() != null ?
                    String.join(",", dto.getTargetJobs()) : "";

            // 5. 查询或新建记录
            QueryWrapper<AssessmentResult> query = new QueryWrapper<>();
            query.eq("student_id", studentId).last("LIMIT 1");
            AssessmentResult result = assessmentResultService.getOne(query);

            if (result == null) {
                result = new AssessmentResult();
                result.setStudentId(studentId);
            }

            // 6. 填充新字段
            result.setGeneralScore(avgScore);
            result.setTargetJobs(targetJobsStr);
            result.setSkillDetails(skillJson);
            result.setIsComplete(true);

            // 7. 时间同步 (解决数据库字段非空问题)
            LocalDateTime now = LocalDateTime.now();
            result.setCreateTime(now);           // 新字段
            result.setAssessmentDate(now.toLocalDate()); // 旧字段

            // 8. 保存
            boolean success = assessmentResultService.saveOrUpdate(result);

            if (success) {
                return ResponseEntity.ok(Map.of("message", "测评提交成功"));
            } else {
                return ResponseEntity.status(500).body("提交失败");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("提交处理失败: " + e.getMessage());
        }
    }
}