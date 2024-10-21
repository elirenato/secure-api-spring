package com.company.secureapispring.customer.repositories;

import com.company.secureapispring.customer.entities.StateProvince;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateProvinceRepository extends JpaRepository<StateProvince, Integer> {
    List<StateProvince> findAllByCountryId(Integer countryId, Sort sortable);
}
