package com.wanderlust.service.impl;

import com.wanderlust.entity.Booking;
import com.wanderlust.entity.Destination;
import com.wanderlust.entity.User;
import com.wanderlust.repository.BookingRepository;
import com.wanderlust.repository.DestinationRepository;
import com.wanderlust.repository.UserRepository;
import com.wanderlust.service.BookingService;
import com.wanderlust.utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> createBooking(Long userId, Long destId, LocalDate startDate, Integer people, String note) {
        // 1. 校验数据
        if (startDate.isBefore(LocalDate.now())) {
            return Result.error("出发日期不能早于今天");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户未找到"));
        Destination dest = destinationRepository.findById(destId)
                .orElseThrow(() -> new RuntimeException("景点不存在"));

        // 2. 构建订单
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setDestination(dest);
        booking.setStartDate(startDate);
        booking.setPeopleCount(people);
        booking.setNote(note);

        // Mock 价格计算：假设每个人 2000 元基础费 + 随机溢价
        booking.setTotalPrice(people * 2000.0);
        booking.setStatus("PENDING"); // 默认待确认

        // 3. 保存
        bookingRepository.save(booking);
        return Result.success("预订申请提交成功！请在个人中心查看状态。");
    }
}