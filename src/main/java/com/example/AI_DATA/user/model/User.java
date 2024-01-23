package com.example.AI_DATA.user.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@Entity
@Table(name = "user")
@AllArgsConstructor
public class User {
    @Id
    @Column(name=  "id")
    private String id;

    private String password;


    public User(String password) {
        this.password = password;

    }

    public User() {}
}

