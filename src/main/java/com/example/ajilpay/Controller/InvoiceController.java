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
        return ResponseEntity.status(200).body(invoices);
    }

    @PostMapping("/add")
    public ResponseEntity addInvoice(@RequestBody @Valid Invoice invoice) {

        invoiceService.addInvoice(invoice);
        return ResponseEntity.status(200).body(new ApiResponse("Invoice added successfully"));
    }

    @PutMapping("/{storeId}/{customerId}/{invoiceId}")
    public ResponseEntity updateInvoice(
            @PathVariable Integer storeId,
            @PathVariable Integer customerId,
            @PathVariable Integer invoiceId,
            @RequestBody @Valid Invoice invoice
    ) {
        invoiceService.updateInvoice(storeId, customerId, invoiceId, invoice);
        return ResponseEntity.status(200).body(new ApiResponse("Invoice updated successfully"));
    }





    @DeleteMapping("/delete/{invoiceId}")
    public ResponseEntity deleteInvoice(@PathVariable Integer invoiceId) {
        invoiceService.deleteInvoice(invoiceId);
        return ResponseEntity.status (200).body( new ApiResponse("Invoice deleted successfully"));
    }


    @GetMapping("/get-last-invoice/customer/{customerId}")
    public ResponseEntity getLastInvoiceWithItems(@PathVariable Integer customerId) {
        List invoiceWithItems = invoiceService.getLastInvoiceWithItems(customerId);
        return ResponseEntity.status(200).body(invoiceWithItems);
    }
}
