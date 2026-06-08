package com.wanderlust.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 谁订的？
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User user;

    // 订的哪里？
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    private Destination destination;

    // 核心业务字段
    private LocalDate startDate;    // 出发日期
    private Integer peopleCount;    // 出行人数
    private String status;          // PENDING(待确认), CONFIRMED(已确认), CANCELLED(已取消)
    private Double totalPrice;      // 预估价格 (可以是 mock 数据)

    @Column(length = 500)
    private String note;            // 备注需求

    private LocalDateTime createTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        if (this.status == null) this.status = "PENDING";
    }
}