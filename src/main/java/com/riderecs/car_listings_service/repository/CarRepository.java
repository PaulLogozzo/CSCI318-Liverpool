package com.riderecs.car_listings_service.repository;

import com.riderecs.car_listings_service.entity.Car;
import com.riderecs.car_listings_service.entity.CarStatus;
import com.riderecs.car_listings_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    
    Page<Car> findByStatusOrderByCreatedAtDesc(CarStatus status, Pageable pageable);
    
    Page<Car> findBySellerAndStatusOrderByCreatedAtDesc(User seller, CarStatus status, Pageable pageable);
    
    List<Car> findBySeller(User seller);
    
    Optional<Car> findByIdAndSeller(Long id, User seller);
    
    @Query("SELECT c FROM Car c WHERE c.status = :status AND " +
           "(:keyword IS NULL OR " +
           "LOWER(c.make) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.model) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:make IS NULL OR LOWER(c.make) = LOWER(:make)) AND " +
           "(:model IS NULL OR LOWER(c.model) = LOWER(:model)) AND " +
           "(:minYear IS NULL OR c.year >= :minYear) AND " +
           "(:maxYear IS NULL OR c.year <= :maxYear) AND " +
           "(:maxMileage IS NULL OR c.mileage <= :maxMileage) AND " +
           "(:minPrice IS NULL OR c.askingPrice >= :minPrice) AND " +
           "(:maxPrice IS NULL OR c.askingPrice <= :maxPrice) AND " +
           "(:condition IS NULL OR c.condition = :condition) AND " +
           "(:fuelType IS NULL OR LOWER(c.fuelType) = LOWER(:fuelType)) AND " +
           "(:transmission IS NULL OR LOWER(c.transmission) = LOWER(:transmission)) AND " +
           "(:location IS NULL OR LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%')))")
    Page<Car> findCarsWithCriteria(
            @Param("status") CarStatus status,
            @Param("keyword") String keyword,
            @Param("make") String make,
            @Param("model") String model,
            @Param("minYear") Integer minYear,
            @Param("maxYear") Integer maxYear,
            @Param("maxMileage") Integer maxMileage,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("condition") com.riderecs.car_listings_service.entity.CarCondition condition,
            @Param("fuelType") String fuelType,
            @Param("transmission") String transmission,
            @Param("location") String location,
            Pageable pageable
    );
    
    @Query("SELECT DISTINCT c.make FROM Car c WHERE c.status = :status ORDER BY c.make")
    List<String> findDistinctMakesByStatus(@Param("status") CarStatus status);
    
    @Query("SELECT DISTINCT c.model FROM Car c WHERE c.status = :status AND LOWER(c.make) = LOWER(:make) ORDER BY c.model")
    List<String> findDistinctModelsByMakeAndStatus(@Param("make") String make, @Param("status") CarStatus status);
    
    long countByStatus(CarStatus status);
}
