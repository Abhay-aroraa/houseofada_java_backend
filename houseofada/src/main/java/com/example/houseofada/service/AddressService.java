package com.example.houseofada.service;

import com.example.houseofada.dto.AddressRequest;
import com.example.houseofada.exception.UserNotFoundException;
import com.example.houseofada.model.Address;
import com.example.houseofada.model.User;
import com.example.houseofada.repository.AddressRepository;
import com.example.houseofada.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    // ✅ Add Address
    public Address addAddress(Long userId, AddressRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Address address = Address.builder()
                .user(user)
                .customerName(request.getCustomerName())
                .phone(request.getPhone())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .pincode(request.getPincode())
                .build();

        return addressRepository.save(address);
    }


    public List<Address> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }


    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }


    public Address updateAddress(Long addressId, AddressRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new UserNotFoundException("Address not found"));

        address.setCustomerName(request.getCustomerName());
        address.setPhone(request.getPhone());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setPincode(request.getPincode());

        return addressRepository.save(address);
    }

}
