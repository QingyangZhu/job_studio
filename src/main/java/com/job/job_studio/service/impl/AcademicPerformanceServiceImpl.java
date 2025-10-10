package com.job.job_studio.service.impl;

import com.job.job_studio.entity.AcademicPerformance;
import com.job.job_studio.mapper.AcademicPerformanceMapper;
import com.job.job_studio.service.AcademicPerformanceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AcademicPerformanceServiceImpl extends ServiceImpl<AcademicPerformanceMapper, AcademicPerformance> implements AcademicPerformanceService {

    private final AcademicPerformanceMapper academicPerformanceMapper;

    @Autowired
    public AcademicPerformanceServiceImpl(AcademicPerformanceMapper academicPerformanceMapper) {
        this.academicPerformanceMapper = academicPerformanceMapper;
    }

    @Override
    public List<AcademicPerformance> getPerformanceByAlumniId(Long alumniId) {
        // 根据外键 alumni_id 查询，并按学年和学期排序，以描绘生涯曲线
        QueryWrapper<AcademicPerformance> wrapper = new QueryWrapper<>();
        wrapper.eq("alumni_id", alumniId)
                .orderByAsc("academic_year")
                .orderByAsc("semester"); // 注意：实际应用中需要确保 semester 字段可以正确排序 (如 "1" 和 "2" 或 "秋季" 和 "春季")
        return academicPerformanceMapper.selectList(wrapper);
    }

    @Override
    public List<AcademicPerformance> getMajorGpaTrends(BigDecimal minMajorGpa) {
        // 查询主修科目 GPA 不低于指定值的记录
        QueryWrapper<AcademicPerformance> wrapper = new QueryWrapper<>();
        if (minMajorGpa!= null) {
            wrapper.ge("major_gpa", minMajorGpa);
        }
        return academicPerformanceMapper.selectList(wrapper);
    }
}