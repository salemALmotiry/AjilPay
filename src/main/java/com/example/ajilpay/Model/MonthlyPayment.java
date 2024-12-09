package com.example.ajilpay.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@Check(constraints = "paymentStatus = 'PENDING' or paymentStatus = 'PAID' or paymentStatus = 'PARTIAL'")
public class MonthlyPayment
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;

    @NotNull(message = "Customer ID cannot be null")
    @Column(columnDefinition = "INT NOT NULL")
    private Integer customerId;

    @NotNull(message = "Store ID cannot be null")
    @Column(columnDefinition = "INT NOT NULL")
    private Integer storeId;

    @NotNull(message = "Month cannot be null")
    @Column(columnDefinition = "DATE NOT NULL")
    private LocalDate month;

    @NotNull(message = "Total due cannot be null")
    @Positive(message = "Total due must be positive")
    @Column(columnDefinition = "DECIMAL(10,2) NOT NULL")
    private Double totalDue;

    @PositiveOrZero(message = "Total paid must be zero or positive")
    @Column(columnDefinition = "DECIMAL(10,2) DEFAULT 0.0")
    private Double totalPaid = 0.0;

    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private String paymentStatus = "PENDING";

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}
