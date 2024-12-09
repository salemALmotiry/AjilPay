package com.example.ajilpay.Model;

import com.example.ajilpay.DTO.CustomerOverViewDTO;
import com.example.ajilpay.DTO.PaymentBehaviorOverViewDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CombinedPaymentSummary {

    private CustomerOverViewDTO customerOverView;
    private PaymentBehaviorOverViewDTO paymentBehaviorOverView;
}
