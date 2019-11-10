package com.rideal.api.ridealBackend.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Data
public class ApiError {
    private HttpStatus status;
    private String message;
    private List<String> errors;

    public ApiError(HttpStatus status, String message, String error) {
        this.status = status;
        this.message = message;
        errors = Collections.singletonList(error);
    }
}
