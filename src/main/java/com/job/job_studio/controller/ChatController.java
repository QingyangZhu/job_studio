package com.job.job_studio.controller;

import com.job.job_studio.service.DeepSeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final DeepSeekService deepSeekService;

    @Autowired
    public ChatController(DeepSeekService deepSeekService) {
        this.deepSeekService = deepSeekService;
    }

    /**
     * POST /api/v1/chat/ask
     * 接口功能：接收用户消息，调用 DeepSeek AI，并返回回复。
     * @param requestBody 包含 "message" 字段的JSON体。
     * @return 包含 "response" 字段的JSON体。
     */
    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> askAI(@RequestBody Map<String, String> requestBody) {
        String userMessage = requestBody.get("message");

        if (userMessage == null || userMessage.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "消息内容不能为空。"));
        }

        // 调用 DeepSeek Service
        String aiResponse = deepSeekService.getAIResponse(userMessage);

        return ResponseEntity.ok(Map.of("response", aiResponse));
    }
}