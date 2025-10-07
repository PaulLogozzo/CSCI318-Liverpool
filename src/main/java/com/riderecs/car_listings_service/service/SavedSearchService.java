package com.riderecs.car_listings_service.service;

import com.riderecs.car_listings_service.dto.SavedSearchRequest;
import com.riderecs.car_listings_service.dto.SavedSearchResponse;
import com.riderecs.car_listings_service.entity.SavedSearch;
import com.riderecs.car_listings_service.entity.User;
import com.riderecs.car_listings_service.repository.SavedSearchRepository;
import com.riderecs.car_listings_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SavedSearchService {
    
    @Autowired
    private SavedSearchRepository savedSearchRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public SavedSearchResponse createSavedSearch(SavedSearchRequest request, Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        // Check if a saved search with the same name already exists for this buyer
        if (savedSearchRepository.existsByBuyerAndName(buyer, request.getName())) {
            throw new RuntimeException("A saved search with this name already exists");
        }
        
        SavedSearch savedSearch = new SavedSearch();
        savedSearch.setName(request.getName());
        savedSearch.setKeyword(request.getKeyword());
        savedSearch.setMake(request.getMake());
        savedSearch.setModel(request.getModel());
        savedSearch.setMinYear(request.getMinYear());
        savedSearch.setMaxYear(request.getMaxYear());
        savedSearch.setMaxMileage(request.getMaxMileage());
        savedSearch.setMinPrice(request.getMinPrice());
        savedSearch.setMaxPrice(request.getMaxPrice());
        savedSearch.setCondition(request.getCondition());
        savedSearch.setFuelType(request.getFuelType());
        savedSearch.setTransmission(request.getTransmission());
        savedSearch.setLocation(request.getLocation());
        savedSearch.setEmailNotifications(request.getEmailNotifications());
        savedSearch.setBuyer(buyer);
        
        SavedSearch saved = savedSearchRepository.save(savedSearch);
        return convertToResponse(saved);
    }
    
    public SavedSearchResponse updateSavedSearch(Long searchId, SavedSearchRequest request, Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        SavedSearch savedSearch = savedSearchRepository.findByIdAndBuyer(searchId, buyer)
                .orElseThrow(() -> new RuntimeException("Saved search not found or unauthorized"));
        
        // Check if a saved search with the same name already exists for this buyer (excluding current one)
        if (!savedSearch.getName().equals(request.getName()) && 
            savedSearchRepository.existsByBuyerAndName(buyer, request.getName())) {
            throw new RuntimeException("A saved search with this name already exists");
        }
        
        savedSearch.setName(request.getName());
        savedSearch.setKeyword(request.getKeyword());
        savedSearch.setMake(request.getMake());
        savedSearch.setModel(request.getModel());
        savedSearch.setMinYear(request.getMinYear());
        savedSearch.setMaxYear(request.getMaxYear());
        savedSearch.setMaxMileage(request.getMaxMileage());
        savedSearch.setMinPrice(request.getMinPrice());
        savedSearch.setMaxPrice(request.getMaxPrice());
        savedSearch.setCondition(request.getCondition());
        savedSearch.setFuelType(request.getFuelType());
        savedSearch.setTransmission(request.getTransmission());
        savedSearch.setLocation(request.getLocation());
        savedSearch.setEmailNotifications(request.getEmailNotifications());
        
        SavedSearch updated = savedSearchRepository.save(savedSearch);
        return convertToResponse(updated);
    }
    
    public void deleteSavedSearch(Long searchId, Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        SavedSearch savedSearch = savedSearchRepository.findByIdAndBuyer(searchId, buyer)
                .orElseThrow(() -> new RuntimeException("Saved search not found or unauthorized"));
        
        savedSearch.setActive(false);
        savedSearchRepository.save(savedSearch);
    }
    
    public SavedSearchResponse getSavedSearchById(Long searchId, Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        SavedSearch savedSearch = savedSearchRepository.findByIdAndBuyer(searchId, buyer)
                .orElseThrow(() -> new RuntimeException("Saved search not found or unauthorized"));
        
        return convertToResponse(savedSearch);
    }
    
    public List<SavedSearchResponse> getBuyerSavedSearches(Long buyerId) {
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        
        List<SavedSearch> savedSearches = savedSearchRepository.findByBuyerAndActiveTrueOrderByCreatedAtDesc(buyer);
        
        return savedSearches.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private SavedSearchResponse convertToResponse(SavedSearch savedSearch) {
        SavedSearchResponse response = new SavedSearchResponse();
        response.setId(savedSearch.getId());
        response.setName(savedSearch.getName());
        response.setKeyword(savedSearch.getKeyword());
        response.setMake(savedSearch.getMake());
        response.setModel(savedSearch.getModel());
        response.setMinYear(savedSearch.getMinYear());
        response.setMaxYear(savedSearch.getMaxYear());
        response.setMaxMileage(savedSearch.getMaxMileage());
        response.setMinPrice(savedSearch.getMinPrice());
        response.setMaxPrice(savedSearch.getMaxPrice());
        response.setCondition(savedSearch.getCondition());
        response.setFuelType(savedSearch.getFuelType());
        response.setTransmission(savedSearch.getTransmission());
        response.setLocation(savedSearch.getLocation());
        response.setActive(savedSearch.getActive());
        response.setEmailNotifications(savedSearch.getEmailNotifications());
        response.setCreatedAt(savedSearch.getCreatedAt());
        response.setUpdatedAt(savedSearch.getUpdatedAt());
        
        return response;
    }
}