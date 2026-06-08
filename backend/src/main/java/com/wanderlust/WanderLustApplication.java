package com.wanderlust;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync // 🔥 关键：开启异步支持，否则并行检索会变成串行！
public class WanderLustApplication {

    public static void main(String[] args) {
        SpringApplication.run(WanderLustApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  WanderLust AI 后端启动成功   ლ(´ڡ`ლ)ﾞ");
        System.out.println("👉 接口文档地址: http://localhost:8080/swagger-ui.html (如果配了Swagger)");
    }
}