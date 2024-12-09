package com.example.ajilpay.Service;

import com.example.ajilpay.Model.InvoiceItem;
import com.example.ajilpay.Repository.InvoiceItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InvoiceItemService {

    private final InvoiceItemRepository invoiceItemRepository;

    public List<InvoiceItem> getAllInvoiceItems() {
        return invoiceItemRepository.findAll();
    }

    public List<InvoiceItem> getInvoiceItemsByInvoiceId(Integer invoiceId) {
        return invoiceItemRepository.findItemsByInvoiceId(invoiceId);
    }

    public void addInvoiceItem(InvoiceItem invoiceItem) {
        invoiceItemRepository.save(invoiceItem);
    }

    public void deleteInvoiceItem(Integer itemId) {
        invoiceItemRepository.deleteById(itemId);
    }


}
