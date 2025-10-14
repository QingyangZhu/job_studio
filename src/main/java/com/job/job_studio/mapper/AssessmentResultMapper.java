package com.job.job_studio.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.job.job_studio.entity.AssessmentResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AssessmentResultMapper extends BaseMapper<AssessmentResult> {

    /**
     * 根据学生ID和数据完整性状态查询最近一次评测记录
     * 用于后端 KBL-1 逻辑：判断是否已存在完整的评测报告
     * @param studentId 学号
     * @param isComplete 完整性标志 (true/false)
     * @return 最近一次评测结果列表（通常只取最新一条）
     */
    List<AssessmentResult> selectLatestByStudentIdAndCompletion(Long studentId, Boolean isComplete);

    /**
     * 查询某个学生的所有评测记录，用于历史轨迹展示
     * @param studentId 学号
     * @return 评测记录列表，按日期倒序
     */
    List<AssessmentResult> selectAllByStudentId(Long studentId);
}