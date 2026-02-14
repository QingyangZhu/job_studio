package com.job.job_studio.controller;

import com.job.job_studio.dto.PasswordUpdateDTO;
import com.job.job_studio.dto.UserProfileUpdateDTO;
import com.job.job_studio.entity.StudentInfo;
import com.job.job_studio.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 获取当前登录用户名 (学号)
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    /**
     * 获取个人资料
     * GET /api/users/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            String username = getCurrentUsername();
            StudentInfo info = userService.getStudentProfile(username);
            return ResponseEntity.ok(info != null ? info : Map.of());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "获取资料失败"));
        }
    }

    /**
     * 更新个人资料
     * POST /api/users/update
     */
    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UserProfileUpdateDTO dto) {
        try {
            String username = getCurrentUsername();
            userService.updateProfile(username, dto);
            return ResponseEntity.ok(Map.of("message", "更新成功"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", "更新失败: " + e.getMessage()));
        }
    }

    /**
     * 修改密码
     * POST /api/users/password
     */
    @PostMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateDTO dto) {
        try {
            String username = getCurrentUsername();
            userService.updatePassword(username, dto);
            return ResponseEntity.ok(Map.of("message", "密码修改成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}