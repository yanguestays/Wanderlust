package com.wanderlust.repository;

import com.wanderlust.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    // 找某个景点的所有评论，按时间倒序
    List<Review> findByDestinationIdOrderByCreateTimeDesc(Long destinationId);
}