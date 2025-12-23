package com.job.job_studio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.job.job_studio.entity.AssessmentResult;
import com.job.job_studio.entity.KgNode;
import com.job.job_studio.entity.KgRelationship;
import com.job.job_studio.mapper.AssessmentResultMapper;
import com.job.job_studio.mapper.KgNodeMapper;
import com.job.job_studio.mapper.KgRelationshipMapper;
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
    public List<String> getAllJobRoles() {
        // 直接从数据库查询所有 node_type 为 'JobRole' 的节点名称
        QueryWrapper<KgNode> query = new QueryWrapper<>();
        query.eq("node_type", "JobRole");
        return kgNodeMapper.selectList(query)
                .stream()
                .map(KgNode::getName)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> generateForceGraphData(String jobRoleName, Long studentId) {
        // 1. 查询根节点 (JobRole)
        KgNode rootNode = kgNodeMapper.selectOne(new QueryWrapper<KgNode>()
                .eq("name", jobRoleName)
                .eq("node_type", "JobRole"));

        if (rootNode == null) {
            return new HashMap<>(); // 未找到岗位，返回空
        }

        // 2. 执行 BFS (广度优先搜索) 动态抓取子图
        // 定义集合用于存储最终结果 (自动去重)
        Set<Long> visitedNodeIds = new HashSet<>();
        List<KgNode> resultNodes = new ArrayList<>();
        List<KgRelationship> resultLinks = new ArrayList<>();

        // 初始化队列
        Queue<KgNode> queue = new LinkedList<>();
        queue.add(rootNode);
        visitedNodeIds.add(rootNode.getNodeId());
        resultNodes.add(rootNode);

        int maxDepth = 3; // 搜索深度：岗位 -> 核心技能 -> 子技能 -> 知识点
        int currentDepth = 0;

        while (!queue.isEmpty() && currentDepth < maxDepth) {
            int levelSize = queue.size();
            // 处理当前层的所有节点
            for (int i = 0; i < levelSize; i++) {
                KgNode currentNode = queue.poll();

                // 【数据库查询关键点】：只查询以当前节点为 Source 的关系
                List<KgRelationship> relationships = kgRelationshipMapper.selectList(
                        new QueryWrapper<KgRelationship>().eq("source_id", currentNode.getNodeId())
                );

                for (KgRelationship rel : relationships) {
                    resultLinks.add(rel);

                    // 如果目标节点还没被访问过，则查询出来并加入队列
                    Long targetId = rel.getTargetId();
                    if (!visitedNodeIds.contains(targetId)) {
                        KgNode targetNode = kgNodeMapper.selectById(targetId);
                        if (targetNode != null) {
                            visitedNodeIds.add(targetId);
                            resultNodes.add(targetNode);
                            queue.add(targetNode);
                        }
                    }
                }
            }
            currentDepth++;
        }

        // 3. 获取用户技能状态 (保持原有逻辑)
        Map<String, Integer> userSkills = getUserAcquiredSkills(studentId);

        // 4. 转换数据格式
        return transformToEChartsFormat(resultNodes, resultLinks, jobRoleName, userSkills);
    }

    /**
     * 获取用户技能 (逻辑保持不变，用于模拟)
     */
    private Map<String, Integer> getUserAcquiredSkills(Long studentId) {
        // 实际逻辑应查数据库，这里简化模拟
        // ... (保持原代码中的模拟逻辑即可，或者根据 AssessmentResult 真实查询)
        Map<String, Integer> skills = new HashMap<>();
        QueryWrapper<AssessmentResult> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId).orderByDesc("assessment_date").last("LIMIT 1");
        AssessmentResult res = assessmentResultMapper.selectOne(wrapper);

        if (res != null) {
            if (res.getJavaScore() != null && res.getJavaScore() >= 4) skills.put("Java基础", 1);
            if (res.getPythonScore() != null && res.getPythonScore() >= 4) skills.put("Python数据分析", 1);
            if (res.getSqlScore() != null && res.getSqlScore() >= 4) skills.put("SQL查询", 1);
            // 简单模拟前端技能
            if (studentId != null) skills.put("JavaScript", 1);
        }
        return skills;
    }

    /**
     * 转换为 ECharts 格式
     */
    private Map<String, Object> transformToEChartsFormat(List<KgNode> nodes, List<KgRelationship> links, String jobTitle, Map<String, Integer> userSkills) {
        List<Map<String, Object>> echartsNodes = new ArrayList<>();
        List<Map<String, Object>> echartsLinks = new ArrayList<>();

        // 构建节点
        for (KgNode node : nodes) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", String.valueOf(node.getNodeId()));
            map.put("name", node.getName());
            map.put("symbolSize", node.getSymbolSize());
            map.put("category", getCategoryIndex(node.getCategory())); // 映射分类索引

            // 状态标记
            boolean isAcquired = userSkills.containsKey(node.getName());
            map.put("isUserAcquired", isAcquired);
            // 简单判断 GAP: 如果是核心技能且未掌握
            boolean isGap = "HardSkill".equals(node.getCategory()) && !isAcquired;
            map.put("isGap", isGap);

            echartsNodes.add(map);
        }

        // 构建边
        for (KgRelationship link : links) {
            Map<String, Object> map = new HashMap<>();
            map.put("source", String.valueOf(link.getSourceId()));
            map.put("target", String.valueOf(link.getTargetId()));
            map.put("value", link.getRelType());
            echartsLinks.add(map);
        }

        // 定义图例 (Categories)
        List<Map<String, String>> categories = List.of(
                Map.of("name", "岗位核心"),   // 0
                Map.of("name", "硬技能"),     // 1
                Map.of("name", "软技能"),     // 2
                Map.of("name", "基础门槛"),   // 3
                Map.of("name", "进阶加分")    // 4
        );

        Map<String, Object> result = new HashMap<>();
        result.put("jobTitle", jobTitle);
        result.put("nodes", echartsNodes);
        result.put("links", echartsLinks);
        result.put("categories", categories);
        return result;
    }

    // 辅助方法：将 category 字符串转为数组索引
    private int getCategoryIndex(String category) {
        if (category == null) return 1;
        switch (category) {
            case "JobRole": return 0;
            case "HardSkill": return 1;
            case "SoftSkill": return 2;
            case "Threshold": return 3;
            case "Differentiating": return 4;
            default: return 1;
        }
    }
}