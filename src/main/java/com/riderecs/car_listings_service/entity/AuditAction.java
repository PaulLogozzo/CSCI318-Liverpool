package com.riderecs.car_listings_service.entity;

public enum AuditAction {
    CREATE,         // Entity created
    UPDATE,         // Entity updated
    DELETE,         // Entity deleted
    VIEW,           // Entity viewed (for sensitive operations)
    LOGIN,          // User login
    LOGOUT,         // User logout
    SEND_MESSAGE,   // Message sent
    PRICE_CHANGE,   // Price changed
    STATUS_CHANGE,  // Status changed
    PURCHASE,       // Transaction/purchase made
    INSPECTION,     // Inspection related action
    SEARCH,         // Search performed (for analytics)
    EXPORT,         // Data exported
    IMPORT,         // Data imported
    ADMIN_ACTION    // Administrative action
}