package com.wanderlust.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingDTO {
    private Long id;
    private String destinationTitle;
    private String destinationImage;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer peopleCount;
    private Double totalPrice;
    private String status;
    private String note;
}