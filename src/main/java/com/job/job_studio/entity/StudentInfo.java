package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student_info") // 映射到 student_info 表
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
}