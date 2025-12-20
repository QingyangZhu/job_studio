package com.job.job_studio.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.job.job_studio.dto.StudentInfoDTO;
import com.job.job_studio.entity.StudentInfo;

import java.util.List;

public interface StudentInfoService extends IService<StudentInfo> {

    /**
     * 根据学号查询学生基础信息
     * @param studentId 学号
     * @return 匹配的学生信息
     */
    StudentInfo getByStudentId(Long studentId);

    /**
     * 获取所有学生的简要信息，用于前端筛选列表
     * @return 所有学生信息列表
     */
    List<StudentInfo> getAllStudents();

    /**
     * BMS列表：查询学生信息列表（支持分页、排序和筛选）
     * @param pageNum 页码 (1-based)
     * @param pageSize 每页大小
     * @param major 筛选条件：专业
     * @param keyword 搜索关键词
     * @return 分页后的 StudentInfoDTO 列表
     */
    Page<StudentInfoDTO> getStudentPage(int pageNum, int pageSize, String major, String keyword);
}