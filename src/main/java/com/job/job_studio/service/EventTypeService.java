package com.job.job_studio.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.job.job_studio.entity.EventType;

public interface EventTypeService extends IService<EventType> {

    /**
     * 根据事件类型名称查询事件类型，用于事件筛选和分类。
     * @param eventTypeName 事件类型名称
     * @return 匹配的 EventType 实体
     */
    EventType getByEventTypeName(String eventTypeName);
}