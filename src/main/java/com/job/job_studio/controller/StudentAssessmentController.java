package com.job.job_studio.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.job.job_studio.entity.StudentInfo;
import com.job.job_studio.entity.AssessmentResult;
import com.job.job_studio.entity.StudentExperience;
import com.job.job_studio.service.StudentInfoService;
import com.job.job_studio.service.AssessmentResultService;
import com.job.job_studio.service.StudentExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 基础路径定义为 /api/v1/students
@RestController
@RequestMapping("/students")
// 由于您已配置全局 CORS，这里不再需要 @CrossOrigin
public class StudentAssessmentController {

    private final StudentInfoService studentInfoService;
    private final AssessmentResultService assessmentResultService;
    private final StudentExperienceService studentExperienceService;

    @Autowired
    public StudentAssessmentController(StudentInfoService studentInfoService,
                                       AssessmentResultService assessmentResultService,
                                       StudentExperienceService studentExperienceService) {
        this.studentInfoService = studentInfoService;
        this.assessmentResultService = assessmentResultService;
        this.studentExperienceService = studentExperienceService;
    }

    /**
     * GET /api/v1/students/list
     * 接口功能：获取所有在读学生的简要列表，用于前端筛选器的下拉菜单。
     * @return 包含学生ID, 姓名, 专业等字段的列表。
     */
    @GetMapping("/list")
    public ResponseEntity<List<StudentInfo>> getStudentList() {
        List<StudentInfo> students = studentInfoService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    /**
     * GET /api/v1/students/{studentId}/status
     * 接口功能：【核心决策接口】检查该学生的能力评测数据是否完整。
     * 使用 @PathVariable 绑定路径中的学生ID [1, 2, 3]。
     * @param studentId 学号/ID
     * @return 包含 isComplete 和 redirectUrl 的 Map。
     */
    @GetMapping("/{studentId}/status")
    public ResponseEntity<Map<String, Object>> checkAssessmentStatus(@PathVariable Long studentId) {
        // 调用 Service 层实现 KBL-1 逻辑
        Map<String, Object> status = assessmentResultService.checkAssessmentStatus(studentId);

        // 如果 Service 返回的 dataExists 为 false，则学生ID无效
        if (!(Boolean) status.getOrDefault("dataExists", true)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(status);
    }

    /**
     * GET /api/v1/students/{studentId}/profile
     * 接口功能：获取学生完整的、最新的评测数据和实践经历，用于左上角的评测结果可视化。
     * 仅当前端通过 status 接口确认 isComplete 为 true 后才应调用此接口。
     * @param studentId 学号/ID
     * @return 聚合了所有能力的完整学生画像。
     */
    @GetMapping("/{studentId}/profile")
    public ResponseEntity<Map<String, Object>> getStudentProfile(@PathVariable Long studentId) {
        // 1. 获取最新的完整能力评测结果
        AssessmentResult assessment = assessmentResultService.getLatestCompleteAssessment(studentId);

        // 2. 获取学生的基础信息
        StudentInfo info = studentInfoService.getByStudentId(studentId);

        // 3. 获取实践经历（用于高光成就展示）
        List<StudentExperience> experiences = studentExperienceService.getExperiencesByStudentId(studentId);

        if (assessment == null) {
            // 如果接口被错误调用，但后端没有完整数据，返回 404
            return ResponseEntity.notFound().build();
        }

        // 4. 聚合所有数据，构建完整的学生画像 DTO (Map 模拟)
        Map<String, Object> profile = new HashMap<>();
        profile.put("info", info); // 基础信息
        profile.put("assessment", assessment); // 核心 KSQ 评测得分
        profile.put("experiences", experiences); // 实践经历列表

        return ResponseEntity.ok(profile);
    }

    // --- 新增管理接口 ---

    /**
     * 新增学生信息
     */
    @PostMapping
    public ResponseEntity<String> addStudent(@RequestBody StudentInfo student) {
        boolean success = studentInfoService.save(student);
        return success ? ResponseEntity.ok("新增成功") : ResponseEntity.badRequest().body("新增失败");
    }

    /**
     * 更新学生信息
     */
    @PutMapping
    public ResponseEntity<String> updateStudent(@RequestBody StudentInfo student) {
        boolean success = studentInfoService.updateById(student);
        return success ? ResponseEntity.ok("更新成功") : ResponseEntity.badRequest().body("更新失败");
    }

    /**
     * 删除学生信息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable Long id) {
        boolean success = studentInfoService.removeById(id);
        return success ? ResponseEntity.ok("删除成功") : ResponseEntity.badRequest().body("删除失败");
    }

    /**
     * [Admin] 获取指定学生的测评详情，用于后台回显
     * @param studentId 学生ID
     * @return 测评对象，如果不存在则返回默认空结构
     */
    @GetMapping("/{studentId}/assessment/detail")
    public ResponseEntity<AssessmentResult> getAssessmentDetail(@PathVariable Long studentId) {
        // 使用 Service 或 Mapper 直接查询
        // 这里假设 assessmentResultService.getOne 配合 wrapper 使用
        QueryWrapper<AssessmentResult> query = new QueryWrapper<>();
        query.eq("student_id", studentId)
                .orderByDesc("assessment_date") // 取最近的一次
                .last("LIMIT 1");

        AssessmentResult result = assessmentResultService.getOne(query);

        // 如果没有数据，返回一个带有 studentId 的空对象，方便前端处理新增逻辑
        if (result == null) {
            result = new AssessmentResult();
            result.setStudentId(studentId);
            result.setIsComplete(false); // 标记为未完成
        }

        return ResponseEntity.ok(result);
    }

    /**
     * [Admin] 管理员保存/更新学生的测评数据（补充测评）
     * @param studentId 路径参数确保 ID 一致性
     * @param result 前端提交的测评数据
     */
    @PostMapping("/{studentId}/assessment/save")
    public ResponseEntity<String> saveAssessment(
            @PathVariable Long studentId,
            @RequestBody AssessmentResult result) {

        // 1. 强制绑定 StudentID，防止数据错乱
        result.setStudentId(studentId);

        // 2. 设置必要的默认值
        if (result.getAssessmentDate() == null) {
            result.setAssessmentDate(LocalDate.now());
        }
        result.setIsComplete(true); // 管理员录入的视为有效完整数据

        // 3. 判断是新增还是更新
        // 如果 assessmentId 为空，则是新增；否则是更新
        // MyBatis-Plus 的 saveOrUpdate 会根据主键自动判断，但为了保险，我们可以先查一下
        boolean success = assessmentResultService.saveOrUpdate(result);

        if (success) {
            return ResponseEntity.ok("测评数据保存成功");
        } else {
            return ResponseEntity.badRequest().body("保存失败");
        }
    }
}