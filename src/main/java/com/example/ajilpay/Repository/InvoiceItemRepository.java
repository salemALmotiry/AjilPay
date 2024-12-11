package com.example.ajilpay.Repository;

import com.example.ajilpay.Model.InvoiceItem;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Integer> {
    List<InvoiceItem> findInvoiceItemByInvoiceId(Integer invoiceId);


    @Query("SELECT i FROM InvoiceItem i WHERE i.itemName = ?1 AND i.invoiceId = ?2 ORDER BY i.createdAt DESC LIMIT 1")
    InvoiceItem findFirstByItemNameAndInvoiceIdOrderByCreatedAtDesc(String itemName, Integer invoiceId);

    @Query("SELECT ii FROM InvoiceItem ii WHERE ii.invoiceId =?1")
    List<InvoiceItem> findItemsByInvoiceId(Integer invoiceId);


    @Query("SELECT i FROM InvoiceItem i JOIN Invoice inv ON i.invoiceId = inv.invoiceId WHERE inv.storeId = :storeId")
    List<InvoiceItem> findInvoiceItemsByStoreId(Integer storeId);

    @Query("SELECT i FROM InvoiceItem i WHERE i.itemName =?1 ORDER BY i.createdAt ASC LIMIT 1")
    InvoiceItem findFirstByItemNameOrderByAppliedAtAsc(String itemName);

}
