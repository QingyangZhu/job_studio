package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
// 移除所有 JPA 相关的导入

@Data
@TableName("event_types") // 映射到 event_types 表
public class EventType {

    @TableId("event_type_id")
    private Long eventTypeId;

    @TableField("event_type_name")
    private String eventTypeName;

    @TableField("description")
    private String description;

    // 【MyBatis-Plus 模式】：移除关系列表 List<AlumniEvent> alumniEvents;
}