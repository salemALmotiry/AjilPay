package com.example.ajilpay.DTO;

import com.example.ajilpay.Model.Invoice;
import com.example.ajilpay.Model.InvoiceItem;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InvoiceRequestDTO {


    @Valid
    private Invoice invoice;
    @Valid
    private List<InvoiceItem> items;


}
