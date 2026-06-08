package com.wanderlust.utils;

import org.springframework.stereotype.Component;
import java.util.List;

@Component // 确保加了这个注解，否则 Spring 扫不到
public class VectorStore {

    /**
     * 计算两个向量的余弦相似度 (Cosine Similarity)
     * 公式: (A . B) / (||A|| * ||B||)
     * 范围: -1.0 到 1.0 (值越大越相似)
     */
    public double cosineSimilarity(List<Double> vectorA, List<Double> vectorB) {
        // 1. 基础校验：向量不能为空，且维度必须一致
        if (vectorA == null || vectorB == null || vectorA.isEmpty() || vectorB.isEmpty() || vectorA.size() != vectorB.size()) {
            return 0.0;
        }

        double dotProduct = 0.0; // 点积
        double normA = 0.0;      // A的模长平方
        double normB = 0.0;      // B的模长平方

        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }

        // 2. 数学保护：防止分母为 0 (除零保护)
        if (normA == 0 || normB == 0) {
            return 0.0;
        }

        // 3. 计算相似度
        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));

        // 4. 防止 NaN (Not a Number) 情况
        if (Double.isNaN(similarity)) {
            return 0.0;
        }

        return similarity;
    }
}