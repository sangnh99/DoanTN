package com.example.demodatn.config;

import com.example.demodatn.domain.ErrorDetails;
import com.example.demodatn.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomeException(CustomException customException, WebRequest webRequest){
        ErrorDetails errorDetails = new ErrorDetails(new Date(), customException.getMessage(), customException.getCode());
        return new ResponseEntity<>(errorDetails, customException.getHttpStatus());
    }
}
