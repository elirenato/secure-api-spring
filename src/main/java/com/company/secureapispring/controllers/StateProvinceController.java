package com.company.secureapispring.controllers;

import com.company.secureapispring.entities.StateProvince;
import com.company.secureapispring.services.StateProvinceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/state-provinces")
public class StateProvinceController {
    private final StateProvinceService stateProvince;

    public StateProvinceController(StateProvinceService stateProvince) {
        this.stateProvince = stateProvince;
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public StateProvince get(@PathVariable Integer id) {
        return stateProvince.get(id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<StateProvince> findAll() {
        return stateProvince.findAll();
    }
}