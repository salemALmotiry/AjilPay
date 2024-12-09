package com.example.ajilpay.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UnusualItemDTO {
    private String itemName;
    private int totalPurchases;
    private LocalDate lastPurchased;


}
