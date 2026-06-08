package com.wanderlust.controller;

import com.wanderlust.dto.UserDTO;
import com.wanderlust.entity.User;
import com.wanderlust.repository.UserRepository; // 或者用 Service
import com.wanderlust.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin") // 对应前端请求的 /admin 前缀
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;

    // 前端请求的是: GET http://localhost:8080/admin/users
    @GetMapping("/users")
    public Result<List<UserDTO>> getAllUsers() {
        // 1. 从数据库查出原生实体 (带有一堆危险的关联关系)
        List<User> users = userRepository.findAll();

        // 2. 转换成安全的 DTO
        List<UserDTO> userDTOs = users.stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getRole(),
                        user.getAvatar()
                ))
                .collect(Collectors.toList());

        // 3. 返回干净的数据
        return Result.success("获取成功", userDTOs);
    }
}