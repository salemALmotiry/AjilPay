package com.example.ajilpay.Service;

import com.example.ajilpay.Model.PaymentHistory;
import com.example.ajilpay.Repository.PaymentHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    public List<PaymentHistory> getAllPaymentHistory() {
        return paymentHistoryRepository.findAll();
    }

    public List<PaymentHistory> getPaymentHistoryByMonthlyPaymentId(Integer monthlyPaymentId) {
        return paymentHistoryRepository.findPaymentHistoriesByMonthlyPaymentId(monthlyPaymentId);
    }

    public void addPaymentHistory(PaymentHistory paymentHistory) {
        PaymentHistory paymentHistory1 = paymentHistoryRepository.findByMonthlyPaymentId(paymentHistory.getMonthlyPaymentId());
        if (paymentHistory1 == null) {
            throw new RuntimeException("This payment history mot exists");
        }
        if (paymentHistory1.getMonthlyPaymentId() == paymentHistory.getMonthlyPaymentId()){
            throw new RuntimeException("This payment history mot exists");
        }
        paymentHistoryRepository.save(paymentHistory);
    }

    public void deletePaymentHistory(Integer historyId) {
        PaymentHistory paymentHistory = paymentHistoryRepository.findByMonthlyPaymentId(historyId);
        if (paymentHistory == null) {
            throw new RuntimeException("This payment history mot exists");
        }
        paymentHistoryRepository.deleteById(historyId);
    }
}
