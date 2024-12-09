package com.example.ajilpay.Controller;

import com.example.ajilpay.ApiResponse.ApiResponse;
import com.example.ajilpay.DTO.*;
import com.example.ajilpay.Model.*;
import com.example.ajilpay.Service.CustomerService;
import com.example.ajilpay.Service.StoreService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/store")
public class StoreController {

    private final StoreService storeService;
    private final CustomerService customerService;

    @GetMapping("/get-all")
    public ResponseEntity getAllStores() {
        List<Store> stores = storeService.getAllStores();
        return ResponseEntity.ok(stores);
    }

    @PostMapping("/add")
    public ResponseEntity addStore(@RequestBody @Valid Store store, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        storeService.addStore(store);
        return ResponseEntity.status(201).body(new ApiResponse("Store added successfully"));
    }

    @GetMapping("/store/{storeId}/monthly-revenue")
    public ResponseEntity<BigDecimal> getMonthlyRevenue(@PathVariable Integer storeId) {
        BigDecimal totalRevenue = storeService.getMonthlyRevenueForStore(storeId);
        return ResponseEntity.status(200).body(totalRevenue);
    }

    @PostMapping("/store/{storeId}/customer/{customerId}/create-monthly-payment")
    public ResponseEntity<ApiResponse> createMonthlyPayment(
            @PathVariable Integer storeId,
            @PathVariable Integer customerId) {

        storeService.createMonthlyPayment(storeId, customerId);
        return ResponseEntity.status(200).body(new ApiResponse("Monthly payment for customer generated successfully."));
    }


    @PostMapping("/store/{storeId}/customer/{customerId}/amount/{amount}/process-payment")
    public ResponseEntity<String> makePayment(@PathVariable Integer storeId, @PathVariable Integer customerId, @PathVariable double amount) {

        String response = storeService.processCustomerPayment(storeId, customerId, amount);
        return ResponseEntity.ok(response);

    }
    @GetMapping("/store/{storeId}/customer/{customerId}/payment-summary")
    public ResponseEntity getCombinedPaymentSummary(@PathVariable Integer storeId,@PathVariable Integer customerId) {
        CombinedPaymentSummary summary = storeService.getCombinedPaymentSummary(storeId,customerId);
        return ResponseEntity.status(200).body(summary);
    }

    @GetMapping("/store/{storeId}/customer/{customerId}/customer-over-view")
    public ResponseEntity getCustomerOverView(@PathVariable Integer storeId,@PathVariable Integer customerId) {
        CustomerOverViewDTO summary = storeService.getCustomerOverView(storeId,customerId);
        return ResponseEntity.status(200).body(summary);

    }

    @GetMapping("/store/{storeId}/customer/{customerId}/payment-behavior")
    public ResponseEntity get(@PathVariable Integer storeId,@PathVariable Integer customerId) {
        PaymentBehaviorOverViewDTO summary = storeService.analyzePaymentBehavior(storeId,customerId);
        return ResponseEntity.status(200).body(summary);
    }

    @GetMapping("/{store_id}/store-insights")
    public ResponseEntity<StoreDashboardInsightsDTO> getStoreDashboardInsights(@PathVariable Integer store_id) {
        StoreDashboardInsightsDTO insights = storeService.getStoreInsights(store_id);
        return ResponseEntity.status(200).body(insights);
    }

    @GetMapping("/store/{storeId}/limit/{limit}/top-selling-items")
    public ResponseEntity getTopSellingItems(@PathVariable Integer storeId,@PathVariable int limit) {
        List<Map<String, Object>> topSellingItems = storeService.getTopSellingItems(storeId, limit);
        return ResponseEntity.ok(topSellingItems);
    }

    @PostMapping("/store/{storeId}/customer/{customerId}/evaluate-trustworthiness-manual")
    public ResponseEntity evaluateTrustworthinessManual(@PathVariable Integer storeId, @PathVariable Integer customerId) {
        Map<String, Object> evaluationResult = storeService.evaluateCustomerTrustworthiness(customerId, storeId);
        return ResponseEntity.ok(evaluationResult);
    }

    @GetMapping("/{storeId}/{monthsAhead}/sales-forecast")
    public SalesForecastDTO generateSalesForecast(@PathVariable Integer storeId, @PathVariable Integer monthsAhead) {
        return storeService.forecastStoreSales(storeId, monthsAhead);
    }

    @GetMapping("/{storeId}/suspicious-activity")
    public ResponseEntity<Map<String, Object>> detectSuspiciousActivity(@PathVariable Integer storeId) {
        return ResponseEntity.ok(storeService.detectSuspiciousActivity(storeId));
    }

    @GetMapping("/store/{storeId}/abnormal-spending")
    public ResponseEntity<List<Map<String, Object>>> getAbnormalSpending(
            @PathVariable Integer storeId) {


        List<Map<String, Object>> abnormalInvoices = storeService.detectAbnormalSpending(storeId);
        return ResponseEntity.ok(abnormalInvoices);
    }



}
