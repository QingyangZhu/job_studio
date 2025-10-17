package com.job.job_studio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.job.job_studio.entity.AssessmentResult;
import com.job.job_studio.entity.KgNode;
import com.job.job_studio.entity.KgRelationship;
import com.job.job_studio.mapper.KgNodeMapper;
import com.job.job_studio.mapper.KgRelationshipMapper;
import com.job.job_studio.mapper.AssessmentResultMapper; // 引入学生评测结果
import com.job.job_studio.service.JobGraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobGraphServiceImpl implements JobGraphService {

    private final KgNodeMapper kgNodeMapper;
    private final KgRelationshipMapper kgRelationshipMapper;
    private final AssessmentResultMapper assessmentResultMapper;

    @Autowired
    public JobGraphServiceImpl(KgNodeMapper kgNodeMapper, KgRelationshipMapper kgRelationshipMapper, AssessmentResultMapper assessmentResultMapper) {
        this.kgNodeMapper = kgNodeMapper;
        this.kgRelationshipMapper = kgRelationshipMapper;
        this.assessmentResultMapper = assessmentResultMapper;
    }

    @Override
    public Map<String, Object> generateForceGraphData(String jobRoleName, Long studentId) {
        // 1. 获取所有 KG 数据 (简化，实际应按 jobRole 过滤)
        List<KgNode> allNodes = kgNodeMapper.selectList(null);
        List<KgRelationship> allRelationships = kgRelationshipMapper.selectList(null);

        // 2. 获取用户技能状态 (用于个性化路径计算)
        Map<String, Integer> userSkills = getUserAcquiredSkills(studentId);

        // 3. 核心逻辑：A*路径模拟与数据转换
        Map<String, Object> graphData = transformToEChartsFormat(allNodes, allRelationships, jobRoleName, userSkills);

        return graphData;
    }

    /**
     * 模拟获取用户的已掌握技能（简化：只检查 JS 和 React 状态）
     */
    private Map<String, Integer> getUserAcquiredSkills(Long studentId) {
        // 假设学生只要 Python/Java/JS 技能得分 > 3，就算已掌握
        QueryWrapper<AssessmentResult> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .orderByDesc("assessment_date")
                .last("LIMIT 1");

        AssessmentResult latestAssessment = assessmentResultMapper.selectOne(wrapper);

        Map<String, Integer> skills = new HashMap<>();
        // 模拟：根据模拟数据 22010101 (张小明)的 AssessmentResult，假设其 JS > 3
        if (latestAssessment!= null && latestAssessment.getJavaScore() >= 4) {
            skills.put("JavaScript ES6+", 1); // 标记已掌握 JS
        }
        return skills;
    }

    /**
     * 将 KG 数据转换为 ECharts Force Graph 格式，并标记推荐路径。
     */
    private Map<String, Object> transformToEChartsFormat(List<KgNode> allNodes, List<KgRelationship> allRelationships, String jobRoleName, Map<String, Integer> userSkills) {
        Map<Long, KgNode> nodeMap = allNodes.stream().collect(Collectors.toMap(KgNode::getNodeId, node -> node));
        List<Map<String, Object>> echartsNodes = new ArrayList<>();
        List<Map<String, Object>> echartsLinks = new ArrayList<>();

        // 1. 遍历节点并应用个性化状态（颜色）
        allNodes.forEach(node -> {
            Map<String, Object> echartsNode = new HashMap<>();

            // 使用节点ID作为ECharts的唯一ID
            echartsNode.put("id", String.valueOf(node.getNodeId()));
            echartsNode.put("name", node.getName());
            echartsNode.put("category", node.getCategory());
            echartsNode.put("symbolSize", node.getSymbolSize());

            // 标记个性化状态 (用于前端着色)
            boolean isAcquired = userSkills.containsKey(node.getName());
            boolean isGap = node.getNodeType().equals("Competency") && node.getCategory().contains("Threshold") &&!isAcquired;

            echartsNode.put("isUserAcquired", isAcquired);
            echartsNode.put("isGap", isGap);

            echartsNodes.add(echartsNode);
        });

        // 2. 遍历关系并应用路径推荐 (A* 模拟)
        for (KgRelationship rel : allRelationships) {
            Map<String, Object> echartsLink = new HashMap<>();

            // 查找源节点和目标节点
            KgNode sourceNode = nodeMap.get(rel.getSourceId());
            KgNode targetNode = nodeMap.get(rel.getTargetId());

            if (sourceNode == null || targetNode == null) continue;

            // 模拟最短路径计算：如果目标技能是缺失的 (isGap)，并且源技能是已掌握或资源，则标记为推荐路径
            boolean isRecommended = false;
            // 路径推荐逻辑：从"已掌握技能" 或 "Resource" -> "缺失技能"的边，标记为推荐路径
            if ((userSkills.containsKey(sourceNode.getName()) || sourceNode.getNodeType().equals("Resource")) &&
            targetNode.getNodeType().equals("Competency") &&
                    !userSkills.containsKey(targetNode.getName())) {

                isRecommended = true;
            }

            echartsLink.put("source", String.valueOf(rel.getSourceId()));
            echartsLink.put("target", String.valueOf(rel.getTargetId()));
            echartsLink.put("name", rel.getRelType());
            echartsLink.put("weight", rel.getWeight());
            echartsLink.put("isRecommendedPath", isRecommended); // 路径高亮标记

            echartsLinks.add(echartsLink);
        }

        // 3. 构建最终 Map
        Map<String, Object> result = new HashMap<>();
        result.put("jobTitle", jobRoleName);
        result.put("nodes", echartsNodes);
        result.put("links", echartsLinks);
        // ECharts Categories
        result.put("categories", List.of(
                Map.of("name", "岗位角色", "symbol", "pin"),
                Map.of("name", "门槛技能", "symbol", "circle"),
                Map.of("name", "核心技能", "symbol", "rect"),
                Map.of("name", "软技能", "symbol", "diamond"),
                Map.of("name", "学习资源", "symbol", "triangle"),
                Map.of("name", "专业指标", "symbol", "star")
        ));

        return result;
    }
}