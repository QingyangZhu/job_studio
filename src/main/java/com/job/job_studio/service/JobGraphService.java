package com.job.job_studio.service;

import java.util.List;
import java.util.Map;

public interface JobGraphService {

    /**
     * 根据目标岗位和用户ID生成力导向图数据结构。
     * 在MVP阶段，该方法模拟 A* 算法和 KG 查询逻辑。
     * @param jobRoleName 目标岗位名称（例如："前端开发工程师"）
     * @param studentId 用户ID (用于获取其当前技能，影响路径计算)
     * @return 符合 ECharts Graph 格式的 JSON 数据 (Map)
     */
    Map<String, Object> generateForceGraphData(String jobRoleName, Long studentId);
    List<String> getAllJobRoles();
}