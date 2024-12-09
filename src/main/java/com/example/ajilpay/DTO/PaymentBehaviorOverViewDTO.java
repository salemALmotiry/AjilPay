package com.example.ajilpay.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentBehaviorOverViewDTO {
    private double averageMonthlyPayment;
    private double latePayments;
    private double increaseInPayments;
    private double paymentFrequency;
    private double longestGap;

}
