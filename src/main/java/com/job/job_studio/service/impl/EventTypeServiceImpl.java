package com.job.job_studio.service.impl;

import com.job.job_studio.entity.EventType;
import com.job.job_studio.mapper.EventTypeMapper;
import com.job.job_studio.service.EventTypeService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventTypeServiceImpl extends ServiceImpl<EventTypeMapper, EventType> implements EventTypeService {

    private final EventTypeMapper eventTypeMapper;

    @Autowired
    public EventTypeServiceImpl(EventTypeMapper eventTypeMapper) {
        this.eventTypeMapper = eventTypeMapper;
    }

    @Override
    public EventType getByEventTypeName(String eventTypeName) {
        // 根据事件类型名称查询，例如查找 "编程竞赛" 的 ID
        QueryWrapper<EventType> wrapper = new QueryWrapper<>();
        wrapper.eq("event_type_name", eventTypeName);
        return eventTypeMapper.selectOne(wrapper);
    }
}