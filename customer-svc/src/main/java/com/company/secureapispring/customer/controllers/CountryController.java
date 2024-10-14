package com.company.secureapispring.customer.controllers;

import com.company.secureapispring.customer.entities.Country;
import com.company.secureapispring.customer.entities.StateProvince;
import com.company.secureapispring.customer.services.CountryService;
import com.company.secureapispring.customer.services.StateProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/countries")
@RequiredArgsConstructor
public class CountryController {
    private final CountryService countryService;
    private final StateProvinceService stateProvinceService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<Country> findAll() {
        return countryService.findAll();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{countryId}/state-provinces")
    public List<StateProvince> findStateProvinces(@PathVariable Integer countryId) {
        return stateProvinceService.findAll(countryId);
    }
}
