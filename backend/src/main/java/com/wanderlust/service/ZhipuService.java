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
public class ZhipuService {

    @Value("${zhipu.api.key}")
    private String apiKey;

    @Value("${zhipu.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Double> getEmbedding(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "embedding-2"); // 智谱的专用向量模型
            body.put("input", text);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            // 发送请求
            String response = restTemplate.postForObject(apiUrl, entity, String.class);

            // 解析 JSON: data[0].embedding
            JsonNode root = mapper.readTree(response);
            JsonNode embeddingNode = root.path("data").get(0).path("embedding");

            List<Double> vector = new ArrayList<>();
            if (embeddingNode.isArray()) {
                for (JsonNode node : embeddingNode) {
                    vector.add(node.asDouble());
                }
            }
            return vector;
        } catch (Exception e) {
            log.error("智谱向量生成失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}