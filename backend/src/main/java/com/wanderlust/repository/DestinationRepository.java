package com.wanderlust.repository;

import com.wanderlust.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {

    /**
     * 🔥 模糊搜索：地名、描述、国家、月份
     */
    @Query("SELECT d FROM Destination d WHERE " +
            "d.title LIKE %:keyword% OR " +
            "d.description LIKE %:keyword% OR " +
            "d.country LIKE %:keyword% OR " +
            "CAST(d.bestMonth AS string) LIKE %:keyword%")
    List<Destination> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 🔥 按月份推荐：根据传入月份查询，并按评分降序排列
     */
    @Query("SELECT d FROM Destination d WHERE d.bestMonth = :month ORDER BY d.rating DESC")
    List<Destination> findByBestMonthOrderByRatingDesc(@Param("month") Integer month);
}