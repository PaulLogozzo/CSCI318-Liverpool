package com.riderecs.car_listings_service.dto;

import com.riderecs.car_listings_service.entity.CarCondition;
import java.math.BigDecimal;

public class CarSearchCriteria {
    
    private String keyword;
    private String make;
    private String model;
    private Integer minYear;
    private Integer maxYear;
    private Integer maxMileage;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private CarCondition condition;
    private String fuelType;
    private String transmission;
    private String location;
    private String sortBy = "createdAt"; // Default sort
    private String sortDirection = "DESC"; // Default sort direction
    private Integer page = 0; // Default page
    private Integer size = 20; // Default page size
    
    // Constructors
    public CarSearchCriteria() {}
    
    // Getters and Setters
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getMinYear() { return minYear; }
    public void setMinYear(Integer minYear) { this.minYear = minYear; }
    
    public Integer getMaxYear() { return maxYear; }
    public void setMaxYear(Integer maxYear) { this.maxYear = maxYear; }
    
    public Integer getMaxMileage() { return maxMileage; }
    public void setMaxMileage(Integer maxMileage) { this.maxMileage = maxMileage; }
    
    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }
    
    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }
    
    public CarCondition getCondition() { return condition; }
    public void setCondition(CarCondition condition) { this.condition = condition; }
    
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    
    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }
    
    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }
    
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    
    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }
}