package com.riderecs.car_listings_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class MessageRequest {
    
    @NotNull(message = "Inspection ID is required")
    private Long inspectionId;
    
    @NotBlank(message = "Message cannot be empty")
    @Size(max = 2000, message = "Message cannot exceed 2000 characters")
    private String message;
    
    // Constructors
    public MessageRequest() {}
    
    public MessageRequest(Long inspectionId, String message) {
        this.inspectionId = inspectionId;
        this.message = message;
    }
    
    // Getters and Setters
    public Long getInspectionId() { return inspectionId; }
    public void setInspectionId(Long inspectionId) { this.inspectionId = inspectionId; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}