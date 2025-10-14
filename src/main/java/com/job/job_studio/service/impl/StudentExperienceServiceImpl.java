package com.job.job_studio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.job.job_studio.entity.StudentExperience;
import com.job.job_studio.mapper.StudentExperienceMapper;
import com.job.job_studio.service.StudentExperienceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentExperienceServiceImpl extends ServiceImpl<StudentExperienceMapper, StudentExperience> implements StudentExperienceService {

    private final StudentExperienceMapper studentExperienceMapper;

    @Autowired
    public StudentExperienceServiceImpl(StudentExperienceMapper studentExperienceMapper) {
        this.studentExperienceMapper = studentExperienceMapper;
    }

    @Override
    public List<StudentExperience> getExperiencesByStudentId(Long studentId) {
        // 查询该学生的所有经历，按开始日期倒序排列
        QueryWrapper<StudentExperience> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .orderByDesc("exp_start_date");
        return studentExperienceMapper.selectList(wrapper);
    }

    @Override
    public StudentExperience getHighestCompetitionAchievement(Long studentId) {
        // 聚合查询逻辑：查找该学生获得的最高竞赛等级记录
        QueryWrapper<StudentExperience> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .in("exp_type", "竞赛") // 限定为竞赛类型
                .orderByDesc("competition_level") // 按竞赛级别倒序排序 (4为国际级，3为国家级) [2, 3]
                .orderByAsc("competition_rank") // 奖项内部排序 (可选，如一等奖优先)
                .last("LIMIT 1");

        return studentExperienceMapper.selectOne(wrapper);
    }
}