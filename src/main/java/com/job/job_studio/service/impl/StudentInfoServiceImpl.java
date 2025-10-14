package com.job.job_studio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.job.job_studio.entity.StudentInfo;
import com.job.job_studio.mapper.StudentInfoMapper;
import com.job.job_studio.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentInfoServiceImpl extends ServiceImpl<StudentInfoMapper, StudentInfo> implements StudentInfoService {

    private final StudentInfoMapper studentInfoMapper;

    @Autowired
    public StudentInfoServiceImpl(StudentInfoMapper studentInfoMapper) {
        this.studentInfoMapper = studentInfoMapper;
    }

    @Override
    public StudentInfo getByStudentId(Long studentId) {
        // 使用 BaseMapper 提供的 selectById 方法，或 QueryWrapper
        return studentInfoMapper.selectById(studentId);
    }

    @Override
    public List<StudentInfo> getAllStudents() {
        // 直接返回所有学生信息，用于前端筛选列表的初始化
        QueryWrapper<StudentInfo> wrapper = new QueryWrapper<>();
        wrapper.select("student_id", "name", "major", "enrollment_year") // 仅选择必要字段
                .orderByAsc("enrollment_year")
                .orderByAsc("student_id");
        return studentInfoMapper.selectList(wrapper);
    }
}