package com.wanderlust.controller;

import com.wanderlust.dto.ReviewDTO;
import com.wanderlust.entity.Destination;
import com.wanderlust.entity.Review;
import com.wanderlust.entity.User;
import com.wanderlust.repository.DestinationRepository;
import com.wanderlust.repository.ReviewRepository;
import com.wanderlust.repository.UserRepository;
import com.wanderlust.utils.Result;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    // 1. 获取某景点的评论列表
    @GetMapping("/dest/{destId}")
    public Result<List<ReviewDTO>> getReviews(@PathVariable Long destId) {
        List<Review> reviews = reviewRepository.findByDestinationIdOrderByCreateTimeDesc(destId);

        // 转 DTO
        List<ReviewDTO> dtos = reviews.stream().map(r -> {
            ReviewDTO dto = new ReviewDTO();
            dto.setId(r.getId());
            dto.setContent(r.getContent());
            dto.setRating(r.getRating());
            dto.setCreateTime(r.getCreateTime());
            if (r.getUser() != null) {
                dto.setUsername(r.getUser().getUsername());
                dto.setAvatar(r.getUser().getAvatar());
            }
            return dto;
        }).collect(Collectors.toList());

        return Result.success(dtos);
    }

    // 2. 提交评论
    @PostMapping("/add")
    public Result<String> addReview(@RequestBody ReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Destination dest = destinationRepository.findById(request.getDestId())
                .orElseThrow(() -> new RuntimeException("景点不存在"));

        // 保存评论
        Review review = new Review();
        review.setUser(user);
        review.setDestination(dest);
        review.setContent(request.getContent());
        review.setRating(request.getRating());
        review.setCreateTime(LocalDateTime.now());
        reviewRepository.save(review);

        // 🔥 核心逻辑：重新计算平均分并更新到景点表
        updateDestinationRating(dest);

        return Result.success("评价成功");
    }

    private void updateDestinationRating(Destination dest) {
        List<Review> reviews = reviewRepository.findByDestinationIdOrderByCreateTimeDesc(dest.getId());
        if (!reviews.isEmpty()) {
            double avg = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            // 保留1位小数
            double newRating = Math.round(avg * 10.0) / 10.0;

            // 假设 Destination 实体有 setAvgRating 或 setRating 方法
            // 如果你的字段叫 rating，就用 setRating
            // 这里为了保险，尝试去设置 (你需要确保实体里有这个字段)
            dest.setRating(newRating); // 或者 dest.setAvgRating(newRating);
            destinationRepository.save(dest);
        }
    }

    @Data
    static class ReviewRequest {
        private Long userId;
        private Long destId;
        private String content;
        private Integer rating;
    }
}