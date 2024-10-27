package com.company.secureapispring.auth.repositories;

import com.company.secureapispring.auth.entities.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByAlias(@Param("alias") String alias);
}
