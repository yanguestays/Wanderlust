package com.wanderlust.service.impl;

import com.wanderlust.entity.Destination;
import com.wanderlust.entity.Favorite;
import com.wanderlust.entity.User;
import com.wanderlust.repository.DestinationRepository;
import com.wanderlust.repository.FavoriteRepository;
import com.wanderlust.repository.UserRepository;
import com.wanderlust.service.FavoriteService;
import com.wanderlust.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> toggleFavorite(Long userId, Long destId) {
        // 1. 先查是否已收藏
        Optional<Favorite> existing = favoriteRepository.findByUserIdAndDestinationId(userId, destId);

        if (existing.isPresent()) {
            // 2. 存在 -> 执行取消逻辑
            favoriteRepository.delete(existing.get());
            return Result.success("已取消收藏");
        } else {
            // 3. 不存在 -> 执行添加逻辑
            // 这里要做严格的空值检查，防止外键报错
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("当前用户异常，无法收藏"));
            Destination dest = destinationRepository.findById(destId)
                    .orElseThrow(() -> new RuntimeException("景点不存在，无法收藏"));

            Favorite fav = new Favorite();
            fav.setUser(user);
            fav.setDestination(dest);
            favoriteRepository.save(fav);
            return Result.success("收藏成功");
        }
    }

    @Override
    public Result<Boolean> checkStatus(Long userId, Long destId) {
        boolean exists = favoriteRepository.findByUserIdAndDestinationId(userId, destId).isPresent();
        return Result.success(exists);
    }
}