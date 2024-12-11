package com.example.ajilpay.Service;
import com.example.ajilpay.ApiResponse.ApiException;
import com.example.ajilpay.DTO.*;
import com.example.ajilpay.Model.*;

import com.example.ajilpay.Repository.*;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

@Service
@AllArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final CustomerRepository customerRepository;
    private final MonthlyPaymentRepository monthlyPaymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;

    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }


    public void addStore(Store store) {
        Store store1 = storeRepository.findStoreByStoreId(store.getStoreId());
        if (store1 != null) {
            throw new ApiException("Store already exists");
        }

        storeRepository.save(store);
    }

    //_______________________________________________________




    public void createMonthlyPayment(Integer storeId, Integer customerId) {
        Store store = storeRepository.findStoreByStoreId(storeId);
        if (store == null) {
            throw new ApiException("Store not found");
        }


        Customer customer = customerRepository.findCustomerByCustomerId(customerId);


        if (customer == null) {
            throw new ApiException("Customer not found");
        }
        if (!store.getStoreId().equals(customer.getStoreId())){
            throw new ApiException("Customer not found in the specified store.");
        }

        LocalDate currentDate = LocalDate.now();

        LocalDate lastGenerationDate = monthlyPaymentRepository.getLastMonthlyPaymentDateForCustomer(storeId, customerId);


        // Initialize with the first invoice date if no previous payments exist
        if (lastGenerationDate == null) {
            LocalDate firstInvoiceDate = invoiceRepository.findFirstInvoiceByCustomerId(customerId).toLocalDate().withDayOfMonth(1);
            if (firstInvoiceDate == null) {
                throw new ApiException("No invoices found for this customer.");
            }
            lastGenerationDate = firstInvoiceDate.withDayOfMonth(1); // Start at the beginning of the first invoice month
        }

        // Iterate over each month until the current month
        while (!lastGenerationDate.isAfter(currentDate.withDayOfMonth(1))) {
            List<Invoice> invoices = invoiceRepository.findInvoicesForCustomerInDateRange(
                    customerId,
                    lastGenerationDate.atStartOfDay(),
                    lastGenerationDate.plusMonths(1).atStartOfDay()
            );


            // If invoices exist, calculate and save the monthly payment
            if (!invoices.isEmpty()) {
                double totalDue = 0.0;

                for (Invoice invoice : invoices) {
                    totalDue += invoice.getTotalAmount();
                }

                // Check for an existing payment for this month
                if (monthlyPaymentRepository.existsMonthlyPaymentByCustomerIdAndStoreIdAndMonth(customerId, storeId, lastGenerationDate)) {
                    lastGenerationDate = lastGenerationDate.plusMonths(1); // Skip to the next month
                    continue;
                }

                // Create and save the monthly payment
                MonthlyPayment monthlyPayment = new MonthlyPayment();
                monthlyPayment.setCustomerId(customerId);
                monthlyPayment.setStoreId(storeId);
                monthlyPayment.setMonth(lastGenerationDate);
                monthlyPayment.setTotalDue(totalDue);
                monthlyPayment.setTotalPaid(0.0);
                monthlyPayment.setPaymentStatus("PENDING");
                monthlyPayment.setCreatedAt(LocalDateTime.now());

                monthlyPaymentRepository.save(monthlyPayment);
            }

            // Move to the next month
            lastGenerationDate = lastGenerationDate.plusMonths(1);
        }
    }

    public String processCustomerPayment(Integer storeId, Integer customerId, double paymentAmount) {
        Store store = storeRepository.findStoreByStoreId(storeId);
        if (store == null) {
            throw new ApiException("Store not found");
        }

        Customer customer = customerRepository.findCustomerByCustomerId(customerId);
        if (customer == null) {
            throw new ApiException("Customer not found");
        }

        // Fetch all unpaid and partial monthly payments for the customer, sorted by the oldest first
        List<MonthlyPayment> monthlyPayments = monthlyPaymentRepository.findMonthlyPaymentByPendingAndPartial(customerId, storeId);

        if (monthlyPayments.isEmpty()) {
            throw new ApiException("No unpaid and partial monthly payments found for the customer");
        }
        double remainingAmount = paymentAmount;

        for (MonthlyPayment payment : monthlyPayments) {
            if (remainingAmount == 0) break;

            // Calculate the total due for the month
            double totalDue = payment.getTotalDue() - payment.getTotalPaid();
            double appliedAmount;

            if (totalDue > 0) {
                if (remainingAmount >= totalDue) {
                    // Fully pay this month's due
                    appliedAmount = totalDue;
                    payment.setTotalPaid(payment.getTotalDue());
                    payment.setPaymentStatus("PAID");
                    remainingAmount -= totalDue;
                } else {
                    // Partially pay this month's due
                    appliedAmount = remainingAmount;
                    payment.setTotalPaid(payment.getTotalPaid() + remainingAmount);
                    payment.setPaymentStatus("PARTIAL");
                    remainingAmount = 0;
                }

                // Check if a payment history for this monthly payment exists
                PaymentHistory existingHistory = paymentHistoryRepository.findByMonthlyPaymentId(payment.getPaymentId());
                if (existingHistory != null) {
                    // Update the existing payment history
                    existingHistory.setAmountApplied(existingHistory.getAmountApplied() + appliedAmount);
                    existingHistory.setAppliedAt(LocalDateTime.now());
                    paymentHistoryRepository.save(existingHistory);
                } else {
                    // Create a new payment history record
                    PaymentHistory paymentHistory = new PaymentHistory();
                    paymentHistory.setMonthlyPaymentId(payment.getPaymentId());
                    paymentHistory.setCustomerId(customerId);
                    paymentHistory.setAmountApplied(appliedAmount);
                    paymentHistory.setAppliedAt(LocalDateTime.now());
                    paymentHistoryRepository.save(paymentHistory);
                }
            }
        }

        if (remainingAmount > 0) {
            return "Payment applied successfully, but there's excess money of " + remainingAmount + " to be returned to the customer.";
        }

        return "Payment applied successfully!";
    }


    public CustomerOverViewDTO getCustomerOverView(Integer storeId, Integer customerId) {
        Store store = storeRepository.findStoreByStoreId(storeId);
        if (store == null) {
            throw new ApiException("Store not found");
        }

        Customer customer = customerRepository.findCustomerByCustomerId(customerId);

        if (customer == null) {
            throw new ApiException("Customer not found");
        }
        if (!store.getStoreId().equals(customer.getStoreId())){
            throw new ApiException("Customer not found in the specified store.");
        }

        List<MonthlyPayment> payments = monthlyPaymentRepository.findMonthlyPaymentByCustomerId(customerId);
        int totalPaid = 0;
        int totalDue = 0;
        int partialPaymentCount = 0;
        int fullPaymentsCount = 0;

        for (MonthlyPayment payment : payments) {
            totalPaid += payment.getTotalPaid();
            totalDue += payment.getTotalDue();
            if (payment.getPaymentStatus().equals("PARTIAL")) {
                partialPaymentCount++;
            } else if (payment.getPaymentStatus().equals("PAID")) {
                fullPaymentsCount++;
            }
        }

        int outstandingBalance = totalDue - totalPaid;
        double paymentCompletionRate = totalDue > 0 ? (fullPaymentsCount * 100.0) / payments.size() : 0;

        return new CustomerOverViewDTO(totalPaid, totalDue, outstandingBalance, paymentCompletionRate, partialPaymentCount);
    }


    public PaymentBehaviorOverViewDTO analyzePaymentBehavior(Integer storeId, Integer customerId) {

        Store store = storeRepository.findStoreByStoreId(storeId);
        if (store == null) {
            throw new ApiException("Store not found");
        }

        Customer customer = customerRepository.findCustomerByCustomerId(customerId);

        if (customer == null) {
            throw new ApiException("Customer not found");
        }
        if (!store.getStoreId().equals(customer.getStoreId())){
            throw new ApiException("Customer not found in the specified store.");
        }

        List<MonthlyPayment> payments = monthlyPaymentRepository.findMonthlyPaymentByStoreIdAndCustomerId(storeId,customerId);
        double totalPayments = 0;
        double totalMonths = payments.size();
        double latePayments = 0;
        double longestGap = 0;
        double currentGap = 0;
        double lastPaymentAmount = 0;
        double increaseInPayments = 0;

        for (int i = 0; i < payments.size(); i++) {
            MonthlyPayment payment = payments.get(i);
            totalPayments += payment.getTotalPaid();

            // Track late payments
            if (payment.getCreatedAt().isAfter(payment.getMonth().atStartOfDay())) {
                latePayments++;
            }

            // Check for increases in payments
            if (i > 0 && payment.getTotalPaid() > lastPaymentAmount) {
                increaseInPayments += payment.getTotalPaid() - lastPaymentAmount;
            }
            lastPaymentAmount = payment.getTotalPaid();

            // Calculate gaps in payments
            if (payment.getTotalPaid() == 0) {
                currentGap++;
                longestGap = Math.max(longestGap, currentGap);
            } else {
                currentGap = 0;
            }
        }

        double averageMonthlyPayment = totalMonths > 0 ? totalPayments / totalMonths : 0;

        return new PaymentBehaviorOverViewDTO(averageMonthlyPayment, latePayments, increaseInPayments, payments.size(), longestGap);
    }

    public CombinedPaymentSummary getCombinedPaymentSummary(Integer storeId,Integer customerId) {

        Store store = storeRepository.findStoreByStoreId(storeId);
        if (store == null) {
            throw new ApiException("Store not found");
        }

        Customer customer = customerRepository.findCustomerByCustomerId(customerId);

        if (customer == null) {
            throw new ApiException("Customer not found");
        }
        if (!store.getStoreId().equals(customer.getStoreId())){
            throw new ApiException("Customer not found in the specified store.");
        }

        CustomerOverViewDTO customerOverView = getCustomerOverView(storeId,customerId);
        PaymentBehaviorOverViewDTO behaviorOverView = analyzePaymentBehavior(storeId,customerId);

        return new CombinedPaymentSummary(customerOverView, behaviorOverView);
    }

   public StoreDashboardInsightsDTO getStoreInsights(Integer storeId) {

            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime endOfMonth = LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth()).withHour(23).withMinute(59).withSecond(59);

            List<Invoice> invoices = invoiceRepository.findInvoicesByStoreIdAndCreatedAtBetween(storeId, startOfMonth, endOfMonth);


            double totalRevenue = 0;
            double totalInvoiceAmount = 0;
            int totalInvoices = invoices.size();
            Map<Integer, Double> customerSpending = new HashMap<>();
            int[] hourCount = new int[24];
            Set<Integer> repeatCustomers = new HashSet<>();

            // Calculate total revenue, average invoice amount, and customer spending
            for (Invoice invoice : invoices) {
                double invoiceAmount = invoice.getTotalAmount();
                totalRevenue += invoiceAmount;

                // Calculate customer spending
                int customerId = invoice.getCustomerId();
                customerSpending.put(customerId, customerSpending.getOrDefault(customerId, 0.0) + invoiceAmount);

                // Track the hour the invoice was created
                int hour = invoice.getCreatedAt().getHour();
                hourCount[hour]++;

                // Track repeat customers (customers who made more than one purchase)
                repeatCustomers.add(customerId);
            }

            // Calculate average invoice amount
            double averageInvoiceAmount = totalInvoices > 0 ? totalRevenue / totalInvoices : 0;

            // Find top spending customer
            int topCustomerId = -1;
            double maxSpending = 0;
            for (Map.Entry<Integer, Double> entry : customerSpending.entrySet()) {
                if (entry.getValue() > maxSpending) {
                    maxSpending = entry.getValue();
                    topCustomerId = entry.getKey();
                }
            }
            String topCustomer = topCustomerId != -1 ? customerRepository.findCustomerByCustomerId(topCustomerId).getUsername() : "N/A";

            // Find peak activity period (most invoices created in one hour)
            int peakHour = -1;
            int maxInvoicesInHour = 0;
            for (int i = 0; i < 24; i++) {
                if (hourCount[i] > maxInvoicesInHour) {
                    maxInvoicesInHour = hourCount[i];
                    peakHour = i;
                }
            }

            // Calculate repeat customer rate
            double repeatCustomerRate = 0;
            if (totalInvoices > 0) {
                repeatCustomerRate = (double) repeatCustomers.size() / totalInvoices * 100;
            }

            System.out.println(totalRevenue);
            // Return insights
            return new StoreDashboardInsightsDTO(
                    totalRevenue,
                    averageInvoiceAmount,
                    topCustomer,
                    peakHour,
                    repeatCustomerRate
            );
        }

    public List<Map<String, Object>> getTopSellingItems(Integer storeId, int limit) {
        List<InvoiceItem> items = invoiceItemRepository.findInvoiceItemsByStoreId(storeId);

        if (items.isEmpty()) {
            throw new ApiException("No sales data found for the store");
        }


        Map<String, Map<String, Object>> itemDataMap = new HashMap<>();

        for (InvoiceItem item : items) {
            String itemName = item.getItemName();

            if (!itemDataMap.containsKey(itemName)) {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("itemName", itemName);
                itemData.put("totalQuantity", 0);
                itemData.put("totalRevenue", 0.0);
                itemDataMap.put(itemName, itemData);
            }

            Map<String, Object> itemData = itemDataMap.get(itemName);
            itemData.put("totalQuantity", (Integer) itemData.get("totalQuantity") + item.getQuantity());
            itemData.put("totalRevenue", (Double) itemData.get("totalRevenue") + item.getSubtotal());
        }

        List<Map<String, Object>> sortedItems = new ArrayList<>(itemDataMap.values());

        for (int i = 0; i < sortedItems.size() - 1; i++) {
            for (int j = i + 1; j < sortedItems.size(); j++) {
                if ((Integer) sortedItems.get(i).get("totalQuantity") < (Integer) sortedItems.get(j).get("totalQuantity")) {
                    Map<String, Object> temp = sortedItems.get(i);
                    sortedItems.set(i, sortedItems.get(j));
                    sortedItems.set(j, temp);
                }
            }
        }
        return sortedItems.stream().limit(limit).toList();
    }

    public Map<String, Object> evaluateCustomerTrustworthiness(Integer customerId, Integer storeId) {

        Customer customer = customerRepository.findCustomerByCustomerId(customerId);
        if (customer == null) {
            throw new ApiException("Customer not found");
        }

        Store store = storeRepository.findStoreByStoreId(storeId);

        if (store == null) {
            throw new ApiException("Store not found");
        }
        if (store.getStoreId().equals(customer.getStoreId())){
            throw new ApiException("Customer already trustworthiness");
        }

        // Fetch Payment History for the Customer
        List<PaymentHistory> paymentHistories = paymentHistoryRepository.findPaymentHistoriesByCustomerId(customerId);

        // Calculate Payment Punctuality Score
        long onTimePayments = 0;
        for (PaymentHistory history : paymentHistories) {
            MonthlyPayment payment = monthlyPaymentRepository.findMonthlyPaymentByMonthlyPaymentId(history.getMonthlyPaymentId());

            LocalDate paymentDueDate = payment.getMonth().plusMonths(1);
            if (history.getAppliedAt().isBefore(paymentDueDate.atStartOfDay())) {
                onTimePayments++;
            }
        }
        long totalPayments = paymentHistories.size();
        double paymentPunctualityScore = totalPayments == 0 ? 0 : (onTimePayments / (double) totalPayments) * 50;

        double totalOutstandingBalance = 0.0;
        List<MonthlyPayment> monthlyPayments = monthlyPaymentRepository.findMonthlyPaymentByCustomerIdAndPaymentStatus(customerId,"PENDING");

        for (MonthlyPayment payment : monthlyPayments) {
            totalOutstandingBalance += payment.getTotalDue() - payment.getTotalPaid();
        }


        List<Invoice> invoices = invoiceRepository.findInvoiceByCustomerId(customerId);

        double averageInvoiceAmount = 0.0;

        if (invoices.isEmpty()) {
            averageInvoiceAmount = 0.0;
        }else {
            double totalAmount = 0.0;
            for (Invoice invoice : invoices) {
                totalAmount += invoice.getTotalAmount();
            }
            averageInvoiceAmount = totalAmount / invoices.size();

        }
        double outstandingThreshold = averageInvoiceAmount * 2; // 2 times the average invoice amount


        double creditUtilizationScore = 0;
        if (totalOutstandingBalance > outstandingThreshold) {
            creditUtilizationScore = 0;  // No credit left, so the score is 0
        } else {
            creditUtilizationScore = (1 - (totalOutstandingBalance / outstandingThreshold)) * 50;
        }

        //Calculate the Credit Score
        double creditScore = 50 - (creditUtilizationScore / 2);

        if (creditScore<0){
            creditScore = 0;
        }

        // Combine Scores and Generate Recommendation
        double totalScore = paymentPunctualityScore + creditScore;
        String recommendation = totalScore >= 70 ? "Trustworthy" : "Not Trustworthy";

        // Build Evaluation Result
        Map<String, Object> evaluationResult = new HashMap<>();
        evaluationResult.put("customerId", customerId);
        evaluationResult.put("newStoreId", store.getStoreName());
        evaluationResult.put("trustScore", totalScore);
        evaluationResult.put("recommendation", recommendation);
        evaluationResult.put("details", Map.of(
                "onTimePayments", onTimePayments,
                "totalPayments", totalPayments,
                "totalOutstandingBalance", totalOutstandingBalance,
                "creditUtilization", creditUtilizationScore,
                "paymentPunctualityScore", paymentPunctualityScore,
                "creditScore", creditScore
        ));

        return evaluationResult;
    }

    //_____________________________________Generate Sales  Forecast____________________________________________
    public SalesForecastDTO forecastStoreSales(Integer storeId, Integer monthsAhead) {

        Store store = storeRepository.findStoreByStoreId(storeId);
        if (store == null) {
            throw new ApiException("Store not found");
        }

        //Fetch historical sales data for the past 12 months
        List<Invoice> invoices = invoiceRepository.findInvoicesByStoreIdAndCreatedAtBetween(
                storeId,
                LocalDate.now().minusMonths(12).atStartOfDay(),
                LocalDate.now().atStartOfDay()
        );

        if (invoices.isEmpty()) {
            throw new ApiException("No sales data available for the past year.");
        }

        //Analyze past sales trends
        double averageMonthlySales = calculateAverageMonthlySales(invoices);
        List<Double> pastRevenues = calculateMonthlyRevenues(invoices);

        //Apply seasonality adjustments
        Map<Month, Double> seasonality = getSeasonalityAdjustment(invoices);

        //Calculate standard deviation
        //https://www.youtube.com/watch?v=J3SuIC0HLxI
        //https://www.programiz.com/java-programming/examples/standard-deviation
        double standardDeviation = calculateStandardDeviation(pastRevenues);

        //forecast for the next months
        List<ForecastedMonthDTO> forecastedMonths = new ArrayList<>();
        for (int i = 1; i <= monthsAhead; i++) {
            Month forecastMonth = LocalDate.now().plusMonths(i).getMonth();
            double seasonalityFactor = seasonality.getOrDefault(forecastMonth, 1.0);
            double forecastedRevenue = averageMonthlySales * seasonalityFactor;

            // Confidence calculation based on standard deviation and forecasted revenue
            double confidence = 100 - (standardDeviation / forecastedRevenue * 100);
            confidence = Math.max(50, Math.min(confidence, 95)); // Ensure confidence is between 50% and 95%

            forecastedMonths.add(new ForecastedMonthDTO(forecastMonth, forecastedRevenue, (int) confidence));
        }

        return new SalesForecastDTO(storeId, forecastedMonths);
    }

    private double calculateAverageMonthlySales(List<Invoice> invoices) {
        Map<Month, Double> monthlySales = new HashMap<>();

        for (Invoice invoice : invoices) {
            Month month = invoice.getCreatedAt().getMonth();
            monthlySales.put(month, monthlySales.getOrDefault(month, 0.0) + invoice.getTotalAmount());
        }

        return monthlySales.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private List<Double> calculateMonthlyRevenues(List<Invoice> invoices) {
        Map<Month, Double> monthlyRevenues = new HashMap<>();

        for (Invoice invoice : invoices) {
            Month month = invoice.getCreatedAt().getMonth();
            monthlyRevenues.put(month, monthlyRevenues.getOrDefault(month, 0.0) + invoice.getTotalAmount());
        }

        return new ArrayList<>(monthlyRevenues.values());
    }

    private Map<Month, Double> getSeasonalityAdjustment(List<Invoice> invoices) {
        Map<Month, Double> monthlySales = new HashMap<>();
        Map<Month, Integer> monthlyCount = new HashMap<>();

        // Aggregate monthly sales
        for (Invoice invoice : invoices) {
            Month month = invoice.getCreatedAt().getMonth();
            monthlySales.put(month, monthlySales.getOrDefault(month, 0.0) + invoice.getTotalAmount());
            monthlyCount.put(month, monthlyCount.getOrDefault(month, 0) + 1);
        }

        // Calculate seasonality as an adjustment factor (relative to the average monthly sales)
        double averageMonthlySales = monthlySales.values().stream().mapToDouble(Double::doubleValue).average().orElse(1.0);
        Map<Month, Double> seasonalityFactors = new HashMap<>();
        for (Month month : monthlySales.keySet()) {
            double averageForMonth = monthlySales.get(month) / monthlyCount.get(month);
            seasonalityFactors.put(month, averageForMonth / averageMonthlySales);
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

    //_____________________________Detect Suspicious Activity___________________________

    public Map<String, Object> detectSuspiciousActivity(Integer storeId) {
        Store store = storeRepository.findStoreByStoreId(storeId);
        if (store == null) {
            throw new ApiException("Store not found");
        }

        List<Invoice> invoices = invoiceRepository.findInvoiceByStoreId(storeId);
        List<InvoiceItem> allItems = new ArrayList<>();
        for (Invoice invoice : invoices) {
            List<InvoiceItem> items = invoiceItemRepository.findItemsByInvoiceId(invoice.getInvoiceId());
            allItems.addAll(items);
        }
        // Group items by name and analyze
        Map<String, List<InvoiceItem>> itemsGroupedByName = new HashMap<>();

        for (InvoiceItem item : allItems) {
            String itemName = item.getItemName();
            if (!itemsGroupedByName.containsKey(itemName)) {
                itemsGroupedByName.put(itemName, new ArrayList<>());
            }
            itemsGroupedByName.get(itemName).add(item);
        }

        List<Map<String, Object>> suspiciousItems = analyzeSuspiciousItems(itemsGroupedByName);

        List<Map<String, Object>> suspiciousInvoices = new ArrayList<>();

        for (Invoice invoice : invoices) {
            if (isAnomalousPurchaseTime(invoice.getCreatedAt())) {

                Map<String, Object> invoiceDetails = new HashMap<>();
                invoiceDetails.put("invoiceId", invoice.getInvoiceId());
                invoiceDetails.put("reason", "Anomalous Purchase Time");
                invoiceDetails.put("createdAt", invoice.getCreatedAt());
                suspiciousInvoices.add(invoiceDetails);
            }
        }


        // Combine results
        Map<String, Object> response = new HashMap<>();
        response.put("suspiciousItems", suspiciousItems);
        response.put("suspiciousInvoices", suspiciousInvoices);

        return response;
    }

    private List<Map<String, Object>> analyzeSuspiciousItems(Map<String, List<InvoiceItem>> itemsGroupedByName) {
        List<Map<String, Object>> suspiciousItems = new ArrayList<>();

        for (Map.Entry<String, List<InvoiceItem>> entry : itemsGroupedByName.entrySet()) {
            String itemName = entry.getKey();
            List<InvoiceItem> items = entry.getValue();

            // averages
            double averageQuantity = items.stream().mapToDouble(InvoiceItem::getQuantity).average().orElse(0);
            double averagePrice = items.stream().mapToDouble(InvoiceItem::getPricePerUnit).average().orElse(0);

            for (InvoiceItem item : items) {
                // Detect unusual quantities
                if (item.getQuantity() > 3 * averageQuantity) {
                    suspiciousItems.add(createSuspiciousItemLog(item, "Unusual Quantity", averageQuantity));
                }

                averagePrice = Double.parseDouble(String.format("%.2f", averagePrice));
                // Detect significant price changes
                if (Math.abs(item.getPricePerUnit() - averagePrice) > averagePrice * 0.2) {
                    suspiciousItems.add(createSuspiciousItemLog(item, "Significant Price Change", averagePrice));
                }
            }
        }

        return suspiciousItems;
    }

    private Map<String, Object> createSuspiciousItemLog(InvoiceItem item, String reason, double referenceValue) {
        return Map.of(
                "itemName", item.getItemName(),
                "quantity", item.getQuantity(),
                "pricePerUnit", item.getPricePerUnit(),
                "reason", reason,
                "referenceValue", referenceValue
        );
    }

    private boolean isAnomalousPurchaseTime(LocalDateTime createdAt) {
        int hour = createdAt.getHour();
        return hour < 6 || hour > 22; // purchases made between 10 PM and 6 AM
    }

    //_____________________________end detectSuspiciousActivity_________________________


    public List<Map<String, Object>> detectAbnormalSpending(Integer storeId) {
        LocalDate startDate = LocalDate.now().withDayOfMonth(1);
        LocalDate endDate = LocalDate.now();
        System.out.println(startDate);
        System.out.println(endDate);
        double thresholdPercentage= 1;




        List<Invoice> invoices = invoiceRepository.findInvoicesByCustomerIdAfterAndCreatedAtBetween(
                storeId,
                startDate.atStartOfDay(),
                endDate.atStartOfDay()
        );

        if (invoices.isEmpty()) {
            throw new ApiException("No invoices found for the given period.");
        }

        // Calculate the average invoice total
        double totalRevenue = invoices.stream().mapToDouble(Invoice::getTotalAmount).sum();
        double averageSpending = totalRevenue / invoices.size();

        // Detect abnormal invoices
        double threshold = averageSpending * (1 + (thresholdPercentage / 100));
        List<Map<String, Object>> abnormalInvoices = new ArrayList<>();

        for (Invoice invoice : invoices) {
            if (invoice.getTotalAmount() > threshold) {
                Customer customer = customerRepository.findCustomerByCustomerId(invoice.getCustomerId());
                Map<String, Object> abnormalInvoice = new HashMap<>();
                abnormalInvoice.put("invoiceId", invoice.getInvoiceId());
                abnormalInvoice.put("customer", customer );
                abnormalInvoice.put("totalAmount", invoice.getTotalAmount());
                abnormalInvoice.put("createdAt", invoice.getCreatedAt());
                abnormalInvoice.put("difference", invoice.getTotalAmount() - averageSpending);
                abnormalInvoice.put("percentageAboveAverage", ((invoice.getTotalAmount() - averageSpending) / averageSpending) * 100);
                abnormalInvoices.add(abnormalInvoice);
            }
        }

        return abnormalInvoices;
    }

    // _______________________________ Add invoice _____________________________
    public String addInvoiceWithItems(Integer storeId, Integer customerId, Map<String, Object> requestBody) {

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


        Map<String, Object> invoiceMap = (Map<String, Object>) requestBody.get("invoice");
        if (invoiceMap == null || !invoiceMap.containsKey("totalAmount")) {
            throw new ApiException("Invalid invoice data");
        }

        Invoice invoice = new Invoice();
        invoice.setStoreId(storeId);
        invoice.setCustomerId(customerId);
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setTotalAmount(Double.parseDouble(invoiceMap.get("totalAmount").toString()));
        invoiceRepository.save(invoice);


        List<Map<String, Object>> items = (List<Map<String, Object>>) requestBody.get("items");
        if (items == null || items.isEmpty()) {
            return "invoice saved";  // save invoice without items
        }

        double totalItemsPrice = 0;

        List<InvoiceItem> pendingItems = new ArrayList<>();
        for (Map<String, Object> itemMap : items) {
            if (invoiceItemRepository.findFirstByItemNameAndInvoiceIdOrderByCreatedAtDesc(itemMap.get("itemName").toString(),invoice.getInvoiceId()).getItemName().equals(itemMap.get("itemName").toString())){
                continue;
            }
            InvoiceItem item = new InvoiceItem();
            item.setInvoiceId(invoice.getInvoiceId());
            item.setItemName(itemMap.get("itemName").toString());
            item.setQuantity(Integer.parseInt(itemMap.get("quantity").toString()));
            item.setPricePerUnit(Double.parseDouble(itemMap.get("pricePerUnit").toString()));
            item.setSubtotal(item.getPricePerUnit() * item.getQuantity());
            totalItemsPrice += item.getSubtotal();
            pendingItems.add(item);
        }


        if ( Math.abs(totalItemsPrice - invoice.getTotalAmount()) > 0.01)  {
           return "Invoice added without items total items price does not match the invoice total amount";
        }else{
            for (InvoiceItem item : pendingItems) {

                invoiceItemRepository.save(item);
            }
        }

        return "Invoice and invoice item added successfully";
    }

    public void addItemToInvoice(Integer invoiceId,Integer storeId,Integer customerId, InvoiceItem invoiceItem) {
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

    //_____________________________________________________
    public void assignCustomerToStore(Integer customerId, Integer storeId) {
        Store store = storeRepository.findStoreByStoreId(storeId);
        if (store == null) {
            throw new ApiException("Store not found");
        }


        Customer customer = customerRepository.findCustomerByCustomerId(customerId);
        if (customer == null) {
            throw new ApiException("Customer not found");
        }


        if (customer.getStoreId() != null) {
            if (customer.getStoreId().equals(storeId)) {
                throw new ApiException("Customer is already associated with this store.");
            }
            throw new ApiException("Customer is already associated with another store.");
        }

        customer.setStoreId(storeId);
        customerRepository.save(customer);
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

}





