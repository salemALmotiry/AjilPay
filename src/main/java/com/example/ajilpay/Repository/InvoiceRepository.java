package com.example.ajilpay.Repository;

import com.example.ajilpay.Model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    Invoice findByInvoiceId(Integer id);

    List<Invoice> findByStoreId(Integer storeId);

    List<Invoice> findInvoiceByCustomerId(Integer customerId);



    @Query("SELECT i FROM Invoice i WHERE i.customerId = :customerId ORDER BY i.createdAt DESC")
    Invoice findLastInvoiceByCustomerId(Integer customerId);

    @Query("SELECT  MIN(i.createdAt) FROM Invoice i WHERE i.customerId =?1")

    LocalDateTime findFirstInvoiceByCustomerId(Integer customerId);

    @Query("SELECT i FROM Invoice i WHERE i.customerId = :customerId AND i.createdAt BETWEEN :startDate AND :endDate")
    List<Invoice> findInvoicesForCustomerInDateRange(
            @Param("customerId") Integer customerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);




    List<Invoice>findInvoicesByStoreIdAndCreatedAtBetween(Integer storeId,LocalDateTime startOfMonth,LocalDateTime endOfMonth);

    List<Invoice>findInvoicesByCustomerIdAfterAndCreatedAtBetween(Integer storeId,LocalDateTime startOfMonth,LocalDateTime endOfMonth);


}
