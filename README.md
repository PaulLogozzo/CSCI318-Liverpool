# Car Listings Service API

This microservice provides functionality for car listing management, including posting advertisements, searching for vehicles, saving searches, and managing favorite listings.

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

## API Endpoints

### Car Listings
- `POST /api/cars` - Create a new car listing
- `PUT /api/cars/{carId}` - Update an existing car listing
- `DELETE /api/cars/{carId}` - Deactivate a car listing
- `PUT /api/cars/{carId}/sold` - Mark car as sold
- `GET /api/cars/{carId}` - Get car listing details
- `GET /api/cars` - Get all active car listings (paginated)
- `GET /api/cars/search` - Search cars with filters
- `GET /api/cars/seller/{sellerId}` - Get listings by seller
- `GET /api/cars/makes` - Get available car makes
- `GET /api/cars/models?make={make}` - Get models for a specific make

### Saved Searches
- `POST /api/saved-searches` - Create a saved search
- `PUT /api/saved-searches/{searchId}` - Update a saved search
- `DELETE /api/saved-searches/{searchId}` - Delete a saved search
- `GET /api/saved-searches/{searchId}` - Get saved search details
- `GET /api/saved-searches` - Get all user's saved searches

### Favorites
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

## Database Schema

The service uses the following entities:

- **User**: Represents buyers and sellers
- **Car**: Car listing details
- **SavedSearch**: User's saved search criteria
- **Favourite**: User's favorite car listings

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

## Getting Started

1. Ensure Java 17+ is installed
2. Run `mvn spring-boot:run`
3. Access the application at `http://localhost:8081`
4. Use the H2 console at `http://localhost:8081/h2-console` for database inspection

## Testing

The service includes comprehensive validation:
- Input validation using Jakarta Validation annotations
- Business logic validation (e.g., preventing duplicate favorites)
- Proper error handling with meaningful HTTP status codes
