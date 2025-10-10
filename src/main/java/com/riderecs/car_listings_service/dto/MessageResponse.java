package com.riderecs.car_listings_service.dto;

import java.time.LocalDateTime;

public class MessageResponse {
    
    private Long id;
    private Long inspectionId;
    private Long senderId;
    private String senderName;
    private String message;
    private LocalDateTime sentAt;
    private Boolean isRead;
    
    // Constructors
    public MessageResponse() {}
    
    public MessageResponse(Long id, Long inspectionId, Long senderId, String senderName, 
                          String message, LocalDateTime sentAt, Boolean isRead) {
        this.id = id;
        this.inspectionId = inspectionId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.sentAt = sentAt;
        this.isRead = isRead;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getInspectionId() { return inspectionId; }
    public void setInspectionId(Long inspectionId) { this.inspectionId = inspectionId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
}