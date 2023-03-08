package com.company.secureapispring.customer.controllers;

import com.company.secureapispring.customer.entities.Country;
import com.company.secureapispring.customer.services.CountryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/countries")
public class CountryController {
    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Country get(@PathVariable Integer id) {
        return countryService.get(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<Country> findAll() {
        return countryService.findAll();
    }
}
