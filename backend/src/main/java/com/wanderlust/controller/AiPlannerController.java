package com.wanderlust.controller;

import com.wanderlust.service.DeepSeekService;
import com.wanderlust.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiPlannerController {

    private final DeepSeekService deepSeekService;

    /**
     * 生成旅行计划接口
     */
    @PostMapping("/plan")
    public Result<String> createPlan(@RequestBody Map<String, String> params) {
        // 参数校验
        String destination = params.get("destination");
        if (destination == null || destination.isEmpty()) {
            return Result.error("目的地不能为空");
        }

        String days = params.getOrDefault("days", "3");
        String budget = params.getOrDefault("budget", "适中");
        String companion = params.getOrDefault("companion", "朋友");

        // 调用 Service
        String planMarkdown = deepSeekService.generatePlan(destination, days, budget, companion);

        return Result.success(planMarkdown);
    }
}