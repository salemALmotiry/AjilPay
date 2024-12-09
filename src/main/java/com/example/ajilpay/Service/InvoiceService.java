package com.example.ajilpay.Service;

import com.example.ajilpay.ApiResponse.ApiException;
import com.example.ajilpay.Model.Invoice;
import com.example.ajilpay.Model.InvoiceItem;
import com.example.ajilpay.Repository.InvoiceItemRepository;
import com.example.ajilpay.Repository.InvoiceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final InvoiceItemService invoiceItemService;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getInvoicesByStoreId(Integer storeId) {
        return invoiceRepository.findByStoreId(storeId);
    }

    public List<Invoice> getInvoicesByCustomerId(Integer customerId) {
        return invoiceRepository.findInvoiceByCustomerId(customerId);
    }

    public void addInvoice(Invoice invoice) {
        invoiceRepository.save(invoice);
    }


    public void deleteInvoice(Integer invoiceId) {
        invoiceRepository.deleteById(invoiceId);
    }

    public List<Object> getLastInvoiceWithItems(Integer customerId) {
        Invoice lastInvoice = invoiceRepository.findLastInvoiceByCustomerId(customerId);

        if (lastInvoice == null) {
            throw new ApiException("No invoice found for the given customer");
        }

        List<InvoiceItem> invoiceItemsList = fetchInvoiceItems(lastInvoice.getInvoiceId());
        List<Object> invoiceWithItems = new ArrayList<>();

        invoiceWithItems.add(lastInvoice);
        if (!invoiceItemsList.isEmpty()) {
            invoiceWithItems.add(invoiceItemsList);
        }

        return invoiceWithItems;
    }

    public LocalDateTime getFistInvoice(Integer customerId) {
        return invoiceRepository.findFirstInvoiceByCustomerId(customerId);
    }

    private List<InvoiceItem> fetchInvoiceItems(Integer invoiceId) {
        return invoiceItemService.getInvoiceItemsByInvoiceId(invoiceId);
    }

    public  List<Invoice> getInvoicesForCustomerInDateRange(Integer customerId, LocalDateTime startDate, LocalDateTime endDate){

        return invoiceRepository.findInvoicesForCustomerInDateRange(customerId,startDate,endDate);
    }

    public double getAverageInvoiceAmountForCustomer(Integer customerId) {
        List<Invoice> invoices = invoiceRepository.findInvoiceByCustomerId(customerId);

        if (invoices.isEmpty()) {
            return 0.0;
        }

        double totalAmount = 0.0;
        for (Invoice invoice : invoices) {
            totalAmount += invoice.getTotalAmount();
        }

        return totalAmount / invoices.size();
    }


}
