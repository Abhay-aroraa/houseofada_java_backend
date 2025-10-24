package com.example.houseofada.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressDTO {
    private String customerName;
    private String phone;
    private String addressLine;
    private String city;
    private String pincode;
}
