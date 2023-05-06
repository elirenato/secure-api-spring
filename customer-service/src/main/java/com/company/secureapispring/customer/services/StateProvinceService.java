package com.company.secureapispring.customer.services;

import com.company.secureapispring.customer.entities.StateProvince;
import com.company.secureapispring.customer.repositories.StateProvinceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateProvinceService {
    private final StateProvinceRepository stateProvinceRepository;

    public StateProvinceService(StateProvinceRepository stateProvinceRepository) {
        this.stateProvinceRepository = stateProvinceRepository;
    }

    public StateProvince get(Integer id) {
        return stateProvinceRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public List<StateProvince> findAll() {
        return stateProvinceRepository.findAll(Sort.by("name"));
    }
}
