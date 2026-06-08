package com.wanderlust.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "destination")
public class Destination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 对应 Excel A列: 目的地名称
    @Column(nullable = false)
    private String title;

    // 对应 Excel B列: 国家/地区
    private String country;

    // 对应 Excel C列: 最佳旅行月份
    @Column(name = "best_month")
    private Integer bestMonth;

    // 对应 Excel D列: 景点描述与氛围
    // 描述通常很长，必须用 TEXT，否则超过 255 字符会报错
    @Column(columnDefinition = "TEXT")
    private String description;

    // 对应 Excel E列: 风景大图 URL
    // URL 有时候会很长，给 500 比较保险
    @Column(name = "poster_url", length = 500)
    private String posterUrl;

    // 推荐指数
    private Double rating;

    // 🔥🔥🔥 核心修改：向量数据 (1024维数组转成的 String)
    // 1. @Lob: 告诉 JPA 这是一个大对象 (Large Object)
    // 2. columnDefinition = "LONGTEXT": 强制 MySQL 使用 LONGTEXT 类型 (最大 4GB)，
    //    或者使用 "TEXT" (最大 64KB)。因为向量 JSON 很长 (约 15KB)，普通 TEXT 勉强够，
    //    但为了绝对安全，建议用 LONGTEXT，避免任何截断风险。
    @Lob
    @Column(name = "embedding_vector", columnDefinition = "LONGTEXT")
    private String embeddingVector;
}