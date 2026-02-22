package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("assessment_result")
public class AssessmentResult {

    // === 核心修复点：映射数据库的 assessment_id ===
    @TableId(value = "assessment_id", type = IdType.AUTO)
    private Long id; // Java里还是叫 id，方便代码调用，但映射到 DB 的 assessment_id

    @TableField("student_id")
    private Long studentId;

    @TableField("assessment_date")
    private LocalDate assessmentDate; // 保留旧字段兼容

    @TableField("create_time")
    private LocalDateTime createTime; // 新增字段，记录精确时间

    @TableField("is_complete")
    private Boolean isComplete;

    // === 新增的能力画像字段 ===
    @TableField("general_score")
    private Double generalScore;

    @TableField("target_jobs")
    private String targetJobs;

    @TableField("skill_details")
    private String skillDetails; // JSON 字符串

    // === 保留原有的旧字段 (防止报错，虽然新逻辑可能暂不用它们) ===
    @TableField("gpa_major")
    private Double gpaMajor;

    @TableField("python_score")
    private Integer pythonScore;

    @TableField("java_score")
    private Integer javaScore;

    // ... 其他旧字段可以按需保留，如果不报错可以不写
}