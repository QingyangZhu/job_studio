package com.job.job_studio.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.job.job_studio.entity.KgNode;
import com.job.job_studio.entity.KgRelationship;
import com.job.job_studio.mapper.KgNodeMapper;
import com.job.job_studio.mapper.KgRelationshipMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统启动时自动检查并填充知识图谱数据
 */
@Component
public class GraphDataInitializer implements CommandLineRunner {

    private final KgNodeMapper nodeMapper;
    private final KgRelationshipMapper relMapper;

    public GraphDataInitializer(KgNodeMapper nodeMapper, KgRelationshipMapper relMapper) {
        this.nodeMapper = nodeMapper;
        this.relMapper = relMapper;
    }

    @Override
    public void run(String... args) {
        // 如果数据库为空，则初始化数据
        if (nodeMapper.selectCount(null) == 0) {
            System.out.println("正在初始化知识图谱数据...");
            initJavaBackendGraph();
            initDataAnalystGraph();
            System.out.println("知识图谱数据初始化完成。");
        }
    }

    private void initJavaBackendGraph() {
        // 1. 创建根节点
        Long jobId = createNode("Java后端工程师", "JobRole", "JobRole", 50);

        // 2. 创建一级技能 (核心)
        Long javaCore = createNode("Java Core", "HardSkill", "Competency", 30);
        Long spring = createNode("Spring生态", "HardSkill", "Competency", 30);
        Long db = createNode("数据库", "HardSkill", "Competency", 30);
        Long cs = createNode("计算机基础", "HardSkill", "Competency", 25);

        createLink(jobId, javaCore, "REQUIRES", 10);
        createLink(jobId, spring, "REQUIRES", 10);
        createLink(jobId, db, "REQUIRES", 10);
        createLink(jobId, cs, "REQUIRES", 8);

        // 3. 创建二级技能
        Long jvm = createNode("JVM", "HardSkill", "Competency", 20);
        Long concurrency = createNode("多线程", "HardSkill", "Competency", 20);
        createLink(javaCore, jvm, "CONTAINS", 5);
        createLink(javaCore, concurrency, "CONTAINS", 5);

        Long boot = createNode("Spring Boot", "HardSkill", "Competency", 25);
        Long cloud = createNode("Microservices", "HardSkill", "Competency", 25);
        createLink(spring, boot, "PREREQUISITE_FOR", 8);
        createLink(boot, cloud, "PREREQUISITE_FOR", 8);

        Long mysql = createNode("MySQL", "HardSkill", "Competency", 25);
        Long redis = createNode("Redis", "HardSkill", "Competency", 20);
        createLink(db, mysql, "CONTAINS", 8);
        createLink(db, redis, "CONTAINS", 6);
    }

    private void initDataAnalystGraph() {
        // 1. 创建根节点
        Long jobId = createNode("数据分析师", "JobRole", "JobRole", 50);

        // 2. 核心技能
        Long python = createNode("Python数据分析", "HardSkill", "Competency", 30);
        Long bi = createNode("BI工具", "HardSkill", "Competency", 30);
        Long stats = createNode("统计学基础", "HardSkill", "Competency", 25);

        createLink(jobId, python, "REQUIRES", 10);
        createLink(jobId, bi, "REQUIRES", 9);
        createLink(jobId, stats, "REQUIRES", 8);

        // 3. 子技能
        Long pandas = createNode("Pandas/Numpy", "HardSkill", "Competency", 20);
        Long sql = createNode("SQL查询", "HardSkill", "Competency", 25); // 注意：SQL可能被复用

        createLink(python, pandas, "CONTAINS", 8);
        createLink(python, sql, "RELATED_TO", 6);

        Long tableau = createNode("Tableau", "HardSkill", "Competency", 20);
        createLink(bi, tableau, "CONTAINS", 8);
    }

    // 辅助方法：创建节点（带去重检查）
    private Long createNode(String name, String category, String type, Integer size) {
        KgNode existing = nodeMapper.selectOne(new QueryWrapper<KgNode>().eq("name", name));
        if (existing != null) return existing.getNodeId();

        KgNode node = new KgNode();
        node.setName(name);
        node.setCategory(category);
        node.setNodeType(type);
        node.setSymbolSize(size);
        nodeMapper.insert(node);
        return node.getNodeId();
    }

    // 辅助方法：创建关系
    private void createLink(Long source, Long target, String type, Integer weight) {
        KgRelationship rel = new KgRelationship();
        rel.setSourceId(source);
        rel.setTargetId(target);
        rel.setRelType(type);
        rel.setWeight(BigDecimal.valueOf(weight));
        relMapper.insert(rel);
    }
}