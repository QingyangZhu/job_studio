package com.job.job_studio.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.job.job_studio.dto.AuthResponse;
import com.job.job_studio.dto.LoginRequest;
import com.job.job_studio.entity.AssessmentResult;
import com.job.job_studio.mapper.AssessmentResultMapper;
import com.job.job_studio.security.LoginUser;
import com.job.job_studio.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    AssessmentResultMapper assessmentResultMapper;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        // 1. Spring Security 认证 (会自动调用 UserDetailsService)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. 获取用户信息
        LoginUser userDetails = (LoginUser) authentication.getPrincipal();
        String jwt = jwtUtils.generateJwtToken(userDetails.getUsername());

        // 3. 检查测评状态 (核心业务逻辑)
        boolean assessmentCompleted = false;

        if ("STUDENT".equals(userDetails.getRole()) && userDetails.getStudentId() != null) {
            // 查询 AssessmentResult 表是否有记录
            Long count = assessmentResultMapper.selectCount(
                    new QueryWrapper<AssessmentResult>().eq("student_id", userDetails.getStudentId())
            );
            assessmentCompleted = (count > 0);
        } else if ("ADMIN".equals(userDetails.getRole())) {
            assessmentCompleted = true; // 管理员不需要测评
        }

        // 4. 返回完整信息
        return ResponseEntity.ok(new AuthResponse(
                jwt,
                userDetails.getRole(),
                userDetails.getStudentId(),
                assessmentCompleted
        ));
    }
}