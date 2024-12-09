package com.example.ajilpay.Controller;

import com.example.ajilpay.ApiResponse.ApiResponse;
import com.example.ajilpay.Model.Invoice;
import com.example.ajilpay.Service.InvoiceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping("/get-all")
    public ResponseEntity getAllInvoices() {
        List<Invoice> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }

    @PostMapping("/add")
    public ResponseEntity addInvoice(@RequestBody @Valid Invoice invoice, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        invoiceService.addInvoice(invoice);
        return ResponseEntity.status(201).body(new ApiResponse("Invoice added successfully"));
    }

    @GetMapping("/get-by-store/{storeId}")
    public ResponseEntity getInvoicesByStoreId(@PathVariable Integer storeId) {
        List<Invoice> invoices = invoiceService.getInvoicesByStoreId(storeId);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/get-by-customer/{customerId}")
    public ResponseEntity getInvoicesByCustomerId(@PathVariable Integer customerId) {
        List<Invoice> invoices = invoiceService.getInvoicesByCustomerId(customerId);
        return ResponseEntity.ok(invoices);
    }



    @DeleteMapping("/delete/{invoiceId}")
    public ResponseEntity deleteInvoice(@PathVariable Integer invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
        return ResponseEntity.ok(new ApiResponse("Invoice deleted successfully"));
    }


    @GetMapping("/get-last-invoice/customer/{customerId}")
    public ResponseEntity getLastInvoiceWithItems(@PathVariable Integer customerId) {
        List invoiceWithItems = invoiceService.getLastInvoiceWithItems(customerId);
        return ResponseEntity.status(200).body(invoiceWithItems);
    }
}
