package com.riderecs.car_listings_service.service;

import com.riderecs.car_listings_service.dto.CarListingResponse;
import com.riderecs.car_listings_service.entity.*;
import com.riderecs.car_listings_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnalyticsService {
    
    @Autowired
    private CarViewRepository carViewRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private FavouriteRepository favouriteRepository;
    
    @Autowired
    private SavedSearchRepository savedSearchRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Get most popular cars this week
    public Page<CarListingResponse> getPopularCarsThisWeek(Pageable pageable) {
        LocalDateTime weekStart = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY)).withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Object[]> results = carViewRepository.findMostViewedCarsThisWeek(weekStart, pageable);
        return convertCarIdsToCarListingResponse(results, pageable);
    }
    
    // Get most popular cars this month
    public Page<CarListingResponse> getPopularCarsThisMonth(Pageable pageable) {
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Object[]> results = carViewRepository.findMostViewedCarsThisMonth(monthStart, pageable);
        return convertCarIdsToCarListingResponse(results, pageable);
    }
    
    // Get trending cars (gaining popularity)
    public Page<CarListingResponse> getTrendingCars(Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime recentEnd = now;
        LocalDateTime recentStart = now.minusDays(7); // Last 7 days
        LocalDateTime previousEnd = recentStart;
        LocalDateTime previousStart = previousEnd.minusDays(7); // Previous 7 days
        
        List<Object[]> results = carViewRepository.findTrendingCars(recentStart, recentEnd, previousStart, previousEnd, pageable);
        return convertCarIdsToCarListingResponse(results, pageable);
    }
    
    // Get popular cars for homepage (combined metrics)
    public Page<CarListingResponse> getHomepagePopularCars(Pageable pageable) {
        // For homepage, we'll use the last 30 days as a good balance
        LocalDateTime monthStart = LocalDateTime.now().minusDays(30);
        List<Object[]> results = carViewRepository.findMostViewedCarsInPeriod(monthStart, LocalDateTime.now(), pageable);
        return convertCarIdsToCarListingResponse(results, pageable);
    }
    
    // Get most popular cars in custom date range
    public Page<CarListingResponse> getPopularCarsInDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay(); // Include the end date
        List<Object[]> results = carViewRepository.findMostViewedCarsInPeriod(start, end, pageable);
        return convertCarIdsToCarListingResponse(results, pageable);
    }
    
    // Get view statistics for a specific car
    public Map<String, Object> getCarViewStatistics(Long carId) {
        Object[] stats = carViewRepository.getViewStatsByCarId(carId);
        Map<String, Object> result = new HashMap<>();
        
        if (stats != null && stats.length >= 4) {
            result.put("totalViews", stats[0] != null ? ((Number) stats[0]).longValue() : 0L);
            result.put("uniqueViews", stats[1] != null ? ((Number) stats[1]).longValue() : 0L);
            result.put("firstView", stats[2]);
            result.put("lastView", stats[3]);
        } else {
            result.put("totalViews", 0L);
            result.put("uniqueViews", 0L);
            result.put("firstView", null);
            result.put("lastView", null);
        }
        
        return result;
    }
    
    // Record a car view (for tracking popularity)
    public void recordCarView(Long carId, Long userId, String ipAddress, String userAgent) {
        Optional<Car> carOpt = carRepository.findById(carId);
        if (!carOpt.isPresent()) {
            return; // Car not found
        }
        
        Car car = carOpt.get();
        
        // Check if this user/IP has viewed this car recently (within last hour to avoid spam)
        LocalDateTime recentTime = LocalDateTime.now().minusHours(1);
        List<CarView> recentViews = carViewRepository.findRecentViewsBySameUser(carId, userId, ipAddress, recentTime);
        
        if (!recentViews.isEmpty()) {
            return; // Already viewed recently, don't count again
        }
        
        CarView carView = new CarView();
        carView.setCar(car);
        carView.setIpAddress(ipAddress);
        carView.setUserAgent(userAgent);
        
        if (userId != null) {
            Optional<User> userOpt = userRepository.findById(userId);
            userOpt.ifPresent(carView::setViewer);
        }
        
        carViewRepository.save(carView);
    }
    
    // Get platform statistics
    public Map<String, Object> getPlatformStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Total cars
        long totalCars = carRepository.countByStatus(CarStatus.ACTIVE);
        stats.put("totalActiveCars", totalCars);
        
        // Total users
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);
        
        // Total views this month
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<CarView> monthViews = carViewRepository.findByViewedAtBetween(monthStart, LocalDateTime.now());
        stats.put("viewsThisMonth", monthViews.size());
        
        // Total favourites
        long totalFavourites = favouriteRepository.count();
        stats.put("totalFavourites", totalFavourites);
        
        return stats;
    }
    
    // Get most favorited cars
    public Page<CarListingResponse> getMostFavoritedCars(Pageable pageable) {
        // We need to create a custom query for this since FavouriteRepository doesn't have it
        List<Object[]> results = getFavouriteCountsGroupedByCarId(pageable);
        return convertCarIdsToCarListingResponse(results, pageable);
    }
    
    // Get popular search terms
    public List<Map<String, Object>> getPopularSearchTerms(int limit) {
        // Since we don't have a search log table, we'll use SavedSearch data as a proxy
        List<SavedSearch> allSearches = savedSearchRepository.findAll();
        
        Map<String, Integer> termCounts = new HashMap<>();
        
        for (SavedSearch search : allSearches) {
            // Count keywords
            if (search.getKeyword() != null && !search.getKeyword().trim().isEmpty()) {
                String[] keywords = search.getKeyword().toLowerCase().split("\\s+");
                for (String keyword : keywords) {
                    termCounts.merge(keyword.trim(), 1, Integer::sum);
                }
            }
            
            // Count makes
            if (search.getMake() != null && !search.getMake().trim().isEmpty()) {
                termCounts.merge(search.getMake().toLowerCase().trim(), 1, Integer::sum);
            }
            
            // Count models
            if (search.getModel() != null && !search.getModel().trim().isEmpty()) {
                termCounts.merge(search.getModel().toLowerCase().trim(), 1, Integer::sum);
            }
        }
        
        return termCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("term", entry.getKey());
                    result.put("count", entry.getValue());
                    return result;
                })
                .collect(Collectors.toList());
    }
    
    // Get user engagement metrics
    public Map<String, Object> getUserEngagementMetrics(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> metrics = new HashMap<>();
        
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.now();
        
        // Views in period
        List<CarView> views = carViewRepository.findByViewedAtBetween(start, end);
        metrics.put("totalViews", views.size());
        
        // Unique viewers
        Set<String> uniqueViewers = views.stream()
                .map(view -> view.getViewer() != null ? view.getViewer().getId().toString() : view.getIpAddress())
                .collect(Collectors.toSet());
        metrics.put("uniqueViewers", uniqueViewers.size());
        
        // Average views per day
        long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
        if (daysBetween > 0) {
            metrics.put("averageViewsPerDay", views.size() / (double) daysBetween);
        } else {
            metrics.put("averageViewsPerDay", 0.0);
        }
        
        return metrics;
    }
    
    // Helper method to convert car IDs from query results to CarListingResponse
    private Page<CarListingResponse> convertCarIdsToCarListingResponse(List<Object[]> results, Pageable pageable) {
        List<Long> carIds = results.stream()
                .map(result -> ((Number) result[0]).longValue())
                .collect(Collectors.toList());
        
        if (carIds.isEmpty()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        
        // Fetch cars and maintain the order from the query results
        Map<Long, Car> carMap = carRepository.findAllById(carIds).stream()
                .collect(Collectors.toMap(Car::getId, car -> car));
        
        List<CarListingResponse> responses = carIds.stream()
                .map(carMap::get)
                .filter(Objects::nonNull)
                .filter(car -> car.getStatus() == CarStatus.ACTIVE) // Only active cars
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, responses.size());
    }
    
    // Helper method to get favourite counts grouped by car ID
    private List<Object[]> getFavouriteCountsGroupedByCarId(Pageable pageable) {
        // Since we can't easily add a custom query to FavouriteRepository without modifying it,
        // we'll fetch all favourites and group them manually
        List<Favourite> allFavourites = favouriteRepository.findAll();
        
        Map<Long, Long> carFavouriteCounts = allFavourites.stream()
                .filter(fav -> fav.getCar().getStatus() == CarStatus.ACTIVE)
                .collect(Collectors.groupingBy(
                        fav -> fav.getCar().getId(),
                        Collectors.counting()
                ));
        
        return carFavouriteCounts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize())
                .map(entry -> new Object[]{entry.getKey(), entry.getValue()})
                .collect(Collectors.toList());
    }
    
    // Helper method to convert Car entity to CarListingResponse
    private CarListingResponse convertToResponse(Car car) {
        CarListingResponse response = new CarListingResponse();
        response.setId(car.getId());
        response.setMake(car.getMake());
        response.setModel(car.getModel());
        response.setYear(car.getYear());
        response.setMileage(car.getMileage());
        response.setCondition(car.getCondition());
        response.setAskingPrice(car.getAskingPrice());
        response.setDescription(car.getDescription());
        response.setColor(car.getColor());
        response.setFuelType(car.getFuelType());
        response.setTransmission(car.getTransmission());
        response.setNumberOfDoors(car.getNumberOfDoors());
        response.setEngineSize(car.getEngineSize());
        response.setLocation(car.getLocation());
        response.setContactPhone(car.getContactPhone());
        response.setContactEmail(car.getContactEmail());
        response.setStatus(car.getStatus());
        response.setCreatedAt(car.getCreatedAt());
        response.setUpdatedAt(car.getUpdatedAt());
        
        // Set seller info
        User seller = car.getSeller();
        CarListingResponse.SellerInfo sellerInfo = new CarListingResponse.SellerInfo(
                seller.getId(),
                seller.getFirstName(),
                seller.getLastName(),
                seller.getUsername()
        );
        response.setSeller(sellerInfo);
        
        return response;
    }
}