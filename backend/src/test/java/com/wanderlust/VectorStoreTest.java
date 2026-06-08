package com.wanderlust;

import com.wanderlust.utils.VectorStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("向量余弦相似度计算")
class VectorStoreTest {

    private VectorStore vectorStore;

    @BeforeEach
    void setUp() {
        vectorStore = new VectorStore();
    }

    @Test
    @DisplayName("相同向量应返回 1.0（完全相似）")
    void identicalVectorsShouldReturnOne() {
        List<Double> vec = List.of(1.0, 2.0, 3.0);
        double similarity = vectorStore.cosineSimilarity(vec, vec);
        assertEquals(1.0, similarity, 0.0001);
    }

    @Test
    @DisplayName("正交向量应返回 0.0")
    void orthogonalVectorsShouldReturnZero() {
        List<Double> vecA = List.of(1.0, 0.0, 0.0);
        List<Double> vecB = List.of(0.0, 1.0, 0.0);
        double similarity = vectorStore.cosineSimilarity(vecA, vecB);
        assertEquals(0.0, similarity, 0.0001);
    }

    @Test
    @DisplayName("相反方向向量应返回负值")
    void oppositeVectorsShouldReturnNegative() {
        List<Double> vecA = List.of(1.0, 2.0, 3.0);
        List<Double> vecB = List.of(-1.0, -2.0, -3.0);
        double similarity = vectorStore.cosineSimilarity(vecA, vecB);
        assertEquals(-1.0, similarity, 0.0001);
    }

    @Test
    @DisplayName("null 或空向量应返回 0.0 而不是崩溃")
    void nullOrEmptyShouldReturnZero() {
        assertEquals(0.0, vectorStore.cosineSimilarity(null, List.of(1.0, 2.0)));
        assertEquals(0.0, vectorStore.cosineSimilarity(List.of(1.0, 2.0), null));
        assertEquals(0.0, vectorStore.cosineSimilarity(List.of(), List.of(1.0)));
    }

    @Test
    @DisplayName("维度不匹配应返回 0.0")
    void differentDimensionsShouldReturnZero() {
        List<Double> vecA = List.of(1.0, 2.0, 3.0);
        List<Double> vecB = List.of(1.0, 2.0);
        assertEquals(0.0, vectorStore.cosineSimilarity(vecA, vecB));
    }

    @Test
    @DisplayName("零向量应返回 0.0（防止除零）")
    void zeroVectorShouldReturnZero() {
        List<Double> zeroVec = List.of(0.0, 0.0, 0.0);
        List<Double> normalVec = List.of(1.0, 2.0, 3.0);
        assertEquals(0.0, vectorStore.cosineSimilarity(zeroVec, normalVec));
    }

    @Test
    @DisplayName("高维向量应正常计算")
    void highDimensionalVectorsShouldWork() {
        // 模拟 1024 维向量的简化版
        Double[] a = new Double[100];
        Double[] b = new Double[100];
        for (int i = 0; i < 100; i++) {
            a[i] = Math.random();
            b[i] = Math.random();
        }
        double similarity = vectorStore.cosineSimilarity(List.of(a), List.of(b));
        // 结果应在 [-1, 1] 范围
        assertTrue(similarity >= -1.0 && similarity <= 1.0);
    }
}
