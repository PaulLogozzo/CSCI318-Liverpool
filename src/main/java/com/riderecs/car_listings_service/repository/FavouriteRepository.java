package com.riderecs.car_listings_service.repository;

import com.riderecs.car_listings_service.entity.Car;
import com.riderecs.car_listings_service.entity.Favourite;
import com.riderecs.car_listings_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    
    Optional<Favourite> findByBuyerAndCar(User buyer, Car car);
    
    @Query("SELECT f FROM Favourite f JOIN FETCH f.car WHERE f.buyer = :buyer ORDER BY f.createdAt DESC")
    Page<Favourite> findByBuyerOrderByCreatedAtDesc(@Param("buyer") User buyer, Pageable pageable);
    
    boolean existsByBuyerAndCar(User buyer, Car car);
    
    void deleteByBuyerAndCar(User buyer, Car car);
    
    Long countByBuyer(User buyer);
}