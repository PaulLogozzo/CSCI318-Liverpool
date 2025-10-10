package com.riderecs.car_listings_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MarketAverageResponse {
    
    private Long id;
    private String make;
    private String model;
    private Integer year;
    private BigDecimal averagePrice;
    private BigDecimal medianPrice;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Double averageMileage;
    private Integer medianMileage;
    private Double averageDaysOnMarket;
    private Integer totalListings;
    private Integer soldListings;
    private LocalDateTime lastCalculated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional calculated fields
    private BigDecimal priceRange;
    private Double soldPercentage;
    private String liquidityLevel;
    private String priceCategory;
    
    // Constructors
    public MarketAverageResponse() {}
    
    public MarketAverageResponse(String make, String model, Integer year) {
        this.make = make;
        this.model = model;
        this.year = year;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public BigDecimal getAveragePrice() { return averagePrice; }
    public void setAveragePrice(BigDecimal averagePrice) { this.averagePrice = averagePrice; }
    
    public BigDecimal getMedianPrice() { return medianPrice; }
    public void setMedianPrice(BigDecimal medianPrice) { this.medianPrice = medianPrice; }
    
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    
    public Double getAverageMileage() { return averageMileage; }
    public void setAverageMileage(Double averageMileage) { this.averageMileage = averageMileage; }
    
    public Integer getMedianMileage() { return medianMileage; }
    public void setMedianMileage(Integer medianMileage) { this.medianMileage = medianMileage; }
    
    public Double getAverageDaysOnMarket() { return averageDaysOnMarket; }
    public void setAverageDaysOnMarket(Double averageDaysOnMarket) { this.averageDaysOnMarket = averageDaysOnMarket; }
    
    public Integer getTotalListings() { return totalListings; }
    public void setTotalListings(Integer totalListings) { this.totalListings = totalListings; }
    
    public Integer getSoldListings() { return soldListings; }
    public void setSoldListings(Integer soldListings) { this.soldListings = soldListings; }
    
    public LocalDateTime getLastCalculated() { return lastCalculated; }
    public void setLastCalculated(LocalDateTime lastCalculated) { this.lastCalculated = lastCalculated; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public BigDecimal getPriceRange() { return priceRange; }
    public void setPriceRange(BigDecimal priceRange) { this.priceRange = priceRange; }
    
    public Double getSoldPercentage() { return soldPercentage; }
    public void setSoldPercentage(Double soldPercentage) { this.soldPercentage = soldPercentage; }
    
    public String getLiquidityLevel() { return liquidityLevel; }
    public void setLiquidityLevel(String liquidityLevel) { this.liquidityLevel = liquidityLevel; }
    
    public String getPriceCategory() { return priceCategory; }
    public void setPriceCategory(String priceCategory) { this.priceCategory = priceCategory; }
}