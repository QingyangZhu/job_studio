package com.job.job_studio.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class DeepSeekService {

    private final String deepseekApiKey;
    private final String deepseekModel;
    private final WebClient webClient;
    private final ObjectMapper objectMapper; // 推荐将 ObjectMapper 也作为 Bean 注入

    // [解决方案] 将 @Value 对应的配置作为构造函数参数注入
    public DeepSeekService(
            WebClient.Builder webClientBuilder,
            ObjectMapper objectMapper,
            @Value("${deepseek.api.key}") String deepseekApiKey,
            @Value("${deepseek.base.url}") String deepseekBaseUrl,
            @Value("${deepseek.model}") String deepseekModel
    ) {
        this.deepseekApiKey = deepseekApiKey;
        this.deepseekModel = deepseekModel;
        this.objectMapper = objectMapper;

        // 在这里，所有参数都已经被 Spring 注入，不再是 null
        this.webClient = webClientBuilder
                .baseUrl(deepseekBaseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * 调用 DeepSeek Chat Completion API 获取 AI 助手的回复
     * @param userMessage 用户输入的消息
     * @return AI助手的回复文本
     */
    public String getAIResponse(String userMessage) {
        String systemPrompt = "你是一名专业的【大数据与软件学院本科就业工作室】的AI生涯规划导师（MCP助手）。你的回复必须基于职业发展数据分析，结合学生的K-S-Q能力模型和就业市场趋势，提供精准、实用的就业建议、技能提升和岗位匹配指导。";

        List<Map<String, String>> messages = List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userMessage)
        );

        Map<String, Object> requestBody = Map.of(
                "model", deepseekModel,
                "messages", messages,
                "stream", false
        );

        try {
            String responseBody = webClient.post()
                    .uri("/chat/completions")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + deepseekApiKey)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // 在传统 Spring MVC 项目中，阻塞是可接受的

            if (responseBody != null) {
                // 使用注入的 ObjectMapper 实例
                Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);

                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.getOrDefault("content", "AI助手未能提供有效回复。");
                }
            }
            return "API响应为空或格式错误。";

        } catch (Exception e) {
            // 考虑使用日志框架记录错误
            // log.error("调用AI服务失败", e);
            e.printStackTrace();
            return "调用AI服务失败：" + e.getMessage();
        }
    }
}