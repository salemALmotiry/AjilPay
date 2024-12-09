package com.example.ajilpay.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class StoreOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ownerId;

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(columnDefinition = "VARCHAR(50) NOT NULL")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    @Column(columnDefinition = "VARCHAR(100) UNIQUE NOT NULL")
    private String email;

    @NotBlank(message = "Password hash cannot be empty")
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String passwordHash;

    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
}
