package com.company.secureapispring.controllers;


import com.company.secureapispring.entities.Country;
import com.company.secureapispring.entities.StateProvince;
import com.company.secureapispring.factory.Factory;
import com.company.secureapispring.repositories.CountryRepository;
import com.company.secureapispring.repositories.StateProvinceRepository;
import com.company.secureapispring.utils.TestJWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StateProvinceControllerIT extends AbstractIT {

    private static String STATE_PROVINCES_ENDPOINT = "/api/state-provinces";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateProvinceRepository stateProvinceRepository;

    @Test
    public void testGetWhenAuthenticated() throws Exception {
        Country country = Factory
                .country()
                .build(countryRepository::saveAndFlush);
        StateProvince expected = Factory
                .stateProvince()
                .with(StateProvince::setCountry, country)
                .build(stateProvinceRepository::saveAndFlush);
        mockMvc.perform(get(StateProvinceControllerIT.STATE_PROVINCES_ENDPOINT + "/" + expected.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(expected.getId())))
                .andExpect(jsonPath("abbreviation", is(expected.getAbbreviation())))
                .andExpect(jsonPath("name", is(expected.getName())))
                .andExpect(jsonPath("country.id", is(country.getId().intValue())));
    }

    @Test
    public void testGetWithoutAuthenticationThenFail() throws Exception {
        Integer id = Factory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(StateProvinceControllerIT.STATE_PROVINCES_ENDPOINT + "/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetWhenAuthenticatedAndNotFoundThenFail() throws Exception {
        Integer id = Factory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(StateProvinceControllerIT.STATE_PROVINCES_ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindAllWhenAuthenticated() throws Exception {
        Country country = Factory
                .country()
                .build(countryRepository::saveAndFlush);
        List<StateProvince> expectedCountries =  IntStream
                .range(1, Factory.getFaker().number().numberBetween(2, 5))
                .mapToObj(n -> Factory
                        .stateProvince()
                        .with(StateProvince::setCountry, country)
                        .build(stateProvinceRepository::saveAndFlush)
                )
                .sorted(Comparator.comparing(StateProvince::getName))
                .collect(Collectors.toList());
        int lastIndex = expectedCountries.size()-1;
        mockMvc.perform(get(StateProvinceControllerIT.STATE_PROVINCES_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedCountries.size())))
                .andExpect(jsonPath("[0].name", equalTo(expectedCountries.get(0).getName())))
                .andExpect(jsonPath("[0].country.id", is(country.getId().intValue())))
                .andExpect(jsonPath(
                        String.format("[%d].name", lastIndex),
                        equalTo(expectedCountries.get(lastIndex).getName())
                ))
                .andExpect(jsonPath(
                        String.format("[%d].country.id", lastIndex),
                        is(country.getId().intValue())
                ));
    }

    @Test
    public void testListCountriesWithoutAuthentication() throws Exception {
        this.mockMvc.perform(
                        get(StateProvinceControllerIT.STATE_PROVINCES_ENDPOINT)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
