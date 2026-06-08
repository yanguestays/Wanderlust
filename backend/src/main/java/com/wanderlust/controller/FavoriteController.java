package com.wanderlust.controller;

import com.wanderlust.dto.FavoriteDTO;
import com.wanderlust.entity.Destination;
import com.wanderlust.entity.Favorite;
import com.wanderlust.entity.User;
import com.wanderlust.repository.DestinationRepository;
import com.wanderlust.repository.FavoriteRepository;
import com.wanderlust.repository.UserRepository;
import com.wanderlust.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // 🔥 使用 java.time
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    // 1. 获取用户收藏
    @GetMapping("/user/{userId}")
    public Result<List<FavoriteDTO>> getUserFavorites(@PathVariable Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        List<FavoriteDTO> dtos = favorites.stream().map(f -> {
            FavoriteDTO dto = new FavoriteDTO();
            dto.setId(f.getId());
            if (f.getDestination() != null) {
                dto.setDestinationId(f.getDestination().getId());
                dto.setDestinationTitle(f.getDestination().getTitle());
                dto.setDestinationImage(f.getDestination().getPosterUrl());
                dto.setDestinationCountry(f.getDestination().getCountry());
            }
            dto.setCreateTime(f.getCreateTime());
            return dto;
        }).collect(Collectors.toList());

        return Result.success(dtos);
    }

    // 2. 检查收藏状态
    @GetMapping("/check")
    public Result<Boolean> checkFavorite(@RequestParam Long userId, @RequestParam Long destId) {
        List<Favorite> list = favoriteRepository.findByUserId(userId);
        boolean exists = list.stream()
                .anyMatch(f -> f.getDestination() != null && f.getDestination().getId().equals(destId));
        return Result.success(exists);
    }

    // 3. 切换收藏
    @PostMapping("/toggle")
    public Result<String> toggleFavorite(@RequestBody Map<String, Long> params) {
        Long userId = params.get("userId");
        Long destId = params.get("destId");

        List<Favorite> list = favoriteRepository.findByUserId(userId);
        Optional<Favorite> existing = list.stream()
                .filter(f -> f.getDestination() != null && f.getDestination().getId().equals(destId))
                .findFirst();

        if (existing.isPresent()) {
            favoriteRepository.delete(existing.get());
            return Result.success("已取消收藏");
        } else {
            Favorite fav = new Favorite();
            User user = userRepository.findById(userId).orElseThrow();
            Destination dest = destinationRepository.findById(destId).orElseThrow();

            fav.setUser(user);
            fav.setDestination(dest);
            // 🔥 类型匹配：使用 LocalDateTime
            fav.setCreateTime(LocalDateTime.now());

            favoriteRepository.save(fav);
            return Result.success("已加入收藏");
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        favoriteRepository.deleteById(id);
        return Result.success("删除成功");
    }
}