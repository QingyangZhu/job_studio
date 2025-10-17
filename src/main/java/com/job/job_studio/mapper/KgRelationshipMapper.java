package com.job.job_studio.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.job_studio.entity.KgRelationship;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface KgRelationshipMapper extends BaseMapper<KgRelationship> {

    // 简化：获取所有关系
    List<KgRelationship> selectAllRelationships();
}