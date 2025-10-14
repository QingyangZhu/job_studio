package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("assessment_result") // 映射到 assessment_result 表
public class AssessmentResult {

    // 主键：评测记录ID
    @TableId("assessment_id")
    private Long assessmentId;

    // 外键：关联 StudentInfo
    @TableField("student_id")
    private Long studentId;

    @TableField("assessment_date")
    private LocalDate assessmentDate;

    // 核心字段：用于前端判断是否需要跳转问卷
    @TableField("is_complete")
    private Boolean isComplete;

    // 学术基础 (K)
    @TableField("gpa_major")
    private BigDecimal gpaMajor;

    @TableField("core_gpa_change")
    private BigDecimal coreGpaChange;

    // 硬技能 (S) - 评分范围 1-5
    @TableField("python_score")
    private Integer pythonScore;

    @TableField("java_score")
    private Integer javaScore;

    @TableField("sql_score")
    private Integer sqlScore;

    @TableField("bigdata_frameworks_score")
    private Integer bigdataFrameworksScore;

    // 软技能/素质 (Q) - 评分范围 1-5
    @TableField("teamwork_score")
    private Integer teamworkScore;

    @TableField("communication_score")
    private Integer communicationScore;

    @TableField("problem_solving_score")
    private Integer problemSolvingScore;

    @TableField("resilience_score")
    private Integer resilienceScore;

    // 职业倾向
    @TableField("disc_type")
    private String discType;
}