package com.Santino.Suscripciones.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.ResponseEntity;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler{

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handlePlanNoExiste(Exception ex){
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PlanAlreadyExists.class)
    public ResponseEntity<ErrorResponse> handlePlanExiste(Exception ex){
        ErrorResponse error = new ErrorResponse(HttpStatus.CONFLICT.value(), "Plan already exists", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex){
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}