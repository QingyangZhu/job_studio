package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
// 移除所有 JPA 相关的导入

@Data
@TableName("alumni_events") // 映射到 alumni_events 表
public class AlumniEvent {

    @TableId("event_instance_id")
    private Long eventInstanceId;

    // 外键字段：关联 AlumniInfo
    @TableField("alumni_id")
    private Long alumniId;

    // 外键字段：关联 EventType
    @TableField("event_type_id")
    private Long eventTypeId;

    @TableField("event_name")
    private String eventName;

    @TableField("event_start_date")
    private LocalDate eventStartDate;

    @TableField("event_end_date")
    private LocalDate eventEndDate;

    @TableField("outcome")
    private String outcome;

    @TableField("event_level")
    private String eventLevel;

    // 【MyBatis-Plus 模式】：移除关系对象 AlumniInfo alumniInfo 和 EventType eventType;
}