package com.company.secureapispring.customer.controllers;


import com.company.secureapispring.common.utils.TestJWTUtils;
import com.company.secureapispring.customer.entities.Country;
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

public class CountryControllerIT extends AbstractIT {

    private static String ENDPOINT = "/countries";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetWhenAuthenticated() throws Exception {
        Country expected = EntityFactory
                .country()
                .build(this.emTest);
        mockMvc.perform(get(CountryControllerIT.ENDPOINT + "/" + expected.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(expected.getId())))
                .andExpect(jsonPath("abbreviation", is(expected.getAbbreviation())))
                .andExpect(jsonPath("name", is(expected.getName())));
    }

    @Test
    public void testGetWithoutAuthenticationThenFail() throws Exception {
        Integer id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(CountryControllerIT.ENDPOINT + "/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetWhenAuthenticatedAndNotFoundThenFail() throws Exception {
        Integer id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(CountryControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testFindAllWhenAuthenticated() throws Exception {
        List<Country> expected =  IntStream
                .range(1, EntityFactory.getFaker().number().numberBetween(2, 5))
                .mapToObj(n -> EntityFactory
                        .country()
                        .build(this.emTest)
                )
                .sorted(Comparator.comparing(Country::getName))
                .collect(Collectors.toList());
        int lastIndex = expected.size()-1;
        mockMvc.perform(get(CountryControllerIT.ENDPOINT)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expected.size())))
                .andExpect(jsonPath("[0].name", equalTo(expected.get(0).getName())))
                .andExpect(jsonPath(String.format("[%d].name", lastIndex), equalTo(expected.get(lastIndex).getName())));
    }

    @Test
    public void testFindAllWithoutAuthentication() throws Exception {
        this.mockMvc.perform(
                        get(CountryControllerIT.ENDPOINT)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
