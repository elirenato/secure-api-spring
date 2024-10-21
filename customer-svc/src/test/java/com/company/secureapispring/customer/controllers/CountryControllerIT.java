package com.company.secureapispring.customer.controllers;


import com.company.secureapispring.auth.utils.TestJWTUtils;
import com.company.secureapispring.customer.AbstractIT;
import com.company.secureapispring.customer.CustomerSvcAppIT;
import com.company.secureapispring.customer.entities.Country;
import com.company.secureapispring.customer.entities.StateProvince;
import com.company.secureapispring.customer.factory.EntityFactory;
import com.company.secureapispring.customer.repositories.CountryRepository;
import com.company.secureapispring.customer.repositories.StateProvinceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@CustomerSvcAppIT
@Transactional
public class CountryControllerIT extends AbstractIT {

    private static final String ENDPOINT = "/countries";
    private static final String STATE_PROVINCES_ENDPOINT = "/countries/%d/state-provinces";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateProvinceRepository stateProvinceRepository;

    @Test
    public void testFindAllCountriesWhenAuthenticated() throws Exception {
        List<Country> expectedCountries = new ArrayList<>();
        expectedCountries.add(EntityFactory
                .country()
                        .with(Country::setAbbreviation, "USA")
                .persit(countryRepository));
        expectedCountries.add(EntityFactory
                .country()
                .with(Country::setAbbreviation, "BRA")
                .persit(countryRepository));
        expectedCountries.add(EntityFactory
                .country()
                .with(Country::setAbbreviation, "CAN")
                .persit(countryRepository));
        expectedCountries.sort(Comparator.comparing(Country::getName));

        mockMvc.perform(get(CountryControllerIT.ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "any"
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedCountries.size())))
                .andExpect(jsonPath("[0].name", equalTo(expectedCountries.getFirst().getName())))
                .andExpect(jsonPath(String.format(
                        "[%d].name",
                        expectedCountries.size()-1),
                        equalTo(expectedCountries.getLast().getName())
                ));
    }

    @Test
    public void testFindAllCountriesWithoutAuthentication() throws Exception {
        this.mockMvc.perform(
                        get(CountryControllerIT.ENDPOINT)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private String getBaseEndpointForStateProvinces(Integer countryId) {
        return String.format(CountryControllerIT.STATE_PROVINCES_ENDPOINT, countryId);
    }

    @Test
    public void testFindAllStateProvincesWhenAuthenticated() throws Exception {
        Country country = EntityFactory
                .country()
                .persit(countryRepository);

        List<StateProvince> expectedStateProvinces = new ArrayList<>();
        expectedStateProvinces.add(EntityFactory
                .stateProvince()
                .with(StateProvince::setAbbreviation, "BC")
                .with(StateProvince::setCountry, country)
                .persit(stateProvinceRepository));
        expectedStateProvinces.add(EntityFactory
                .stateProvince()
                .with(StateProvince::setAbbreviation, "AB")
                .with(StateProvince::setCountry, country)
                .persit(stateProvinceRepository));
        expectedStateProvinces.add(EntityFactory
                .stateProvince()
                .with(StateProvince::setAbbreviation, "ON")
                .with(StateProvince::setCountry, country)
                .persit(stateProvinceRepository));
        expectedStateProvinces.sort(Comparator.comparing(StateProvince::getName));

        int lastIndex = expectedStateProvinces.size()-1;
        mockMvc.perform(get(getBaseEndpointForStateProvinces(country.getId()))
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "any"
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedStateProvinces.size())))
                .andExpect(jsonPath("[0].name", equalTo(expectedStateProvinces.getFirst().getName())))
                .andExpect(jsonPath("[0].country.id", is(country.getId())))
                .andExpect(jsonPath(
                        String.format("[%d].name", lastIndex),
                        equalTo(expectedStateProvinces.getLast().getName())
                ))
                .andExpect(jsonPath(
                        String.format("[%d].country.id", lastIndex),
                        is(country.getId())
                ));
    }

    @Test
    public void testFindAllStateProvincesWithoutAuthentication() throws Exception {
        Country country = EntityFactory
                .country()
                .persit(countryRepository);
        this.mockMvc.perform(
                        get(getBaseEndpointForStateProvinces(country.getId()))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
