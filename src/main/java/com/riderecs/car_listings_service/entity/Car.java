package com.riderecs.car_listings_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "cars")
public class Car {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String make;
    
    @Column(nullable = false)
    private String model;
    
    @Column(name = "year_made", nullable = false)
    private Integer year;
    
    @Column(nullable = false)
    private Integer mileage;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarCondition condition;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal askingPrice;
    
    @Column(length = 2000)
    private String description;
    
    @Column
    private String color;
    
    @Column
    private String fuelType;
    
    @Column
    private String transmission;
    
    @Column
    private Integer numberOfDoors;
    
    @Column
    private String engineSize;
    
    @Column
    private String location;
    
    @Column
    private String contactPhone;
    
    @Column
    private String contactEmail;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status = CarStatus.ACTIVE;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
    
    @OneToMany(mappedBy = "car", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Favourite> favourites;
    
    // Constructors
    public Car() {}
    
    public Car(String make, String model, Integer year, Integer mileage, 
               CarCondition condition, BigDecimal askingPrice, User seller) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.mileage = mileage;
        this.condition = condition;
        this.askingPrice = askingPrice;
        this.seller = seller;
        this.status = CarStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
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
    
    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }
    
    public List<Favourite> getFavourites() { return favourites; }
    public void setFavourites(List<Favourite> favourites) { this.favourites = favourites; }
}