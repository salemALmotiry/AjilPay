package com.example.ajilpay.Repository;

import com.example.ajilpay.Model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Integer> {
    List<PaymentHistory> findPaymentHistoriesByMonthlyPaymentId(Integer monthlyPaymentId);

    List<PaymentHistory> findPaymentHistoriesByCustomerId(Integer customerId);

    @Query("SELECT ph FROM PaymentHistory ph WHERE ph.monthlyPaymentId = :monthlyPaymentId")
    PaymentHistory findByMonthlyPaymentId(@Param("monthlyPaymentId") Integer monthlyPaymentId);


}
