package com.company.secureapispring.customer.services;

import com.company.secureapispring.customer.constants.CacheName;
import com.company.secureapispring.customer.entities.Country;
import com.company.secureapispring.customer.repositories.CountryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CountryService {
    private final CountryRepository countryRepository;

    @Cacheable(cacheNames = CacheName.ALL_COUNTRIES)
    public List<Country> findAll() {
        return countryRepository.findAll(Sort.by("name"));
    }
}
