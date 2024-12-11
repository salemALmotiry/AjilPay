package com.example.ajilpay.Service;

import com.example.ajilpay.Model.Customer;
import com.example.ajilpay.Model.MonthlyPayment;
import com.example.ajilpay.Model.Store;
import com.example.ajilpay.Repository.CustomerRepository;
import com.example.ajilpay.Repository.MonthlyPaymentRepository;
import com.example.ajilpay.Repository.StoreRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class MonthlyPaymentService {

    private final MonthlyPaymentRepository monthlyPaymentRepository;
    private final StoreRepository storeRepository;
    private final CustomerRepository customerRepository;

    public List<MonthlyPayment> getAllMonthlyPayments() {
        return monthlyPaymentRepository.findAll();
    }

    public void addMonthlyPayment(MonthlyPayment monthlyPayment) {

        Store store = storeRepository.findStoreByStoreId(monthlyPayment.getStoreId());
        if (store == null) {
            throw new RuntimeException("Store not found");
        }
        Customer customer = customerRepository.findCustomerByCustomerId(monthlyPayment.getCustomerId());
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }
        if (customer.getStoreId() != store.getStoreId()) {
            throw new RuntimeException("Customer is not associated with store");
        }

        monthlyPaymentRepository.save(monthlyPayment);
    }

    public void deleteMonthlyPayment(Integer paymentId) {
        if ( monthlyPaymentRepository.findMonthlyPaymentByMonthlyPaymentId(paymentId) ==null )
            throw new RuntimeException("Monthly payment not found");

        monthlyPaymentRepository.deleteMonthlyPaymentByPaymentId(paymentId);
    }


    // _____________________________________________________________
    public List<MonthlyPayment> getMonthlyPaymentsByCustomerId(Integer customerId) {
        return monthlyPaymentRepository.findMonthlyPaymentByCustomerId(customerId);
    }

    public List<MonthlyPayment> getMonthlyPaymentsByStoreId(Integer storeId) {
        return monthlyPaymentRepository.findMonthlyPaymentByStoreId(storeId);
    }

    public List<MonthlyPayment> getMonthlyPaymentsByStatus(String status) {
        return monthlyPaymentRepository.findMonthlyPaymentByPaymentStatus(status);
    }


}
