package com.job.job_studio.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.job_studio.entity.AcademicPerformance;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AcademicPerformanceMapper extends BaseMapper<AcademicPerformance> {
    // BaseMapper 提供了针对 AcademicPerformance 实体的所有基础操作
}