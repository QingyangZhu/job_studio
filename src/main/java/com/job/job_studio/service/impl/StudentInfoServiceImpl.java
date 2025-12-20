package com.job.job_studio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.job.job_studio.dto.StudentInfoDTO;
import com.job.job_studio.entity.StudentInfo;
import com.job.job_studio.mapper.StudentInfoMapper;
import com.job.job_studio.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        wrapper.select("student_id", "name", "major", "enrollment_year", "class_name") // 仅选择必要字段
                .orderByAsc("enrollment_year")
                .orderByAsc("student_id");
        return studentInfoMapper.selectList(wrapper);
    }

    @Override
    public Page<StudentInfoDTO> getStudentPage(int pageNum, int pageSize, String major, String keyword) {
        // MyBatis-Plus 分页对象
        Page<StudentInfo> page = new Page<>(pageNum, pageSize);

        // 构建查询条件
        QueryWrapper<StudentInfo> wrapper = new QueryWrapper<>();

        // 专业筛选
        if (major!= null &&!major.isEmpty()) {
            wrapper.eq("major", major);
        }

        // 关键词搜索（针对姓名、学号）
        if (keyword!= null &&!keyword.isEmpty()) {
            wrapper.and(w -> w.like("name", keyword).or().like("student_id", keyword));
        }

        // 默认排序：按入学年份降序
        wrapper.orderByDesc("enrollment_year");

        // 执行查询
        Page<StudentInfo> resultPage = studentInfoMapper.selectPage(page, wrapper);

        // 实体转 DTO
        Page<StudentInfoDTO> dtoPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<StudentInfoDTO> dtoList = resultPage.getRecords().stream()
                .map(StudentInfoDTO::fromEntity)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }
}