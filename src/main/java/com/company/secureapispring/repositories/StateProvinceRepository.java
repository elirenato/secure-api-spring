package com.company.secureapispring.repositories;

import com.company.secureapispring.entities.StateProvince;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateProvinceRepository extends JpaRepository<StateProvince, Integer> {
}
