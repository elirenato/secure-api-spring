package com.company.secureapispring.customer.services;

import com.company.secureapispring.customer.entities.StateProvince;
import com.company.secureapispring.customer.repositories.StateProvinceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StateProvinceService {
    private final StateProvinceRepository stateProvinceRepository;

    public List<StateProvince> findAll(Integer countryId) {
        return stateProvinceRepository.findAllByCountryId(countryId, Sort.by("name"));
    }
}
