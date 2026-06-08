package com.wanderlust.service;

import com.wanderlust.entity.Role;
import com.wanderlust.entity.User;
import com.wanderlust.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // 1. 记得导包
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // 2. 注入加密器

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);

        // 🔥 修复点：必须用 matches 方法比对密码
        // 左边是用户输入的明文，右边是数据库里的密文
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("账号或密码错误");
        }
        return user;
    }

    public User register(String username, String password) {
        if (userRepository.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);

        // 🔥 修复点：注册时必须加密存储
        user.setPassword(passwordEncoder.encode(password));

        // 兼容处理：如果没有 Role 枚举，确保这里逻辑正确
        try {
            user.setRole(Role.USER.name());
        } catch (Exception e) {
            user.setRole("USER");
        }

        user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + username);
        return userRepository.save(user);
    }
}