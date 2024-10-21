package com.company.secureapispring.customer.exceptions;

import com.company.secureapispring.common.exceptions.AbstractGlobalExceptionHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler extends AbstractGlobalExceptionHandler {
    @Override
    protected String getMessage(String constraintName, DataIntegrityViolationException ex) {
        String message;
        switch (constraintName) {
            case "fk_state_province_id" -> message = "Please enter a valid State/Province.";
            case "customers_email_key" -> message = "There is already a customer registered with this email address. Please use a different email.";
            default -> {
                log.error("The constraint name '{}' is unknown. Detail message: {}", constraintName, ex.getMessage());
                message = "One or more relationship of the object is not valid.";
            }
        }
        return message;
    }
}
