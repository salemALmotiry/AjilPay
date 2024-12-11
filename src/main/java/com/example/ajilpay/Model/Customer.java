package com.example.ajilpay.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Check(constraints = "payment = 'postpaid' or payment = 'fixed'")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer customerId;


    @Column(columnDefinition = "INT")
    private Integer storeId;

    @NotEmpty(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(columnDefinition = "VARCHAR(50) NOT NULL")
    private String username;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    @Column(columnDefinition = "VARCHAR(100) UNIQUE NOT NULL")
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,20}$", message = "password RegEx rules: \n" +
            "- Length: between 8 to 20 characters.\n" +
            "- Must contain at least:\n" +
            "   - One uppercase letter (A-Z)\n" +
            "   - One lowercase letter (a-z)\n" +
            "   - One number (0-9)\n" +
            "   - One special character (e.g., `!@#$%^&*`)\n" +
            "- No spaces allowed.")
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String password;


    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'postpaid'")
    private String payment = "postpaid";


    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
}
