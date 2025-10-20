package com.job.job_studio.service;

import com.job.job_studio.vo.AlumniTimelineVO;

public interface AlumniTimelineService {

    /**
     * 获取指定校友的多维度成长轨迹数据，用于 D3.js 可视化。
     * @param alumniId 校友ID
     * @return 聚合后的 AlumniTimelineVO 对象
     */
    AlumniTimelineVO getAlumniTimelineData(Long alumniId);
}