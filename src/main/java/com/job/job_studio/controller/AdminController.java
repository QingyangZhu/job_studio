package com.job.job_studio.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.job.job_studio.dto.AdminDashboardVO;
import com.job.job_studio.entity.AlumniInfo;
import com.job.job_studio.entity.AssessmentResult;
import com.job.job_studio.entity.StudentInfo;
import com.job.job_studio.service.AlumniInfoService;
import com.job.job_studio.service.AssessmentResultService;
import com.job.job_studio.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final StudentInfoService studentInfoService;
    private final AlumniInfoService alumniInfoService;
    private final AssessmentResultService assessmentResultService;

    // 简单的配置注入，用于鉴权
    @Value("${admin.username:admin}")
    private String adminUsername;
    @Value("${admin.password:123456}")
    private String adminPassword;

    public AdminController(StudentInfoService studentInfoService,
                           AlumniInfoService alumniInfoService,
                           AssessmentResultService assessmentResultService) {
        this.studentInfoService = studentInfoService;
        this.alumniInfoService = alumniInfoService;
        this.assessmentResultService = assessmentResultService;
    }

    /**
     * 管理员登录接口
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        if (adminUsername.equals(username) && adminPassword.equals(password)) {
            Map<String, Object> response = new HashMap<>();
            response.put("token", "mock-token-" + System.currentTimeMillis()); // 模拟 Token
            response.put("username", username);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "用户名或密码错误"));
    }

    /**
     * 获取仪表盘统计数据
     */
    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardVO> getDashboardStats() {
        AdminDashboardVO vo = new AdminDashboardVO();

        // 1. 基础计数
        long studentCount = studentInfoService.count();
        long alumniCount = alumniInfoService.count();
        vo.setTotalStudents(studentCount);
        vo.setTotalAlumni(alumniCount);

        // 2. 计算评测完成率
        QueryWrapper<AssessmentResult> query = new QueryWrapper<>();
        query.select("distinct student_id").eq("is_complete", true);
        long completedCount = assessmentResultService.count(query);
        vo.setAssessmentCount(completedCount);
        vo.setCompletionRate(studentCount > 0 ? (double) completedCount / studentCount : 0.0);

        // 3. 聚合分布数据 (使用 Java Stream 处理，实际生产建议写 SQL Group By)
        // 3.1 学生专业分布
        Map<String, Long> majorDist = studentInfoService.list().stream()
                .collect(Collectors.groupingBy(StudentInfo::getMajor, Collectors.counting()));
        vo.setStudentMajorDistribution(majorDist);

        // 3.2 校友毕业年份分布
        Map<String, Long> yearDist = alumniInfoService.list().stream()
                .collect(Collectors.groupingBy(
                        a -> String.valueOf(a.getGraduationYear()),
                        Collectors.counting()
                ));
        vo.setAlumniGraduationYearDistribution(yearDist);

        return ResponseEntity.ok(vo);
    }
}