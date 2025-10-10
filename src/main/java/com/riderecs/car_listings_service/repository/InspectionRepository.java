package com.riderecs.car_listings_service.repository;

import com.riderecs.car_listings_service.entity.Inspection;
import com.riderecs.car_listings_service.entity.InspectionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    
    // Find inspections by buyer
    Page<Inspection> findByBuyerId(Long buyerId, Pageable pageable);
    
    // Find inspections by seller
    Page<Inspection> findBySellerId(Long sellerId, Pageable pageable);
    
    // Find inspections by car
    Page<Inspection> findByCarId(Long carId, Pageable pageable);
    
    // Find inspections by status
    Page<Inspection> findByStatus(InspectionStatus status, Pageable pageable);
    
    // Find inspections by buyer and status
    Page<Inspection> findByBuyerIdAndStatus(Long buyerId, InspectionStatus status, Pageable pageable);
    
    // Find inspections by seller and status
    Page<Inspection> findBySellerIdAndStatus(Long sellerId, InspectionStatus status, Pageable pageable);
    
    // Find inspections scheduled within a date range
    @Query("SELECT i FROM Inspection i WHERE i.scheduledDateTime BETWEEN :startDate AND :endDate")
    List<Inspection> findByScheduledDateTimeBetween(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
    
    // Find inspections for a specific seller within a date range
    @Query("SELECT i FROM Inspection i WHERE i.seller.id = :sellerId AND i.scheduledDateTime BETWEEN :startDate AND :endDate")
    List<Inspection> findBySellerIdAndScheduledDateTimeBetween(@Param("sellerId") Long sellerId,
                                                              @Param("startDate") LocalDateTime startDate,
                                                              @Param("endDate") LocalDateTime endDate);
    
    // Check for conflicting inspections for a seller
    // Using a simpler approach by checking if scheduled time overlaps with the requested time range
    @Query("SELECT i FROM Inspection i WHERE i.seller.id = :sellerId " +
           "AND i.status IN ('REQUESTED', 'CONFIRMED') " +
           "AND i.scheduledDateTime < :endTime " +
           "AND i.scheduledDateTime >= :startTime")
    List<Inspection> findConflictingInspections(@Param("sellerId") Long sellerId,
                                              @Param("startTime") LocalDateTime startTime,
                                              @Param("endTime") LocalDateTime endTime);
    
    // Count unread messages for an inspection
    @Query("SELECT COUNT(m) FROM InspectionMessage m WHERE m.inspection.id = :inspectionId AND m.isRead = false")
    Integer countUnreadMessages(@Param("inspectionId") Long inspectionId);
    
    // Find inspection with detailed information
    @Query("SELECT i FROM Inspection i " +
           "LEFT JOIN FETCH i.car " +
           "LEFT JOIN FETCH i.buyer " +
           "LEFT JOIN FETCH i.seller " +
           "WHERE i.id = :id")
    Optional<Inspection> findByIdWithDetails(@Param("id") Long id);
}