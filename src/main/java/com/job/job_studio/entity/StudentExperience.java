package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@Data
@TableName("student_experience") // 映射到 student_experience 表
public class StudentExperience {

    // 主键：经历记录ID
    @TableId("experience_id")
    private Long experienceId;

    // 外键：关联 StudentInfo
    @TableField("student_id")
    private Long studentId;

    @TableField("exp_type")
    private String expType;

    @TableField("exp_name")
    private String expName;

    @TableField("exp_start_date")
    private LocalDate expStartDate;

    @TableField("exp_end_date")
    private LocalDate expEndDate;

    // 竞赛相关指标
    @TableField("competition_level")
    private Integer competitionLevel; // 0-无, 1-校级, 2-省级, 3-国家级, 4-国际级

    @TableField("competition_rank")
    private String competitionRank;

    // 干部经历相关指标
    @TableField("cadre_role")
    private String cadreRole;

    @TableField("cadre_duration_months")
    private Integer cadreDurationMonths;
}