package com.company.secureapispring.auth.services;

import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.repositories.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    public Optional<Organization> findByAlias(String alias) {
        return organizationRepository.findByAlias(alias);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Organization sync(Organization organization, Organization detachedOrganization) {
        BeanUtils.copyProperties(detachedOrganization, organization);
        organization = organizationRepository.save(organization);
        organizationRepository.flush();
        return organization;
    }
}
