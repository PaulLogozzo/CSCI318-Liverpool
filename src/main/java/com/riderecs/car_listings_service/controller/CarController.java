package com.riderecs.car_listings_service.controller;

import com.riderecs.car_listings_service.dto.CarListingRequest;
import com.riderecs.car_listings_service.dto.CarListingResponse;
import com.riderecs.car_listings_service.dto.CarSearchCriteria;
import com.riderecs.car_listings_service.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@CrossOrigin(origins = "*")
public class CarController {
    
    @Autowired
    private CarService carService;
    
    @PostMapping
    public ResponseEntity<CarListingResponse> createCarListing(
            @Valid @RequestBody CarListingRequest request,
            @RequestHeader("User-Id") Long sellerId) {
        try {
            CarListingResponse response = carService.createCarListing(request, sellerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PutMapping("/{carId}")
    public ResponseEntity<CarListingResponse> updateCarListing(
            @PathVariable Long carId,
            @Valid @RequestBody CarListingRequest request,
            @RequestHeader("User-Id") Long sellerId) {
        try {
            CarListingResponse response = carService.updateCarListing(carId, request, sellerId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @DeleteMapping("/{carId}")
    public ResponseEntity<Void> deactivateCarListing(
            @PathVariable Long carId,
            @RequestHeader("User-Id") Long sellerId) {
        try {
            carService.deactivateCarListing(carId, sellerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @PutMapping("/{carId}/sold")
    public ResponseEntity<Void> markCarAsSold(
            @PathVariable Long carId,
            @RequestHeader("User-Id") Long sellerId) {
        try {
            carService.markCarAsSold(carId, sellerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/{carId}")
    public ResponseEntity<CarListingResponse> getCarById(@PathVariable Long carId) {
        try {
            CarListingResponse response = carService.getCarById(carId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<CarListingResponse>> searchCars(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer minYear,
            @RequestParam(required = false) Integer maxYear,
            @RequestParam(required = false) Integer maxMileage,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) String fuelType,
            @RequestParam(required = false) String transmission,
            @RequestParam(required = false) String location,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        CarSearchCriteria criteria = new CarSearchCriteria();
        criteria.setKeyword(keyword);
        criteria.setMake(make);
        criteria.setModel(model);
        criteria.setMinYear(minYear);
        criteria.setMaxYear(maxYear);
        criteria.setMaxMileage(maxMileage);
        if (minPrice != null) criteria.setMinPrice(java.math.BigDecimal.valueOf(minPrice));
        if (maxPrice != null) criteria.setMaxPrice(java.math.BigDecimal.valueOf(maxPrice));
        if (condition != null) {
            try {
                criteria.setCondition(com.riderecs.car_listings_service.entity.CarCondition.valueOf(condition.toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Invalid condition, ignore
            }
        }
        criteria.setFuelType(fuelType);
        criteria.setTransmission(transmission);
        criteria.setLocation(location);
        criteria.setSortBy(sortBy);
        criteria.setSortDirection(sortDirection);
        criteria.setPage(page);
        criteria.setSize(size);
        
        Page<CarListingResponse> results = carService.searchCars(criteria);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping
    public ResponseEntity<Page<CarListingResponse>> getAllActiveCarListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<CarListingResponse> results = carService.getActiveCarListings(page, size);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<Page<CarListingResponse>> getSellerCarListings(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<CarListingResponse> results = carService.getSellerCarListings(sellerId, page, size);
            return ResponseEntity.ok(results);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/makes")
    public ResponseEntity<List<String>> getAvailableMakes() {
        List<String> makes = carService.getAvailableMakes();
        return ResponseEntity.ok(makes);
    }
    
    @GetMapping("/models")
    public ResponseEntity<List<String>> getAvailableModels(@RequestParam String make) {
        List<String> models = carService.getAvailableModels(make);
        return ResponseEntity.ok(models);
    }
}