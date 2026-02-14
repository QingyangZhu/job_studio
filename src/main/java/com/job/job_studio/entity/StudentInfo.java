package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student_info")
public class StudentInfo {

    // 主键：学号 (对应 student_id)
    @TableId("student_id")
    private Long studentId;

    @TableField("name")
    private String name;

    @TableField("major")
    private String major;

    @TableField("class_name")
    private String className;

    @TableField("enrollment_year")
    private Integer enrollmentYear;

    @TableField("contact_email")
    private String contactEmail;

    // === 新增字段 ===

    @TableField("phone")
    private String phone;

    @TableField("target_job")
    private String targetJob;

    @TableField("github_link")
    private String githubLink;

    @TableField("bio")
    private String bio;
}