package com.riderecs.car_listings_service.controller;

import com.riderecs.car_listings_service.dto.MarketAverageResponse;
import com.riderecs.car_listings_service.service.MarketAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market")
@CrossOrigin(origins = "*")
public class MarketController {
    
    @Autowired
    private MarketAnalysisService marketAnalysisService;
    
    // Get market average for specific make, model, and year
    @GetMapping("/average")
    public ResponseEntity<MarketAverageResponse> getMarketAverage(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam Integer year) {
        try {
            MarketAverageResponse marketAverage = marketAnalysisService.getMarketAverage(make, model, year);
            return ResponseEntity.ok(marketAverage);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Get all market averages for a specific make
    @GetMapping("/averages/make/{make}")
    public ResponseEntity<List<MarketAverageResponse>> getMarketAveragesByMake(
            @PathVariable String make) {
        List<MarketAverageResponse> averages = marketAnalysisService.getMarketAveragesByMake(make);
        return ResponseEntity.ok(averages);
    }
    
    // Get all market averages for a specific make and model
    @GetMapping("/averages/make/{make}/model/{model}")
    public ResponseEntity<List<MarketAverageResponse>> getMarketAveragesByMakeAndModel(
            @PathVariable String make,
            @PathVariable String model) {
        List<MarketAverageResponse> averages = marketAnalysisService.getMarketAveragesByMakeAndModel(make, model);
        return ResponseEntity.ok(averages);
    }
    
    // Get available makes in the market data
    @GetMapping("/makes")
    public ResponseEntity<List<String>> getAvailableMakes() {
        List<String> makes = marketAnalysisService.getAvailableMakes();
        return ResponseEntity.ok(makes);
    }
    
    // Get available models for a specific make
    @GetMapping("/models/{make}")
    public ResponseEntity<List<String>> getAvailableModels(@PathVariable String make) {
        List<String> models = marketAnalysisService.getAvailableModels(make);
        return ResponseEntity.ok(models);
    }
    
    // Get available years for a specific make and model
    @GetMapping("/years/{make}/{model}")
    public ResponseEntity<List<Integer>> getAvailableYears(
            @PathVariable String make,
            @PathVariable String model) {
        List<Integer> years = marketAnalysisService.getAvailableYears(make, model);
        return ResponseEntity.ok(years);
    }
    
    // Get price distribution for make, model, year
    @GetMapping("/price-distribution")
    public ResponseEntity<Map<String, Object>> getPriceDistribution(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam Integer year) {
        Map<String, Object> distribution = marketAnalysisService.getPriceDistribution(make, model, year);
        return ResponseEntity.ok(distribution);
    }
    
    // Get market trends (top performing models)
    @GetMapping("/trends/top-performers")
    public ResponseEntity<List<MarketAverageResponse>> getTopPerformingModels(
            @RequestParam(defaultValue = "10") int limit) {
        List<MarketAverageResponse> topPerformers = marketAnalysisService.getTopPerformingModels(limit);
        return ResponseEntity.ok(topPerformers);
    }
    
    // Get high liquidity markets (models with many sold listings)
    @GetMapping("/trends/high-liquidity")
    public ResponseEntity<List<MarketAverageResponse>> getHighLiquidityMarkets(
            @RequestParam(defaultValue = "5") int minSoldListings,
            @RequestParam(defaultValue = "10") int limit) {
        List<MarketAverageResponse> highLiquidity = marketAnalysisService.getHighLiquidityMarkets(minSoldListings, limit);
        return ResponseEntity.ok(highLiquidity);
    }
    
    // Compare multiple models
    @GetMapping("/compare")
    public ResponseEntity<List<MarketAverageResponse>> compareModels(
            @RequestParam List<String> makes,
            @RequestParam List<String> models,
            @RequestParam List<Integer> years) {
        try {
            List<MarketAverageResponse> comparison = marketAnalysisService.compareModels(makes, models, years);
            return ResponseEntity.ok(comparison);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get market insights for a specific model
    @GetMapping("/insights")
    public ResponseEntity<Map<String, Object>> getMarketInsights(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam(required = false) Integer year) {
        Map<String, Object> insights = marketAnalysisService.getMarketInsights(make, model, year);
        return ResponseEntity.ok(insights);
    }
    
    // Get depreciation analysis
    @GetMapping("/depreciation/{make}/{model}")
    public ResponseEntity<Map<String, Object>> getDepreciationAnalysis(
            @PathVariable String make,
            @PathVariable String model,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer endYear) {
        Map<String, Object> depreciation = marketAnalysisService.getDepreciationAnalysis(make, model, startYear, endYear);
        return ResponseEntity.ok(depreciation);
    }
    
    // Get market summary statistics
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getMarketSummary() {
        Map<String, Object> summary = marketAnalysisService.getMarketSummary();
        return ResponseEntity.ok(summary);
    }
    
    // Refresh market averages (admin endpoint)
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshMarketAverages(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer year) {
        try {
            Map<String, Object> result = marketAnalysisService.refreshMarketAverages(userId, make, model, year);
            return ResponseEntity.ok(result);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }
    
    // Get price prediction for a car
    @GetMapping("/price-prediction")
    public ResponseEntity<Map<String, Object>> getPricePrediction(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam Integer year,
            @RequestParam Integer mileage,
            @RequestParam(required = false) String condition) {
        Map<String, Object> prediction = marketAnalysisService.getPricePrediction(make, model, year, mileage, condition);
        return ResponseEntity.ok(prediction);
    }
}