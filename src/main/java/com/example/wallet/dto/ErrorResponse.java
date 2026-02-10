package com.example.wallet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private LocalDateTime timestamp;
    private String path;
    private int status;

    public static ErrorResponse of(String message, String path, int status) {
        ErrorResponse response = new ErrorResponse();
        response.setMessage(message);
        response.setTimestamp(LocalDateTime.now());
        response.setPath(path);
        response.setStatus(status);
        return response;
    }
}