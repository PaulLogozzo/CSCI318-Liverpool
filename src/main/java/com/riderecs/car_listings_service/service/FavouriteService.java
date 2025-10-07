package com.riderecs.car_listings_service.service;

import com.riderecs.car_listings_service.dto.CarListingResponse;
import com.riderecs.car_listings_service.dto.FavouriteResponse;
import com.riderecs.car_listings_service.entity.Car;
import com.riderecs.car_listings_service.entity.CarStatus;
import com.riderecs.car_listings_service.entity.Favourite;
import com.riderecs.car_listings_service.entity.User;
import com.riderecs.car_listings_service.repository.CarRepository;
import com.riderecs.car_listings_service.repository.FavouriteRepository;
import com.riderecs.car_listings_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FavouriteService {
    
    @Autowired
    private FavouriteRepository favouriteRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private CarService carService;
    
    public FavouriteResponse addToFavourites(Long carId, Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car listing not found"));
        
        if (car.getStatus() != CarStatus.ACTIVE) {
            throw new RuntimeException("Car listing is not available");
        }
        
        // Check if already favourited
        if (favouriteRepository.existsByBuyerAndCar(buyer, car)) {
            throw new RuntimeException("Car listing is already in favourites");
        }
        
        Favourite favourite = new Favourite(buyer, car);
        Favourite saved = favouriteRepository.save(favourite);
        
        return convertToResponse(saved);
    }
    
    public void removeFromFavourites(Long carId, Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car listing not found"));
        
        Favourite favourite = favouriteRepository.findByBuyerAndCar(buyer, car)
                .orElseThrow(() -> new RuntimeException("Car listing is not in favourites"));
        
        favouriteRepository.delete(favourite);
    }
    
    public Page<FavouriteResponse> getBuyerFavourites(Long buyerId, int page, int size) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Favourite> favourites = favouriteRepository.findByBuyerOrderByCreatedAtDesc(buyer, pageable);
        
        return favourites.map(this::convertToResponse);
    }
    
    public boolean isFavourite(Long carId, Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car listing not found"));
        
        return favouriteRepository.existsByBuyerAndCar(buyer, car);
    }
    
    public Long getFavouritesCount(Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        return favouriteRepository.countByBuyer(buyer);
    }
    
    private FavouriteResponse convertToResponse(Favourite favourite) {
        CarListingResponse carResponse = convertCarToResponse(favourite.getCar());
        
        FavouriteResponse response = new FavouriteResponse();
        response.setId(favourite.getId());
        response.setCreatedAt(favourite.getCreatedAt());
        response.setCar(carResponse);
        
        return response;
    }
    
    private CarListingResponse convertCarToResponse(Car car) {
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
}