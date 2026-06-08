package com.wanderlust.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DeepSeekService {

    @Value("${deepseek.api.key}")
    private String apiKey;

    @Value("${deepseek.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 核心功能：意图重写 (Query Rewriting)
     */
    public String rewriteQuery(String userQuery) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "deepseek-chat");
            body.put("stream", false);
            body.put("temperature", 0.1);

            // 构造 Prompt - 已改为旅游目的地场景
            String prompt = "你是一个专业的旅游目的地搜索关键词优化师。你的任务是将用户的模糊搜索转化为数据库能听懂的关键词。\n" +
                    "规则：\n" +
                    "1. 如果用户输入负面情绪（如不开心、累），输出能缓解该情绪的旅游类型（如：治愈 放松 休闲）。\n" +
                    "2. 如果用户输入明确类型（如海岛），请输出该类型及其 2-3 个核心关联词（如：海岛 沙滩 阳光），以增加匹配概率。\n" +
                    "3. 如果用户描述需求（如想去爬山看日出），提取核心关键词（如：爬山 日出 徒步）。\n" +
                    "4. 直接输出关键词，用空格分隔。不要输出任何解释。不要输出'推荐'二字。";

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", prompt));
            messages.add(Map.of("role", "user", "content", userQuery));

            body.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            String response = restTemplate.postForObject(apiUrl, entity, String.class);

            JsonNode root = mapper.readTree(response);
            String aiAnswer = root.path("choices").get(0).path("message").path("content").asText();

            return aiAnswer.trim();

        } catch (Exception e) {
            log.error("DeepSeek 意图重写失败: {}", e.getMessage());
            return userQuery; // 失败降级为原词
        }
    }
    public String generatePlan(String destination, String days, String budget, String companion) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            // 1. 构建更加结构化的 Prompt
            String prompt = String.format(
                    "你是一位资深旅行规划师。用户通过我们的 RRF 混合检索系统锁定了目的地【%s】。\n" +
                            "请基于以下条件生成一份详细的行程安排：\n" +
                            "- 时长：%s 天\n" +
                            "- 预算偏好：%s\n" +
                            "- 同行人员：%s\n\n" +
                            "要求：\n" +
                            "1. 输出格式必须为 Markdown。\n" +
                            "2. 每天推荐 2-3 个核心景点，并简述理由。\n" +
                            "3. 包含当地必吃美食推荐。\n" +
                            "4. 语气要像一位老朋友一样热情且专业。",
                    destination, days, budget, companion
            );

            Map<String, Object> body = new HashMap<>();
            body.put("model", "deepseek-chat");
            // 暂时使用非流式 (stream=false)，确保逻辑先跑通。后续可升级为 SSE 流式推送。
            body.put("stream", false);
            body.put("temperature", 1.2); // 提高创造力
            body.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // 复用 postForObject
            String response = restTemplate.postForObject(apiUrl, entity, String.class);

            // 解析响应
            JsonNode root = mapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();

        } catch (Exception e) {
            log.error("AI 规划生成失败: {}", e.getMessage());
            return "AI 大脑正在繁忙中，请稍后再试... (Err: " + e.getMessage() + ")";
        }
    }
}