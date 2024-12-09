package com.example.ajilpay.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SalesForecastDTO {

    private Integer storeId;
    private List<ForecastedMonthDTO> forecastedMonths;


}
