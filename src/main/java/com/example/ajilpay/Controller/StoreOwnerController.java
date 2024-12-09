package com.example.ajilpay.Controller;

import com.example.ajilpay.ApiResponse.ApiResponse;
import com.example.ajilpay.Model.StoreOwner;
import com.example.ajilpay.Service.StoreOwnerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/store-owners")
public class StoreOwnerController {

    private final StoreOwnerService storeOwnerService;

    @GetMapping("/get-all")
    public ResponseEntity getAllStoreOwners() {
        List<StoreOwner> storeOwners = storeOwnerService.getAllStoreOwners();
        return ResponseEntity.ok(storeOwners);
    }

    @PostMapping("/add")
    public ResponseEntity addStoreOwner(@RequestBody @Valid StoreOwner storeOwner, Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(errors.getFieldError().getDefaultMessage());
        }
        storeOwnerService.addStoreOwner(storeOwner);
        return ResponseEntity.status(201).body(new ApiResponse("Store Owner added successfully"));
    }

    @DeleteMapping("/delete/{ownerId}")
    public ResponseEntity deleteStoreOwner(@PathVariable Integer ownerId) {
        storeOwnerService.deleteStoreOwner(ownerId);
        return ResponseEntity.ok(new ApiResponse("Store Owner deleted successfully"));
    }

    @GetMapping("/get-by-email/{email}")
    public ResponseEntity getStoreOwnerByEmail(@PathVariable String email) {
        StoreOwner storeOwner = storeOwnerService.getStoreOwnerByEmail(email);
        return ResponseEntity.ok(storeOwner);
    }

    @GetMapping("/get-by-username/{username}")
    public ResponseEntity getStoreOwnerByUsername(@PathVariable String username) {
        StoreOwner storeOwner = storeOwnerService.getStoreOwnerByUsername(username);
        return ResponseEntity.ok(storeOwner);
    }
}
