package com.riderecs.car_listings_service.repository;

import com.riderecs.car_listings_service.entity.Transaction;
import com.riderecs.car_listings_service.entity.TransactionStatus;
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
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    // Find transactions by buyer
    Page<Transaction> findByBuyerId(Long buyerId, Pageable pageable);
    
    // Find transactions by seller
    Page<Transaction> findBySellerId(Long sellerId, Pageable pageable);
    
    // Find transactions by car
    List<Transaction> findByCarId(Long carId);
    
    // Find transactions by status
    Page<Transaction> findByStatus(TransactionStatus status, Pageable pageable);
    
    // Find transactions by buyer and status
    Page<Transaction> findByBuyerIdAndStatus(Long buyerId, TransactionStatus status, Pageable pageable);
    
    // Find transactions by seller and status
    Page<Transaction> findBySellerIdAndStatus(Long sellerId, TransactionStatus status, Pageable pageable);
    
    // Find transactions within date range
    @Query("SELECT t FROM Transaction t WHERE t.transactionDate BETWEEN :startDate AND :endDate")
    Page<Transaction> findByTransactionDateBetween(@Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate,
                                                  Pageable pageable);
    
    // Find user's purchase history (as buyer)
    @Query("SELECT t FROM Transaction t WHERE t.buyer.id = :userId ORDER BY t.transactionDate DESC")
    Page<Transaction> findPurchaseHistory(@Param("userId") Long userId, Pageable pageable);
    
    // Find user's sales history (as seller)
    @Query("SELECT t FROM Transaction t WHERE t.seller.id = :userId ORDER BY t.transactionDate DESC")
    Page<Transaction> findSalesHistory(@Param("userId") Long userId, Pageable pageable);
    
    // Find completed transactions for market analysis
    @Query("SELECT t FROM Transaction t WHERE t.status = 'COMPLETED' AND t.car.make = :make AND t.car.model = :model AND t.car.year = :year")
    List<Transaction> findCompletedTransactionsByMakeModelYear(@Param("make") String make,
                                                              @Param("model") String model,
                                                              @Param("year") Integer year);
    
    // Calculate total sales for a user
    @Query("SELECT COALESCE(SUM(t.salePrice), 0) FROM Transaction t WHERE t.seller.id = :sellerId AND t.status = 'COMPLETED'")
    Double getTotalSalesBySeller(@Param("sellerId") Long sellerId);
    
    // Calculate total purchases for a user
    @Query("SELECT COALESCE(SUM(t.salePrice), 0) FROM Transaction t WHERE t.buyer.id = :buyerId AND t.status = 'COMPLETED'")
    Double getTotalPurchasesByBuyer(@Param("buyerId") Long buyerId);
    
    // Count completed transactions by user
    @Query("SELECT COUNT(t) FROM Transaction t WHERE (t.buyer.id = :userId OR t.seller.id = :userId) AND t.status = 'COMPLETED'")
    Long countCompletedTransactionsByUser(@Param("userId") Long userId);
    
    // Find transaction with detailed information
    @Query("SELECT t FROM Transaction t " +
           "LEFT JOIN FETCH t.car " +
           "LEFT JOIN FETCH t.buyer " +
           "LEFT JOIN FETCH t.seller " +
           "WHERE t.id = :id")
    Optional<Transaction> findByIdWithDetails(@Param("id") Long id);
}