package com.wanderlust.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // 1. 告诉 Spring Boot 这是一个配置类，启动时要加载它
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 添加映射路径
        registry.addMapping("/**") // 2. 所有的接口 (如 /api/**) 都应用此规则
                .allowedOriginPatterns("*") // 3. 允许所有的前端域名访问 (开发阶段为了省事写 *，生产环境要写具体域名)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 4. 允许这几种 HTTP 方法
                .allowedHeaders("*") // 5. 允许所有的 Header (比如 Token)
                .allowCredentials(true) // 6. 允许携带凭证 (如 Cookies)
                .maxAge(3600); // 7. 这个配置的有效期 (1小时内不用再次预检)
    }
}