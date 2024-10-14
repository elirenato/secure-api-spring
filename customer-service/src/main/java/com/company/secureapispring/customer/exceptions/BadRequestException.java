package com.company.secureapispring.customer.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

public class BadRequestException extends RuntimeException implements ErrorResponse {
    private Map<String, String> errors = new HashMap<>();
    private ProblemDetail body;

    public BadRequestException() {
        super("Invalid request content.");
        this.body = ProblemDetail.forStatusAndDetail(this.getStatusCode(), "Invalid request content.");
        this.body.setProperty("errors", this.errors);
    }

    public BadRequestException withError(String propertyName, String message) {
        this.errors.put(propertyName, message);
        return this;
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }

    @Override
    public ProblemDetail getBody() {
        return this.body;
    }


}
