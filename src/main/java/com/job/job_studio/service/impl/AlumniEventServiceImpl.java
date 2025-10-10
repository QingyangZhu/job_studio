package com.job.job_studio.service.impl;

import com.job.job_studio.entity.AlumniEvent;
import com.job.job_studio.mapper.AlumniEventMapper;
import com.job.job_studio.service.AlumniEventService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlumniEventServiceImpl extends ServiceImpl<AlumniEventMapper, AlumniEvent> implements AlumniEventService {

    private final AlumniEventMapper alumniEventMapper;

    @Autowired
    public AlumniEventServiceImpl(AlumniEventMapper alumniEventMapper) {
        this.alumniEventMapper = alumniEventMapper;
    }

    @Override
    public List<AlumniEvent> getEventsByAlumniId(Long alumniId) {
        // 查询指定校友的所有特殊事件
        QueryWrapper<AlumniEvent> wrapper = new QueryWrapper<>();
        wrapper.eq("alumni_id", alumniId)
                .orderByDesc("event_start_date"); // 按事件开始日期倒序
        return alumniEventMapper.selectList(wrapper);
    }

    @Override
    public List<AlumniEvent> getEventsByLevel(String eventLevel) {
        // 查询特定级别（如国家级）的事件，用于评估学生的综合实践能力 [1, 2]
        QueryWrapper<AlumniEvent> wrapper = new QueryWrapper<>();
        wrapper.eq("event_level", eventLevel);
        return alumniEventMapper.selectList(wrapper);
    }
}