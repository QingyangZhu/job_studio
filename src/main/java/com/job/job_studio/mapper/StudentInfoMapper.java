package com.job.job_studio.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.job_studio.entity.StudentInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentInfoMapper extends BaseMapper<StudentInfo> {

    /**
     * 根据入学年份查询学生列表（用于筛选）
     * @param enrollmentYear 入学年份
     * @return 匹配的学生信息列表
     */
    List<StudentInfo> selectByEnrollmentYear(Integer enrollmentYear);

    /**
     * 根据学号精确查询单个学生信息（用于身份验证和数据查找）
     * @param studentId 学号
     * @return 匹配的学生信息
     */
    StudentInfo selectByStudentId(Long studentId);
}