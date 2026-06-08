package com.wanderlust.service;

import com.wanderlust.entity.User;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface AdminService {
    void importDestinations(MultipartFile file);
    List<User> getAllUsers(); // 获取用户列表规范
    void deleteUserSafely(Long id); // 安全删除规范
}