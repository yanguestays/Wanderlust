package com.wanderlust.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanderlust.entity.Destination;
import com.wanderlust.repository.DestinationRepository;
import lombok.extern.slf4j.Slf4j;
import com.wanderlust.utils.RRFAlgorithm;
import com.wanderlust.utils.VectorStore;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeService {

    private final ZhipuService zhipuService;       // 眼睛：负责向量化
    private final DeepSeekService deepSeekService; // 大脑：负责意图重写
    private final DestinationRepository destinationRepository; // 仓库：负责 SQL 查库

    // 算法工具：手（计算余弦相似度）
    private final VectorStore vectorStore;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 内存向量索引 (模拟向量数据库)
    private final Map<Long, List<Double>> vectorIndex = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        refreshIndex();
    }

    // 初始化索引
    public void refreshIndex() {
        List<Destination> destinations = destinationRepository.findAll();
        vectorIndex.clear();
        for (Destination destination : destinations) {
            if (destination.getEmbeddingVector() != null && !destination.getEmbeddingVector().isEmpty()) {
                try {
                    List<Double> vector = objectMapper.readValue(
                            destination.getEmbeddingVector(),
                            new TypeReference<List<Double>>() {}
                    );
                    vectorIndex.put(destination.getId(), vector);
                } catch (Exception e) {
                    log.error("解析向量失败 ID: {}", destination.getId());
                }
            }
        }
        log.info("向量索引构建完成，共加载 {} 条数据", vectorIndex.size());
    }

    /**
     * 🔥🔥 核心入口：混合检索 (Hybrid Search) 🔥🔥
     * 对应架构图：双路召回 + 并行执行 + 动态融合
     */
    public List<Destination> hybridSearch(String rawQuery) {
        if (rawQuery == null || rawQuery.trim().isEmpty()) return new ArrayList<>();

        long start = System.currentTimeMillis();

        // 1. 【大脑】DeepSeek 意图理解 (串行，统领全局)
        String optimizedQuery = deepSeekService.rewriteQuery(rawQuery);

        // 简单判断是否为精确意图 (例如：包含数字年份，或者非常短的名字)
        boolean isSpecific = rawQuery.matches(".*\\d{4}.*") || rawQuery.length() < 4;

        log.info("[DeepSeek] 意图重写: {} | 类型: {}", optimizedQuery, isSpecific ? "精确" : "泛化");

        // 2. 【双手】真正的并行 (Parallel Execution)

        // 左手：向量检索 (异步线程)
        CompletableFuture<List<Destination>> vectorTask = CompletableFuture.supplyAsync(() -> {
            try {
                // 拿着优化后的词去向量化
                List<Double> vec = zhipuService.getEmbedding(optimizedQuery);
                // 执行纯向量计算
                return searchVectorOnly(vec, 0.38);
            } catch (Exception e) {
                log.warn("向量路失败 (熔断降级): {}", e.getMessage());
                return new ArrayList<Destination>();
            }
        });

        // 右手：SQL 检索 (异步线程)
        CompletableFuture<List<Destination>> sqlTask = CompletableFuture.supplyAsync(() -> {
            try {
                // 拿着优化后的词去 SQL 查
                return destinationRepository.searchByKeyword(optimizedQuery);
            } catch (Exception e) {
                log.warn("SQL路失败 (熔断降级): {}", e.getMessage());
                return new ArrayList<Destination>();
            }
        });

        // 3. 【会师】等待两路结果
        List<Destination> vectorResults;
        List<Destination> sqlResults;
        try {
            // allOf 等待所有任务结束
            CompletableFuture.allOf(vectorTask, sqlTask).join();
            vectorResults = vectorTask.get();
            sqlResults = sqlTask.get();
        } catch (Exception e) {
            log.error("混合检索并行执行异常", e);
            return new ArrayList<>();
        }

        log.info("召回统计 -> 向量: {}条 | SQL: {}条", vectorResults.size(), sqlResults.size());

        // 4. 【策略】RRF 加权融合 (调用外部工具类，降低耦合)
        // 原来的 adaptiveFusion 方法已移除，改为调用工具类
        List<Destination> finalResults = RRFAlgorithm.combine(vectorResults, sqlResults, isSpecific);

        long end = System.currentTimeMillis();
        log.info("混合检索总耗时: {}ms", end - start);

        return finalResults;
    }

    /**
     * 内部方法：纯向量计算 (不含 AI 调用)
     */
    private List<Destination> searchVectorOnly(List<Double> queryVector, double threshold) {
        if (queryVector == null || queryVector.isEmpty()) return new ArrayList<>();

        Map<Long, Double> scores = new HashMap<>();
        for (Map.Entry<Long, List<Double>> entry : vectorIndex.entrySet()) {
            double score = vectorStore.cosineSimilarity(queryVector, entry.getValue());
            if (score > threshold) {
                scores.put(entry.getKey(), score);
            }
        }

        if (scores.isEmpty()) return new ArrayList<>();

        // 按分数排序取 Top 10
        return scores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(10)
                .map(entry -> destinationRepository.findById(entry.getKey()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}