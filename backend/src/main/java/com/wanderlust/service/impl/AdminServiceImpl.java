package com.wanderlust.service.impl;

import com.wanderlust.entity.User;
import com.wanderlust.repository.UserRepository;
import com.wanderlust.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 这是一个实现类 (Class)
 * 它必须实现 AdminService 接口中定义的所有方法
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    /**
     * 逻辑 1：获取所有用户并脱敏
     */
    @Override
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        // 🛡️ 工业规范：保护隐私，不向前端暴露哈希后的密码
        users.forEach(u -> u.setPassword(null));
        return users;
    }

    /**
     * 逻辑 2：带有业务保护的删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserSafely(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("该用户不存在"));

        // 🛡️ 核心规则：禁止删除 admin
        if ("admin".equalsIgnoreCase(user.getUsername())) {
            throw new RuntimeException("系统内置管理员账号禁止删除！");
        }

        // 级联删除由 User 实体类的 CascadeType.ALL 负责
        userRepository.deleteById(id);
        log.info("管理员注销了用户: {}", user.getUsername());
    }

    /**
     * 逻辑 3：Excel 导入 (此处省略具体 EasyExcel 代码，保持你原有的即可)
     */
    @Override
    public void importDestinations(MultipartFile file) {
        log.info("开始处理文件导入: {}", file.getOriginalFilename());
        // ... 原有导入逻辑
    }
}