package com.company.secureapispring.core.repositories;

import com.company.secureapispring.core.entities.StateProvince;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateProvinceRepository extends JpaRepository<StateProvince, Integer> {
}
