package com.company.secureapispring.controllers;

import com.company.secureapispring.entities.Customer;
import com.company.secureapispring.exceptions.BadRequestException;
import com.company.secureapispring.services.CustomerService;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('managers')")
    public Customer create(@Valid @RequestBody Customer input) {
        if (input.getId() != null) {
            throw (new BadRequestException()).withError("id", "must be null");
        }
        return customerService.create(input);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Customer get(@NotNull @PathVariable Long id) {
        return customerService.get(id);
    }

    @JsonView(Customer.ListJsonView.class)
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<Customer> listAll() {
        return customerService.listAll();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('managers')")
    public Customer update(@PathVariable Long id, @Valid @RequestBody Customer input) {
        if (!Objects.equals(id, input.getId())) {
            throw (new BadRequestException()).withError("id", "must be equals id from url");
        }
        return customerService.update(input);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('managers')")
    public Customer delete(@NotNull @PathVariable Long id) {
        return customerService.delete(id);
    }
}
