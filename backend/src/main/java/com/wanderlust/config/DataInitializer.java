package com.wanderlust.config; // 建议放在 config 包

import com.wanderlust.entity.User;
import com.wanderlust.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin") == null) {
            log.info("正在初始化管理员账号...");
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            admin.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=admin");

            userRepository.save(admin);
            log.info("管理员账号初始化完成: admin / admin123");
        }
    }
}