package com.riderecs.car_listings_service.dto;

import java.time.LocalDateTime;

public class FavouriteResponse {
    
    private Long id;
    private LocalDateTime createdAt;
    private CarListingResponse car;
    
    // Constructors
    public FavouriteResponse() {}
    
    public FavouriteResponse(Long id, LocalDateTime createdAt, CarListingResponse car) {
        this.id = id;
        this.createdAt = createdAt;
        this.car = car;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public CarListingResponse getCar() { return car; }
    public void setCar(CarListingResponse car) { this.car = car; }
}