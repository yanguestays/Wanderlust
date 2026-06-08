package com.wanderlust.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FavoriteDTO {
    private Long id;
    private Long destinationId;
    private String destinationTitle;
    private String destinationImage;
    private String destinationCountry;
    private LocalDateTime createTime;
}