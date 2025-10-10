package com.riderecs.car_listings_service.controller;

import com.riderecs.car_listings_service.dto.InspectionRequest;
import com.riderecs.car_listings_service.dto.InspectionResponse;
import com.riderecs.car_listings_service.dto.MessageRequest;
import com.riderecs.car_listings_service.dto.MessageResponse;
import com.riderecs.car_listings_service.entity.InspectionStatus;
import com.riderecs.car_listings_service.service.InspectionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inspections")
@CrossOrigin(origins = "*")
public class InspectionController {
    
    @Autowired
    private InspectionService inspectionService;
    
    // Create a new inspection request
    @PostMapping
    public ResponseEntity<InspectionResponse> createInspection(
            @Valid @RequestBody InspectionRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            InspectionResponse inspection = inspectionService.createInspection(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(inspection);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // Get inspection by ID
    @GetMapping("/{id}")
    public ResponseEntity<InspectionResponse> getInspection(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            InspectionResponse inspection = inspectionService.getInspectionById(id, userId);
            return ResponseEntity.ok(inspection);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Get user's inspections (as buyer)
    @GetMapping("/buyer")
    public ResponseEntity<Page<InspectionResponse>> getBuyerInspections(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) InspectionStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InspectionResponse> inspections = inspectionService.getBuyerInspections(userId, status, pageable);
        return ResponseEntity.ok(inspections);
    }
    
    // Get user's inspections (as seller)
    @GetMapping("/seller")
    public ResponseEntity<Page<InspectionResponse>> getSellerInspections(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) InspectionStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InspectionResponse> inspections = inspectionService.getSellerInspections(userId, status, pageable);
        return ResponseEntity.ok(inspections);
    }
    
    // Get inspections for a specific car
    @GetMapping("/car/{carId}")
    public ResponseEntity<Page<InspectionResponse>> getCarInspections(
            @PathVariable Long carId,
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InspectionResponse> inspections = inspectionService.getCarInspections(carId, userId, pageable);
        return ResponseEntity.ok(inspections);
    }
    
    // Update inspection status
    @PutMapping("/{id}/status")
    public ResponseEntity<InspectionResponse> updateInspectionStatus(
            @PathVariable Long id,
            @RequestParam InspectionStatus status,
            @RequestHeader("User-Id") Long userId) {
        try {
            InspectionResponse inspection = inspectionService.updateInspectionStatus(id, status, userId);
            return ResponseEntity.ok(inspection);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Reschedule inspection
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<InspectionResponse> rescheduleInspection(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDateTime,
            @RequestHeader("User-Id") Long userId) {
        try {
            InspectionResponse inspection = inspectionService.rescheduleInspection(id, newDateTime, userId);
            return ResponseEntity.ok(inspection);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Get seller availability for a specific date range
    @GetMapping("/availability/{sellerId}")
    public ResponseEntity<List<LocalDateTime>> getSellerAvailability(
            @PathVariable Long sellerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LocalDateTime> availability = inspectionService.getSellerAvailability(sellerId, startDate, endDate);
        return ResponseEntity.ok(availability);
    }
    
    // Send message for inspection
    @PostMapping("/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @Valid @RequestBody MessageRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            MessageResponse message = inspectionService.sendMessage(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Get messages for inspection
    @GetMapping("/{inspectionId}/messages")
    public ResponseEntity<Page<MessageResponse>> getInspectionMessages(
            @PathVariable Long inspectionId,
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<MessageResponse> messages = inspectionService.getInspectionMessages(inspectionId, userId, pageable);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Mark messages as read for inspection
    @PutMapping("/{inspectionId}/messages/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long inspectionId,
            @RequestHeader("User-Id") Long userId) {
        try {
            inspectionService.markMessagesAsRead(inspectionId, userId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Get unread message count for user
    @GetMapping("/messages/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount(@RequestHeader("User-Id") Long userId) {
        Long count = inspectionService.getUnreadMessageCount(userId);
        return ResponseEntity.ok(count);
    }
}