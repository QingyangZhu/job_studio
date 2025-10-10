package com.job.job_studio.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.job_studio.entity.CareerDetails;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CareerDetailsMapper extends BaseMapper<CareerDetails> {
    // BaseMapper 提供了针对 CareerDetails 实体的所有基础操作
}