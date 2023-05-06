package com.company.secureapispring.customer.repositories;

import com.company.secureapispring.customer.entities.StateProvince;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StateProvinceRepository extends JpaRepository<StateProvince, Integer> {
}
