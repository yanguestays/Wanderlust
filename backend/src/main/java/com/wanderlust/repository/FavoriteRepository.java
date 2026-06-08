package com.wanderlust.repository;

import com.wanderlust.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    // 核心查询：某人是否收藏了某地
    Optional<Favorite> findByUserIdAndDestinationId(Long userId, Long destinationId);

    List<Favorite> findByUserIdOrderByCreateTimeDesc(Long userId);

    List<Favorite> findByUserId(Long userId);
}