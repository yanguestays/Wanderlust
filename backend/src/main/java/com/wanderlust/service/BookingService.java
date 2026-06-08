package com.wanderlust.service;

import com.wanderlust.utils.Result;
import java.time.LocalDate;

public interface BookingService {
    /**
     * 创建订单
     */
    Result<String> createBooking(Long userId, Long destId, LocalDate startDate, Integer people, String note);
}