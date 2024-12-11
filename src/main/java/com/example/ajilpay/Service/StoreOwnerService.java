package com.example.ajilpay.Service;

import com.example.ajilpay.Model.StoreOwner;
import com.example.ajilpay.Repository.StoreOwnerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class StoreOwnerService {

    private final StoreOwnerRepository storeOwnerRepository;

    public List<StoreOwner> getAllStoreOwners() {
        return storeOwnerRepository.findAll();
    }

    public void addStoreOwner(StoreOwner storeOwner) {
        storeOwnerRepository.save(storeOwner);
    }

    public StoreOwner getStoreOwnerByEmail(String email) {
        return storeOwnerRepository.findStoreOwnerByEmail(email);
    }

    public StoreOwner getStoreOwnerByUsername(String username) {
        return storeOwnerRepository.findStoreOwnerByUsername(username);
    }

    public void deleteStoreOwner(Integer ownerId) {
        storeOwnerRepository.deleteStoreOwnerByOwnerId(ownerId);
    }

    public StoreOwner getStoreOwnerById(Integer ownerId) {
        return storeOwnerRepository.findStoreOwnerByOwnerId(ownerId);
    }
}
