package com.test.ordertest.adapter.in.web.advice;

import com.test.ordertest.adapter.in.web.controller.OrderController;
import com.test.ordertest.application.exception.OrderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@Slf4j
@ControllerAdvice(assignableTypes = {OrderController.class})
public class OrderAdvice {
    private static final String ERROR = "Error:";

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleArgumentMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error(ERROR, ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error(ERROR, ex);
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return new ResponseEntity<>(processFieldErrors(fieldErrors), (HttpStatus.BAD_REQUEST));
    }


    @ExceptionHandler(OrderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleOrderException(OrderException ex) {
        log.error(ERROR, ex);
        return new ResponseEntity<>(ex.getMessage(), getStatusCode(ex.getErrorCode()));
    }

    private HttpStatus getStatusCode(int errorCode) {
        if(errorCode == OrderException.OUT_OF_BUSINESS_TIME) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        } else if(errorCode == OrderException.NOT_RECORD_FOUND_FOR_ID) {
            return HttpStatus.NOT_FOUND;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleInternalError(Exception ex) {
        log.error(ERROR, ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private String processFieldErrors(List<org.springframework.validation.FieldError> fieldErrors) {
        StringBuilder errorBuilder = new StringBuilder();
        for (org.springframework.validation.FieldError fieldError : fieldErrors) {
            errorBuilder.append(fieldError.getField()).append(" : ")
                    .append(fieldError.getDefaultMessage());
        }
        return errorBuilder.toString();
    }
}
