package com.job.job_studio.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.job_studio.entity.StudentExperience;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentExperienceMapper extends BaseMapper<StudentExperience> {

    /**
     * 查询某个学生的所有实践经历（竞赛、干部、项目等）
     * @param studentId 学号
     * @return 实践经历记录列表
     */
    List<StudentExperience> selectByStudentId(Long studentId);

    /**
     * 查询特定级别（如国家级）的竞赛或高等级实践经历
     * @param studentId 学号
     * @param minLevel 最小竞赛等级 (例如：3代表国家级)
     * @return 高级别实践经历列表
     */
    List<StudentExperience> selectHighLevelExperiences(Long studentId, Integer minLevel);
}