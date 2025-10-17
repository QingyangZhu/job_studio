package com.job.job_studio.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.job_studio.entity.KgNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface KgNodeMapper extends BaseMapper<KgNode> {

    /**
     * 联表查询：根据目标岗位（JobRole）名称获取所有相关节点和关系 [1]。
     * 实际生产中应使用图数据库查询语言（如 Cypher）来替代复杂的 SQL JOIN。
     */
    @Select("SELECT * FROM kg_node WHERE node_type = #{nodeType} OR node_id IN " +
            "(SELECT source_id FROM kg_relationship WHERE target_id IN " +
            " (SELECT node_id FROM kg_node WHERE name = #{jobRoleName} AND node_type = 'JobRole')) " +
            "OR node_id IN (SELECT target_id FROM kg_relationship WHERE source_id IN " +
            " (SELECT node_id FROM kg_node WHERE name = #{jobRoleName} AND node_type = 'JobRole'))")
    List<KgNode> selectNodesByJobRole(@Param("jobRoleName") String jobRoleName, @Param("nodeType") String nodeType);

    // 简化：获取所有节点
    List<KgNode> selectAllNodes();
}