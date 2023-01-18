package com.company.secureapispring.services;

import com.company.secureapispring.entities.Country;
import com.company.secureapispring.repositories.CountryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;

    public Country getCountry(Integer id) {
        return countryRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAll(Sort.by("name"));
    }
}
