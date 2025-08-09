package com.example.language_learning.requests;

import lombok.Data;

@Data
public class BookRequest {
    private String language;
    private String difficulty;
}
