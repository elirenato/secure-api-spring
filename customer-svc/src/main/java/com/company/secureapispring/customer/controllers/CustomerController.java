package com.company.secureapispring.customer.controllers;

import com.company.secureapispring.customer.entities.Customer;
import com.company.secureapispring.customer.exceptions.BadRequestException;
import com.company.secureapispring.customer.services.CustomerService;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Secured("ROLE_ADMIN")
    public Customer create(@Valid @RequestBody Customer input) {
        if (input.getId() != null) {
            throw (new BadRequestException()).withError("id", "must be null");
        }
        return customerService.create(input);
    }

    @GetMapping("/{id}")
    @Secured(value = {"ROLE_ADMIN", "ROLE_ANALYST"})
    public Customer get(@NotNull @PathVariable Long id) {
        return customerService.get(id);
    }

    @JsonView(Customer.ListJsonView.class)
    @GetMapping
    @Secured(value = {"ROLE_ADMIN", "ROLE_ANALYST"})
    public List<Customer> listAll() {
        return customerService.listAll();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Secured("ROLE_ADMIN")
    public Customer update(@PathVariable Long id, @Valid @RequestBody Customer input) {
        if (!Objects.equals(id, input.getId())) {
            throw (new BadRequestException()).withError("id", "must be equals id from url");
        }
        return customerService.update(input);
    }

    @DeleteMapping(path = "/{id}")
    @Secured("ROLE_ADMIN")
    public Customer delete(@NotNull @PathVariable Long id) {
        return customerService.delete(id);
    }
}
