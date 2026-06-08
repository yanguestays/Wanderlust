package com.wanderlust.service;

import com.wanderlust.utils.Result;

public interface FavoriteService {
    /**
     * 切换收藏状态（已收藏则取消，未收藏则添加）
     * @param userId 用户ID
     * @param destId 景点ID
     * @return 操作结果提示
     */
    Result<String> toggleFavorite(Long userId, Long destId);

    /**
     * 检查当前是否已收藏
     * @param userId 用户ID
     * @param destId 景点ID
     * @return true=已收藏, false=未收藏
     */
    Result<Boolean> checkStatus(Long userId, Long destId);
}