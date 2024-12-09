package com.example.ajilpay.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerOverViewDTO {
    private int totalPaid;
    private int totalDue;
    private int outstandingBalance;
    private double paymentCompletionRate;
    private int partialPaymentCount;



}
