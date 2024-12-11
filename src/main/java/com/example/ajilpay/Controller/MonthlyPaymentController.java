package com.example.ajilpay.Controller;

import com.example.ajilpay.ApiResponse.ApiResponse;
import com.example.ajilpay.Model.MonthlyPayment;
import com.example.ajilpay.Service.MonthlyPaymentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/monthly-payments")
public class MonthlyPaymentController {

    private final MonthlyPaymentService monthlyPaymentService;

    @GetMapping("/get")
    public ResponseEntity getAllMonthlyPayments() {
        List<MonthlyPayment> monthlyPayments = monthlyPaymentService.getAllMonthlyPayments();
        return ResponseEntity.status(200).body(monthlyPayments);
    }

    @PostMapping("/add")
    public ResponseEntity addMonthlyPayment(@RequestBody @Valid MonthlyPayment monthlyPayment) {

        monthlyPaymentService.addMonthlyPayment(monthlyPayment);
        return ResponseEntity.status(200).body(new ApiResponse("Monthly Payment added successfully"));
    }


    @DeleteMapping("/delete/{paymentId}")
    public ResponseEntity deleteMonthlyPayment(@PathVariable Integer paymentId) {
        monthlyPaymentService.deleteMonthlyPayment(paymentId);
        return ResponseEntity.status(200).body(new ApiResponse("Monthly Payment deleted successfully"));
    }
}
