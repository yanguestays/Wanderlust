package com.wanderlust.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data // Lombok 会自动为所有字段生成 get/set 方法
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    // 🔥 之前你的代码里缺了这个！加上它，AuthService 就不报错了
    private String avatar;

    private String role; // ADMIN 或 USER

    // 级联删除收藏
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Favorite> favorites;
}