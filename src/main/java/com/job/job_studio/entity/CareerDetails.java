package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
// 移除所有 JPA 相关的导入

@Data
@TableName("career_details") // 映射到 career_details 表
public class CareerDetails {

    @TableId("career_id")
    private Long careerId;

    // 外键字段：关联 AlumniInfo
    @TableField("alumni_id")
    private Long alumniId;

    @TableField("company_name")
    private String companyName;

    @TableField("industry")
    private String industry;

    @TableField("job_title")
    private String jobTitle;

    @TableField("employment_type")
    private String employmentType;

    @TableField("job_start_date")
    private LocalDate jobStartDate;

    @TableField("job_end_date")
    private LocalDate jobEndDate;

    @TableField("work_city")
    private String workCity;

    @TableField("job_description")
    private String jobDescription;

    // 【MyBatis-Plus 模式】：移除关系对象 AlumniInfo alumniInfo;
}