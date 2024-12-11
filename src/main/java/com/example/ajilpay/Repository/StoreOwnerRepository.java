package com.example.ajilpay.Repository;

import com.example.ajilpay.Model.StoreOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreOwnerRepository extends JpaRepository<StoreOwner, Integer> {
    StoreOwner findStoreOwnerByUsername(String username);

    StoreOwner findStoreOwnerByEmail(String email);

    StoreOwner findStoreOwnerByOwnerId(Integer storeOwnerId);

    void deleteStoreOwnerByOwnerId(Integer storeOwnerId);

}
