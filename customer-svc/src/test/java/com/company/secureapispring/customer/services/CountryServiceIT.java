package com.company.secureapispring.customer.services;

import com.company.secureapispring.customer.AbstractIT;
import com.company.secureapispring.customer.CustomerSvcAppIT;
import com.company.secureapispring.customer.entities.Country;
import com.company.secureapispring.customer.factory.EntityFactory;
import com.company.secureapispring.customer.repositories.CountryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@CustomerSvcAppIT
@Transactional
public class CountryServiceIT extends AbstractIT {
    @Autowired
    private CountryService countryService;

    @SpyBean
    private CountryRepository countryRepository;

    @Test
    public void testFindAllIsCacheable() {
        EntityFactory
                .country()
                .with(Country::setAbbreviation, "USA")
                .persit(countryRepository);

        countryService.findAll();
        countryService.findAll();

        Mockito.verify(countryRepository, Mockito.times(1)).findAll(Sort.by("name"));
    }
}
