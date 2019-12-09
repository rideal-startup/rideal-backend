package com.rideal.api.ridealBackend.handlers;

import com.rideal.api.ridealBackend.errors.ApiError;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MultipartHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({MultipartException.class})
    public ResponseEntity<ApiError> handlerMultipartException(Exception ex, WebRequest request) {
        ApiError apiError =
                new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), "Invalid request, file is not Multipart");
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}