package com.example.ajilpay.Service;

import com.example.ajilpay.Model.MonthlyPayment;
import com.example.ajilpay.Repository.MonthlyPaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class MonthlyPaymentService {

    private final MonthlyPaymentRepository monthlyPaymentRepository;

    public List<MonthlyPayment> getAllMonthlyPayments() {
        return monthlyPaymentRepository.findAll();
    }

    public void addMonthlyPayment(MonthlyPayment monthlyPayment) {
        monthlyPaymentRepository.save(monthlyPayment);
    }

    public void deleteMonthlyPayment(Integer paymentId) {
        monthlyPaymentRepository.deleteById(paymentId);
    }


    // _____________________________________________________________
    public List<MonthlyPayment> getMonthlyPaymentsByCustomerId(Integer customerId) {
        return monthlyPaymentRepository.findMonthlyPaymentByCustomerId(customerId);
    }


    public List<MonthlyPayment> getMonthlyPaymentsByStoreIdCustomerId(Integer storeId,Integer customerId) {
        return monthlyPaymentRepository.findMonthlyPaymentByStoreIdAndCustomerId(storeId,customerId);
    }


    public List<MonthlyPayment> getMonthlyPaymentsByStoreId(Integer storeId) {
        return monthlyPaymentRepository.findMonthlyPaymentByStoreId(storeId);
    }

    public List<MonthlyPayment> getMonthlyPaymentsByStatus(String status) {
        return monthlyPaymentRepository.findMonthlyPaymentByPaymentStatus(status);
    }

    public List<MonthlyPayment> getMonthlyPaymentsByCustomerIdAndStatus(Integer customerId,String status) {
        return monthlyPaymentRepository.findMonthlyPaymentByCustomerIdAndPaymentStatus(customerId,status);
    }
    public LocalDate getLastMonthlyPaymentDate(Integer soresId){
        return monthlyPaymentRepository.getLastMonthlyPaymentDate(soresId);
    }

    public LocalDate getLastMonthlyPaymentDateByCustomer(Integer storeId, Integer customerId){
        return monthlyPaymentRepository.getLastMonthlyPaymentDateForCustomer(storeId,customerId);
    }

    public boolean existMonthlyPayment(Integer customerId, Integer storeId, LocalDate date){
        return monthlyPaymentRepository.existsMonthlyPaymentByCustomerIdAndStoreIdAndMonth(customerId,storeId,date);
    }

    public List<MonthlyPayment> getMonthlyPaymentByStatus(Integer customerId, Integer storeId){
        return monthlyPaymentRepository.findMonthlyPaymentByPendingAndPartial(customerId,storeId);
    }
}
