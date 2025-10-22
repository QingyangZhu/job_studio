package com.job.job_studio.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.job_studio.dto.LocationJobCountDTO;
import com.job.job_studio.entity.CareerDetails;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CareerDetailsMapper extends BaseMapper<CareerDetails> {
    // BaseMapper 提供了针对 CareerDetails 实体的所有基础操作
    /**
     * 联表查询：按工作地点（省份/城市）和岗位类型聚合校友人数
     * 假设 work_city 存储格式为 "省份,城市" 或直接是城市名
     */
    @Select("SELECT " +
            "SUBSTRING_INDEX(work_city, ',', 1) AS province, " +
            "SUBSTRING_INDEX(work_city, ',', -1) AS city, " +
            "job_title AS jobTitle, " +
            "COUNT(alumni_id) AS jobCount " +
            "FROM career_details " +
            "GROUP BY province, city, jobTitle")
    List<LocationJobCountDTO> aggregateJobDistribution();
}