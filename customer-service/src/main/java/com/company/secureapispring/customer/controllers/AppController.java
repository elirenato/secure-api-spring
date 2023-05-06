package com.company.secureapispring.customer.controllers;

import org.springframework.boot.info.BuildProperties;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app")
public class AppController {

    private BuildProperties buildProperties;

    public AppController(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

    @GetMapping(value = "/build")
    @PreAuthorize("isAuthenticated()")
    public BuildProperties getAppVersion() {
        return buildProperties;
    }
}
