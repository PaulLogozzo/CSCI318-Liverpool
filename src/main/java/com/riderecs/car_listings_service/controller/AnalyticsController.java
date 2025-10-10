package com.riderecs.car_listings_service.controller;

import com.riderecs.car_listings_service.dto.CarListingResponse;
import com.riderecs.car_listings_service.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    // Get most popular cars this week
    @GetMapping("/popular/week")
    public ResponseEntity<Page<CarListingResponse>> getPopularCarsThisWeek(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CarListingResponse> popularCars = analyticsService.getPopularCarsThisWeek(pageable);
        return ResponseEntity.ok(popularCars);
    }
    
    // Get most popular cars this month
    @GetMapping("/popular/month")
    public ResponseEntity<Page<CarListingResponse>> getPopularCarsThisMonth(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CarListingResponse> popularCars = analyticsService.getPopularCarsThisMonth(pageable);
        return ResponseEntity.ok(popularCars);
    }
    
    // Get trending cars (gaining popularity)
    @GetMapping("/trending")
    public ResponseEntity<Page<CarListingResponse>> getTrendingCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CarListingResponse> trendingCars = analyticsService.getTrendingCars(pageable);
        return ResponseEntity.ok(trendingCars);
    }
    
    // Get popular cars for homepage (combined metrics)
    @GetMapping("/homepage/popular")
    public ResponseEntity<Page<CarListingResponse>> getHomepagePopularCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CarListingResponse> popularCars = analyticsService.getHomepagePopularCars(pageable);
        return ResponseEntity.ok(popularCars);
    }
    
    // Get most popular cars in custom date range
    @GetMapping("/popular/custom")
    public ResponseEntity<Page<CarListingResponse>> getPopularCarsInRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CarListingResponse> popularCars = analyticsService.getPopularCarsInDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(popularCars);
    }
    
    // Get view statistics for a specific car
    @GetMapping("/views/{carId}")
    public ResponseEntity<Map<String, Object>> getCarViewStats(@PathVariable Long carId) {
        Map<String, Object> stats = analyticsService.getCarViewStatistics(carId);
        return ResponseEntity.ok(stats);
    }
    
    // Record a car view (for tracking popularity)
    @PostMapping("/views/{carId}")
    public ResponseEntity<Void> recordCarView(
            @PathVariable Long carId,
            @RequestHeader(value = "User-Id", required = false) Long userId,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            @RequestHeader(value = "X-Real-IP", required = false) String realIP,
            @RequestHeader(value = "User-Agent", required = false) String userAgent) {
        
        // Determine the actual IP address
        String ipAddress = forwardedFor != null ? forwardedFor : realIP;
        if (ipAddress == null) {
            ipAddress = "unknown";
        }
        
        analyticsService.recordCarView(carId, userId, ipAddress, userAgent);
        return ResponseEntity.ok().build();
    }
    
    // Get platform statistics
    @GetMapping("/stats/platform")
    public ResponseEntity<Map<String, Object>> getPlatformStatistics() {
        Map<String, Object> stats = analyticsService.getPlatformStatistics();
        return ResponseEntity.ok(stats);
    }
    
    // Get most favorited cars
    @GetMapping("/favorites/top")
    public ResponseEntity<Page<CarListingResponse>> getMostFavoritedCars(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CarListingResponse> favoritedCars = analyticsService.getMostFavoritedCars(pageable);
        return ResponseEntity.ok(favoritedCars);
    }
    
    // Get search analytics (popular search terms)
    @GetMapping("/search/popular-terms")
    public ResponseEntity<List<Map<String, Object>>> getPopularSearchTerms(
            @RequestParam(defaultValue = "10") int limit) {
        List<Map<String, Object>> popularTerms = analyticsService.getPopularSearchTerms(limit);
        return ResponseEntity.ok(popularTerms);
    }
    
    // Get user engagement metrics
    @GetMapping("/engagement")
    public ResponseEntity<Map<String, Object>> getUserEngagementMetrics(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> metrics = analyticsService.getUserEngagementMetrics(startDate, endDate);
        return ResponseEntity.ok(metrics);
    }
}