package com.example.language_learning.requests;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
    private String language; // For initial settings
    private String difficulty; // For initial settings
}
