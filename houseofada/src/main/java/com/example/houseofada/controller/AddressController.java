package com.example.houseofada.controller;

import com.example.houseofada.dto.AddressRequest;
import com.example.houseofada.model.Address;
import com.example.houseofada.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/add/{userId}")
    public ResponseEntity<Address> addAddress(@PathVariable Long userId,
                                              @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.addAddress(userId, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Address>> getUserAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }
    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Address deleted successfully"
        ));
    }


    @PutMapping("/update/{addressId}")
    public ResponseEntity<Address> updateAddress(@PathVariable Long addressId,
                                                 @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.updateAddress(addressId, request));
    }
}
