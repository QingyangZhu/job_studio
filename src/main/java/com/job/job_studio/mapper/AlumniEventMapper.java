package com.job.job_studio.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.job_studio.entity.AlumniEvent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AlumniEventMapper extends BaseMapper<AlumniEvent> {
    // BaseMapper 提供了针对 AlumniEvent 实体的所有基础操作
}