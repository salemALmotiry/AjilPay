package com.example.ajilpay.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InvoiceItemPriceChangeDTO {
    private Integer itemId;
    private String itemName;
    private Double originalPrice;
    private Double currentPrice;
    private Double priceDifference;
    private Double percentageChange;
}