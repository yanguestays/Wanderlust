package com.wanderlust.controller;

import com.wanderlust.dto.BookingDTO;
import com.wanderlust.entity.Booking;
import com.wanderlust.entity.Destination;
import com.wanderlust.entity.User;
import com.wanderlust.repository.BookingRepository;
import com.wanderlust.repository.DestinationRepository;
import com.wanderlust.repository.UserRepository;
import com.wanderlust.utils.Result;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final DestinationRepository destinationRepository;

    // 1. 获取用户订单
    @GetMapping("/user/{userId}")
    public Result<List<BookingDTO>> getUserBookings(@PathVariable Long userId) {
        List<Booking> bookings = bookingRepository.findByUserId(userId);

        List<BookingDTO> dtos = bookings.stream().map(b -> {
            BookingDTO dto = new BookingDTO();
            dto.setId(b.getId());
            if (b.getDestination() != null) {
                dto.setDestinationTitle(b.getDestination().getTitle());
                dto.setDestinationImage(b.getDestination().getPosterUrl());
            }
            dto.setStartDate(b.getStartDate());
            dto.setPeopleCount(b.getPeopleCount());
            dto.setTotalPrice(b.getTotalPrice());
            dto.setStatus(b.getStatus());
            dto.setNote(b.getNote());
            return dto;
        }).collect(Collectors.toList());

        return Result.success(dtos);
    }

    // 2. 创建订单
    @PostMapping("/create")
    public Result<String> createBooking(@RequestBody BookingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Destination dest = destinationRepository.findById(request.getDestId())
                .orElseThrow(() -> new RuntimeException("目的地不存在"));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setDestination(dest);

        // 🔥 类型匹配：使用 LocalDate
        booking.setStartDate(request.getStartDate());
        booking.setPeopleCount(request.getPeople());
        booking.setNote(request.getNote());
        booking.setStatus("PENDING");

        // 🔥 类型匹配：使用 LocalDateTime
        booking.setCreateTime(LocalDateTime.now());

        // 模拟价格计算
        double basePrice = 1000.0;
        booking.setTotalPrice(basePrice * request.getPeople());

        bookingRepository.save(booking);
        return Result.success("预订成功！");
    }

    @Data
    public static class BookingRequest {
        private Long userId;
        private Long destId;
        private LocalDate startDate; // 🔥 必须是 LocalDate
        private Integer people;
        private String note;
    }
}