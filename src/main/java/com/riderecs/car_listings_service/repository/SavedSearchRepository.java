package com.riderecs.car_listings_service.repository;

import com.riderecs.car_listings_service.entity.SavedSearch;
import com.riderecs.car_listings_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long> {
    
    List<SavedSearch> findByBuyerAndActiveTrue(User buyer);
    
    Optional<SavedSearch> findByIdAndBuyer(Long id, User buyer);
    
    List<SavedSearch> findByBuyerAndActiveTrueOrderByCreatedAtDesc(User buyer);
    
    boolean existsByBuyerAndName(User buyer, String name);
}