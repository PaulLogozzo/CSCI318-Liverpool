package com.riderecs.car_listings_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "market_averages", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"make", "model", "car_year"}))
public class MarketAverage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String make;
    
    @Column(nullable = false)
    private String model;
    
    @Column(name = "car_year", nullable = false)
    private Integer year;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal averagePrice;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal medianPrice;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal minPrice;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal maxPrice;
    
    @Column(nullable = false)
    private Double averageMileage;
    
    @Column(nullable = false)
    private Integer medianMileage;
    
    @Column(nullable = false)
    private Double averageDaysOnMarket;
    
    @Column(nullable = false)
    private Integer totalListings;
    
    @Column(nullable = false)
    private Integer soldListings;
    
    @Column(nullable = false)
    private LocalDateTime lastCalculated;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public MarketAverage() {}
    
    public MarketAverage(String make, String model, Integer year) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.lastCalculated = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (lastCalculated == null) {
            lastCalculated = LocalDateTime.now();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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
}