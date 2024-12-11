package com.example.ajilpay.Repository;

import com.example.ajilpay.Model.MonthlyPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MonthlyPaymentRepository extends JpaRepository<MonthlyPayment, Integer> {

    List<MonthlyPayment> findMonthlyPaymentByCustomerId(Integer customerId);

    void deleteMonthlyPaymentByPaymentId(Integer paymentId);

    @Query("SELECT ph FROM MonthlyPayment ph WHERE ph.paymentId =?1 ")
    MonthlyPayment findMonthlyPaymentByMonthlyPaymentId(Integer monthlyPaymentId);

    List<MonthlyPayment> findMonthlyPaymentByStoreIdAndCustomerId(Integer storeId,Integer customerId);

    List<MonthlyPayment> findMonthlyPaymentByStoreId(Integer storeId);

    List<MonthlyPayment> findMonthlyPaymentByPaymentStatus(String paymentStatus);

    List<MonthlyPayment> findMonthlyPaymentByCustomerIdAndPaymentStatus(Integer customerId,String paymentStatus);



    MonthlyPayment findMonthlyPaymentByCustomerIdAndMonth(Integer customerId,LocalDate month);


    @Query("SELECT MAX(mp.month) FROM MonthlyPayment mp WHERE mp.storeId =?1")
    LocalDate getLastMonthlyPaymentDate(Integer storeId);

    @Query("SELECT MAX(mp.month) FROM MonthlyPayment mp WHERE mp.storeId =?1 AND mp.customerId =?2")
    LocalDate getLastMonthlyPaymentDateForCustomer(Integer storeId, Integer customerId);

    @Query("SELECT CASE WHEN COUNT(mp) > 0 THEN TRUE ELSE FALSE END FROM MonthlyPayment mp WHERE mp.customerId = :customerId AND mp.storeId = :storeId AND mp.month = :month")
    boolean existsMonthlyPaymentByCustomerIdAndStoreIdAndMonth(@Param("customerId") Integer customerId, @Param("storeId") Integer storeId, @Param("month") LocalDate month);

    List<MonthlyPayment> findMonthlyPaymentByCustomerIdAndStoreIdAndPaymentStatus(Integer customerId, Integer storeId,String paymentStatus);

    @Query("SELECT p FROM MonthlyPayment p WHERE p.customerId =?1 AND p.storeId =?2 AND (p.paymentStatus = 'PENDING' OR p.paymentStatus = 'PARTIAL') ORDER BY p.month ASC")
    List<MonthlyPayment> findMonthlyPaymentByPendingAndPartial( Integer customerId, Integer storeId);



}
