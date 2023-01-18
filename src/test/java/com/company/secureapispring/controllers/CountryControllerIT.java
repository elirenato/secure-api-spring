package com.company.secureapispring.controllers;


import com.company.secureapispring.SecureApiSpringIT;
import com.company.secureapispring.entities.Country;
import com.company.secureapispring.factory.Factory;
import com.company.secureapispring.repositories.CountryRepository;
import com.company.secureapispring.utils.TestJWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CountryControllerIT extends AbstractIT {

    private static String COUNTRY_ENDPOINT = "/api/countries";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    public void testGetCountryWhenAuthenticated() throws Exception {
        Country expectedCountry = Factory
                .country()
                .build(countryRepository::saveAndFlush);
        mockMvc.perform(get(CountryControllerIT.COUNTRY_ENDPOINT + "/" + expectedCountry.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(expectedCountry.getId())))
                .andExpect(jsonPath("abbreviation", is(expectedCountry.getAbbreviation())))
                .andExpect(jsonPath("name", is(expectedCountry.getName())));
    }

    @Test
    public void testGetCountryWithoutAuthenticationThenFail() throws Exception {
        Country expectedCountry = Factory
                .country()
                .build(countryRepository::saveAndFlush);
        mockMvc.perform(get(CountryControllerIT.COUNTRY_ENDPOINT + "/" + expectedCountry.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetCountryWhenAuthenticatedAndNotFoundThenFail() throws Exception {
        mockMvc.perform(get(CountryControllerIT.COUNTRY_ENDPOINT + "/" + Integer.MAX_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testListCountriesWhenAuthenticated() throws Exception {
        List<Country> expectedCountries =  IntStream
                .range(1, Factory.getFaker().number().numberBetween(2, 5))
                .mapToObj(n -> Factory
                        .country()
                        .build(countryRepository::saveAndFlush)
                )
                .sorted(Comparator.comparing(Country::getName))
                .collect(Collectors.toList());
        int lastIndex = expectedCountries.size()-1;
        mockMvc.perform(get(CountryControllerIT.COUNTRY_ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedCountries.size())))
                .andExpect(jsonPath("[0].name", equalTo(expectedCountries.get(0).getName())))
                .andExpect(jsonPath(String.format("[%d].name", lastIndex), equalTo(expectedCountries.get(lastIndex).getName())));
    }

    @Test
    public void testListCountriesWithoutAuthentication() throws Exception {
        this.mockMvc.perform(
                        get(CountryControllerIT.COUNTRY_ENDPOINT)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
