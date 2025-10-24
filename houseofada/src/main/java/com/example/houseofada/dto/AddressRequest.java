package com.example.houseofada.dto;

import lombok.Data;

@Data
public class AddressRequest {
    private String customerName;
    private String phone;
    private String addressLine;
    private String city;
    private String pincode;
}
