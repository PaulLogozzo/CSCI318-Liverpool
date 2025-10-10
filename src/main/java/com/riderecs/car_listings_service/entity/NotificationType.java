package com.riderecs.car_listings_service.entity;

public enum NotificationType {
    PRICE_CHANGE,           // Car price has been updated
    LISTING_STATUS_CHANGE,  // Car status changed (ACTIVE, SOLD, etc.)
    NEW_INSPECTION_REQUEST, // New inspection request received
    INSPECTION_CONFIRMED,   // Inspection has been confirmed
    INSPECTION_CANCELLED,   // Inspection has been cancelled
    INSPECTION_RESCHEDULED, // Inspection has been rescheduled
    NEW_MESSAGE,            // New message received
    SAVED_SEARCH_MATCH,     // New listing matches saved search
    TRANSACTION_UPDATE,     // Transaction status update
    LISTING_EXPIRING,       // Listing is about to expire
    SYSTEM_NOTIFICATION     // General system notifications
}