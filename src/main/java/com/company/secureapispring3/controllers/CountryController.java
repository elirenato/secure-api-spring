package com.company.secureapispring3.controllers;

import com.company.secureapispring3.entities.Country;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {
    @PreAuthorize("hasAuthority('managers')")
    @GetMapping
    public List<Country> listAllCountries() {
        return Arrays.asList(new Country(1, "BR", "Brazil"));
    }
}
