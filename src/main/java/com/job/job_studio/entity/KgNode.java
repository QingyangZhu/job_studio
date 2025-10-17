package com.job.job_studio.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("kg_node")
public class KgNode {

    @TableId("node_id")
    private Long nodeId;

    @TableField("name")
    private String name;

    @TableField("node_type")
    private String nodeType;

    @TableField("category")
    private String category;

    @TableField("description")
    private String description;

    @TableField("symbol_size")
    private Integer symbolSize;
}