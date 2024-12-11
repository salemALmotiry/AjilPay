package com.example.ajilpay.Controller;

import com.example.ajilpay.ApiResponse.ApiResponse;
import com.example.ajilpay.DTO.InvoiceItemPriceChangeDTO;
import com.example.ajilpay.DTO.InvoiceSummaryDTO;
import com.example.ajilpay.DTO.InvoicesForecastDTO;
import com.example.ajilpay.Model.Customer;
import com.example.ajilpay.Model.InvoiceItem;
import com.example.ajilpay.Service.CustomerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private final CustomerService customerService;


    @GetMapping("/get-all")
    public ResponseEntity getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.status(200).body(customers);
    }

    @PostMapping("/add")
    public ResponseEntity addCustomer(@RequestBody @Valid Customer customer) {

        customerService.addCustomer(customer);
        return ResponseEntity.status(200).body(new ApiResponse("Customer added successfully"));
    }

    @PutMapping("/{customerId}/checkout")
    public ResponseEntity  checkoutFromStore(@PathVariable Integer customerId) {
        customerService.checkoutCustomer(customerId);
        return ResponseEntity.status(200).body(new ApiResponse("Customer has successfully checked out from the store."));
    }


    @GetMapping("/invoices/{customer_id}")
    public ResponseEntity getInvoiceSummary(@PathVariable Integer customer_id) {
        List<InvoiceSummaryDTO> invoiceSummaries = customerService.getInvoiceSummaryForCustomer(customer_id);

        return ResponseEntity.status(200).body(invoiceSummaries);
    }

    @PostMapping("/add-item/{invoiceId}")
    public ResponseEntity  addItemToInvoice(
            @PathVariable Integer invoiceId,
            @RequestBody @Valid InvoiceItem invoiceItem) {

        customerService.addItemToInvoice(invoiceId, invoiceItem);
        return ResponseEntity.status(200).body(new ApiResponse("item added to invoice"));
    }

    @RequestMapping("/unusual-items/{customerId}")
    public ResponseEntity getUnusualItems(@PathVariable Integer customerId) {
        List unusualItems = customerService.getUnusualItems(customerId);
        return ResponseEntity.status(200).body(unusualItems);
    }

    @GetMapping("/customer/{customerId}/monthsAhead/{monthsAhead}/invoices-forecast")
    public ResponseEntity generateInvoicesForecast(@PathVariable Integer customerId, @PathVariable Integer monthsAhead) {
        InvoicesForecastDTO forecast = customerService.forecastCustomerInvoices(customerId, monthsAhead);
        return ResponseEntity.status(200).body(forecast);
    }

    @GetMapping("/customer/{customerId}/price-changes")
    public ResponseEntity getPriceChangesForCustomer(@PathVariable Integer customerId) {
        List<InvoiceItemPriceChangeDTO> priceChanges = customerService.getPriceChangesForCustomer(customerId);
        return ResponseEntity.status(200).body(priceChanges);
    }
}
