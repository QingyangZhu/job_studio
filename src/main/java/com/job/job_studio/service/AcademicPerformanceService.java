package com.job.job_studio.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.job.job_studio.entity.AcademicPerformance;

import java.math.BigDecimal;
import java.util.List;

public interface AcademicPerformanceService extends IService<AcademicPerformance> {

    /**
     * 【生涯曲线数据支持】查询指定校友ID的所有学期 GPA 记录，并按学年学期排序。
     * @param alumniId 校友ID
     * @return GPA 记录列表
     */
    List<AcademicPerformance> getPerformanceByAlumniId(Long alumniId);

    /**
     * 【分析数据支持】获取所有毕业生的主修科目 GPA 记录，用于分析专业核心能力的整体趋势 [1]。
     * @param minMajorGpa 筛选的最低主修科目 GPA 值（可选）
     * @return GPA 记录列表
     */
    List<AcademicPerformance> getMajorGpaTrends(BigDecimal minMajorGpa);
}