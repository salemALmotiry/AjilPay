package com.example.ajilpay.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InvoiceSummaryDTO {
    private Integer invoiceId;
    private Double totalAmount;
    private Double outstandingAmount;
    private LocalDateTime createdAt;
    private String paymentStatus;


}
