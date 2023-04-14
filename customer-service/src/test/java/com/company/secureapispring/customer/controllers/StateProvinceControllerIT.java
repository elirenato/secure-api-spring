package com.company.secureapispring.customer.controllers;


import com.company.secureapispring.common.utils.TestJWTUtils;
import com.company.secureapispring.customer.entities.Country;
import com.company.secureapispring.customer.entities.StateProvince;
import com.company.secureapispring.customer.factory.EntityFactory;
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

    private static String ENDPOINT = "/state-provinces";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetWhenAuthenticated() throws Exception {
        Country country = EntityFactory
                .country()
                .build(this.emTest);
        StateProvince expected = EntityFactory
                .stateProvince()
                .with(StateProvince::setCountry, country)
                .build(this.emTest);
        mockMvc.perform(get(StateProvinceControllerIT.ENDPOINT + "/" + expected.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(expected.getId())))
                .andExpect(jsonPath("abbreviation", is(expected.getAbbreviation())))
                .andExpect(jsonPath("name", is(expected.getName())))
                .andExpect(jsonPath("country.id", is(country.getId().intValue())));
    }

    @Test
    public void testGetWithoutAuthenticationThenFail() throws Exception {
        Integer id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(StateProvinceControllerIT.ENDPOINT + "/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetWhenAuthenticatedAndNotFoundThenFail() throws Exception {
        Integer id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(StateProvinceControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindAllWhenAuthenticated() throws Exception {
        Country country = EntityFactory
                .country()
                .build(this.emTest);
        List<StateProvince> expectedCountries =  IntStream
                .range(1, EntityFactory.getFaker().number().numberBetween(2, 5))
                .mapToObj(n -> EntityFactory
                        .stateProvince()
                        .with(StateProvince::setCountry, country)
                        .build(this.emTest)
                )
                .sorted(Comparator.comparing(StateProvince::getName))
                .collect(Collectors.toList());
        int lastIndex = expectedCountries.size()-1;
        mockMvc.perform(get(StateProvinceControllerIT.ENDPOINT)
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
                        get(StateProvinceControllerIT.ENDPOINT)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
