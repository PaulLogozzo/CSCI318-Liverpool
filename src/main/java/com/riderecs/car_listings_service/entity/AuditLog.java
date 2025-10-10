package com.riderecs.car_listings_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;
    
    @Column(nullable = false)
    private String entityType; // CAR, USER, TRANSACTION, INSPECTION, etc.
    
    @Column
    private Long entityId;
    
    @Column(length = 2000)
    private String details; // JSON or description of what changed
    
    @Column(length = 4000)
    private String oldValues; // Previous values (JSON format)
    
    @Column(length = 4000)
    private String newValues; // New values (JSON format)
    
    @Column
    private String ipAddress;
    
    @Column
    private String userAgent;
    
    @Column(nullable = false)
    private Boolean flaggedForReview = false;
    
    @Column
    private String flagReason;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
    
    // Constructors
    public AuditLog() {}
    
    public AuditLog(User user, AuditAction action, String entityType, Long entityId) {
        this.user = user;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.timestamp = LocalDateTime.now();
    }
    
    public AuditLog(User user, AuditAction action, String entityType, Long entityId, 
                   String details, String oldValues, String newValues) {
        this(user, action, entityType, entityId);
        this.details = details;
        this.oldValues = oldValues;
        this.newValues = newValues;
    }
    
    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
    
    // Helper method to flag for review
    public void flagForReview(String reason) {
        this.flaggedForReview = true;
        this.flagReason = reason;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public AuditAction getAction() { return action; }
    public void setAction(AuditAction action) { this.action = action; }
    
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public String getOldValues() { return oldValues; }
    public void setOldValues(String oldValues) { this.oldValues = oldValues; }
    
    public String getNewValues() { return newValues; }
    public void setNewValues(String newValues) { this.newValues = newValues; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public Boolean getFlaggedForReview() { return flaggedForReview; }
    public void setFlaggedForReview(Boolean flaggedForReview) { this.flaggedForReview = flaggedForReview; }
    
    public String getFlagReason() { return flagReason; }
    public void setFlagReason(String flagReason) { this.flagReason = flagReason; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}