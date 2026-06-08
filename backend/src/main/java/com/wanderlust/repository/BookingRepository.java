package com.wanderlust.repository;

import com.wanderlust.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // 查某人的所有订单（按时间倒序）
    List<Booking> findByUserIdOrderByCreateTimeDesc(Long userId);

    List<Booking> findByUserId(Long userId);
}