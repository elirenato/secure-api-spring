package com.company.secureapispring.customer.exceptions;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@RestControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    protected ProblemDetail handleEntityNotFoundException(EntityNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(BadRequestException.class)
    protected ProblemDetail handleEntityNotFoundException(BadRequestException ex) {
        return ex.getBody();
    }

	/**
	 * Maps DataIntegrityViolationException to a 409 Conflict HTTP status code.
	 */
	@ExceptionHandler({ DataIntegrityViolationException.class })
	public ProblemDetail handleDataIntegrityViolationException(Exception ex) {
        String regex = "(\\bviolates foreign key constraint\\b|\\bviolates unique constraint\\b) \"([^\"]+)\"";
        Matcher matcher = Pattern.compile(regex).matcher(ex.getMessage());
        String constraintName = matcher.find() ? matcher.group(2) : StringUtils.EMPTY;
        String message;
        switch (constraintName) {
            case "fk_state_province_id" -> message = "Please enter a valid State/Province.";
            case "customers_email_key" -> message = "There is already a customer registered with this email address. Please use a different email.";
            default -> {
                log.error("The constraint name '{}' is unknown. Detail message: {}", constraintName, ex.getMessage());
                message = "One or more relationship of the object is not valid.";
            }
        }
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
	}

    @Override
    @Nullable
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getBody().getDetail());
        problemDetail.setProperty("errors", errors);
        return this.handleExceptionInternal(ex, problemDetail, headers, status, request);
    }
}