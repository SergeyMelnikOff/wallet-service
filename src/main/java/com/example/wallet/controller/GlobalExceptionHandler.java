package com.example.wallet.controller;

import com.example.wallet.dto.ErrorResponse;
import com.example.wallet.exception.InsufficientFundsException;
import com.example.wallet.exception.WalletNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWalletNotFound(
            WalletNotFoundException ex, HttpServletRequest request) {

        log.error("Wallet not found: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.NOT_FOUND.value()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(
            InsufficientFundsException ex, HttpServletRequest request) {

        log.error("Insufficient funds: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ErrorResponse error = ErrorResponse.of(
                message,
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(
            HttpMessageNotReadableException ex, HttpServletRequest request) {

        String message = "Invalid JSON format";
        if (ex.getMessage() != null && ex.getMessage().contains("OperationType")) {
            message = "Invalid operationType. Allowed values: DEPOSIT, WITHDRAW";
        }

        ErrorResponse error = ErrorResponse.of(
                message,
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {

        String message = "Invalid parameter type for '" + ex.getName() + "'. Expected: " +
                (ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "valid type");

        ErrorResponse error = ErrorResponse.of(
                message,
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        log.error("Illegal argument: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.of(
                ex.getMessage(),
                request.getRequestURI(),
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.of(
                "Internal server error",
                request.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}