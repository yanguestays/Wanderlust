package com.wanderlust.controller;

import com.wanderlust.entity.Destination;
import com.wanderlust.repository.DestinationRepository;
import com.wanderlust.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/destinations") // 🔥 注意：这里没有 /api，前端请求也别带 /api
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationRepository destinationRepository;

    // 获取所有目的地
    @GetMapping
    public Result<List<Destination>> getAll() {
        return Result.success(destinationRepository.findAll());
    }

    // 获取单个详情
    @GetMapping("/{id}")
    public Result<Destination> getDetail(@PathVariable Long id) {
        return destinationRepository.findById(id)
                .map(Result::success)
                .orElse(Result.error("目的地不存在"));
    }
}