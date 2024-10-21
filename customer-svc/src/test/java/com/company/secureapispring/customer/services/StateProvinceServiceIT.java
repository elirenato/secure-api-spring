package com.company.secureapispring.customer.services;

import com.company.secureapispring.customer.AbstractIT;
import com.company.secureapispring.customer.CustomerSvcAppIT;
import com.company.secureapispring.customer.entities.Country;
import com.company.secureapispring.customer.entities.StateProvince;
import com.company.secureapispring.customer.factory.EntityFactory;
import com.company.secureapispring.customer.repositories.CountryRepository;
import com.company.secureapispring.customer.repositories.StateProvinceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@CustomerSvcAppIT
@Transactional
public class StateProvinceServiceIT extends AbstractIT {
    @Autowired
    private StateProvinceService stateProvinceService;

    @Autowired
    private CountryRepository countryRepository;

    @SpyBean
    private StateProvinceRepository stateProvinceRepository;

    @Test
    public void testFindAllIsCacheable() {
        Country country = EntityFactory
                .country()
                .with(Country::setAbbreviation, "CAN")
                .persit(countryRepository);

        EntityFactory
                .stateProvince()
                .with(StateProvince::setAbbreviation, "BC")
                .with(StateProvince::setCountry, country)
                .persit(stateProvinceRepository);

        stateProvinceService.findAll(country.getId());
        stateProvinceService.findAll(country.getId());

        Mockito.verify(stateProvinceRepository, Mockito.times(1)).findAllByCountryId(country.getId(), Sort.by("name"));
    }
}
