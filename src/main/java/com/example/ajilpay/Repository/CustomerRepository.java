package com.example.ajilpay.Repository;

import com.example.ajilpay.Model.Customer;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    List<Customer> findCustomerByStoreId(Integer storeId);


    Customer findCustomerByUsername(String username);

    Customer findCustomerByEmail(String email);

    Customer findCustomerByCustomerId(Integer customerId);

    Customer findCustomerByCustomerIdAndStoreId(Integer customerId, Integer storeId);
}
