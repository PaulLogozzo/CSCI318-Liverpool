package com.riderecs.car_listings_service.repository;

import com.riderecs.car_listings_service.entity.MarketAverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MarketAverageRepository extends JpaRepository<MarketAverage, Long> {
    
    // Find market average by make, model, and year
    Optional<MarketAverage> findByMakeAndModelAndYear(String make, String model, Integer year);
    
    // Find all market averages for a specific make
    List<MarketAverage> findByMake(String make);
    
    // Find all market averages for a specific make and model
    List<MarketAverage> findByMakeAndModel(String make, String model);
    
    // Find all market averages for a specific year range
    List<MarketAverage> findByYearBetween(Integer startYear, Integer endYear);
    
    // Find market averages that need recalculation (older than specified time)
    @Query("SELECT ma FROM MarketAverage ma WHERE ma.lastCalculated < :cutoffDate")
    List<MarketAverage> findByLastCalculatedBefore(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Get all available makes
    @Query("SELECT DISTINCT ma.make FROM MarketAverage ma ORDER BY ma.make")
    List<String> findDistinctMakes();
    
    // Get all available models for a make
    @Query("SELECT DISTINCT ma.model FROM MarketAverage ma WHERE ma.make = :make ORDER BY ma.model")
    List<String> findDistinctModelsByMake(@Param("make") String make);
    
    // Get all available years for a make and model
    @Query("SELECT DISTINCT ma.year FROM MarketAverage ma WHERE ma.make = :make AND ma.model = :model ORDER BY ma.year DESC")
    List<Integer> findDistinctYearsByMakeAndModel(@Param("make") String make, @Param("model") String model);
    
    // Find top performing models by average price
    @Query("SELECT ma FROM MarketAverage ma ORDER BY ma.averagePrice DESC")
    List<MarketAverage> findTopPerformingModels();
    
    // Find market averages with high liquidity (many sold listings)
    @Query("SELECT ma FROM MarketAverage ma WHERE ma.soldListings >= :minSoldListings ORDER BY ma.soldListings DESC")
    List<MarketAverage> findHighLiquidityMarkets(@Param("minSoldListings") Integer minSoldListings);
}