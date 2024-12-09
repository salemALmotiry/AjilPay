package com.example.ajilpay.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer storeId;

    @NotBlank(message = "Store name cannot be empty")
    @Size(min = 3, max = 100, message = "Store name must be between 3 and 100 characters")
    @Column(columnDefinition = "VARCHAR(100) NOT NULL")
    private String storeName;

    @NotNull(message = "Owner ID cannot be null")
    @Column(columnDefinition = "INT NOT NULL")
    private Integer ownerId;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    @Column(columnDefinition = "VARCHAR(255)")
    private String address;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Contact number must be valid")
    @Column(columnDefinition = "VARCHAR(15)")
    private String contactNumber;
}
