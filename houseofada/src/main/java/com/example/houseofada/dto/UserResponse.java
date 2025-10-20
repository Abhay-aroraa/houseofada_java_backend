package com.example.houseofada.dto;



import com.example.houseofada.model.User;
import lombok.Data;

import java.util.List;

@Data
public class UserResponse {
    private List<User> users;
    private long totalCount;

    public UserResponse(List<User> users, long totalCount) {
        this.users = users;
        this.totalCount = totalCount;
    }

    // getters and setters
}
