package com.riderecs.car_listings_service.service;

import com.riderecs.car_listings_service.dto.MarketAverageResponse;
import com.riderecs.car_listings_service.entity.*;
import com.riderecs.car_listings_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarketAnalysisService {
    
    @Autowired
    private MarketAverageRepository marketAverageRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Get market average for specific make, model, and year
    public MarketAverageResponse getMarketAverage(String make, String model, Integer year) {
        MarketAverage marketAverage = marketAverageRepository.findByMakeAndModelAndYear(make, model, year)
                .orElseThrow(() -> new IllegalArgumentException("Market data not found for specified make, model, and year"));
        
        return convertToResponse(marketAverage);
    }
    
    // Get all market averages for a specific make
    public List<MarketAverageResponse> getMarketAveragesByMake(String make) {
        List<MarketAverage> averages = marketAverageRepository.findByMake(make);
        return averages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Get all market averages for a specific make and model
    public List<MarketAverageResponse> getMarketAveragesByMakeAndModel(String make, String model) {
        List<MarketAverage> averages = marketAverageRepository.findByMakeAndModel(make, model);
        return averages.stream()
                .map(this::convertToResponse)
                .sorted(Comparator.comparing(MarketAverageResponse::getYear, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }
    
    // Get available makes in the market data
    public List<String> getAvailableMakes() {
        return marketAverageRepository.findDistinctMakes();
    }
    
    // Get available models for a specific make
    public List<String> getAvailableModels(String make) {
        return marketAverageRepository.findDistinctModelsByMake(make);
    }
    
    // Get available years for a specific make and model
    public List<Integer> getAvailableYears(String make, String model) {
        return marketAverageRepository.findDistinctYearsByMakeAndModel(make, model);
    }
    
    // Get price distribution for make, model, year
    public Map<String, Object> getPriceDistribution(String make, String model, Integer year) {
        Optional<MarketAverage> marketAverageOpt = marketAverageRepository.findByMakeAndModelAndYear(make, model, year);
        if (!marketAverageOpt.isPresent()) {
            return Collections.emptyMap();
        }
        
        MarketAverage marketAverage = marketAverageOpt.get();
        Map<String, Object> distribution = new HashMap<>();
        
        distribution.put("make", make);
        distribution.put("model", model);
        distribution.put("year", year);
        distribution.put("averagePrice", marketAverage.getAveragePrice());
        distribution.put("medianPrice", marketAverage.getMedianPrice());
        distribution.put("minPrice", marketAverage.getMinPrice());
        distribution.put("maxPrice", marketAverage.getMaxPrice());
        distribution.put("priceRange", marketAverage.getMaxPrice().subtract(marketAverage.getMinPrice()));
        distribution.put("standardDeviation", calculateStandardDeviation(marketAverage));
        
        // Price percentiles (approximated)
        BigDecimal range = marketAverage.getMaxPrice().subtract(marketAverage.getMinPrice());
        distribution.put("percentile25", marketAverage.getMinPrice().add(range.multiply(new BigDecimal("0.25"))));
        distribution.put("percentile75", marketAverage.getMinPrice().add(range.multiply(new BigDecimal("0.75"))));
        
        return distribution;
    }
    
    // Get market trends (top performing models)
    public List<MarketAverageResponse> getTopPerformingModels(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<MarketAverage> topPerformers = marketAverageRepository.findTopPerformingModels();
        
        return topPerformers.stream()
                .limit(limit)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Get high liquidity markets
    public List<MarketAverageResponse> getHighLiquidityMarkets(int minSoldListings, int limit) {
        List<MarketAverage> highLiquidity = marketAverageRepository.findHighLiquidityMarkets(minSoldListings);
        
        return highLiquidity.stream()
                .limit(limit)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    // Compare multiple models
    public List<MarketAverageResponse> compareModels(List<String> makes, List<String> models, List<Integer> years) {
        if (makes.size() != models.size() || models.size() != years.size()) {
            throw new IllegalArgumentException("Makes, models, and years lists must have the same size");
        }
        
        List<MarketAverageResponse> comparison = new ArrayList<>();
        
        for (int i = 0; i < makes.size(); i++) {
            String make = makes.get(i);
            String model = models.get(i);
            Integer year = years.get(i);
            
            Optional<MarketAverage> marketAverage = marketAverageRepository.findByMakeAndModelAndYear(make, model, year);
            if (marketAverage.isPresent()) {
                comparison.add(convertToResponse(marketAverage.get()));
            } else {
                // Create placeholder response for missing data
                MarketAverageResponse placeholder = new MarketAverageResponse(make, model, year);
                placeholder.setAveragePrice(BigDecimal.ZERO);
                comparison.add(placeholder);
            }
        }
        
        return comparison;
    }
    
    // Get market insights for a specific model
    public Map<String, Object> getMarketInsights(String make, String model, Integer year) {
        Map<String, Object> insights = new HashMap<>();
        
        if (year != null) {
            // Specific year insights
            Optional<MarketAverage> marketAverageOpt = marketAverageRepository.findByMakeAndModelAndYear(make, model, year);
            if (marketAverageOpt.isPresent()) {
                MarketAverage marketAverage = marketAverageOpt.get();
                insights.putAll(generateInsightsForMarketAverage(marketAverage));
            }
        } else {
            // All years for this make/model
            List<MarketAverage> allYears = marketAverageRepository.findByMakeAndModel(make, model);
            if (!allYears.isEmpty()) {
                insights.putAll(generateInsightsForModelAllYears(allYears));
            }
        }
        
        insights.put("make", make);
        insights.put("model", model);
        insights.put("year", year);
        insights.put("generatedAt", LocalDateTime.now());
        
        return insights;
    }
    
    // Get depreciation analysis
    public Map<String, Object> getDepreciationAnalysis(String make, String model, Integer startYear, Integer endYear) {
        int actualStartYear = startYear != null ? startYear : LocalDateTime.now().getYear() - 10;
        int actualEndYear = endYear != null ? endYear : LocalDateTime.now().getYear();
        
        List<MarketAverage> yearlyData = marketAverageRepository.findByMakeAndModel(make, model)
                .stream()
                .filter(ma -> ma.getYear() >= actualStartYear && ma.getYear() <= actualEndYear)
                .sorted(Comparator.comparing(MarketAverage::getYear))
                .collect(Collectors.toList());
        
        Map<String, Object> depreciation = new HashMap<>();
        depreciation.put("make", make);
        depreciation.put("model", model);
        depreciation.put("startYear", actualStartYear);
        depreciation.put("endYear", actualEndYear);
        
        if (yearlyData.size() < 2) {
            depreciation.put("analysis", "Insufficient data for depreciation analysis");
            return depreciation;
        }
        
        // Calculate year-over-year depreciation
        List<Map<String, Object>> yearlyDepreciation = new ArrayList<>();
        BigDecimal totalDepreciation = BigDecimal.ZERO;
        
        for (int i = 1; i < yearlyData.size(); i++) {
            MarketAverage current = yearlyData.get(i);
            MarketAverage previous = yearlyData.get(i - 1);
            
            BigDecimal priceDiff = previous.getAveragePrice().subtract(current.getAveragePrice());
            BigDecimal depreciationRate = priceDiff.divide(previous.getAveragePrice(), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
            
            Map<String, Object> yearData = new HashMap<>();
            yearData.put("fromYear", previous.getYear());
            yearData.put("toYear", current.getYear());
            yearData.put("fromPrice", previous.getAveragePrice());
            yearData.put("toPrice", current.getAveragePrice());
            yearData.put("depreciationAmount", priceDiff);
            yearData.put("depreciationRate", depreciationRate);
            
            yearlyDepreciation.add(yearData);
            totalDepreciation = totalDepreciation.add(depreciationRate);
        }
        
        depreciation.put("yearlyDepreciation", yearlyDepreciation);
        depreciation.put("averageAnnualDepreciation", totalDepreciation.divide(new BigDecimal(yearlyData.size() - 1), 2, RoundingMode.HALF_UP));
        depreciation.put("totalPeriodDepreciation", yearlyData.get(0).getAveragePrice().subtract(yearlyData.get(yearlyData.size() - 1).getAveragePrice()));
        
        return depreciation;
    }
    
    // Get market summary statistics
    public Map<String, Object> getMarketSummary() {
        List<MarketAverage> allMarketData = marketAverageRepository.findAll();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalMarketEntries", allMarketData.size());
        summary.put("totalMakes", getAvailableMakes().size());
        
        // Calculate aggregated statistics
        if (!allMarketData.isEmpty()) {
            BigDecimal totalValue = allMarketData.stream()
                    .map(MarketAverage::getAveragePrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal overallAveragePrice = totalValue.divide(new BigDecimal(allMarketData.size()), 2, RoundingMode.HALF_UP);
            summary.put("overallAveragePrice", overallAveragePrice);
            
            Optional<BigDecimal> maxPrice = allMarketData.stream()
                    .map(MarketAverage::getMaxPrice)
                    .max(BigDecimal::compareTo);
            Optional<BigDecimal> minPrice = allMarketData.stream()
                    .map(MarketAverage::getMinPrice)
                    .min(BigDecimal::compareTo);
            
            summary.put("marketHighestPrice", maxPrice.orElse(BigDecimal.ZERO));
            summary.put("marketLowestPrice", minPrice.orElse(BigDecimal.ZERO));
            
            int totalSoldListings = allMarketData.stream()
                    .mapToInt(MarketAverage::getSoldListings)
                    .sum();
            int totalListings = allMarketData.stream()
                    .mapToInt(MarketAverage::getTotalListings)
                    .sum();
            
            summary.put("totalSoldListings", totalSoldListings);
            summary.put("totalListings", totalListings);
            summary.put("overallSoldPercentage", totalListings > 0 ? (double) totalSoldListings / totalListings * 100 : 0.0);
        }
        
        summary.put("lastUpdated", LocalDateTime.now());
        
        return summary;
    }
    
    // Refresh market averages
    public Map<String, Object> refreshMarketAverages(Long userId, String make, String model, Integer year) {
        // Check if user is admin (simplified - check if user exists and has admin role)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new SecurityException("User not found"));
        
        // Note: In a real application, you'd check user roles here
        // For now, allowing any authenticated user to refresh
        
        Map<String, Object> result = new HashMap<>();
        int refreshedCount = 0;
        
        if (make != null && model != null && year != null) {
            // Refresh specific entry
            refreshedCount = refreshSpecificMarketAverage(make, model, year);
        } else {
            // Refresh all entries that are older than 24 hours
            LocalDateTime cutoffTime = LocalDateTime.now().minusHours(24);
            List<MarketAverage> outdatedEntries = marketAverageRepository.findByLastCalculatedBefore(cutoffTime);
            
            for (MarketAverage entry : outdatedEntries) {
                refreshSpecificMarketAverage(entry.getMake(), entry.getModel(), entry.getYear());
                refreshedCount++;
            }
        }
        
        result.put("refreshedEntries", refreshedCount);
        result.put("timestamp", LocalDateTime.now());
        result.put("requestedBy", userId);
        
        return result;
    }
    
    // Get price prediction for a car
    public Map<String, Object> getPricePrediction(String make, String model, Integer year, Integer mileage, String condition) {
        Map<String, Object> prediction = new HashMap<>();
        
        Optional<MarketAverage> marketAverageOpt = marketAverageRepository.findByMakeAndModelAndYear(make, model, year);
        if (!marketAverageOpt.isPresent()) {
            prediction.put("error", "Market data not available for specified vehicle");
            return prediction;
        }
        
        MarketAverage marketAverage = marketAverageOpt.get();
        BigDecimal basePrice = marketAverage.getAveragePrice();
        
        // Adjust for mileage (simplified algorithm)
        double mileageAdjustment = calculateMileageAdjustment(mileage, marketAverage.getAverageMileage());
        BigDecimal mileageAdjustedPrice = basePrice.multiply(new BigDecimal(1 + mileageAdjustment));
        
        // Adjust for condition
        double conditionMultiplier = getConditionMultiplier(condition);
        BigDecimal finalPrediction = mileageAdjustedPrice.multiply(new BigDecimal(conditionMultiplier));
        
        prediction.put("make", make);
        prediction.put("model", model);
        prediction.put("year", year);
        prediction.put("mileage", mileage);
        prediction.put("condition", condition);
        prediction.put("predictedPrice", finalPrediction.setScale(2, RoundingMode.HALF_UP));
        prediction.put("marketAveragePrice", basePrice);
        prediction.put("priceRange", Map.of(
                "low", finalPrediction.multiply(new BigDecimal("0.9")).setScale(2, RoundingMode.HALF_UP),
                "high", finalPrediction.multiply(new BigDecimal("1.1")).setScale(2, RoundingMode.HALF_UP)
        ));
        prediction.put("confidence", calculatePredictionConfidence(marketAverage));
        prediction.put("factors", Map.of(
                "mileageAdjustment", mileageAdjustment,
                "conditionMultiplier", conditionMultiplier,
                "marketSampleSize", marketAverage.getTotalListings()
        ));
        
        return prediction;
    }
    
    // Helper method to refresh specific market average
    private int refreshSpecificMarketAverage(String make, String model, Integer year) {
        // This would typically recalculate market averages from current car listings
        // For now, we'll just update the lastCalculated timestamp
        Optional<MarketAverage> marketAverageOpt = marketAverageRepository.findByMakeAndModelAndYear(make, model, year);
        
        if (marketAverageOpt.isPresent()) {
            MarketAverage marketAverage = marketAverageOpt.get();
            
            // In a real implementation, you would:
            // 1. Query all active and sold cars of this make/model/year
            // 2. Calculate new averages, medians, etc.
            // 3. Update the entity
            
            // For now, just update the timestamp
            marketAverage.setLastCalculated(LocalDateTime.now());
            marketAverageRepository.save(marketAverage);
            
            return 1;
        }
        
        return 0;
    }
    
    // Helper method to calculate mileage adjustment
    private double calculateMileageAdjustment(Integer actualMileage, Double averageMileage) {
        if (averageMileage == null || averageMileage == 0) {
            return 0.0;
        }
        
        double mileageDifference = actualMileage - averageMileage;
        // Adjust price by approximately -0.1% per 1000 miles above average
        return -(mileageDifference / 1000.0) * 0.001;
    }
    
    // Helper method to get condition multiplier
    private double getConditionMultiplier(String condition) {
        if (condition == null) {
            return 1.0;
        }
        
        switch (condition.toLowerCase()) {
            case "excellent": return 1.15;
            case "very good": return 1.08;
            case "good": return 1.0;
            case "fair": return 0.9;
            case "poor": return 0.75;
            default: return 1.0;
        }
    }
    
    // Helper method to calculate prediction confidence
    private String calculatePredictionConfidence(MarketAverage marketAverage) {
        int sampleSize = marketAverage.getTotalListings();
        
        if (sampleSize >= 50) {
            return "High";
        } else if (sampleSize >= 20) {
            return "Medium";
        } else if (sampleSize >= 5) {
            return "Low";
        } else {
            return "Very Low";
        }
    }
    
    // Helper method to calculate standard deviation (approximated)
    private BigDecimal calculateStandardDeviation(MarketAverage marketAverage) {
        BigDecimal range = marketAverage.getMaxPrice().subtract(marketAverage.getMinPrice());
        // Rough approximation: standard deviation ≈ range / 4
        return range.divide(new BigDecimal("4"), 2, RoundingMode.HALF_UP);
    }
    
    // Helper method to generate insights for a single market average
    private Map<String, Object> generateInsightsForMarketAverage(MarketAverage marketAverage) {
        Map<String, Object> insights = new HashMap<>();
        
        // Price analysis
        BigDecimal priceRange = marketAverage.getMaxPrice().subtract(marketAverage.getMinPrice());
        insights.put("priceVolatility", categorizeVolatility(priceRange, marketAverage.getAveragePrice()));
        
        // Market liquidity
        double soldPercentage = marketAverage.getTotalListings() > 0 ? 
                (double) marketAverage.getSoldListings() / marketAverage.getTotalListings() * 100 : 0.0;
        insights.put("marketLiquidity", categorizeLiquidity(soldPercentage));
        insights.put("soldPercentage", soldPercentage);
        
        // Time on market analysis
        insights.put("timeOnMarketAnalysis", categorizeTimeOnMarket(marketAverage.getAverageDaysOnMarket()));
        
        // Data freshness
        long daysSinceUpdate = ChronoUnit.DAYS.between(marketAverage.getLastCalculated(), LocalDateTime.now());
        insights.put("dataFreshness", daysSinceUpdate <= 7 ? "Fresh" : daysSinceUpdate <= 30 ? "Recent" : "Outdated");
        
        return insights;
    }
    
    // Helper method to generate insights for all years of a model
    private Map<String, Object> generateInsightsForModelAllYears(List<MarketAverage> allYears) {
        Map<String, Object> insights = new HashMap<>();
        
        // Sort by year
        allYears.sort(Comparator.comparing(MarketAverage::getYear));
        
        // Price trend analysis
        if (allYears.size() >= 2) {
            MarketAverage oldest = allYears.get(0);
            MarketAverage newest = allYears.get(allYears.size() - 1);
            
            BigDecimal priceDifference = newest.getAveragePrice().subtract(oldest.getAveragePrice());
            String priceTrend = priceDifference.compareTo(BigDecimal.ZERO) > 0 ? "Increasing" :
                                priceDifference.compareTo(BigDecimal.ZERO) < 0 ? "Decreasing" : "Stable";
            
            insights.put("overallPriceTrend", priceTrend);
            insights.put("priceTrendAmount", priceDifference);
        }
        
        // Average market performance
        BigDecimal averagePrice = allYears.stream()
                .map(MarketAverage::getAveragePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(allYears.size()), 2, RoundingMode.HALF_UP);
        
        insights.put("averagePriceAllYears", averagePrice);
        insights.put("yearsCovered", allYears.size());
        insights.put("yearRange", Map.of(
                "from", allYears.get(0).getYear(),
                "to", allYears.get(allYears.size() - 1).getYear()
        ));
        
        return insights;
    }
    
    // Helper methods for categorization
    private String categorizeVolatility(BigDecimal range, BigDecimal average) {
        if (average.equals(BigDecimal.ZERO)) return "Unknown";
        
        BigDecimal volatilityRatio = range.divide(average, 4, RoundingMode.HALF_UP);
        if (volatilityRatio.compareTo(new BigDecimal("0.5")) > 0) return "High";
        if (volatilityRatio.compareTo(new BigDecimal("0.2")) > 0) return "Medium";
        return "Low";
    }
    
    private String categorizeLiquidity(double soldPercentage) {
        if (soldPercentage >= 80) return "High";
        if (soldPercentage >= 60) return "Medium";
        if (soldPercentage >= 40) return "Low";
        return "Very Low";
    }
    
    private String categorizeTimeOnMarket(Double averageDays) {
        if (averageDays == null) return "Unknown";
        
        if (averageDays <= 30) return "Fast-moving";
        if (averageDays <= 60) return "Normal";
        if (averageDays <= 120) return "Slow-moving";
        return "Very slow-moving";
    }
    
    // Convert MarketAverage entity to MarketAverageResponse DTO
    private MarketAverageResponse convertToResponse(MarketAverage marketAverage) {
        MarketAverageResponse response = new MarketAverageResponse();
        response.setId(marketAverage.getId());
        response.setMake(marketAverage.getMake());
        response.setModel(marketAverage.getModel());
        response.setYear(marketAverage.getYear());
        response.setAveragePrice(marketAverage.getAveragePrice());
        response.setMedianPrice(marketAverage.getMedianPrice());
        response.setMinPrice(marketAverage.getMinPrice());
        response.setMaxPrice(marketAverage.getMaxPrice());
        response.setAverageMileage(marketAverage.getAverageMileage());
        response.setMedianMileage(marketAverage.getMedianMileage());
        response.setAverageDaysOnMarket(marketAverage.getAverageDaysOnMarket());
        response.setTotalListings(marketAverage.getTotalListings());
        response.setSoldListings(marketAverage.getSoldListings());
        response.setLastCalculated(marketAverage.getLastCalculated());
        response.setCreatedAt(marketAverage.getCreatedAt());
        response.setUpdatedAt(marketAverage.getUpdatedAt());
        
        // Calculate additional fields
        response.setPriceRange(marketAverage.getMaxPrice().subtract(marketAverage.getMinPrice()));
        
        double soldPercentage = marketAverage.getTotalListings() > 0 ? 
                (double) marketAverage.getSoldListings() / marketAverage.getTotalListings() * 100 : 0.0;
        response.setSoldPercentage(soldPercentage);
        
        response.setLiquidityLevel(categorizeLiquidity(soldPercentage));
        
        // Categorize price level
        if (marketAverage.getAveragePrice().compareTo(new BigDecimal("50000")) > 0) {
            response.setPriceCategory("Luxury");
        } else if (marketAverage.getAveragePrice().compareTo(new BigDecimal("25000")) > 0) {
            response.setPriceCategory("Premium");
        } else if (marketAverage.getAveragePrice().compareTo(new BigDecimal("15000")) > 0) {
            response.setPriceCategory("Mid-range");
        } else {
            response.setPriceCategory("Budget");
        }
        
        return response;
    }
}