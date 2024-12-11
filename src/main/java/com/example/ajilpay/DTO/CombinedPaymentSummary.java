package com.example.ajilpay.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CombinedPaymentSummary {

    private CustomerOverViewDTO customerOverView;
    private PaymentBehaviorOverViewDTO paymentBehaviorOverView;
}
