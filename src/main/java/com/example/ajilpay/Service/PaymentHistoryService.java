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
        paymentHistoryRepository.save(paymentHistory);
    }

    public void deletePaymentHistory(Integer historyId) {
        paymentHistoryRepository.deleteById(historyId);
    }
}
