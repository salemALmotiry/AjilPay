package com.example.ajilpay.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer invoiceId;

    @NotNull(message = "Store ID cannot be null")
    @Column(columnDefinition = "INT NOT NULL")
    private Integer storeId;

    @NotNull(message = "Customer ID cannot be null")
    @Column(columnDefinition = "INT NOT NULL")
    private Integer customerId;

    @NotNull(message = "Total amount cannot be null")
    @Positive(message = "Total amount must be positive")
    @Column(columnDefinition = "DECIMAL(10,2) NOT NULL")
    private Double totalAmount;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();




}
