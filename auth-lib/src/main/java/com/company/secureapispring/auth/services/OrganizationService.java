package com.company.secureapispring.auth.services;

import com.company.secureapispring.auth.cache.CacheName;
import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.repositories.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class OrganizationService {
    private final OrganizationRepository organizationRepository;

    @Cacheable(cacheNames = CacheName.ORG_BY_ALIAS, unless = "#result == null")
    public Optional<Organization> findByAlias(String alias) {
        return organizationRepository.findByAlias(alias);
    }

    @CacheEvict(cacheNames = CacheName.USER_BY_USERNAME)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Organization sync(Organization organization, Organization detachedOrganization) {
        BeanUtils.copyProperties(detachedOrganization, organization, "id");
        organization = organizationRepository.save(organization);
        organizationRepository.flush();
        return organization;
    }
}
