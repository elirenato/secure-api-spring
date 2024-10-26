package com.company.secureapispring.auth.services;

import com.company.secureapispring.auth.AbstractIT;
import com.company.secureapispring.auth.AuthLibSpringBootTest;
import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.repositories.OrganizationRepository;
import com.company.secureapispring.auth.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.transaction.annotation.Transactional;

@AuthLibSpringBootTest
public class OrganizationServiceIT extends AbstractIT {

    @Autowired
    private OrganizationService organizationService;

    @SpyBean
    private OrganizationRepository organizationRepository;

    @SpyBean
    private UserRepository userRepository;

    @Test
    public void testFindByAlias() {
        organizationRepository.save(makeOrganization());
        Organization organization = organizationRepository.save(makeOrganization());

        Organization found = organizationRepository.findByAlias(organization.getAlias()).orElseThrow();

        Assertions.assertEquals(organization, found);
    }

    @Test
    public void testFindByAliasIsCacheable() {
        Organization organization = organizationRepository.save(makeOrganization());

        organizationService.findByAlias(organization.getAlias());
        organizationService.findByAlias(organization.getAlias());

        Mockito.verify(organizationRepository, Mockito.times(1)).findByAlias(organization.getAlias());
    }

    @Test
    @Transactional
    public void testSyncOrganization() {
        // mock a new organization entity to update the existent one
        Organization newOrganizationInfo = makeOrganization();

        // sync a new organization
        Organization syncResponse = organizationService.sync(new Organization(), newOrganizationInfo);

        // verify the persisted organization
        Organization persistedOrganization = organizationRepository.getReferenceById(syncResponse.getId());

        Assertions.assertEquals(syncResponse, persistedOrganization);

        Assertions.assertEquals(newOrganizationInfo, persistedOrganization);
    }
}
