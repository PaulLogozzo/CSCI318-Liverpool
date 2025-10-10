# Car Listings Service API

This microservice provides functionality for car listing management, including posting advertisements, searching for vehicles, saving searches, and managing favorite listings.

## Getting Started

1. Ensure Java 17+, docker and kafka 3.9.1 is installed
2. Run docker
3. Run `docker run -p 9092:9092 apache/kafka:3.9.1`
4. Run `mvn spring-boot:run`
5. Access the application at `http://localhost:8081`
6. Use the H2 console at `http://localhost:8081/h2-console` for database inspection


## Features Implemented

### 1. Car Listing Management
- **Post Advertisements**: Sellers can create detailed car listings with make, model, year, mileage, condition, and asking price
- **Edit Posts**: Update existing car listings
- **Deactivate/Activate**: Control listing visibility
- **Mark as Sold**: Update listing status when car is sold

### 2. Vehicle Search
- **Keyword Search**: Search across make, model, and description
- **Advanced Filters**: Filter by make, model, year range, price range, mileage, condition, fuel type, transmission, location
- **Sorting**: Sort by creation date, price, year, mileage
- **Pagination**: Navigate through large result sets

### 3. Saved Searches
- **Save Search Criteria**: Buyers can save frequently used search parameters
- **Manage Saved Searches**: Edit, delete, and reactivate saved searches
- **Email Notifications**: Optional notifications for new matching listings (structure ready)

### 4. Favorites Management
- **Add to Favorites**: Save interesting car listings
- **Remove from Favorites**: Manage favorite list
- **View Favorites**: Browse saved favorite listings

### 5. Analytics & Insights
- **Popular Cars**: Track most viewed cars by week, month, or custom date range
- **Trending Analysis**: Identify cars gaining popularity
- **View Statistics**: Detailed view analytics for individual cars
- **Platform Statistics**: Overall platform metrics and user engagement
- **Search Analytics**: Popular search terms and user behavior

### 6. Inspection Management
- **Schedule Inspections**: Buyers can request vehicle inspections
- **Availability Management**: Sellers can set and manage availability windows
- **Status Updates**: Track inspection requests through their lifecycle
- **Messaging System**: Built-in communication between buyers and sellers
- **Conflict Detection**: Prevents scheduling conflicts for sellers

### 7. Market Analysis
- **Price Intelligence**: Average prices by make, model, and year
- **Market Trends**: Top performing models and high liquidity markets
- **Price Prediction**: AI-powered price suggestions based on market data
- **Depreciation Analysis**: Track value changes over time
- **Comparison Tools**: Side-by-side model comparisons

### 8. Transaction Management
- **Purchase Processing**: Handle car purchase transactions
- **Transaction History**: Complete purchase and sales records
- **Status Tracking**: Monitor transaction progress and completion
- **Analytics Dashboard**: Transaction statistics and insights
- **Admin Controls**: Administrative oversight and reporting

## API Endpoints

### Car Listings (`/api/cars`)
- `POST /api/cars` - Create a new car listing
- `PUT /api/cars/{carId}` - Update an existing car listing
- `DELETE /api/cars/{carId}` - Deactivate a car listing
- `PUT /api/cars/{carId}/sold` - Mark car as sold
- `GET /api/cars/{carId}` - Get car listing details
- `GET /api/cars` - Get all active car listings (paginated)
- `GET /api/cars/search` - Search cars with advanced filters
- `GET /api/cars/seller/{sellerId}` - Get listings by seller
- `GET /api/cars/makes` - Get available car makes
- `GET /api/cars/models?make={make}` - Get models for a specific make

### Analytics (`/api/analytics`)
- `GET /api/analytics/popular/week` - Get most popular cars this week
- `GET /api/analytics/popular/month` - Get most popular cars this month
- `GET /api/analytics/trending` - Get trending cars (gaining popularity)
- `GET /api/analytics/homepage/popular` - Get popular cars for homepage
- `GET /api/analytics/popular/custom` - Get popular cars in custom date range
- `GET /api/analytics/views/{carId}` - Get view statistics for a specific car
- `POST /api/analytics/views/{carId}` - Record a car view (for tracking)
- `GET /api/analytics/stats/platform` - Get platform statistics
- `GET /api/analytics/favorites/top` - Get most favorited cars
- `GET /api/analytics/search/popular-terms` - Get popular search terms
- `GET /api/analytics/engagement` - Get user engagement metrics

