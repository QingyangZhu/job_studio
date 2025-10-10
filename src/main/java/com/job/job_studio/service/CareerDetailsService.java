package com.job.job_studio.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.job.job_studio.entity.CareerDetails;

import java.util.List;

public interface CareerDetailsService extends IService<CareerDetails> {

    /**
     * 【知识图谱数据支持】获取所有就业记录，并可按行业筛选，用于生成岗位知识图谱节点。
     * @param industry 行业名称（如：“互联网”、“金融/银行”）
     * @return 匹配的就业记录列表
     */
    List<CareerDetails> getJobsByIndustry(String industry);

    /**
     * 【大屏展示数据支持】获取就业去向的公司名称、职位名称及其所在城市，用于地域和热门岗位可视化 [2]。
     * @return 包含关键字段的就业记录列表
     */
    List<CareerDetails> getAllJobPlacements();
}