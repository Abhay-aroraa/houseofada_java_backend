package com.example.houseofada.model;


import lombok.Data;

@Data
public class AuthResponse {
    private String token;

    // Constructor
    public AuthResponse(String token) {
        this.token = token;
    }

}
