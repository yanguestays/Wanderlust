package com.wanderlust;

import com.wanderlust.entity.Destination;
import com.wanderlust.utils.RRFAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RRF 融合排序算法")
class RRFAlgorithmTest {

    /**
     * 辅助方法：创建一个简单的 Destination
     */
    private Destination createDest(Long id, String title) {
        Destination d = new Destination();
        d.setId(id);
        d.setTitle(title);
        d.setRating(9.0);
        return d;
    }

    @Test
    @DisplayName("两路结果有重叠时，应正确去重并融合排序")
    void shouldDeduplicateAndMerge() {
        List<Destination> vecResults = List.of(
                createDest(1L, "冰岛"),
                createDest(2L, "京都"),
                createDest(3L, "重庆")
        );
        List<Destination> sqlResults = List.of(
                createDest(2L, "京都"),  // 重叠
                createDest(3L, "重庆"),  // 重叠
                createDest(4L, "马尔代夫")
        );

        List<Destination> result = RRFAlgorithm.combine(vecResults, sqlResults, false);

        // 应该去重,只保留 4 个不同 ID
        assertEquals(4, result.size());
    }

    @Test
    @DisplayName("泛化搜索时，向量路权重应高于 SQL 路")
    void fuzzySearchShouldFavorVector() {
        List<Destination> vecResults = new ArrayList<>();
        vecResults.add(createDest(1L, "挪威极光"));  // 排名第1

        List<Destination> sqlResults = new ArrayList<>();
        sqlResults.add(createDest(2L, "漠河"));      // 排名第1

        // isSpecific = false → vectorWeight=2.0, sqlWeight=1.0
        List<Destination> result = RRFAlgorithm.combine(vecResults, sqlResults, false);

        // 向量路第1名应该排在 SQL 路第1名前面
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    @DisplayName("精确搜索时，SQL 路权重应高于向量路")
    void specificSearchShouldFavorSql() {
        List<Destination> vecResults = new ArrayList<>();
        vecResults.add(createDest(1L, "无关景点"));

        List<Destination> sqlResults = new ArrayList<>();
        sqlResults.add(createDest(2L, "精确匹配景点"));

        // isSpecific = true → sqlWeight=3.0, vectorWeight=1.0
        List<Destination> result = RRFAlgorithm.combine(vecResults, sqlResults, true);

        // SQL 路结果应该排第一
        assertEquals(2L, result.get(0).getId());
    }

    @Test
    @DisplayName("空列表输入不应抛出异常")
    void emptyInputsShouldNotCrash() {
        List<Destination> vecResults = new ArrayList<>();
        List<Destination> sqlResults = new ArrayList<>();

        List<Destination> result = RRFAlgorithm.combine(vecResults, sqlResults, false);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("结果不应超过10条（Top-10截断）")
    void shouldLimitToTop10() {
        List<Destination> vecResults = new ArrayList<>();
        List<Destination> sqlResults = new ArrayList<>();

        for (int i = 0; i < 15; i++) {
            vecResults.add(createDest((long) i, "Place-" + i));
            sqlResults.add(createDest((long) (i + 30), "Other-" + i));
        }

        List<Destination> result = RRFAlgorithm.combine(vecResults, sqlResults, false);

        assertTrue(result.size() <= 10);
    }

    @Test
    @DisplayName("单路结果场景（另一路为空）不应崩溃")
    void singlePathResultsShouldWork() {
        List<Destination> vecResults = List.of(
                createDest(1L, "冰岛"),
                createDest(2L, "京都")
        );
        List<Destination> sqlResults = new ArrayList<>();

        List<Destination> result = RRFAlgorithm.combine(vecResults, sqlResults, false);

        assertEquals(2, result.size());
        assertNotNull(result.get(0));
    }
}