### Inspections (`/api/inspections`)
- `POST /api/inspections` - Create a new inspection request
- `GET /api/inspections/{id}` - Get inspection details
- `GET /api/inspections/buyer` - Get buyer's inspections
- `GET /api/inspections/seller` - Get seller's inspections
- `GET /api/inspections/car/{carId}` - Get inspections for a specific car
- `PUT /api/inspections/{id}/status` - Update inspection status
- `PUT /api/inspections/{id}/reschedule` - Reschedule inspection
- `GET /api/inspections/availability/{sellerId}` - Get seller availability
- `POST /api/inspections/messages` - Send inspection message
- `GET /api/inspections/{inspectionId}/messages` - Get inspection messages
- `PUT /api/inspections/{inspectionId}/messages/read` - Mark messages as read
- `GET /api/inspections/messages/unread-count` - Get unread message count

### Market Analysis (`/api/market`)
- `GET /api/market/average` - Get market average for specific make/model/year
- `GET /api/market/averages/make/{make}` - Get market averages by make
- `GET /api/market/averages/make/{make}/model/{model}` - Get averages by make and model
- `GET /api/market/makes` - Get available makes in market data
- `GET /api/market/models/{make}` - Get available models for a make
- `GET /api/market/years/{make}/{model}` - Get available years
- `GET /api/market/price-distribution` - Get price distribution data
- `GET /api/market/trends/top-performers` - Get top performing models
- `GET /api/market/trends/high-liquidity` - Get high liquidity markets
- `GET /api/market/compare` - Compare multiple models
- `GET /api/market/insights` - Get market insights for a model
- `GET /api/market/depreciation/{make}/{model}` - Get depreciation analysis
- `GET /api/market/summary` - Get market summary statistics
- `POST /api/market/refresh` - Refresh market averages (admin)
- `GET /api/market/price-prediction` - Get AI price prediction

### Transactions (`/api/transactions`)
- `POST /api/transactions` - Create a new transaction
- `GET /api/transactions/{id}` - Get transaction details
- `GET /api/transactions/purchases` - Get user's purchase history
- `GET /api/transactions/sales` - Get user's sales history
- `GET /api/transactions/history` - Get complete transaction history
- `GET /api/transactions/car/{carId}` - Get transactions for a car
- `PUT /api/transactions/{id}/status` - Update transaction status
- `PUT /api/transactions/{id}/cancel` - Cancel transaction
- `PUT /api/transactions/{id}/complete` - Complete transaction
- `GET /api/transactions/stats` - Get user's transaction statistics
- `GET /api/transactions/summary` - Get transaction summary for period
- `GET /api/transactions/admin/all` - Get all transactions (admin only)
- `GET /api/transactions/admin/analytics` - Get transaction analytics (admin)

### Saved Searches (`/api/saved-searches`)
- `POST /api/saved-searches` - Create a saved search
- `PUT /api/saved-searches/{searchId}` - Update a saved search
- `DELETE /api/saved-searches/{searchId}` - Delete a saved search
- `GET /api/saved-searches/{searchId}` - Get saved search details
- `GET /api/saved-searches` - Get all user's saved searches

### Favorites (`/api/favourites`)
- `POST /api/favourites/cars/{carId}` - Add car to favorites
- `DELETE /api/favourites/cars/{carId}` - Remove car from favorites
- `GET /api/favourites` - Get user's favorite cars (paginated)
- `GET /api/favourites/cars/{carId}/status` - Check if car is favorited
- `GET /api/favourites/count` - Get total favorites count

## Request/Response Examples

### Create Car Listing
```json
POST /api/cars
Headers: User-Id: 1
{
  "make": "Toyota",
  "model": "Camry",
  "year": 2020,
  "mileage": 25000,
  "condition": "EXCELLENT",
  "askingPrice": 22000.00,
  "description": "Well-maintained sedan with full service history",
  "color": "Silver",
  "fuelType": "Gasoline",
  "transmission": "Automatic",
  "numberOfDoors": 4,
  "engineSize": "2.5L",
  "location": "Sydney, NSW",
  "contactPhone": "+61400123456",
  "contactEmail": "seller@example.com"
}
```

