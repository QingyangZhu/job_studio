package com.job.job_studio.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.job.job_studio.entity.StudentExperience;

import java.util.List;

public interface StudentExperienceService extends IService<StudentExperience> {

    /**
     * 查询指定学生的实践经历列表（竞赛、干部、项目等）
     * @param studentId 学号
     * @return 实践经历记录列表
     */
    List<StudentExperience> getExperiencesByStudentId(Long studentId);

    /**
     * 聚合查询学生在高级别竞赛中获得的最高成就
     * @param studentId 学号
     * @return 最高成就记录
     */
    StudentExperience getHighestCompetitionAchievement(Long studentId);
}