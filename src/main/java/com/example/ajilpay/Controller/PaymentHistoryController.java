package com.example.ajilpay.Controller;

import com.example.ajilpay.ApiResponse.ApiResponse;
import com.example.ajilpay.Model.PaymentHistory;
import com.example.ajilpay.Service.PaymentHistoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/payment-history")
public class PaymentHistoryController {

    private final PaymentHistoryService paymentHistoryService;

    @GetMapping("/get-all")
    public ResponseEntity getAllPaymentHistory() {
        List<PaymentHistory> paymentHistoryList = paymentHistoryService.getAllPaymentHistory();
        return ResponseEntity.status(200).body(paymentHistoryList);
    }

    @PostMapping("/add")
    public ResponseEntity addPaymentHistory(@RequestBody @Valid PaymentHistory paymentHistory) {

        paymentHistoryService.addPaymentHistory(paymentHistory);
        return ResponseEntity.status(200).body(new ApiResponse("Payment History added successfully"));
    }

    @GetMapping("/get-by-payment/{monthlyPaymentId}")
    public ResponseEntity getPaymentHistoryByMonthlyPaymentId(@PathVariable Integer monthlyPaymentId) {
        List<PaymentHistory> paymentHistoryList = paymentHistoryService.getPaymentHistoryByMonthlyPaymentId(monthlyPaymentId);
        return ResponseEntity.status(200).body(paymentHistoryList);
    }

    @DeleteMapping("/delete/{historyId}")
    public ResponseEntity deletePaymentHistory(@PathVariable Integer historyId) {
        paymentHistoryService.deletePaymentHistory(historyId);
        return ResponseEntity.status(200).body(new ApiResponse("Payment History deleted successfully"));
    }
}
