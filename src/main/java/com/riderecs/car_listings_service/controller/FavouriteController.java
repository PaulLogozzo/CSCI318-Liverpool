package com.riderecs.car_listings_service.controller;

import com.riderecs.car_listings_service.dto.FavouriteResponse;
import com.riderecs.car_listings_service.service.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favourites")
@CrossOrigin(origins = "*")
public class FavouriteController {
    
    @Autowired
    private FavouriteService favouriteService;
    
    @PostMapping("/cars/{carId}")
    public ResponseEntity<FavouriteResponse> addToFavourites(
            @PathVariable Long carId,
            @RequestHeader("User-Id") Long buyerId) {
        try {
            FavouriteResponse response = favouriteService.addToFavourites(carId, buyerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (e.getMessage().contains("not available")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            if (e.getMessage().contains("already in favourites")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @DeleteMapping("/cars/{carId}")
    public ResponseEntity<Void> removeFromFavourites(
            @PathVariable Long carId,
            @RequestHeader("User-Id") Long buyerId) {
        try {
            favouriteService.removeFromFavourites(carId, buyerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping
    public ResponseEntity<Page<FavouriteResponse>> getBuyerFavourites(
            @RequestHeader("User-Id") Long buyerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<FavouriteResponse> favourites = favouriteService.getBuyerFavourites(buyerId, page, size);
            return ResponseEntity.ok(favourites);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/cars/{carId}/status")
    public ResponseEntity<Boolean> isFavourite(
            @PathVariable Long carId,
            @RequestHeader("User-Id") Long buyerId) {
        try {
            boolean isFavourite = favouriteService.isFavourite(carId, buyerId);
            return ResponseEntity.ok(isFavourite);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Long> getFavouritesCount(
            @RequestHeader("User-Id") Long buyerId) {
        try {
            Long count = favouriteService.getFavouritesCount(buyerId);
            return ResponseEntity.ok(count);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}