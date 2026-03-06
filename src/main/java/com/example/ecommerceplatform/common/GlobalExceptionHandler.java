package com.example.ecommerceplatform.common;

import com.example.ecommerceplatform.common.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return new ErrorResponseDTO(
                OffsetDateTime.now(),
                404,
                "Not Found",
                ex.getMessage(),
                req.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleNotValid(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> fieldErrors = new HashMap<>();
        for(FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        return new ErrorResponseDTO(
                OffsetDateTime.now(),
                400,
                "Bad Request",
                "Validation failed",
                req.getRequestURI(),
                fieldErrors
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleGeneric(Exception ex, HttpServletRequest req) {
        return new ErrorResponseDTO(
                OffsetDateTime.now(),
                500,
                "Internal Server Error",
                "Something went wrong",
                req.getRequestURI(),
                null
        );
    }
}
