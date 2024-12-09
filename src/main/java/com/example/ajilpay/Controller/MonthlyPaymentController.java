package com.example.ajilpay.Controller;

import com.example.ajilpay.ApiResponse.ApiResponse;
import com.example.ajilpay.Model.MonthlyPayment;
import com.example.ajilpay.Service.MonthlyPaymentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
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
        return ResponseEntity.ok(monthlyPayments);
    }

    @PostMapping("/add")
    public ResponseEntity addMonthlyPayment(@RequestBody @Valid MonthlyPayment monthlyPayment, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        monthlyPaymentService.addMonthlyPayment(monthlyPayment);
        return ResponseEntity.status(201).body(new ApiResponse("Monthly Payment added successfully"));
    }

    @GetMapping("/get-by-customer/{customerId}")
    public ResponseEntity getMonthlyPaymentsByCustomerId(@PathVariable Integer customerId) {
        List<MonthlyPayment> monthlyPayments = monthlyPaymentService.getMonthlyPaymentsByCustomerId(customerId);
        return ResponseEntity.ok(monthlyPayments);
    }

    @GetMapping("/get-by-store/{storeId}")
    public ResponseEntity getMonthlyPaymentsByStoreId(@PathVariable Integer storeId) {
        List<MonthlyPayment> monthlyPayments = monthlyPaymentService.getMonthlyPaymentsByStoreId(storeId);
        return ResponseEntity.ok(monthlyPayments);
    }

    @GetMapping("/get-by-status/{status}")
    public ResponseEntity getMonthlyPaymentsByStatus(@PathVariable String status) {
        List<MonthlyPayment> monthlyPayments = monthlyPaymentService.getMonthlyPaymentsByStatus(status);
        return ResponseEntity.ok(monthlyPayments);
    }

    @DeleteMapping("/delete/{paymentId}")
    public ResponseEntity deleteMonthlyPayment(@PathVariable Integer paymentId) {
        monthlyPaymentService.deleteMonthlyPayment(paymentId);
        return ResponseEntity.ok(new ApiResponse("Monthly Payment deleted successfully"));
    }
}