### Search Cars
```
GET /api/cars/search?keyword=toyota&minYear=2018&maxPrice=30000&sortBy=price&sortDirection=ASC&page=0&size=10
```

### Create Saved Search
```json
POST /api/saved-searches
Headers: User-Id: 2
{
  "name": "Affordable Toyota Cars",
  "make": "Toyota",
  "maxPrice": 25000,
  "minYear": 2017,
  "emailNotifications": true
}
```

### Schedule Inspection
```json
POST /api/inspections
Headers: User-Id: 3
{
  "carId": 123,
  "scheduledDateTime": "2024-03-15T14:00:00",
  "durationMinutes": 60,
  "location": "Seller's Address",
  "notes": "Interested in comprehensive inspection"
}
```

### Get Market Analysis
```
GET /api/market/average?make=Toyota&model=Camry&year=2020
```

### Create Transaction
```json
POST /api/transactions
Headers: User-Id: 4
{
  "carId": 123,
  "salePrice": 22000.00,
  "paymentMethod": "Bank Transfer",
  "notes": "Cash purchase, immediate pickup"
}
```

### Record Car View (Analytics)
```
POST /api/analytics/views/123
Headers: User-Id: 5, User-Agent: Mozilla/5.0...
```

## Database Schema

The service uses the following entities:

### Core Entities
- **User**: Represents buyers and sellers with role-based access
- **Car**: Car listing details with comprehensive specifications
- **SavedSearch**: User's saved search criteria with notification settings
- **Favourite**: User's favorite car listings for quick access

### Analytics & Tracking
- **CarView**: Tracks individual car views for popularity analytics
- **AuditLog**: Comprehensive activity logging for security and analytics

### Inspection System
- **Inspection**: Vehicle inspection scheduling and management
- **InspectionMessage**: Built-in messaging system for inspection coordination
- **SellerAvailability**: Seller availability windows for inspection scheduling

### Market Intelligence
- **MarketAverage**: Aggregated market data for price intelligence
- **Notification**: System notifications for users

### Transaction Management
- **Transaction**: Complete transaction records for car purchases

### Key Features
- **Audit Trail**: Full activity logging across all user actions
- **Notification System**: Real-time notifications for important events
- **Market Intelligence**: Data-driven price insights and trends
- **Inspection Workflow**: Complete inspection lifecycle management

## Configuration

### Database Configuration
- Uses H2 in-memory database for development
- JPA/Hibernate for ORM
- Automatic schema generation

### Server Configuration
- Runs on port 8081
- H2 console available at `/h2-console`

## Authentication Notes

The API expects a `User-Id` header for user identification. In a production environment, this would be replaced with proper JWT token authentication.

### Header Requirements
- **User-Id**: Required for most endpoints to identify the requesting user
- **User-Agent**: Used by analytics endpoints to track user behavior
- **X-Forwarded-For / X-Real-IP**: Used by analytics for IP-based tracking

### Role-Based Access
- **Buyer**: Can search cars, save searches, manage favorites, schedule inspections, make purchases
- **Seller**: Can manage car listings, handle inspections, view sales analytics
- **Both**: Users can act as both buyers and sellers
- **Admin**: Additional access to system-wide analytics and transaction oversight

## Performance Features

### Caching & Optimization
- **Market Data Caching**: Market averages are calculated and cached for performance
- **Pagination**: All list endpoints support pagination to handle large datasets
- **Indexed Queries**: Database indexes on frequently searched fields
- **Audit Trail**: Comprehensive logging without impacting performance

### Scalability Considerations
- **Stateless Design**: All endpoints are stateless for horizontal scaling
- **Database Optimization**: Efficient queries with proper indexing
- **Analytics Processing**: Background processing for analytics calculations
- **Memory Management**: Efficient use of JPA/Hibernate for ORM

## Development & Testing

### Quick Setup
1. Clone the repository
2. Ensure Java 17+, Docker, and Kafka 3.9.1 are installed
3. Run Docker
4. Start Kafka: `docker run -p 9092:9092 apache/kafka:3.9.1`
5. Start application: `mvn spring-boot:run`
6. Access at `http://localhost:8081`
7. H2 Console: `http://localhost:8081/h2-console`

### API Testing
- Use Postman, curl, or any HTTP client
- Remember to include the `User-Id` header in requests
- Check H2 console to verify data persistence
- Monitor application logs for debugging



