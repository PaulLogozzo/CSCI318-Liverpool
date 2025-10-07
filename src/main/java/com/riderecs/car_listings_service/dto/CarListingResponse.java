package com.riderecs.car_listings_service.dto;

import com.riderecs.car_listings_service.entity.CarCondition;
import com.riderecs.car_listings_service.entity.CarStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CarListingResponse {
    
    private Long id;
    private String make;
    private String model;
    private Integer year;
    private Integer mileage;
    private CarCondition condition;
    private BigDecimal askingPrice;
    private String description;
    private String color;
    private String fuelType;
    private String transmission;
    private Integer numberOfDoors;
    private String engineSize;
    private String location;
    private String contactPhone;
    private String contactEmail;
    private CarStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SellerInfo seller;
    
    // Constructors
    public CarListingResponse() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public Integer getMileage() { return mileage; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
    
    public CarCondition getCondition() { return condition; }
    public void setCondition(CarCondition condition) { this.condition = condition; }
    
    public BigDecimal getAskingPrice() { return askingPrice; }
    public void setAskingPrice(BigDecimal askingPrice) { this.askingPrice = askingPrice; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    
    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    
    public Integer getNumberOfDoors() { return numberOfDoors; }
    public void setNumberOfDoors(Integer numberOfDoors) { this.numberOfDoors = numberOfDoors; }
    
    public String getEngineSize() { return engineSize; }
    public void setEngineSize(String engineSize) { this.engineSize = engineSize; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }
    
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    
    public CarStatus getStatus() { return status; }
    public void setStatus(CarStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public SellerInfo getSeller() { return seller; }
    public void setSeller(SellerInfo seller) { this.seller = seller; }
    
    // Inner class for seller information
    public static class SellerInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String username;
        
        // Constructors
        public SellerInfo() {}
        
        public SellerInfo(Long id, String firstName, String lastName, String username) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
    }
}