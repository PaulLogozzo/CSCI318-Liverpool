package com.riderecs.car_listings_service.service;

import com.riderecs.car_listings_service.dto.*;
import com.riderecs.car_listings_service.entity.*;
import com.riderecs.car_listings_service.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class InspectionService {
    
    @Autowired
    private InspectionRepository inspectionRepository;
    
    @Autowired
    private InspectionMessageRepository inspectionMessageRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Create a new inspection request
    public InspectionResponse createInspection(InspectionRequest request, Long buyerId) {
        // Validate car exists and is active
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        
        if (car.getStatus() != CarStatus.ACTIVE) {
            throw new IllegalArgumentException("Car is not available for inspection");
        }
        
        // Validate buyer exists
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));
        
        // Get seller from car
        User seller = car.getSeller();
        
        // Validate buyer is not the seller
        if (buyer.getId().equals(seller.getId())) {
            throw new IllegalArgumentException("Cannot create inspection for your own car");
        }
        
        // Check for conflicting inspections
        LocalDateTime endTime = request.getScheduledDateTime().plusMinutes(request.getDurationMinutes());
        List<Inspection> conflictingInspections = inspectionRepository.findConflictingInspections(
                seller.getId(), request.getScheduledDateTime(), endTime);
        
        if (!conflictingInspections.isEmpty()) {
            throw new IllegalArgumentException("Seller is not available at the requested time");
        }
        
        // Create inspection
        Inspection inspection = new Inspection();
        inspection.setCar(car);
        inspection.setBuyer(buyer);
        inspection.setSeller(seller);
        inspection.setScheduledDateTime(request.getScheduledDateTime());
        inspection.setDurationMinutes(request.getDurationMinutes());
        inspection.setNotes(request.getNotes());
        inspection.setLocation(request.getLocation());
        inspection.setStatus(InspectionStatus.REQUESTED);
        
        Inspection savedInspection = inspectionRepository.save(inspection);
        return convertToResponse(savedInspection);
    }
    
    // Get inspection by ID
    public InspectionResponse getInspectionById(Long inspectionId, Long userId) {
        Inspection inspection = inspectionRepository.findByIdWithDetails(inspectionId)
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found"));
        
        // Check if user is involved in this inspection
        if (!inspection.getBuyer().getId().equals(userId) && !inspection.getSeller().getId().equals(userId)) {
            throw new SecurityException("Access denied");
        }
        
        return convertToResponse(inspection);
    }
    
    // Get buyer's inspections
    public Page<InspectionResponse> getBuyerInspections(Long buyerId, InspectionStatus status, Pageable pageable) {
        Page<Inspection> inspections;
        if (status != null) {
            inspections = inspectionRepository.findByBuyerIdAndStatus(buyerId, status, pageable);
        } else {
            inspections = inspectionRepository.findByBuyerId(buyerId, pageable);
        }
        
        return inspections.map(this::convertToResponse);
    }
    
    // Get seller's inspections
    public Page<InspectionResponse> getSellerInspections(Long sellerId, InspectionStatus status, Pageable pageable) {
        Page<Inspection> inspections;
        if (status != null) {
            inspections = inspectionRepository.findBySellerIdAndStatus(sellerId, status, pageable);
        } else {
            inspections = inspectionRepository.findBySellerId(sellerId, pageable);
        }
        
        return inspections.map(this::convertToResponse);
    }
    
    // Get inspections for a specific car
    public Page<InspectionResponse> getCarInspections(Long carId, Long userId, Pageable pageable) {
        // Verify user has access to this car's inspections
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        
        // Check if user is the seller of the car
        if (!car.getSeller().getId().equals(userId)) {
            throw new SecurityException("Access denied");
        }
        
        Page<Inspection> inspections = inspectionRepository.findByCarId(carId, pageable);
        return inspections.map(this::convertToResponse);
    }
    
    // Update inspection status
    public InspectionResponse updateInspectionStatus(Long inspectionId, InspectionStatus newStatus, Long userId) {
        Inspection inspection = inspectionRepository.findByIdWithDetails(inspectionId)
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found"));
        
        // Validate user can update this inspection
        validateUserCanModifyInspection(inspection, userId, newStatus);
        
        inspection.setStatus(newStatus);
        Inspection updatedInspection = inspectionRepository.save(inspection);
        
        return convertToResponse(updatedInspection);
    }
    
    // Reschedule inspection
    public InspectionResponse rescheduleInspection(Long inspectionId, LocalDateTime newDateTime, Long userId) {
        Inspection inspection = inspectionRepository.findByIdWithDetails(inspectionId)
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found"));
        
        // Only buyer or seller can reschedule
        if (!inspection.getBuyer().getId().equals(userId) && !inspection.getSeller().getId().equals(userId)) {
            throw new SecurityException("Access denied");
        }
        
        // Cannot reschedule completed or cancelled inspections
        if (inspection.getStatus() == InspectionStatus.COMPLETED || 
            inspection.getStatus() == InspectionStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot reschedule completed or cancelled inspection");
        }
        
        // Check for conflicts with new time
        LocalDateTime endTime = newDateTime.plusMinutes(inspection.getDurationMinutes());
        List<Inspection> conflictingInspections = inspectionRepository.findConflictingInspections(
                inspection.getSeller().getId(), newDateTime, endTime);
        
        // Remove current inspection from conflicts
        conflictingInspections.removeIf(insp -> insp.getId().equals(inspectionId));
        
        if (!conflictingInspections.isEmpty()) {
            throw new IllegalArgumentException("Seller is not available at the requested time");
        }
        
        inspection.setScheduledDateTime(newDateTime);
        inspection.setStatus(InspectionStatus.REQUESTED); // Reset to requested for approval
        
        Inspection updatedInspection = inspectionRepository.save(inspection);
        return convertToResponse(updatedInspection);
    }
    
    // Get seller availability
    public List<LocalDateTime> getSellerAvailability(Long sellerId, LocalDate startDate, LocalDate endDate) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        
        List<LocalDateTime> availableSlots = new ArrayList<>();
        
        // Get existing inspections in the date range
        List<Inspection> existingInspections = inspectionRepository.findBySellerIdAndScheduledDateTimeBetween(
                sellerId, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        
        // Generate available time slots (simplified - assuming 9 AM to 6 PM, 1-hour slots)
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            for (int hour = 9; hour < 18; hour++) {
                LocalDateTime slot = currentDate.atTime(hour, 0);
                
                // Check if this slot conflicts with existing inspections
                boolean isAvailable = existingInspections.stream()
                        .noneMatch(inspection -> isTimeSlotConflicting(slot, inspection));
                
                if (isAvailable) {
                    availableSlots.add(slot);
                }
            }
            currentDate = currentDate.plusDays(1);
        }
        
        return availableSlots;
    }
    
    // Send message for inspection
    public MessageResponse sendMessage(MessageRequest request, Long senderId) {
        Inspection inspection = inspectionRepository.findByIdWithDetails(request.getInspectionId())
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found"));
        
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        
        // Verify sender is involved in this inspection
        if (!inspection.getBuyer().getId().equals(senderId) && !inspection.getSeller().getId().equals(senderId)) {
            throw new SecurityException("Access denied");
        }
        
        InspectionMessage message = new InspectionMessage();
        message.setInspection(inspection);
        message.setSender(sender);
        message.setMessage(request.getMessage());
        
        InspectionMessage savedMessage = inspectionMessageRepository.save(message);
        return convertToMessageResponse(savedMessage);
    }
    
    // Get messages for inspection
    public Page<MessageResponse> getInspectionMessages(Long inspectionId, Long userId, Pageable pageable) {
        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found"));
        
        // Verify user is involved in this inspection
        if (!inspection.getBuyer().getId().equals(userId) && !inspection.getSeller().getId().equals(userId)) {
            throw new SecurityException("Access denied");
        }
        
        Page<InspectionMessage> messages = inspectionMessageRepository.findByInspectionIdOrderBySentAtAsc(inspectionId, pageable);
        return messages.map(this::convertToMessageResponse);
    }
    
    // Mark messages as read for inspection
    public void markMessagesAsRead(Long inspectionId, Long userId) {
        Inspection inspection = inspectionRepository.findById(inspectionId)
                .orElseThrow(() -> new IllegalArgumentException("Inspection not found"));
        
        // Verify user is involved in this inspection
        if (!inspection.getBuyer().getId().equals(userId) && !inspection.getSeller().getId().equals(userId)) {
            throw new SecurityException("Access denied");
        }
        
        inspectionMessageRepository.markMessagesAsReadForInspection(inspectionId, userId);
    }
    
    // Get unread message count for user
    public Long getUnreadMessageCount(Long userId) {
        return inspectionMessageRepository.countUnreadMessagesForUser(userId);
    }
    
    // Helper method to validate user can modify inspection
    private void validateUserCanModifyInspection(Inspection inspection, Long userId, InspectionStatus newStatus) {
        boolean isBuyer = inspection.getBuyer().getId().equals(userId);
        boolean isSeller = inspection.getSeller().getId().equals(userId);
        
        if (!isBuyer && !isSeller) {
            throw new SecurityException("Access denied");
        }
        
        // Business rules for status transitions
        switch (newStatus) {
            case CONFIRMED:
                if (!isSeller) {
                    throw new SecurityException("Only seller can confirm inspections");
                }
                if (inspection.getStatus() != InspectionStatus.REQUESTED) {
                    throw new IllegalArgumentException("Can only confirm requested inspections");
                }
                break;
                
            case CANCELLED:
                // Both buyer and seller can cancel
                if (inspection.getStatus() == InspectionStatus.COMPLETED) {
                    throw new IllegalArgumentException("Cannot cancel completed inspection");
                }
                break;
                
            case COMPLETED:
                if (!isSeller) {
                    throw new SecurityException("Only seller can mark inspections as completed");
                }
                if (inspection.getStatus() != InspectionStatus.CONFIRMED) {
                    throw new IllegalArgumentException("Can only complete confirmed inspections");
                }
                break;
                
            default:
                throw new IllegalArgumentException("Invalid status transition");
        }
    }
    
    // Helper method to check if time slot conflicts with inspection
    private boolean isTimeSlotConflicting(LocalDateTime slot, Inspection inspection) {
        LocalDateTime inspectionStart = inspection.getScheduledDateTime();
        LocalDateTime inspectionEnd = inspectionStart.plusMinutes(inspection.getDurationMinutes());
        LocalDateTime slotEnd = slot.plusMinutes(60); // Assuming 1-hour slots
        
        // Check for overlap
        return slot.isBefore(inspectionEnd) && slotEnd.isAfter(inspectionStart);
    }
    
    // Convert Inspection entity to InspectionResponse DTO
    private InspectionResponse convertToResponse(Inspection inspection) {
        InspectionResponse response = new InspectionResponse();
        response.setId(inspection.getId());
        response.setCarId(inspection.getCar().getId());
        response.setCarMake(inspection.getCar().getMake());
        response.setCarModel(inspection.getCar().getModel());
        response.setCarYear(inspection.getCar().getYear());
        response.setBuyerId(inspection.getBuyer().getId());
        response.setBuyerName(inspection.getBuyer().getFirstName() + " " + inspection.getBuyer().getLastName());
        response.setBuyerEmail(inspection.getBuyer().getEmail());
        response.setSellerId(inspection.getSeller().getId());
        response.setSellerName(inspection.getSeller().getFirstName() + " " + inspection.getSeller().getLastName());
        response.setSellerEmail(inspection.getSeller().getEmail());
        response.setScheduledDateTime(inspection.getScheduledDateTime());
        response.setDurationMinutes(inspection.getDurationMinutes());
        response.setStatus(inspection.getStatus());
        response.setNotes(inspection.getNotes());
        response.setLocation(inspection.getLocation());
        response.setCreatedAt(inspection.getCreatedAt());
        response.setUpdatedAt(inspection.getUpdatedAt());
        
        // Get message count
        Long messageCount = inspectionMessageRepository.countByInspectionIdAndIsReadFalse(inspection.getId());
        response.setMessageCount(messageCount.intValue());
        
        return response;
    }
    
    // Convert InspectionMessage entity to MessageResponse DTO
    private MessageResponse convertToMessageResponse(InspectionMessage message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setInspectionId(message.getInspection().getId());
        response.setSenderId(message.getSender().getId());
        response.setSenderName(message.getSender().getFirstName() + " " + message.getSender().getLastName());
        response.setMessage(message.getMessage());
        response.setSentAt(message.getSentAt());
        response.setIsRead(message.getIsRead());
        
        return response;
    }
}