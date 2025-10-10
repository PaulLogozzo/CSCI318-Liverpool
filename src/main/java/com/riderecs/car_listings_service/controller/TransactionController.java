package com.riderecs.car_listings_service.controller;

import com.riderecs.car_listings_service.dto.TransactionRequest;
import com.riderecs.car_listings_service.dto.TransactionResponse;
import com.riderecs.car_listings_service.entity.TransactionStatus;
import com.riderecs.car_listings_service.service.TransactionService;
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
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    // Create a new transaction
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Valid @RequestBody TransactionRequest request,
            @RequestHeader("User-Id") Long userId) {
        try {
            TransactionResponse transaction = transactionService.createTransaction(request, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Get transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            TransactionResponse transaction = transactionService.getTransactionById(id, userId);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Get user's purchase history
    @GetMapping("/purchases")
    public ResponseEntity<Page<TransactionResponse>> getPurchaseHistory(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) TransactionStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> transactions = transactionService.getPurchaseHistory(userId, status, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    // Get user's sales history
    @GetMapping("/sales")
    public ResponseEntity<Page<TransactionResponse>> getSalesHistory(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) TransactionStatus status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> transactions = transactionService.getSalesHistory(userId, status, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    // Get all transactions for a user (both purchases and sales)
    @GetMapping("/history")
    public ResponseEntity<Page<TransactionResponse>> getTransactionHistory(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> transactions = transactionService.getTransactionHistory(
            userId, status, startDate, endDate, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    // Get transactions for a specific car
    @GetMapping("/car/{carId}")
    public ResponseEntity<Page<TransactionResponse>> getCarTransactions(
            @PathVariable Long carId,
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionResponse> transactions = transactionService.getCarTransactions(carId, userId, pageable);
        return ResponseEntity.ok(transactions);
    }
    
    // Update transaction status
    @PutMapping("/{id}/status")
    public ResponseEntity<TransactionResponse> updateTransactionStatus(
            @PathVariable Long id,
            @RequestParam TransactionStatus status,
            @RequestHeader("User-Id") Long userId) {
        try {
            TransactionResponse transaction = transactionService.updateTransactionStatus(id, status, userId);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Cancel transaction
    @PutMapping("/{id}/cancel")
    public ResponseEntity<TransactionResponse> cancelTransaction(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            TransactionResponse transaction = transactionService.cancelTransaction(id, userId);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Complete transaction
    @PutMapping("/{id}/complete")
    public ResponseEntity<TransactionResponse> completeTransaction(
            @PathVariable Long id,
            @RequestHeader("User-Id") Long userId) {
        try {
            TransactionResponse transaction = transactionService.completeTransaction(id, userId);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get user's transaction statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getTransactionStatistics(
            @RequestHeader("User-Id") Long userId) {
        Map<String, Object> stats = transactionService.getUserTransactionStatistics(userId);
        return ResponseEntity.ok(stats);
    }
    
    // Get user's transaction summary for a specific period
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getTransactionSummary(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Map<String, Object> summary = transactionService.getUserTransactionSummary(userId, startDate, endDate);
        return ResponseEntity.ok(summary);
    }
    
    // Admin: Get all transactions (admin only)
    @GetMapping("/admin/all")
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) TransactionStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TransactionResponse> transactions = transactionService.getAllTransactions(
                userId, status, startDate, endDate, pageable);
            return ResponseEntity.ok(transactions);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
    
    // Admin: Get transaction analytics
    @GetMapping("/admin/analytics")
    public ResponseEntity<Map<String, Object>> getTransactionAnalytics(
            @RequestHeader("User-Id") Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            Map<String, Object> analytics = transactionService.getTransactionAnalytics(userId, startDate, endDate);
            return ResponseEntity.ok(analytics);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}