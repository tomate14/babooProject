package org.example.baboobackend.dto;

import lombok.Data;

@Data
public class ApiResponse {
    private String error;
    private String message;

    public ApiResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

}

