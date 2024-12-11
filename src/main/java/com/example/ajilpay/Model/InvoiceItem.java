package com.example.ajilpay.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId;

    @NotNull(message = "Invoice ID cannot be null")
    @Column(columnDefinition = "INT NOT NULL")
    private Integer invoiceId;

    @NotEmpty(message = "Item name cannot be empty")
    @Size(max = 100, message = "Item name must not exceed 100 characters")
    @Column(columnDefinition = "VARCHAR(100) NOT NULL")
    private String itemName;

    @Positive(message = "Quantity must be positive")
    @Column(columnDefinition = "INT DEFAULT 1")
    private Integer quantity = 1;

    @NotEmpty(message = "Price per unit cannot be null")
    @Positive(message = "Price per unit must be positive")
    @Column(columnDefinition = "DECIMAL(10,2) NOT NULL")
    private Double pricePerUnit;

    @PositiveOrZero(message = "Subtotal must be zero or positive")
    @Column(columnDefinition = "DECIMAL(10,2)")
    private Double subtotal;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}
