package com.riderecs.car_listings_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class InspectionRequest {
    
    @NotNull(message = "Car ID is required")
    private Long carId;
    
    @NotNull(message = "Scheduled date and time is required")
    @Future(message = "Inspection must be scheduled for a future date")
    private LocalDateTime scheduledDateTime;
    
    @Positive(message = "Duration must be positive")
    private Integer durationMinutes = 60;
    
    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
    
    @Size(max = 255, message = "Location cannot exceed 255 characters")
    private String location;
    
    // Constructors
    public InspectionRequest() {}
    
    public InspectionRequest(Long carId, LocalDateTime scheduledDateTime) {
        this.carId = carId;
        this.scheduledDateTime = scheduledDateTime;
    }
    
    // Getters and Setters
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    
    public LocalDateTime getScheduledDateTime() { return scheduledDateTime; }
    public void setScheduledDateTime(LocalDateTime scheduledDateTime) { this.scheduledDateTime = scheduledDateTime; }
    
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}