package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
// 移除所有 JPA 相关的导入

@Data
@TableName("alumni_info") // 映射到 alumni_info 表
public class AlumniInfo {

    // 主键，MyBatis-Plus 默认采用雪花算法或数据库自增
    @TableId("alumni_id")
    private Long alumniId;

    // 字段名和列名不同，需要显式映射
    @TableField("student_id")
    private String studentId;

    @TableField("name")
    private String name;

    @TableField("gender")
    private String gender;

    @TableField("major")
    private String major;

    @TableField("graduation_year")
    private Integer graduationYear;

    @TableField("contact_email")
    private String contactEmail;

    // 【MyBatis-Plus 模式】：不再直接定义关系列表（如 List<AcademicPerformance>），
    // 关系数据在 Service 层通过 ID (alumniId) 查询获得。
}