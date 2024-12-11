package com.example.ajilpay.Controller;

import com.example.ajilpay.ApiResponse.ApiResponse;
import com.example.ajilpay.DTO.*;
import com.example.ajilpay.Model.*;
import com.example.ajilpay.Service.CustomerService;
import com.example.ajilpay.Service.StoreService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
        return ResponseEntity.status(200).body(stores);
    }

    @PostMapping("/add")
    public ResponseEntity addStore(@RequestBody @Valid Store store) {

        storeService.addStore(store);
        return ResponseEntity.status(200).body(new ApiResponse("Store added successfully"));
    }



    @PostMapping("/store/{storeId}/customer/{customerId}/create-monthly-payment")
    public ResponseEntity<ApiResponse> createMonthlyPayment(
            @PathVariable Integer storeId,
            @PathVariable Integer customerId) {

        storeService.createMonthlyPayment(storeId, customerId);
        return ResponseEntity.status(200).body(new ApiResponse("Monthly payment for customer generated successfully."));
    }


    @PostMapping("/store/{storeId}/customer/{customerId}/amount/{amount}/process-payment")
    public ResponseEntity makePayment(@PathVariable Integer storeId, @PathVariable Integer customerId, @PathVariable double amount) {

        String response = storeService.processCustomerPayment(storeId, customerId, amount);
        return ResponseEntity.status(200).body(new ApiResponse( response));

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
        return ResponseEntity.status(200).body(topSellingItems);
    }

    @PostMapping("/store/{storeId}/customer/{customerId}/evaluate-trustworthiness-manual")
    public ResponseEntity evaluateTrustworthinessManual(@PathVariable Integer storeId, @PathVariable Integer customerId) {
        Map<String, Object> evaluationResult = storeService.evaluateCustomerTrustworthiness(customerId, storeId);
        return ResponseEntity.status(200).body(evaluationResult);
    }

    @GetMapping("/{storeId}/{monthsAhead}/sales-forecast")
    public SalesForecastDTO generateSalesForecast(@PathVariable Integer storeId, @PathVariable Integer monthsAhead) {
        return storeService.forecastStoreSales(storeId, monthsAhead);
    }

    @GetMapping("/{storeId}/suspicious-activity")
    public ResponseEntity<Map<String, Object>> detectSuspiciousActivity(@PathVariable Integer storeId) {
        return ResponseEntity.status(200).body(storeService.detectSuspiciousActivity(storeId));
    }

    @GetMapping("/store/{storeId}/abnormal-spending")
    public ResponseEntity<List<Map<String, Object>>> getAbnormalSpending(
            @PathVariable Integer storeId) {


        List<Map<String, Object>> abnormalInvoices = storeService.detectAbnormalSpending(storeId);
        return ResponseEntity.status(200).body(abnormalInvoices);
    }

    @PostMapping("/{storeId}/customers/{customerId}/add-invoice")
    public ResponseEntity addInvoice(
            @PathVariable Integer storeId,
            @PathVariable Integer customerId,
            @RequestBody Map<String, Object> requestBody) {

            String response= storeService.addInvoiceWithItems(storeId, customerId, requestBody);
            return ResponseEntity.status(200).body(new ApiResponse( response));

    }

    @PostMapping("/add-item/{invoiceId}/{storeId}/{customerId}")
    public ResponseEntity addItemToInvoice(
            @PathVariable Integer invoiceId,
            @PathVariable Integer storeId,
            @PathVariable Integer customerId,
            @RequestBody @Valid InvoiceItem invoiceItem) {

        storeService.addItemToInvoice(invoiceId,storeId,customerId ,invoiceItem);
        return ResponseEntity.status(200).body(new ApiResponse( "item added to invoice"));
    }


    @PutMapping("/{storeId}/add-customer/{customerId}")
    public ResponseEntity addCustomerToStore(
            @PathVariable Integer storeId,
            @PathVariable Integer customerId) {

            storeService.assignCustomerToStore(customerId, storeId);
            return ResponseEntity.status(200).body(new ApiResponse( "Customer added to the store successfully."));

    }

    @PutMapping("/{customerId}/checkout")
    public ResponseEntity checkoutFromStore(@PathVariable Integer customerId) {
        customerService.checkoutCustomer(customerId);
        return ResponseEntity.status(200).body(new ApiResponse("Customer has successfully checked out from the store."));
    }

}
