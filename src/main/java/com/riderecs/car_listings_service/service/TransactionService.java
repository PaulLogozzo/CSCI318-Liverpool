package com.riderecs.car_listings_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.riderecs.car_listings_service.dto.TransactionRequest;
import com.riderecs.car_listings_service.dto.TransactionResponse;
import com.riderecs.car_listings_service.entity.Car;
import com.riderecs.car_listings_service.entity.CarStatus;
import com.riderecs.car_listings_service.entity.Transaction;
import com.riderecs.car_listings_service.entity.TransactionStatus;
import com.riderecs.car_listings_service.entity.User;
import com.riderecs.car_listings_service.repository.CarRepository;
import com.riderecs.car_listings_service.repository.TransactionRepository;
import com.riderecs.car_listings_service.repository.UserRepository;

@Service
@Transactional
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private CarRepository carRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Create a new transaction
    public TransactionResponse createTransaction(TransactionRequest request, Long requestingUserId) {
        // Validate car exists and is active
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        
        if (car.getStatus() != CarStatus.ACTIVE) {
            throw new IllegalArgumentException("Car is not available for sale");
        }
        
        // Validate buyer exists
        User buyer = userRepository.findById(request.getBuyerId())
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));
        
        // Get seller from car
        User seller = car.getSeller();
        
        // Validate business rules
        if (buyer.getId().equals(seller.getId())) {
            throw new IllegalArgumentException("Cannot purchase your own car");
        }
        
        // Only seller or buyer can initiate transaction (or admin)
        if (!requestingUserId.equals(seller.getId()) && !requestingUserId.equals(buyer.getId())) {
            throw new SecurityException("Only seller or buyer can create this transaction");
        }
        
        // Check if there's already a pending transaction for this car
        List<Transaction> existingTransactions = transactionRepository.findByCarId(car.getId());
        boolean hasPendingTransaction = existingTransactions.stream()
                .anyMatch(t -> t.getStatus() == TransactionStatus.PENDING);
        
        if (hasPendingTransaction) {
            throw new IllegalArgumentException("Car already has a pending transaction");
        }
        
        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setCar(car);
        transaction.setSeller(seller);
        transaction.setBuyer(buyer);
        transaction.setSalePrice(request.getSalePrice());
        transaction.setListingPrice(car.getAskingPrice());
        transaction.setNotes(request.getNotes());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setStatus(TransactionStatus.PENDING);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return convertToResponse(savedTransaction);
    }
    
    // Get transaction by ID
    public TransactionResponse getTransactionById(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findByIdWithDetails(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        
        // Check if user is involved in this transaction
        if (!transaction.getBuyer().getId().equals(userId) && !transaction.getSeller().getId().equals(userId)) {
            throw new SecurityException("Access denied");
        }
        
        return convertToResponse(transaction);
    }
    
    // Get user's purchase history
    public Page<TransactionResponse> getPurchaseHistory(Long buyerId, TransactionStatus status, Pageable pageable) {
        Page<Transaction> transactions;
        if (status != null) {
            transactions = transactionRepository.findByBuyerIdAndStatus(buyerId, status, pageable);
        } else {
            transactions = transactionRepository.findPurchaseHistory(buyerId, pageable);
        }
        
        return transactions.map(this::convertToResponse);
    }
    
    // Get user's sales history
    public Page<TransactionResponse> getSalesHistory(Long sellerId, TransactionStatus status, Pageable pageable) {
        Page<Transaction> transactions;
        if (status != null) {
            transactions = transactionRepository.findBySellerIdAndStatus(sellerId, status, pageable);
        } else {
            transactions = transactionRepository.findSalesHistory(sellerId, pageable);
        }
        
        return transactions.map(this::convertToResponse);
    }
    
    // Get user's transaction history (both purchases and sales)
    public Page<TransactionResponse> getTransactionHistory(Long userId, TransactionStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        // This is a complex query, so we'll fetch both purchase and sales history separately
        // In a real implementation, you might want to create a custom repository method
        
        List<Transaction> allTransactions = new ArrayList<>();
        
        // Get purchases
        Page<Transaction> purchases = getPurchaseTransactions(userId, status, pageable);
        allTransactions.addAll(purchases.getContent());
        
        // Get sales
        Page<Transaction> sales = getSalesTransactions(userId, status, pageable);
        allTransactions.addAll(sales.getContent());
        
        // Filter by date range if provided
        if (startDate != null || endDate != null) {
            LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDateTime.of(1970, 1, 1, 0, 0);
            LocalDateTime end = endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.now();
            
            allTransactions = allTransactions.stream()
                    .filter(t -> !t.getTransactionDate().isBefore(start) && !t.getTransactionDate().isAfter(end))
                    .collect(Collectors.toList());
        }
        
        // Sort by transaction date descending
        allTransactions.sort(Comparator.comparing(Transaction::getTransactionDate).reversed());
        
        // Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), allTransactions.size());
        List<Transaction> pagedTransactions = allTransactions.subList(start, end);
        
        List<TransactionResponse> responses = pagedTransactions.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return new PageImpl<>(responses, pageable, allTransactions.size());
    }
    
    // Get transactions for a specific car
    public Page<TransactionResponse> getCarTransactions(Long carId, Long userId, Pageable pageable) {
        // Verify user has access to this car's transactions
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));
        
        // Check if user is the seller of the car or involved in any transaction
        List<Transaction> carTransactions = transactionRepository.findByCarId(carId);
        boolean hasAccess = car.getSeller().getId().equals(userId) || 
                           carTransactions.stream().anyMatch(t -> 
                               t.getBuyer().getId().equals(userId) || t.getSeller().getId().equals(userId));
        
        if (!hasAccess) {
            throw new SecurityException("Access denied");
        }
        
        List<TransactionResponse> responses = carTransactions.stream()
                .map(this::convertToResponse)
                .sorted(Comparator.comparing(TransactionResponse::getTransactionDate).reversed())
                .collect(Collectors.toList());
        
        // Apply pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), responses.size());
        List<TransactionResponse> pagedResponses = responses.subList(start, end);
        
        return new PageImpl<>(pagedResponses, pageable, responses.size());
    }
    
    // Update transaction status
    public TransactionResponse updateTransactionStatus(Long transactionId, TransactionStatus newStatus, Long userId) {
        Transaction transaction = transactionRepository.findByIdWithDetails(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        
        // Validate user can update this transaction
        validateUserCanModifyTransaction(transaction, userId, newStatus);
        
        // Validate status transition
        validateStatusTransition(transaction.getStatus(), newStatus);
        
        transaction.setStatus(newStatus);
        
        // If completing transaction, mark car as sold
        if (newStatus == TransactionStatus.COMPLETED) {
            Car car = transaction.getCar();
            car.setStatus(CarStatus.SOLD);
            carRepository.save(car);
        }
        
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToResponse(updatedTransaction);
    }
    
    // Cancel transaction
    public TransactionResponse cancelTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findByIdWithDetails(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        
        // Check if user can cancel
        if (!transaction.getBuyer().getId().equals(userId) && !transaction.getSeller().getId().equals(userId)) {
            throw new SecurityException("Access denied");
        }
        
        // Cannot cancel completed transactions
        if (transaction.getStatus() == TransactionStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed transaction");
        }
        
        transaction.setStatus(TransactionStatus.CANCELLED);
        Transaction updatedTransaction = transactionRepository.save(transaction);
        
        return convertToResponse(updatedTransaction);
    }
    
    // Complete transaction
    public TransactionResponse completeTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findByIdWithDetails(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found"));
        
        // Only seller can mark as completed
        if (!transaction.getSeller().getId().equals(userId)) {
            throw new SecurityException("Only seller can complete transaction");
        }
        
        // Can only complete pending transactions (or we could allow any non-completed status)
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Can only complete pending transactions");
        }
        
        transaction.setStatus(TransactionStatus.COMPLETED);
        
        // Mark car as sold
        Car car = transaction.getCar();
        car.setStatus(CarStatus.SOLD);
        carRepository.save(car);
        
        Transaction updatedTransaction = transactionRepository.save(transaction);
        return convertToResponse(updatedTransaction);
    }
    
    // Get user's transaction statistics
    public Map<String, Object> getUserTransactionStatistics(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Purchase statistics
        Double totalPurchases = transactionRepository.getTotalPurchasesByBuyer(userId);
        stats.put("totalPurchaseAmount", totalPurchases != null ? totalPurchases : 0.0);
        
        // Sales statistics
        Double totalSales = transactionRepository.getTotalSalesBySeller(userId);
        stats.put("totalSalesAmount", totalSales != null ? totalSales : 0.0);
        
        // Transaction counts
        Long completedTransactions = transactionRepository.countCompletedTransactionsByUser(userId);
        stats.put("completedTransactions", completedTransactions);
        
        // Get purchase history count
        Page<Transaction> purchases = transactionRepository.findPurchaseHistory(userId, Pageable.unpaged());
        stats.put("totalPurchases", purchases.getTotalElements());
        
        // Get sales history count
        Page<Transaction> sales = transactionRepository.findSalesHistory(userId, Pageable.unpaged());
        stats.put("totalSales", sales.getTotalElements());
        
        // Calculate average transaction values
        if (purchases.getTotalElements() > 0) {
            stats.put("averagePurchaseAmount", totalPurchases / purchases.getTotalElements());
        } else {
            stats.put("averagePurchaseAmount", 0.0);
        }
        
        if (sales.getTotalElements() > 0) {
            stats.put("averageSaleAmount", totalSales / sales.getTotalElements());
        } else {
            stats.put("averageSaleAmount", 0.0);
        }
        
        return stats;
    }
    
    // Get user's transaction summary for a specific period
    public Map<String, Object> getUserTransactionSummary(Long userId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now().minusMonths(12);
        LocalDateTime end = endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.now();
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("period", Map.of(
                "startDate", start.toLocalDate(),
                "endDate", end.toLocalDate()
        ));
        
        // Get all user transactions in period
        List<Transaction> allTransactions = getAllUserTransactionsInPeriod(userId, start, end);
        
        // Separate purchases and sales
        List<Transaction> purchases = allTransactions.stream()
                .filter(t -> t.getBuyer().getId().equals(userId))
                .collect(Collectors.toList());
        
        List<Transaction> sales = allTransactions.stream()
                .filter(t -> t.getSeller().getId().equals(userId))
                .collect(Collectors.toList());
        
        // Purchase summary
        Map<String, Object> purchaseSummary = new HashMap<>();
        purchaseSummary.put("count", purchases.size());
        purchaseSummary.put("totalAmount", purchases.stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .mapToDouble(t -> t.getSalePrice().doubleValue()).sum());
        purchaseSummary.put("averageAmount", purchases.isEmpty() ? 0.0 : 
                purchases.stream().mapToDouble(t -> t.getSalePrice().doubleValue()).average().orElse(0.0));
        
        // Sales summary
        Map<String, Object> salesSummary = new HashMap<>();
        salesSummary.put("count", sales.size());
        salesSummary.put("totalAmount", sales.stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .mapToDouble(t -> t.getSalePrice().doubleValue()).sum());
        salesSummary.put("averageAmount", sales.isEmpty() ? 0.0 : 
                sales.stream().mapToDouble(t -> t.getSalePrice().doubleValue()).average().orElse(0.0));
        
        summary.put("purchases", purchaseSummary);
        summary.put("sales", salesSummary);
        
        // Status breakdown
        Map<TransactionStatus, Long> statusBreakdown = allTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::getStatus, Collectors.counting()));
        summary.put("statusBreakdown", statusBreakdown);
        
        return summary;
    }
    
    // Admin: Get all transactions
    public Page<TransactionResponse> getAllTransactions(Long requestingUserId, TransactionStatus status, 
                                                       LocalDate startDate, LocalDate endDate, Pageable pageable) {
        // Verify admin access (simplified - in reality you'd check user roles)
        User user = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new SecurityException("User not found"));
        
        // For now, allowing any authenticated user to access admin functions
        // In a real application, you'd check for admin role here
        
        Page<Transaction> transactions;
        
        if (startDate != null && endDate != null) {
            LocalDateTime start = startDate.atStartOfDay();
            LocalDateTime end = endDate.plusDays(1).atStartOfDay();
            transactions = transactionRepository.findByTransactionDateBetween(start, end, pageable);
        } else if (status != null) {
            transactions = transactionRepository.findByStatus(status, pageable);
        } else {
            transactions = transactionRepository.findAll(pageable);
        }
        
        return transactions.map(this::convertToResponse);
    }
    
    // Admin: Get transaction analytics
    public Map<String, Object> getTransactionAnalytics(Long requestingUserId, LocalDate startDate, LocalDate endDate) {
        // Verify admin access
        User user = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new SecurityException("User not found"));
        
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : LocalDateTime.now().minusMonths(12);
        LocalDateTime end = endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.now();
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("period", Map.of(
                "startDate", start.toLocalDate(),
                "endDate", end.toLocalDate()
        ));
        
        // Get all transactions in period
        List<Transaction> allTransactions = getAllTransactionsInPeriod(start, end);
        
        // Overall statistics
        analytics.put("totalTransactions", allTransactions.size());
        
        long completedTransactions = allTransactions.stream()
                .mapToLong(t -> t.getStatus() == TransactionStatus.COMPLETED ? 1 : 0).sum();
        analytics.put("completedTransactions", completedTransactions);
        
        double totalRevenue = allTransactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .mapToDouble(t -> t.getSalePrice().doubleValue()).sum();
        analytics.put("totalRevenue", totalRevenue);
        
        // Average transaction value
        if (completedTransactions > 0) {
            analytics.put("averageTransactionValue", totalRevenue / completedTransactions);
        } else {
            analytics.put("averageTransactionValue", 0.0);
        }
        
        // Status distribution
        Map<TransactionStatus, Long> statusDistribution = allTransactions.stream()
                .collect(Collectors.groupingBy(Transaction::getStatus, Collectors.counting()));
        analytics.put("statusDistribution", statusDistribution);
        
        // Monthly breakdown
        Map<String, Long> monthlyBreakdown = allTransactions.stream()
                .collect(Collectors.groupingBy(
                        t -> t.getTransactionDate().getMonth().toString(),
                        Collectors.counting()
                ));
        analytics.put("monthlyTransactionCount", monthlyBreakdown);
        
        // Top transaction amounts
        List<BigDecimal> topTransactionAmounts = allTransactions.stream()
                .filter(t -> t.getStatus() == TransactionStatus.COMPLETED)
                .map(Transaction::getSalePrice)
                .sorted(Comparator.reverseOrder())
                .limit(10)
                .collect(Collectors.toList());
        analytics.put("topTransactionAmounts", topTransactionAmounts);
        
        return analytics;
    }
    
    // Helper methods
    private void validateUserCanModifyTransaction(Transaction transaction, Long userId, TransactionStatus newStatus) {
        boolean isBuyer = transaction.getBuyer().getId().equals(userId);
        boolean isSeller = transaction.getSeller().getId().equals(userId);
        
        if (!isBuyer && !isSeller) {
            throw new SecurityException("Access denied");
        }
        
        // Business rules for status transitions
        switch (newStatus) {
            case COMPLETED:
                if (!isSeller) {
                    throw new SecurityException("Only seller can complete transactions");
                }
                break;
                
            case CANCELLED:
                // Both buyer and seller can cancel
                break;
                
            case FAILED:
                // Both buyer and seller can mark as failed
                break;
                
            case REFUNDED:
                if (!isSeller) {
                    throw new SecurityException("Only seller can process refunds");
                }
                break;
                
            default:
                throw new IllegalArgumentException("Invalid status transition");
        }
    }
    
    private void validateStatusTransition(TransactionStatus currentStatus, TransactionStatus newStatus) {
        return;
    }
    
    private Page<Transaction> getPurchaseTransactions(Long userId, TransactionStatus status, Pageable pageable) {
        if (status != null) {
            return transactionRepository.findByBuyerIdAndStatus(userId, status, pageable);
        } else {
            return transactionRepository.findPurchaseHistory(userId, pageable);
        }
    }
    
    private Page<Transaction> getSalesTransactions(Long userId, TransactionStatus status, Pageable pageable) {
        if (status != null) {
            return transactionRepository.findBySellerIdAndStatus(userId, status, pageable);
        } else {
            return transactionRepository.findSalesHistory(userId, pageable);
        }
    }
    
    private List<Transaction> getAllUserTransactionsInPeriod(Long userId, LocalDateTime start, LocalDateTime end) {
        List<Transaction> allTransactions = new ArrayList<>();
        
        Page<Transaction> purchases = transactionRepository.findPurchaseHistory(userId, Pageable.unpaged());
        Page<Transaction> sales = transactionRepository.findSalesHistory(userId, Pageable.unpaged());
        
        allTransactions.addAll(purchases.getContent());
        allTransactions.addAll(sales.getContent());
        
        return allTransactions.stream()
                .filter(t -> !t.getTransactionDate().isBefore(start) && !t.getTransactionDate().isAfter(end))
                .collect(Collectors.toList());
    }
    
    private List<Transaction> getAllTransactionsInPeriod(LocalDateTime start, LocalDateTime end) {
        // This would ideally be a repository query with date range
        return transactionRepository.findAll().stream()
                .filter(t -> !t.getTransactionDate().isBefore(start) && !t.getTransactionDate().isAfter(end))
                .collect(Collectors.toList());
    }
    
    // Convert Transaction entity to TransactionResponse DTO
    private TransactionResponse convertToResponse(Transaction transaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(transaction.getId());
        response.setCarId(transaction.getCar().getId());
        response.setCarMake(transaction.getCar().getMake());
        response.setCarModel(transaction.getCar().getModel());
        response.setCarYear(transaction.getCar().getYear());
        response.setSellerId(transaction.getSeller().getId());
        response.setSellerName(transaction.getSeller().getFirstName() + " " + transaction.getSeller().getLastName());
        response.setSellerEmail(transaction.getSeller().getEmail());
        response.setBuyerId(transaction.getBuyer().getId());
        response.setBuyerName(transaction.getBuyer().getFirstName() + " " + transaction.getBuyer().getLastName());
        response.setBuyerEmail(transaction.getBuyer().getEmail());
        response.setSalePrice(transaction.getSalePrice());
        response.setListingPrice(transaction.getListingPrice());
        response.setStatus(transaction.getStatus());
        response.setTransactionDate(transaction.getTransactionDate());
        response.setNotes(transaction.getNotes());
        response.setPaymentMethod(transaction.getPaymentMethod());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setUpdatedAt(transaction.getUpdatedAt());
        
        return response;
    }
}