package com.wanderlust.utils;

import com.wanderlust.entity.Destination;
import java.util.*;
import java.util.stream.Collectors;

public class RRFAlgorithm {

    // 独立封装的融合算法
    public static List<Destination> combine(List<Destination> vecList, List<Destination> sqlList, boolean isSpecific) {
        Map<Long, Double> scoreMap = new HashMap<>();
        double k = 60.0;

        // 根据意图动态调整权重
        double sqlWeight = isSpecific ? 3.0 : 1.0;
        double vectorWeight = isSpecific ? 1.0 : 2.0;

        // 处理向量结果
        for (int i = 0; i < vecList.size(); i++) {
            Destination d = vecList.get(i);
            double score = vectorWeight * (1.0 / (k + i + 1));
            scoreMap.merge(d.getId(), score, Double::sum);
        }

        // 处理 SQL 结果
        for (int i = 0; i < sqlList.size(); i++) {
            Destination d = sqlList.get(i);
            double score = sqlWeight * (1.0 / (k + i + 1));
            scoreMap.merge(d.getId(), score, Double::sum);
        }

        // 此时我们只有 ID 和 分数，需要返回对象列表 (注意：调用方需要自己重新通过ID查库或者维护对象映射，
        // 这里为了简单，假设传入的 List 里的对象是完整的，我们最后只返回排好序的对象)
        // 更好的做法是返回 ID 列表，由 Service 去查库。这里简化处理：

        Set<Destination> allDestinations = new HashSet<>();
        allDestinations.addAll(vecList);
        allDestinations.addAll(sqlList);

        return allDestinations.stream()
                .sorted((d1, d2) -> {
                    Double s1 = scoreMap.getOrDefault(d1.getId(), 0.0);
                    Double s2 = scoreMap.getOrDefault(d2.getId(), 0.0);
                    return s2.compareTo(s1); // 降序
                })
                .limit(10)
                .collect(Collectors.toList());
    }
}