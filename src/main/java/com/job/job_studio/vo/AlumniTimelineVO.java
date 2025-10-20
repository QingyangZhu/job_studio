package com.job.job_studio.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 对应 D3.js 曲线所需的最终 JSON 结构
 */
@Data
public class AlumniTimelineVO {

    private Long alumniId;
    private String alumniName;
    private String graduationYear;

    // 智育：GPA 连续数据点 (对应 D3.js 主线图)
    // 结构: [{"date": "2022-09-01", "gpa": 3.85}]
    private List<Map<String, Object>> gpaSeries;

    // 德育/劳育：持续时间区域 (对应 D3.js 阴影矩形)
    // 结构:
    private List<Map<String, Object>> durationTenures;

    // 重大事件：离散标记点 (对应 D3.js 自定义图标)
    // 结构: [{"date": "2024-05-30", "title": "ACM竞赛银奖", "pillar": "智育/美育", "level": "国家级"}]
    private List<Map<String, Object>> majorMilestones;
}