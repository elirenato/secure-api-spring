package com.company.secureapispring.core.services;

import com.company.secureapispring.core.entities.Country;
import com.company.secureapispring.core.repositories.CountryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CountryService {
    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public Country get(Integer id) {
        return countryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Country> findAll() {
        return countryRepository.findAll(Sort.by("name"));
    }
}
