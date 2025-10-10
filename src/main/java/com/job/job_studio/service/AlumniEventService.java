package com.job.job_studio.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.job.job_studio.entity.AlumniEvent;

import java.util.List;

public interface AlumniEventService extends IService<AlumniEvent> {

    /**
     * 【生涯曲线数据支持】查询指定校友ID的所有特殊事件（竞赛、干部、实习等）。
     * @param alumniId 校友ID
     * @return 事件记录列表
     */
    List<AlumniEvent> getEventsByAlumniId(Long alumniId);

    /**
     * 【分析数据支持】查询特定级别（如国家级、省级）的所有特殊事件，用于评估高等级活动参与度 [3, 4]。
     * @param eventLevel 事件级别（如：“国家级”）
     * @return 匹配的事件记录列表
     */
    List<AlumniEvent> getEventsByLevel(String eventLevel);
}