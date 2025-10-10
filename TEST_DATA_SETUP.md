# 🚀 Comprehensive Test Data Setup System

## Overview
This system provides **one-click comprehensive test data creation** for the Car Listings Service, populating **ALL database tables** with realistic, interconnected test data.

## 🎯 What's Included

### API Endpoints
- `POST /api/test-setup` - Create comprehensive test data
- `GET /api/test-setup/status` - Check current data status  
- `DELETE /api/test-setup/clear` - Clear all test data

### 📊 Data Creation Summary

| Table | Records Created | Description |
|-------|-----------------|-------------|
| **Users** | 15 | Sellers, buyers, admins with mixed roles |
| **Cars** | 25 | Multiple makes, models, years, conditions |
| **Favourites** | 30 | User-car preference relationships |
| **Saved Searches** | 20 | Various search criteria combinations |
| **Transactions** | 15 | Different statuses and payment methods |
| **Inspections** | 18 | Scheduled appointments with various statuses |
| **Inspection Messages** | 50+ | Realistic conversation history |
| **Car Views** | 50 | Analytics tracking data |
| **Market Averages** | 60 | Price analysis for 12 make/model combinations across 5 years |
| **Audit Logs** | 40 | System activity tracking with 10% flagged for review |
| **Notifications** | 35 | User alerts with mixed read/unread status |

**🎯 Total: 300+ interconnected records**

## 🏗️ Data Structure & Relationships

### Users (15 records)
- **2 Admin users**: `admin`, `superadmin` (both with BOTH role)
- **4 Sellers**: `john_seller`, `jane_seller` (BOTH), `mike_seller`, `sarah_seller` (BOTH)
- **4 Buyers**: `bob_buyer`, `alice_buyer`, `charlie_buyer` (BOTH), `diana_buyer`
- **5 Mixed role users**: All with BOTH role for maximum testing flexibility

### Cars (25 records)
- **Makes**: Toyota, Honda, Ford, BMW, Audi, Mercedes-Benz, Volkswagen, Nissan, Hyundai, Mazda, Chevrolet, Subaru
- **Years**: 2018-2023 (realistic range)
- **Mileage**: 5,000-100,000 km
- **Prices**: $15,000-$100,000 AUD
- **Conditions**: All enum values (NEW, EXCELLENT, VERY_GOOD, GOOD, FAIR, POOR)
- **Statuses**: Mixed (3 SOLD, 2 INACTIVE, 20 ACTIVE)
- **Locations**: All major Australian cities

### Relationships
- **Favourites**: 30 unique buyer-car pairs (buyers can't favorite their own cars)
- **Saved Searches**: 20 searches with varied criteria (make, price range, year, condition)
- **Transactions**: Up to 15 for SOLD/INACTIVE cars with realistic pricing
- **Inspections**: 18 across different cars and buyers with various statuses
- **Messages**: 2-7 messages per inspection from both buyers and sellers

### Analytics Data
- **Car Views**: 50 views with 70% from logged-in users, 30% anonymous
- **Market Averages**: Comprehensive data for 12 popular make/model combinations
- **Audit Logs**: 40 entries covering all action types with realistic IP addresses

## ✨ Key Features

### 🎯 Realistic Data
- Proper foreign key relationships
- No orphaned records
- Realistic pricing and mileage
- Australian phone numbers and locations
- Mixed statuses for comprehensive testing

### 🔄 Dependency Management
- Creates data in proper dependency order
- Clears data in reverse dependency order
- Maintains referential integrity
- Safe for repeated execution

### 🧪 Testing Ready
- Covers all API endpoints
- Includes edge cases (sold cars, inactive listings)
- Admin users for permission testing
- Mixed roles for comprehensive scenarios

## 📋 Usage Instructions

### 1. Setup Test Data (Recommended)
```bash
POST /api/test-setup
```
**Response:**
```json
{
  "status": "SUCCESS",
  "users_created": 15,
  "cars_created": 25,
  "favourites_created": 30,
  "saved_searches_created": 20,
  "transactions_created": 15,
  "inspections_created": 18,
  "inspection_messages_created": 72,
  "car_views_created": 50,
  "market_averages_created": 60,
  "audit_logs_created": 40,
  "notifications_created": 35,
  "total_records": 340,
  "message": "All test data created successfully!"
}
```

### 2. Check Data Status
```bash
GET /api/test-setup/status
```

### 3. Clear All Data (if needed)
```bash
DELETE /api/test-setup/clear
```

## 🎮 Postman Collection Integration

The complete Postman collection includes:
- **🚀 Complete Test Data Setup (RECOMMENDED)** - One-click comprehensive setup
- **📈 Check Data Status** - Real-time database status
- **🧹 Clear All Test Data** - Safe data cleanup
- **🏗️ Create Test Users - SQL (Legacy)** - Old manual method

### Enhanced Test Scripts
- Comprehensive validation
- Detailed console logging
- Success/failure reporting
- Record count verification

## 🔧 Technical Implementation

### Controller: `TestSetupController.java`
- Clean, modular design
- Proper error handling
- Transaction management
- Dependency injection

### Data Generation Features
- **Random but realistic**: Uses seeded randomization for variety
- **Constraint-aware**: Respects database constraints and relationships
- **Performance optimized**: Batch operations for efficiency
- **Idempotent**: Can be run multiple times safely

## 🚀 Benefits

### For Developers
- **No manual SQL setup** required
- **Instant comprehensive testing environment**
- **Realistic edge cases** covered
- **Easy environment reset**

### For Testing
- **Full API coverage** possible immediately
- **Complex relationship testing** ready
- **Analytics and reporting data** available
- **Admin functionality testing** enabled

### For Demonstrations
- **Rich, realistic data** for demos
- **All features showcased** with real data
- **Professional presentation** ready

## 🔄 Maintenance

The system is designed to be:
- **Self-contained**: No external dependencies
- **Version-controlled**: Part of the codebase
- **Maintainable**: Clear, documented code
- **Extensible**: Easy to add new data types

## 📈 Future Enhancements

Potential improvements:
- **Configurable data volumes** (10, 100, 1000 records)
- **Specific test scenarios** (high-volume, edge cases)
- **Performance benchmarking data**
- **Multi-tenant test data**

---

## 🎯 Quick Start

1. **Start your service**: `mvn spring-boot:run`
2. **Open Postman** and import the collection
3. **Run**: `🚀 Complete Test Data Setup (RECOMMENDED)`
4. **Verify**: `📈 Check Data Status`
5. **Start testing**: All 70+ API endpoints are ready!

**🎉 You now have a fully populated database with 300+ records ready for comprehensive testing!**