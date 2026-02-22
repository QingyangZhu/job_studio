package com.job.job_studio.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper = new ObjectMapper(); // 引入 Jackson 工具

    @Autowired
    public JobGraphServiceImpl(KgNodeMapper kgNodeMapper, KgRelationshipMapper kgRelationshipMapper, AssessmentResultMapper assessmentResultMapper) {
        this.kgNodeMapper = kgNodeMapper;
        this.kgRelationshipMapper = kgRelationshipMapper;
        this.assessmentResultMapper = assessmentResultMapper;
    }

    @Override
    public List<String> getAllJobRoles() {
        QueryWrapper<KgNode> query = new QueryWrapper<>();
        // 确保数据库里列名是 node_type，或者使用 lambda: .eq(KgNode::getNodeType, "JobRole")
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
            return new HashMap<>();
        }

        // 2. 优化后的 BFS (批量查询)
        Set<Long> visitedNodeIds = new HashSet<>();
        List<KgNode> resultNodes = new ArrayList<>();
        List<KgRelationship> resultLinks = new ArrayList<>();

        // 初始化
        Set<Long> currentLevelIds = new HashSet<>(); // 当前层级的节点ID集合
        currentLevelIds.add(rootNode.getNodeId());

        visitedNodeIds.add(rootNode.getNodeId());
        resultNodes.add(rootNode);

        int maxDepth = 3;
        int currentDepth = 0;

        while (!currentLevelIds.isEmpty() && currentDepth < maxDepth) {
            // 【优化点】：一次性查询当前层所有节点发出的关系 (source_id IN (...))
            QueryWrapper<KgRelationship> relQuery = new QueryWrapper<>();
            relQuery.in("source_id", currentLevelIds);
            List<KgRelationship> relationships = kgRelationshipMapper.selectList(relQuery);

            Set<Long> nextLevelIds = new HashSet<>(); // 下一层要查询的ID

            for (KgRelationship rel : relationships) {
                // 添加边
                resultLinks.add(rel);

                Long targetId = rel.getTargetId();
                // 如果目标节点未访问过，则加入待查询列表
                if (!visitedNodeIds.contains(targetId)) {
                    visitedNodeIds.add(targetId);
                    nextLevelIds.add(targetId);
                }
            }

            // 【优化点】：一次性查询下一层的所有节点实体 (node_id IN (...))
            if (!nextLevelIds.isEmpty()) {
                QueryWrapper<KgNode> nodeQuery = new QueryWrapper<>();
                nodeQuery.in("node_id", nextLevelIds);
                List<KgNode> nextNodes = kgNodeMapper.selectList(nodeQuery);
                resultNodes.addAll(nextNodes);
            }

            // 推进到下一层
            currentLevelIds = nextLevelIds;
            currentDepth++;
        }

        // 3. 获取用户技能状态 (修复后的逻辑)
        Map<String, Integer> userSkills = getUserAcquiredSkills(studentId);

        // 4. 转换数据格式
        return transformToEChartsFormat(resultNodes, resultLinks, jobRoleName, userSkills);
    }

    /**
     * 【修复】获取用户技能 (适配新的 JSON 存储结构)
     */
    private Map<String, Integer> getUserAcquiredSkills(Long studentId) {
        Map<String, Integer> skills = new HashMap<>();
        if (studentId == null) return skills;

        // 获取最新的测评结果
        QueryWrapper<AssessmentResult> wrapper = new QueryWrapper<>();
        wrapper.eq("student_id", studentId)
                .orderByDesc("create_time") // 注意：字段名可能叫 create_time 或 assessment_date
                .last("LIMIT 1");
        AssessmentResult res = assessmentResultMapper.selectOne(wrapper);

        if (res != null && res.getSkillDetails() != null) {
            try {
                // 将 JSON 字符串解析为 Map
                // 假设 skillDetails 格式为 {"Java": 80, "Spring": 60}
                Map<String, Integer> rawSkills = objectMapper.readValue(
                        res.getSkillDetails(),
                        new TypeReference<Map<String, Integer>>() {}
                );

                // 过滤出掌握程度达标的技能 (例如分数 >= 60 才算掌握)
                // 这里我们将 value 统一置为 1，表示已点亮
                rawSkills.forEach((k, v) -> {
                    if (v >= 60) { // 阈值可调整
                        skills.put(k, 1);
                    }
                });
            } catch (Exception e) {
                System.err.println("解析技能JSON失败: " + e.getMessage());
            }
        }
        return skills;
    }

    /**
     * 转换为 ECharts 格式 (保持原有逻辑，增加空值保护)
     */
    private Map<String, Object> transformToEChartsFormat(List<KgNode> nodes, List<KgRelationship> links, String jobTitle, Map<String, Integer> userSkills) {
        List<Map<String, Object>> echartsNodes = new ArrayList<>();
        List<Map<String, Object>> echartsLinks = new ArrayList<>();

        // 构建节点
        for (KgNode node : nodes) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", String.valueOf(node.getNodeId()));
            map.put("name", node.getName());

            // 动态大小：如果是岗位节点大一点，其他的根据层级或默认
            int size = "JobRole".equals(node.getCategory()) ? 50 : (node.getSymbolSize() != null ? node.getSymbolSize() : 30);
            map.put("symbolSize", size);

            map.put("category", getCategoryIndex(node.getCategory()));

            // 状态标记
            // 注意：这里用 containsKey 匹配名称，需确保图谱节点名和测评技能名一致
            boolean isAcquired = userSkills.containsKey(node.getName());
            map.put("isUserAcquired", isAcquired);

            // 简单判断 GAP
            boolean isGap = "HardSkill".equals(node.getCategory()) && !isAcquired;
            map.put("isGap", isGap);

            // 设置颜色：如果掌握了显示绿色，GAP显示红色 (ECharts itemStyle)
            if (isAcquired) {
                map.put("itemStyle", Map.of("color", "#2ecc71", "borderColor", "#fff")); // 绿色
            } else if (isGap) {
                map.put("itemStyle", Map.of("color", "#e74c3c")); // 红色
            }

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