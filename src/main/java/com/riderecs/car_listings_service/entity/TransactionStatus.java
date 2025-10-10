package com.riderecs.car_listings_service.entity;

public enum TransactionStatus {
    PENDING,        // Transaction initiated but not completed
    COMPLETED,      // Transaction successfully completed
    CANCELLED,      // Transaction cancelled by either party
    FAILED,         // Transaction failed due to payment or other issues
    REFUNDED        // Transaction was refunded
}