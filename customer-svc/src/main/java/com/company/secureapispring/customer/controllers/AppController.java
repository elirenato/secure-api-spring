package com.company.secureapispring.customer.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppController {
    private final BuildProperties buildProperties;
    private final CacheManager cacheManager;

    @GetMapping(value = "/build")
    @PreAuthorize("isAuthenticated()")
    public BuildProperties getAppVersion() {
        return buildProperties;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/clear-cache")
    @Secured(value = {"ROLE_ADMIN"})
    public void clearCache() {
        cacheManager.getCacheNames().forEach(s -> {
            Optional.ofNullable(cacheManager.getCache(s)).ifPresent(Cache::clear);
        });
    }
}
