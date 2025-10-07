package com.riderecs.car_listings_service.service;

import com.riderecs.car_listings_service.dto.CarListingRequest;
import com.riderecs.car_listings_service.dto.CarListingResponse;
import com.riderecs.car_listings_service.dto.CarSearchCriteria;
import com.riderecs.car_listings_service.entity.Car;
import com.riderecs.car_listings_service.entity.CarStatus;
import com.riderecs.car_listings_service.entity.User;
import com.riderecs.car_listings_service.repository.CarRepository;
import com.riderecs.car_listings_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarService {
    
    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public CarListingResponse createCarListing(CarListingRequest request, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        
        Car car = new Car();
        car.setMake(request.getMake());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setMileage(request.getMileage());
        car.setCondition(request.getCondition());
        car.setAskingPrice(request.getAskingPrice());
        car.setDescription(request.getDescription());
        car.setColor(request.getColor());
        car.setFuelType(request.getFuelType());
        car.setTransmission(request.getTransmission());
        car.setNumberOfDoors(request.getNumberOfDoors());
        car.setEngineSize(request.getEngineSize());
        car.setLocation(request.getLocation());
        car.setContactPhone(request.getContactPhone());
        car.setContactEmail(request.getContactEmail());
        car.setSeller(seller);
        car.setStatus(CarStatus.ACTIVE);
        
        Car savedCar = carRepository.save(car);
        return convertToResponse(savedCar);
    }
    
    public CarListingResponse updateCarListing(Long carId, CarListingRequest request, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        
        Car car = carRepository.findByIdAndSeller(carId, seller)
                .orElseThrow(() -> new RuntimeException("Car listing not found or unauthorized"));
        
        car.setMake(request.getMake());
        car.setModel(request.getModel());
        car.setYear(request.getYear());
        car.setMileage(request.getMileage());
        car.setCondition(request.getCondition());
        car.setAskingPrice(request.getAskingPrice());
        car.setDescription(request.getDescription());
        car.setColor(request.getColor());
        car.setFuelType(request.getFuelType());
        car.setTransmission(request.getTransmission());
        car.setNumberOfDoors(request.getNumberOfDoors());
        car.setEngineSize(request.getEngineSize());
        car.setLocation(request.getLocation());
        car.setContactPhone(request.getContactPhone());
        car.setContactEmail(request.getContactEmail());
        
        Car updatedCar = carRepository.save(car);
        return convertToResponse(updatedCar);
    }
    
    public void deactivateCarListing(Long carId, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        
        Car car = carRepository.findByIdAndSeller(carId, seller)
                .orElseThrow(() -> new RuntimeException("Car listing not found or unauthorized"));
        
        car.setStatus(CarStatus.INACTIVE);
        carRepository.save(car);
    }
    
    public void markCarAsSold(Long carId, Long sellerId) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        
        Car car = carRepository.findByIdAndSeller(carId, seller)
                .orElseThrow(() -> new RuntimeException("Car listing not found or unauthorized"));
        
        car.setStatus(CarStatus.SOLD);
        carRepository.save(car);
    }
    
    public CarListingResponse getCarById(Long carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car listing not found"));
        
        if (car.getStatus() != CarStatus.ACTIVE) {
            throw new RuntimeException("Car listing is not available");
        }
        
        return convertToResponse(car);
    }
    
    public Page<CarListingResponse> searchCars(CarSearchCriteria criteria) {
        // Map sortBy parameter to actual field names
        String sortField = mapSortField(criteria.getSortBy());
        Sort sort = Sort.by(Sort.Direction.fromString(criteria.getSortDirection()), sortField);
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);
        
        Page<Car> cars = carRepository.findCarsWithCriteria(
                CarStatus.ACTIVE,
                criteria.getKeyword(),
                criteria.getMake(),
                criteria.getModel(),
                criteria.getMinYear(),
                criteria.getMaxYear(),
                criteria.getMaxMileage(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                criteria.getCondition(),
                criteria.getFuelType(),
                criteria.getTransmission(),
                criteria.getLocation(),
                pageable
        );
        
        return cars.map(this::convertToResponse);
    }
    
    public Page<CarListingResponse> getActiveCarListings(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Car> cars = carRepository.findByStatusOrderByCreatedAtDesc(CarStatus.ACTIVE, pageable);
        return cars.map(this::convertToResponse);
    }
    
    public Page<CarListingResponse> getSellerCarListings(Long sellerId, int page, int size) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Car> cars = carRepository.findBySellerAndStatusOrderByCreatedAtDesc(seller, CarStatus.ACTIVE, pageable);
        return cars.map(this::convertToResponse);
    }
    
    public List<String> getAvailableMakes() {
        return carRepository.findDistinctMakesByStatus(CarStatus.ACTIVE);
    }
    
    public List<String> getAvailableModels(String make) {
        return carRepository.findDistinctModelsByMakeAndStatus(make, CarStatus.ACTIVE);
    }
    
    private CarListingResponse convertToResponse(Car car) {
        CarListingResponse response = new CarListingResponse();
        response.setId(car.getId());
        response.setMake(car.getMake());
        response.setModel(car.getModel());
        response.setYear(car.getYear());
        response.setMileage(car.getMileage());
        response.setCondition(car.getCondition());
        response.setAskingPrice(car.getAskingPrice());
        response.setDescription(car.getDescription());
        response.setColor(car.getColor());
        response.setFuelType(car.getFuelType());
        response.setTransmission(car.getTransmission());
        response.setNumberOfDoors(car.getNumberOfDoors());
        response.setEngineSize(car.getEngineSize());
        response.setLocation(car.getLocation());
        response.setContactPhone(car.getContactPhone());
        response.setContactEmail(car.getContactEmail());
        response.setStatus(car.getStatus());
        response.setCreatedAt(car.getCreatedAt());
        response.setUpdatedAt(car.getUpdatedAt());
        
        // Set seller info
        User seller = car.getSeller();
        CarListingResponse.SellerInfo sellerInfo = new CarListingResponse.SellerInfo(
                seller.getId(),
                seller.getFirstName(),
                seller.getLastName(),
                seller.getUsername()
        );
        response.setSeller(sellerInfo);
        
        return response;
    }
    
    /**
     * Maps API sort field names to actual entity field names
     */
    private String mapSortField(String sortBy) {
        if (sortBy == null) {
            return "createdAt";
        }
        
        return switch (sortBy.toLowerCase()) {
            case "price" -> "askingPrice";
            case "year" -> "year";
            case "mileage" -> "mileage";
            case "make" -> "make";
            case "model" -> "model";
            case "createdat", "created" -> "createdAt";
            case "updatedat", "updated" -> "updatedAt";
            default -> "createdAt"; // Default sort field
        };
    }
}
