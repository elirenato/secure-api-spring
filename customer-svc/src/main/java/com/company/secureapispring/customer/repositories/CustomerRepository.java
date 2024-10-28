package com.company.secureapispring.customer.repositories;

import com.company.secureapispring.customer.entities.Customer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT c FROM Customer c JOIN FETCH c.stateProvince WHERE c.id = (:id)")
    Optional<Customer> findByIdWithStateProvince(@Param("id") Long id);

    @Query("SELECT c FROM Customer c WHERE c.organization.id = (:organizationId)")
    List<Customer> findAllByOrganizationId(@Param("organizationId") Long organizationId, Sort sort);

}
