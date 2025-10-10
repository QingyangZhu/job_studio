package com.job.job_studio.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.job.job_studio.entity.AlumniInfo;

import java.util.List;

public interface AlumniInfoService extends IService<AlumniInfo> {

    /**
     * 根据学号（studentId）查询单个校友的详细信息。
     * @param studentId 学号
     * @return 匹配的 AlumniInfo 实体
     */
    AlumniInfo getByStudentId(String studentId);

    /**
     * 获取所有校友的基本信息，常用于大屏展示的毕业生总览。
     * @return 所有 AlumniInfo 列表
     */
    List<AlumniInfo> getAllAlumniInfo();

    /**
     * 【生涯曲线数据支持】获取指定校友的完整生涯数据（包含GPA、事件、工作），
     * 用于一次性加载某个学长/学姐的完整成长轨迹。
     * @param alumniId 校友ID
     * @return 包含关联数据的 AlumniInfo 实体
     */
    AlumniInfo getFullAlumniData(Long alumniId);
}