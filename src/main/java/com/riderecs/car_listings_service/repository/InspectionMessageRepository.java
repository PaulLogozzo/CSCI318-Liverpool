package com.riderecs.car_listings_service.repository;

import com.riderecs.car_listings_service.entity.InspectionMessage;
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
public interface InspectionMessageRepository extends JpaRepository<InspectionMessage, Long> {
    
    // Find messages by inspection ID
    Page<InspectionMessage> findByInspectionIdOrderBySentAtAsc(Long inspectionId, Pageable pageable);
    
    // Find messages by inspection ID (all messages)
    List<InspectionMessage> findByInspectionIdOrderBySentAtAsc(Long inspectionId);
    
    // Find unread messages by inspection ID
    List<InspectionMessage> findByInspectionIdAndIsReadFalseOrderBySentAtAsc(Long inspectionId);
    
    // Count unread messages by inspection ID
    Long countByInspectionIdAndIsReadFalse(Long inspectionId);
    
    // Find messages sent by a specific user
    Page<InspectionMessage> findBySenderIdOrderBySentAtDesc(Long senderId, Pageable pageable);
    
    // Find messages in inspections involving a user (as buyer or seller)
    @Query("SELECT im FROM InspectionMessage im " +
           "WHERE im.inspection.buyer.id = :userId OR im.inspection.seller.id = :userId " +
           "ORDER BY im.sentAt DESC")
    Page<InspectionMessage> findMessagesByInvolvedUser(@Param("userId") Long userId, Pageable pageable);
    
    // Find unread messages for a user (across all their inspections)
    @Query("SELECT im FROM InspectionMessage im " +
           "WHERE (im.inspection.buyer.id = :userId OR im.inspection.seller.id = :userId) " +
           "AND im.sender.id != :userId " +
           "AND im.isRead = false " +
           "ORDER BY im.sentAt DESC")
    List<InspectionMessage> findUnreadMessagesForUser(@Param("userId") Long userId);
    
    // Count unread messages for a user
    @Query("SELECT COUNT(im) FROM InspectionMessage im " +
           "WHERE (im.inspection.buyer.id = :userId OR im.inspection.seller.id = :userId) " +
           "AND im.sender.id != :userId " +
           "AND im.isRead = false")
    Long countUnreadMessagesForUser(@Param("userId") Long userId);
    
    // Mark messages as read
    @Modifying
    @Query("UPDATE InspectionMessage im SET im.isRead = true " +
           "WHERE im.inspection.id = :inspectionId " +
           "AND im.sender.id != :userId " +
           "AND im.isRead = false")
    int markMessagesAsReadForInspection(@Param("inspectionId") Long inspectionId, @Param("userId") Long userId);
    
    // Find messages sent within date range
    List<InspectionMessage> findBySentAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Get conversation between two users for a specific inspection
    @Query("SELECT im FROM InspectionMessage im " +
           "WHERE im.inspection.id = :inspectionId " +
           "ORDER BY im.sentAt ASC")
    List<InspectionMessage> getConversationForInspection(@Param("inspectionId") Long inspectionId);
    
    // Get latest message for each inspection involving a user
    @Query("SELECT im FROM InspectionMessage im " +
           "WHERE im.id IN (" +
           "    SELECT MAX(im2.id) FROM InspectionMessage im2 " +
           "    WHERE (im2.inspection.buyer.id = :userId OR im2.inspection.seller.id = :userId) " +
           "    GROUP BY im2.inspection.id" +
           ") " +
           "ORDER BY im.sentAt DESC")
    List<InspectionMessage> getLatestMessagesForUser(@Param("userId") Long userId);
}