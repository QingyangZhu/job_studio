package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
// 移除所有 JPA 相关的导入

@Data
@TableName("academic_performance") // 映射到 academic_performance 表
public class AcademicPerformance {

    @TableId("gpa_id")
    private Long gpaId;

    // 外键字段：关联 AlumniInfo，必须显式保留以支持查询
    @TableField("alumni_id")
    private Long alumniId;

    @TableField("academic_year")
    private String academicYear;

    @TableField("semester")
    private String semester;

    @TableField("overall_gpa")
    private BigDecimal overallGpa;

    @TableField("major_gpa")
    private BigDecimal majorGpa;

    // 【MyBatis-Plus 模式】：移除关系对象 AlumniInfo alumniInfo;
}