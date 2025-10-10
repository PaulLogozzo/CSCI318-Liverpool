package com.riderecs.car_listings_service.repository;

import com.riderecs.car_listings_service.entity.AuditAction;
import com.riderecs.car_listings_service.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    // Find audit logs by user
    Page<AuditLog> findByUserIdOrderByTimestampDesc(Long userId, Pageable pageable);
    
    // Find audit logs by action
    Page<AuditLog> findByActionOrderByTimestampDesc(AuditAction action, Pageable pageable);
    
    // Find audit logs by entity type and ID
    Page<AuditLog> findByEntityTypeAndEntityIdOrderByTimestampDesc(String entityType, Long entityId, Pageable pageable);
    
    // Find audit logs within date range
    Page<AuditLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // Find flagged audit logs for review
    Page<AuditLog> findByFlaggedForReviewTrueOrderByTimestampDesc(Pageable pageable);
    
    // Find audit logs by user and action
    Page<AuditLog> findByUserIdAndActionOrderByTimestampDesc(Long userId, AuditAction action, Pageable pageable);
    
    // Find recent audit logs for an entity
    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType AND al.entityId = :entityId " +
           "AND al.timestamp >= :since ORDER BY al.timestamp DESC")
    List<AuditLog> findRecentAuditLogs(@Param("entityType") String entityType, 
                                      @Param("entityId") Long entityId,
                                      @Param("since") LocalDateTime since);
    
    // Count actions by user in time period
    @Query("SELECT COUNT(al) FROM AuditLog al WHERE al.user.id = :userId " +
           "AND al.timestamp BETWEEN :startDate AND :endDate")
    Long countActionsByUserInPeriod(@Param("userId") Long userId,
                                   @Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);
    
    // Find suspicious activity (multiple actions from same IP in short time)
    @Query("SELECT al.ipAddress, COUNT(al) FROM AuditLog al " +
           "WHERE al.timestamp >= :since " +
           "GROUP BY al.ipAddress " +
           "HAVING COUNT(al) > :threshold " +
           "ORDER BY COUNT(al) DESC")
    List<Object[]> findSuspiciousActivity(@Param("since") LocalDateTime since, @Param("threshold") Long threshold);
    
    // Get audit statistics by action type
    @Query("SELECT al.action, COUNT(al) FROM AuditLog al " +
           "WHERE al.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY al.action ORDER BY COUNT(al) DESC")
    List<Object[]> getAuditStatsByAction(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
    
    // Get most active users in period
    @Query("SELECT al.user.id, al.user.username, COUNT(al) FROM AuditLog al " +
           "WHERE al.timestamp BETWEEN :startDate AND :endDate " +
           "GROUP BY al.user.id, al.user.username " +
           "ORDER BY COUNT(al) DESC")
    List<Object[]> getMostActiveUsers(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     Pageable pageable);
    
    // Find all actions for a specific entity
    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType AND al.entityId = :entityId " +
           "ORDER BY al.timestamp ASC")
    List<AuditLog> findEntityHistory(@Param("entityType") String entityType, @Param("entityId") Long entityId);
    
    // Search audit logs by details content
    @Query("SELECT al FROM AuditLog al WHERE LOWER(al.details) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY al.timestamp DESC")
    Page<AuditLog> searchByDetails(@Param("searchTerm") String searchTerm, Pageable pageable);
}