package com.job.job_studio.service.impl;

import com.job.job_studio.entity.AlumniInfo;
import com.job.job_studio.mapper.AlumniInfoMapper;
import com.job.job_studio.service.AlumniInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlumniInfoServiceImpl extends ServiceImpl<AlumniInfoMapper, AlumniInfo> implements AlumniInfoService {

    private final AlumniInfoMapper alumniInfoMapper;

    // 推荐使用构造器注入 (Constructor Injection)
    @Autowired
    public AlumniInfoServiceImpl(AlumniInfoMapper alumniInfoMapper) {
        this.alumniInfoMapper = alumniInfoMapper;
    }

    @Override
    public AlumniInfo getByStudentId(String studentId) {
        // 使用 QueryWrapper 根据学号进行精确查询
        QueryWrapper<AlumniInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId);
        return alumniInfoMapper.selectOne(wrapper);
    }

    @Override
    public List<AlumniInfo> getAllAlumniInfo() {
        // 直接使用 ServiceImpl 提供的 list 方法查询所有数据
        return this.list();
    }

    @Override
    public AlumniInfo getFullAlumniData(Long alumniId) {
        // 1. 获取基本信息
        AlumniInfo alumniInfo = this.getById(alumniId);

        // 2. 【TODO: 复杂联表查询】
        // 在实际项目中，此处需要手动调用 AcademicPerformanceService, AlumniEventService, CareerDetailsService
        // 来获取关联数据，并手动设置回 alumniInfo 对象中，以实现完整的生涯轨迹数据加载。
        // 例如：
        // List<AcademicPerformance> gpas = academicPerformanceService.getPerformanceByAlumniId(alumniId);
        // alumniInfo.setGpaRecords(gpas);

        return alumniInfo;
    }
}