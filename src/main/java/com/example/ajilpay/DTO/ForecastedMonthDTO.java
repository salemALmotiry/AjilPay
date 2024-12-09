package com.example.ajilpay.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Month;

@Data
@AllArgsConstructor
public class ForecastedMonthDTO {

    private Month month;
    private double forecasted;
    private int confidence;

}
