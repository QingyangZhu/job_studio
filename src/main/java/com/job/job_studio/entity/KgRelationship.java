package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("kg_relationship")
public class KgRelationship {

    @TableId("rel_id")
    private Long relId;

    @TableField("source_id")
    private Long sourceId;

    @TableField("target_id")
    private Long targetId;

    @TableField("rel_type")
    private String relType;

    @TableField("weight")
    private BigDecimal weight;
}