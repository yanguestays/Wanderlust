package com.wanderlust.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Integer rating; // 1-5

    private LocalDateTime createTime;

    // 关联用户
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 关联景点
    @ManyToOne
    @JoinColumn(name = "destination_id")
    @JsonIgnore // 防止死循环
    private Destination destination;
}