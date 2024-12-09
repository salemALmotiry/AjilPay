package com.example.ajilpay.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InvoicesForecastDTO {

    private Integer customerId;
    private List<ForecastedMonthDTO> forecastedMonths;


}
