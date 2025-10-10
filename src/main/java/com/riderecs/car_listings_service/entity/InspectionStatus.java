package com.riderecs.car_listings_service.entity;

public enum InspectionStatus {
    REQUESTED,      // Initial state when buyer requests inspection
    CONFIRMED,      // Seller confirms the inspection time
    RESCHEDULED,    // Inspection has been rescheduled
    COMPLETED,      // Inspection has been completed
    CANCELLED,      // Inspection was cancelled
    NO_SHOW         // One party didn't show up
}