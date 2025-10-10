package com.riderecs.car_listings_service.repository;

import com.riderecs.car_listings_service.entity.CarView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CarViewRepository extends JpaRepository<CarView, Long> {
    
    // Count total views for a car
    Long countByCarId(Long carId);
    
    // Count unique views for a car (by IP/User)
    @Query("SELECT COUNT(DISTINCT CASE WHEN cv.viewer IS NOT NULL THEN cv.viewer.id ELSE cv.ipAddress END) " +
           "FROM CarView cv WHERE cv.car.id = :carId")
    Long countUniqueViewsByCarId(@Param("carId") Long carId);
    
    // Find views within date range
    List<CarView> findByViewedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Get most viewed cars within date range
    @Query("SELECT cv.car.id, COUNT(cv) as viewCount " +
           "FROM CarView cv " +
           "WHERE cv.viewedAt BETWEEN :startDate AND :endDate " +
           "GROUP BY cv.car.id " +
           "ORDER BY viewCount DESC")
    List<Object[]> findMostViewedCarsInPeriod(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate,
                                             Pageable pageable);
    
    // Get most viewed cars this week
    @Query("SELECT cv.car.id, COUNT(cv) as viewCount " +
           "FROM CarView cv " +
           "WHERE cv.viewedAt >= :weekStart " +
           "GROUP BY cv.car.id " +
           "ORDER BY viewCount DESC")
    List<Object[]> findMostViewedCarsThisWeek(@Param("weekStart") LocalDateTime weekStart,
                                             Pageable pageable);
    
    // Get most viewed cars this month
    @Query("SELECT cv.car.id, COUNT(cv) as viewCount " +
           "FROM CarView cv " +
           "WHERE cv.viewedAt >= :monthStart " +
           "GROUP BY cv.car.id " +
           "ORDER BY viewCount DESC")
    List<Object[]> findMostViewedCarsThisMonth(@Param("monthStart") LocalDateTime monthStart,
                                              Pageable pageable);
    
    // Check if user/IP has viewed a car recently (to avoid duplicate counts)
    @Query("SELECT cv FROM CarView cv " +
           "WHERE cv.car.id = :carId " +
           "AND (" +
           "   (cv.viewer.id = :viewerId AND :viewerId IS NOT NULL) OR " +
           "   (cv.ipAddress = :ipAddress AND cv.viewer IS NULL AND :viewerId IS NULL)" +
           ") " +
           "AND cv.viewedAt >= :recentTime")
    List<CarView> findRecentViewsBySameUser(@Param("carId") Long carId,
                                           @Param("viewerId") Long viewerId,
                                           @Param("ipAddress") String ipAddress,
                                           @Param("recentTime") LocalDateTime recentTime);
    
    // Get view statistics for a car
    @Query("SELECT " +
           "COUNT(cv) as totalViews, " +
           "COUNT(DISTINCT CASE WHEN cv.viewer IS NOT NULL THEN cv.viewer.id ELSE cv.ipAddress END) as uniqueViews, " +
           "MIN(cv.viewedAt) as firstView, " +
           "MAX(cv.viewedAt) as lastView " +
           "FROM CarView cv WHERE cv.car.id = :carId")
    Object[] getViewStatsByCarId(@Param("carId") Long carId);
    
    // Find trending cars (most views in recent period compared to previous period)
    @Query("SELECT cv.car.id, COUNT(cv) as recentViews " +
           "FROM CarView cv " +
           "WHERE cv.viewedAt BETWEEN :recentStart AND :recentEnd " +
           "GROUP BY cv.car.id " +
           "HAVING COUNT(cv) > (" +
           "   SELECT COALESCE(COUNT(cv2) * 1.5, 0) " +
           "   FROM CarView cv2 " +
           "   WHERE cv2.car.id = cv.car.id " +
           "   AND cv2.viewedAt BETWEEN :previousStart AND :previousEnd" +
           ") " +
           "ORDER BY recentViews DESC")
    List<Object[]> findTrendingCars(@Param("recentStart") LocalDateTime recentStart,
                                   @Param("recentEnd") LocalDateTime recentEnd,
                                   @Param("previousStart") LocalDateTime previousStart,
                                   @Param("previousEnd") LocalDateTime previousEnd,
                                   Pageable pageable);
}