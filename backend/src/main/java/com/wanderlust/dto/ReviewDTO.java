package com.wanderlust.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO {
    private Long id;
    private String username;
    private String avatar;
    private String content;
    private Integer rating;
    private LocalDateTime createTime;
}