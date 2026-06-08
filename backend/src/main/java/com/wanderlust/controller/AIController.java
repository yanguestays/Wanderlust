package com.wanderlust.controller;

import com.wanderlust.entity.Destination;
import com.wanderlust.repository.DestinationRepository;
import com.wanderlust.service.DestinationService;
import com.wanderlust.service.KnowledgeService;
import com.wanderlust.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
@RequiredArgsConstructor
public class AIController {

    private final KnowledgeService knowledgeService;
    private final DestinationRepository destinationRepository;
    private final DestinationService destinationService; // 🔥 注入 Service 层

    /**
     * AI 混合搜索接口 (词法+向量)
     */
    @GetMapping("/search")
    public Result<List<Destination>> search(@RequestParam String query) {
        return Result.success(knowledgeService.hybridSearch(query));
    }

    /**
     * 根据 ID 获取目的地详情
     */
    @GetMapping("/{id}")
    public Result<Destination> getDetail(@PathVariable Long id) {
        return Result.success(destinationRepository.findById(id).orElse(null));
    }

    /**
     * 🔥 首页推荐接口：现在改为调用按月推荐逻辑
     */
    @GetMapping("/recommend")
    public Result<List<Destination>> recommend() {
        // 调用 DestinationService 中你刚写好的 getRecommendedDestinations()
        // 该方法内部会自动获取当前月份并从数据库筛选
        return Result.success(destinationService.getRecommendedDestinations());
    }
}