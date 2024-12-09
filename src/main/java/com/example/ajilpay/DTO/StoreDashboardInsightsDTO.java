package com.example.ajilpay.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StoreDashboardInsightsDTO {
    private double totalRevenue;
    private double averageInvoiceAmount;
    private String topSpendingCustomer;
    private Integer peakActivityHour;
    private double repeatCustomerRate;


}