package com.example.language_learning.requests;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
