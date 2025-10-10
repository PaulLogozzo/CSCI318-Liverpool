package com.riderecs.car_listings_service.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "car_views")
public class CarView {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewer_id")
    private User viewer; // null for anonymous users
    
    @Column
    private String ipAddress;
    
    @Column
    private String userAgent;
    
    @Column
    private String sessionId; // For tracking anonymous users
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime viewedAt;
    
    // Constructors
    public CarView() {}
    
    public CarView(Car car, User viewer) {
        this.car = car;
        this.viewer = viewer;
        this.viewedAt = LocalDateTime.now();
    }
    
    public CarView(Car car, String ipAddress, String sessionId) {
        this.car = car;
        this.ipAddress = ipAddress;
        this.sessionId = sessionId;
        this.viewedAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        viewedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
    
    public User getViewer() { return viewer; }
    public void setViewer(User viewer) { this.viewer = viewer; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public LocalDateTime getViewedAt() { return viewedAt; }
    public void setViewedAt(LocalDateTime viewedAt) { this.viewedAt = viewedAt; }
}