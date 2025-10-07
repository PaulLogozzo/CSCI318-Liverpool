package com.riderecs.car_listings_service.controller;

import com.riderecs.car_listings_service.dto.SavedSearchRequest;
import com.riderecs.car_listings_service.dto.SavedSearchResponse;
import com.riderecs.car_listings_service.service.SavedSearchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-searches")
@CrossOrigin(origins = "*")
public class SavedSearchController {
    
    @Autowired
    private SavedSearchService savedSearchService;
    
    @PostMapping
    public ResponseEntity<SavedSearchResponse> createSavedSearch(
            @Valid @RequestBody SavedSearchRequest request,
            @RequestHeader("User-Id") Long buyerId) {
        try {
            SavedSearchResponse response = savedSearchService.createSavedSearch(request, buyerId);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @PutMapping("/{searchId}")
    public ResponseEntity<SavedSearchResponse> updateSavedSearch(
            @PathVariable Long searchId,
            @Valid @RequestBody SavedSearchRequest request,
            @RequestHeader("User-Id") Long buyerId) {
        try {
            SavedSearchResponse response = savedSearchService.updateSavedSearch(searchId, request, buyerId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found") || e.getMessage().contains("unauthorized")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            if (e.getMessage().contains("already exists")) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    
    @DeleteMapping("/{searchId}")
    public ResponseEntity<Void> deleteSavedSearch(
            @PathVariable Long searchId,
            @RequestHeader("User-Id") Long buyerId) {
        try {
            savedSearchService.deleteSavedSearch(searchId, buyerId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping("/{searchId}")
    public ResponseEntity<SavedSearchResponse> getSavedSearchById(
            @PathVariable Long searchId,
            @RequestHeader("User-Id") Long buyerId) {
        try {
            SavedSearchResponse response = savedSearchService.getSavedSearchById(searchId, buyerId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
    
    @GetMapping
    public ResponseEntity<List<SavedSearchResponse>> getBuyerSavedSearches(
            @RequestHeader("User-Id") Long buyerId) {
        try {
            List<SavedSearchResponse> searches = savedSearchService.getBuyerSavedSearches(buyerId);
            return ResponseEntity.ok(searches);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}