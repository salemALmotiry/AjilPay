package com.example.ajilpay.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer historyId;

    @NotNull(message = "Monthly payment ID cannot be null")
    @Column(columnDefinition = "INT NOT NULL")
    private Integer monthlyPaymentId;

    @NotNull(message = "Customer ID cannot be null")
    @Column(columnDefinition = "INT NOT NULL")
    private Integer customerId;


    @NotNull(message = "Amount applied cannot be null")
    @Positive(message = "Amount applied must be positive")
    @Column(columnDefinition = "DECIMAL(10,2) NOT NULL")
    private Double amountApplied;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime appliedAt = LocalDateTime.now();


}
