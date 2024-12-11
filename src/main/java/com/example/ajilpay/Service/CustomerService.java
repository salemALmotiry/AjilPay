package com.example.ajilpay.Service;

import com.example.ajilpay.ApiResponse.ApiException;
import com.example.ajilpay.DTO.*;
import com.example.ajilpay.Model.*;
import com.example.ajilpay.Repository.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final InvoiceRepository invoiceRepository;
    private final MonthlyPaymentRepository monthlyPaymentRepository;
    private final InvoiceItemRepository invoiceItemRepository;
    private final StoreRepository storeRepository;
    private final StoreService storeService;


    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }


    public void checkoutCustomer(Integer customerId) {

        Customer customer = customerRepository.findCustomerByCustomerId(customerId);
        if (customer == null) {
            throw new ApiException("Customer not found");
        }
        if (customer.getStoreId() == null) {
            throw new ApiException("Customer is not associated with any store.");
        }
        Store store = storeRepository.findStoreByStoreId(customer.getStoreId());

        if (!store.getStoreId().equals(customer.getStoreId())) {
            throw new ApiException("Store is not associated with any store.");
        }

        Integer storeId = customer.getStoreId();

        storeService.createMonthlyPayment(storeId, customerId);
        List<MonthlyPayment> payments = monthlyPaymentRepository.findMonthlyPaymentByStoreIdAndCustomerId(storeId, customerId);

        double totalOutstandingBalance = 0.0;

        for (MonthlyPayment payment : payments) {
            if (!"PAID".equals(payment.getPaymentStatus())) {
                totalOutstandingBalance += payment.getTotalDue() - payment.getTotalPaid();
            }
        }
        if (totalOutstandingBalance > 0) {
            throw new ApiException("Customer has outstanding payments. Total due: " + totalOutstandingBalance);
        }


        boolean hasUnpaidPayments = false;
        for (MonthlyPayment payment : payments) {
            if (!"PAID".equals(payment.getPaymentStatus())) {
                hasUnpaidPayments = true;
                break;
            }
        }
        if (hasUnpaidPayments) {
            throw new ApiException("Customer still has unpaid or partial payments.");
        }


        customer.setStoreId(null);
        customerRepository.save(customer);
    }
    public void addCustomer(Customer customer) {
        Store store = storeRepository.findStoreByStoreId(customer.getStoreId());
        if (store == null) {
            throw new ApiException("Store not found");
        }
        customerRepository.save(customer);
    }

    //___________________ summary of daily invoices for a customer____________________

    public List<InvoiceSummaryDTO> getInvoiceSummaryForCustomer(Integer customerId) {

        Customer customer = customerRepository.findCustomerByCustomerId(customerId);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }
        List<Invoice> invoices = invoiceRepository.findInvoiceByCustomerId(customerId);

        if (invoices == null) {
            throw new RuntimeException("Invoices not found");
        }
        List<InvoiceSummaryDTO> invoiceSummaries = new ArrayList<>();

        for (Invoice invoice : invoices) {
            double totalAmount = invoice.getTotalAmount();
            double totalPaid = 0;
            double outstandingAmount = totalAmount;


            MonthlyPayment monthlyPayment = monthlyPaymentRepository.findMonthlyPaymentByCustomerIdAndMonth(customerId, invoice.getCreatedAt().withDayOfMonth(1).toLocalDate());
            String paymentStatus = "PENDING";
            if (monthlyPayment != null) {
                paymentStatus = monthlyPayment.getPaymentStatus();
                totalPaid = monthlyPayment.getTotalPaid();
                outstandingAmount = monthlyPayment.getTotalDue() - totalPaid; // Calculate outstanding
            }


            InvoiceSummaryDTO summary = new InvoiceSummaryDTO(
                    invoice.getInvoiceId(),
                    totalAmount,
                    outstandingAmount,
                    invoice.getCreatedAt(),
                    paymentStatus
            );
            invoiceSummaries.add(summary);
        }

        return invoiceSummaries;
    }


    //_________________________ add item to invoice______________________________
    public void addItemToInvoice(Integer invoiceId, InvoiceItem invoiceItem) {
        Invoice invoice = invoiceRepository.findInvoiceByInvoiceId(invoiceId);

        if (invoice == null) {
            throw new RuntimeException("Invoice not found");
        }

        List<InvoiceItem> invoiceItems1 = invoiceItemRepository.findInvoiceItemByInvoiceId(invoice.getCustomerId());
        MonthlyPayment monthlyPayment = monthlyPaymentRepository.findMonthlyPaymentByCustomerIdAndMonth(invoice.getCustomerId(), invoice.getCreatedAt().withDayOfMonth(1).toLocalDate());


        double totalAmountPaid = 0;
        for (InvoiceItem invoiceItem1 : invoiceItems1) {
            if (invoiceItem1.getItemName().equals(invoiceItem.getItemName())) {
                throw new RuntimeException("Item already exists in the invoice");
            }
            totalAmountPaid += invoiceItem1.getSubtotal();

        }

        if ((totalAmountPaid+(invoiceItem.getPricePerUnit()*invoiceItem.getQuantity())) >monthlyPayment.getTotalDue()){
            throw new RuntimeException("Total paid exceeds the due amount");
        }
        // Save the new invoice item
        invoiceItem.setInvoiceId(invoiceId);  // Link the item to the invoice
        invoiceItem.setSubtotal(invoiceItem.getPricePerUnit() * invoiceItem.getQuantity());
        invoiceItemRepository.save(invoiceItem);


    }

    //__________________________________get unusual items______________________________________________

    public List getUnusualItems(Integer customerId) {

        List<Invoice> invoices = invoiceRepository.findInvoiceByCustomerId(customerId);

        List<InvoiceItem> invoiceItems = new ArrayList<>();
        for (Invoice invoice : invoices) {
            invoiceItems.addAll(invoiceItemRepository.findInvoiceItemByInvoiceId(invoice.getInvoiceId()));
        }


        // frequency of each item purchased
        Map<String, Integer> itemPurchaseFrequency = new HashMap<>();
        for (InvoiceItem item : invoiceItems) {
            itemPurchaseFrequency.put(item.getItemName(), itemPurchaseFrequency.getOrDefault(item.getItemName(), 0) + 1);
        }


        int threshold = 0; // 0 for testing

        // Identify unusual items
        List unusualItems = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : itemPurchaseFrequency.entrySet()) {
            if (entry.getValue() > threshold) {
                // Retrieve the last purchase date of the item
                LocalDate lastPurchased = getLastPurchasedDate(entry.getKey(), customerId);
                unusualItems.add(new UnusualItemDTO(entry.getKey(), entry.getValue(), lastPurchased));
            }
        }

        return unusualItems;
    }

    private LocalDate getLastPurchasedDate(String itemName, Integer customerId) {
        InvoiceItem lastPurchasedItem = invoiceItemRepository.findFirstByItemNameAndInvoiceIdOrderByCreatedAtDesc(itemName, customerId);
        return lastPurchasedItem != null ? lastPurchasedItem.getCreatedAt().toLocalDate() : LocalDate.now();
    }

    //___________________________________________end getUnusualItems______________________________

    //___________________________________________ forecastCustomerInvoices _______________________

    public InvoicesForecastDTO forecastCustomerInvoices(Integer customerId, Integer monthsAhead) {

        Customer customer = customerRepository.findCustomerByCustomerId(customerId);
        if (customer == null) {
            throw new ApiException("Customer not found");
        }

        //Fetch historical invoices data for the past 12 months
        List<Invoice> invoices = invoiceRepository.findInvoicesByCustomerIdAfterAndCreatedAtBetween(
                customerId,
                LocalDate.now().minusMonths(12).atStartOfDay(),
                LocalDate.now().atStartOfDay()
        );

        if (invoices.isEmpty()) {
            throw new ApiException("No invoices data available for the past year.");
        }

        //Analyze past invoices
        double averageMonthlyInvoices = calculateAverageMonthlyInvoices(invoices);
        List<Double> pastRevenues = calculateMonthlyInvoices(invoices);

        //Apply seasonality adjustments
        Map<Month, Double> seasonality = getSeasonalityAdjustment(invoices);

        //Calculate standard deviation
        //https://www.youtube.com/watch?v=J3SuIC0HLxI
        //https://www.programiz.com/java-programming/examples/standard-deviation

        double standardDeviation = calculateStandardDeviation(pastRevenues);

        // forecast for the next months
        List<ForecastedMonthDTO> forecastedMonths = new ArrayList<>();
        for (int i = 1; i <= monthsAhead; i++) {
            Month forecastMonth = LocalDate.now().plusMonths(i).getMonth();
            double seasonalityFactor = seasonality.getOrDefault(forecastMonth, 1.0);
            double forecastedRevenue = averageMonthlyInvoices * seasonalityFactor;

            // Confidence calculation based on standard deviation and forecasted
            double confidence = 100 - (standardDeviation / forecastedRevenue * 100);
            confidence = Math.max(50, Math.min(confidence, 95)); // Ensure confidence is between 50% and 95%

            forecastedMonths.add(new ForecastedMonthDTO(forecastMonth, forecastedRevenue, (int) confidence));
        }

        return new InvoicesForecastDTO(customerId, forecastedMonths);
    }

    private double calculateAverageMonthlyInvoices(List<Invoice> invoices) {
        Map<Month, Double> monthlyinvoices = new HashMap<>();

        for (Invoice invoice : invoices) {
            Month month = invoice.getCreatedAt().getMonth();
            monthlyinvoices.put(month, monthlyinvoices.getOrDefault(month, 0.0) + invoice.getTotalAmount());
        }

        return monthlyinvoices.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private List<Double> calculateMonthlyInvoices(List<Invoice> invoices) {
        Map<Month, Double> monthlyInvoices = new HashMap<>();

        for (Invoice invoice : invoices) {
            Month month = invoice.getCreatedAt().getMonth();
            monthlyInvoices.put(month, monthlyInvoices.getOrDefault(month, 0.0) + invoice.getTotalAmount());
        }

        return new ArrayList<>(monthlyInvoices.values());
    }

    private Map<Month, Double> getSeasonalityAdjustment(List<Invoice> invoices) {
        Map<Month, Double> monthlyInvoices = new HashMap<>();
        Map<Month, Integer> monthlyCount = new HashMap<>();


        for (Invoice invoice : invoices) {
            Month month = invoice.getCreatedAt().getMonth();
            monthlyInvoices.put(month, monthlyInvoices.getOrDefault(month, 0.0) + invoice.getTotalAmount());
            monthlyCount.put(month, monthlyCount.getOrDefault(month, 0) + 1);
        }

        // Calculate seasonality as an adjustment factor
        double averageMonthlyInvoices = monthlyInvoices.values().stream().mapToDouble(Double::doubleValue).average().orElse(1.0);
        Map<Month, Double> seasonalityFactors = new HashMap<>();
        for (Month month : monthlyInvoices.keySet()) {
            double averageForMonth = monthlyInvoices.get(month) / monthlyCount.get(month);
            seasonalityFactors.put(month, averageForMonth / averageMonthlyInvoices);
        }

        return seasonalityFactors;
    }

    private double calculateStandardDeviation(List<Double> revenues) {
        if (revenues.isEmpty()) {
            return 0.0;
        }

        double sum = 0.0;
        for (double revenue : revenues) {
            sum += revenue;
        }
        double mean = revenues.size() > 0 ? sum / revenues.size() : 0.0;

        double varianceSum = 0.0;
        for (double revenue : revenues) {
            varianceSum += Math.pow(revenue - mean, 2);
        }
        double variance = revenues.size() > 0 ? varianceSum / revenues.size() : 0.0;

        return Math.sqrt(variance);
    }
    //_________________________________________end generateinvoicesForecast__________________________________________________

    public List<InvoiceItemPriceChangeDTO> getPriceChangesForCustomer(Integer customerId) {
        List<Invoice> invoices = invoiceRepository.findInvoiceByCustomerId(customerId);

        if (invoices.isEmpty()) {
            throw new ApiException("No customer");
        }
        List<InvoiceItemPriceChangeDTO> priceChanges = new ArrayList<>();

        // Loop through the invoices to get all items and compare prices
        for (Invoice invoice : invoices) {

            for (InvoiceItem item : invoiceItemRepository.findItemsByInvoiceId(invoice.getInvoiceId())) {
                InvoiceItem originalItem = invoiceItemRepository.findFirstByItemNameOrderByAppliedAtAsc(item.getItemName());

                if (originalItem != null && !item.getPricePerUnit().equals(originalItem.getPricePerUnit())) {
                    System.out.println(originalItem);
                    double priceDifference = item.getPricePerUnit() - originalItem.getPricePerUnit();
                    double percentageChange = (priceDifference / originalItem.getPricePerUnit()) * 100;

                    double formattedPercentageChange = Double.parseDouble(String.format("%.2f", percentageChange));
                    priceChanges.add(new InvoiceItemPriceChangeDTO(
                            item.getItemId(),
                            item.getItemName(),
                            originalItem.getPricePerUnit(),
                            item.getPricePerUnit(),
                            priceDifference,
                            formattedPercentageChange
                    ));
                }
            }
        }

        return priceChanges;
    }




}


