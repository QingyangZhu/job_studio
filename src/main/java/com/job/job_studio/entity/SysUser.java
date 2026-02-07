package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long userId;
    private String username;
    private String password; // 存储加密后的密码
    private String role;     // ADMIN 或 STUDENT
    private Long studentId;  // 仅学生有值
    private LocalDateTime createTime;
}