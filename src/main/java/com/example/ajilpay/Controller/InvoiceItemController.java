package com.example.ajilpay.Controller;

import com.example.ajilpay.ApiResponse.ApiResponse;
import com.example.ajilpay.Model.InvoiceItem;
import com.example.ajilpay.Service.InvoiceItemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/invoice-items")
public class InvoiceItemController {

    private final InvoiceItemService invoiceItemService;

    @GetMapping("/get-all")
    public ResponseEntity getAllInvoiceItems() {
        List<InvoiceItem> invoiceItems = invoiceItemService.getAllInvoiceItems();
        return ResponseEntity.ok(invoiceItems);
    }

    @PostMapping("/add")
    public ResponseEntity addInvoiceItem(@RequestBody @Valid InvoiceItem invoiceItem, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        invoiceItemService.addInvoiceItem(invoiceItem);
        return ResponseEntity.status(201).body(new ApiResponse("Invoice Item added successfully"));
    }

    @GetMapping("/get-by-invoice/{invoiceId}")
    public ResponseEntity getInvoiceItemsByInvoiceId(@PathVariable Integer invoiceId) {
        List<InvoiceItem> invoiceItems = invoiceItemService.getInvoiceItemsByInvoiceId(invoiceId);
        return ResponseEntity.ok(invoiceItems);
    }

    @DeleteMapping("/delete/{itemId}")
    public ResponseEntity deleteInvoiceItem(@PathVariable Integer itemId) {
        invoiceItemService.deleteInvoiceItem(itemId);
        return ResponseEntity.ok(new ApiResponse("Invoice Item deleted successfully"));
    }
}
