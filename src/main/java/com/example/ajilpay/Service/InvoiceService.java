package com.example.ajilpay.Service;

import com.example.ajilpay.ApiResponse.ApiException;
import com.example.ajilpay.Model.Customer;
import com.example.ajilpay.Model.Invoice;
import com.example.ajilpay.Model.InvoiceItem;
import com.example.ajilpay.Model.Store;
import com.example.ajilpay.Repository.CustomerRepository;
import com.example.ajilpay.Repository.InvoiceItemRepository;
import com.example.ajilpay.Repository.InvoiceRepository;
import com.example.ajilpay.Repository.StoreRepository;
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
    private final StoreRepository storeRepository;
    private final CustomerRepository customerRepository;

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getInvoicesByStoreId(Integer storeId) {
        return invoiceRepository.findInvoiceByStoreId(storeId);
    }

    public List<Invoice> getInvoicesByCustomerId(Integer customerId) {
        return invoiceRepository.findInvoiceByCustomerId(customerId);
    }

    public void addInvoice(Invoice invoice) {
        invoiceRepository.save(invoice);
    }


    public void updateInvoice(Integer storeId,Integer customerId,Integer invoiceId,Invoice invoice) {
        Store store = storeRepository.findStoreByStoreId(storeId);
        if (store == null) {
            throw new ApiException("Store not found");
        }

        Customer customer = customerRepository.findCustomerByCustomerId(customerId);
        if (customer == null) {
            throw new ApiException("Customer not found");
        }

        if (customer.getStoreId() != storeId) {
            throw new ApiException("Customer is not associated with store");
        }

        Invoice oldInvoice = invoiceRepository.findInvoiceByInvoiceId(invoiceId);
        if (oldInvoice == null) {
            throw new ApiException("Invoice not found");
        }
        oldInvoice.setTotalAmount(invoice.getTotalAmount());
        oldInvoice.setInvoiceId(invoiceId);
        oldInvoice.setCustomerId(customerId);
        invoiceRepository.save(oldInvoice);
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



    private List<InvoiceItem> fetchInvoiceItems(Integer invoiceId) {
        return invoiceItemService.getInvoiceItemsByInvoiceId(invoiceId);
    }


}
