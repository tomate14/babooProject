package org.example.baboobackend.config;

import org.example.baboobackend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<ApiResponse> handleIllegalArgumentException(RuntimeException ex, WebRequest request) {
        ApiResponse errorResponse = new ApiResponse("Dato Incorrecto", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ApiResponse errorResponse = new ApiResponse("Server Error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
