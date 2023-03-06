package com.company.secureapispring.core.repositories;

import com.company.secureapispring.core.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT p FROM Customer p JOIN FETCH p.stateProvince WHERE p.id = (:id)")
    Optional<Customer> findByIdWithStateProvince(@Param("id") Long id);
}
