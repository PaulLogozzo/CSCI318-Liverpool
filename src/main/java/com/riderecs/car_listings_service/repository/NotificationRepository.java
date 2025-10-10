package com.riderecs.car_listings_service.repository;

import com.riderecs.car_listings_service.entity.Notification;
import com.riderecs.car_listings_service.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find notifications by user
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find unread notifications by user
    Page<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // Find notifications by type
    Page<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, NotificationType type, Pageable pageable);
    
    // Count unread notifications for a user
    Long countByUserIdAndIsReadFalse(Long userId);
    
    // Find notifications created within date range
    List<Notification> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find notifications that haven't been emailed yet
    @Query("SELECT n FROM Notification n WHERE n.isEmailSent = false AND n.user.email IS NOT NULL")
    List<Notification> findUnsentEmailNotifications();
    
    // Mark all notifications as read for a user
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readTime WHERE n.user.id = :userId AND n.isRead = false")
    int markAllAsReadForUser(@Param("userId") Long userId, @Param("readTime") LocalDateTime readTime);
    
    // Mark notification as read
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readTime WHERE n.id = :notificationId")
    int markAsRead(@Param("notificationId") Long notificationId, @Param("readTime") LocalDateTime readTime);
    
    // Mark email as sent
    @Modifying
    @Query("UPDATE Notification n SET n.isEmailSent = true WHERE n.id = :notificationId")
    int markEmailAsSent(@Param("notificationId") Long notificationId);
    
    // Delete old notifications (cleanup)
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate AND n.isRead = true")
    int deleteOldReadNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    // Find notifications by related entity
    List<Notification> findByRelatedEntityIdAndRelatedEntityType(Long entityId, String entityType);
    
    // Get notification statistics for a user
    @Query("SELECT n.type, COUNT(n) FROM Notification n WHERE n.user.id = :userId GROUP BY n.type")
    List<Object[]> getNotificationStatsByUser(@Param("userId") Long userId);
}