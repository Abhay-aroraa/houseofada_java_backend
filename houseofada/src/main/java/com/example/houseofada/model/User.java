package com.example.houseofada.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")  // table name in database
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)  // email must be unique
    private String email;

    @Column(nullable = false)
    private String password;
    // store hashed password

    @Column(nullable = false)
    private String role;
}
