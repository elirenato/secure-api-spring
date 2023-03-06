package com.company.secureapispring.core.controllers;

import com.company.secureapispring.common.factory.EntityBuilder;
import com.company.secureapispring.common.utils.TestJWTUtils;
import com.company.secureapispring.core.entities.Country;
import com.company.secureapispring.core.entities.Customer;
import com.company.secureapispring.core.entities.StateProvince;
import com.company.secureapispring.core.factory.EntityFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerControllerIT extends AbstractIT {

    private static String ENDPOINT = "/customers";

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    private EntityBuilder<Customer> customerBuilder() {
        Country country = EntityFactory
                .country()
                .build(this.emTest);
        StateProvince stateProvince = EntityFactory
                .stateProvince()
                .with(StateProvince::setCountry, country)
                .build(this.emTest);
        return EntityFactory
                .customer()
                .with(Customer::setStateProvince, stateProvince);
    }

    @Test
    public void testCreateWhenAuthorized() throws Exception {
        Customer input = customerBuilder().build();
        MvcResult result = mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("managers"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isCreated())
                .andReturn();
        Long newId = objectMapper.readValue(result.getResponse().getContentAsString(), Customer.class).getId();
        Customer actual = this.emTest.find(Customer.class, newId);
        assertEquals(input.getPostalCode(), actual.getPostalCode());
        assertEquals(input.getFirstName(), actual.getFirstName());
        assertEquals(input.getLastName(), actual.getLastName());
        assertEquals(input.getAddress(), actual.getAddress());
        assertEquals(input.getAddress2(), actual.getAddress2());
        assertEquals(input.getEmail(), actual.getEmail());
        assertEquals(input.getStateProvince(), actual.getStateProvince());
    }

    @Test
    public void testCreateWhenIdIsNotNullThenFail() throws Exception {
        Customer input = customerBuilder().build();
        input.setId(EntityFactory.getFaker().number().randomNumber());
        mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("managers"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors.id", is("must be null")));
    }

    @Test
    public void testCreateWhenInvalidInputThenFail() throws Exception {
        Customer input = new Customer();
        mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("managers"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors.firstName", is("must not be blank")))
                .andExpect(jsonPath("errors.lastName", is("must not be blank")))
                .andExpect(jsonPath("errors.address", is("must not be blank")))
                .andExpect(jsonPath("errors.postalCode", is("must not be blank")))
                .andExpect(jsonPath("errors.stateProvince", is("must not be null")))
                .andExpect(jsonPath("errors.email", is("must not be blank")));
    }

    @Test
    public void testCreateWhenEmailIsInvalidThenFail() throws Exception {
        Customer input = customerBuilder().build();
        input.setEmail("email");
        mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("managers"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors.email", is("must be a well-formed email address")));
    }

    @Test
    public void testCreateWhenForbiddenThenFail() throws Exception {
        Customer input = customerBuilder().build();
        mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("operators"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetWhenAuthenticated() throws Exception {
        Customer expected = customerBuilder().build(this.emTest);
        mockMvc.perform(get(CustomerControllerIT.ENDPOINT + "/" + expected.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(expected.getId().intValue())))
                .andExpect(jsonPath("firstName", is(expected.getFirstName())))
                .andExpect(jsonPath("lastName", is(expected.getLastName())))
                .andExpect(jsonPath("email", is(expected.getEmail())))
                .andExpect(jsonPath("address", is(expected.getAddress())))
                .andExpect(jsonPath("postalCode", is(expected.getPostalCode())))
                .andExpect(jsonPath("stateProvince.id", is(expected.getStateProvince().getId())));
    }

    @Test
    public void testGetWithoutAuthenticationThenFail() throws Exception {
        Integer id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(CustomerControllerIT.ENDPOINT + "/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetWhenNotFoundThenFail() throws Exception {
        Integer id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(CustomerControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateWhenAuthorized() throws Exception {
        Customer existent = customerBuilder().build(this.emTest);
        Customer input = customerBuilder().build();
        input.setId(existent.getId());
        MvcResult result = mockMvc.perform(put(CustomerControllerIT.ENDPOINT + "/" + existent.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("managers"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andReturn();
        Long newId = objectMapper.readValue(result.getResponse().getContentAsString(), Customer.class).getId();
        Customer actual = this.emTest.find(Customer.class, newId);
        assertEquals(input.getPostalCode(), actual.getPostalCode());
        assertEquals(input.getFirstName(), actual.getFirstName());
        assertEquals(input.getLastName(), actual.getLastName());
        assertEquals(input.getAddress(), actual.getAddress());
        assertEquals(input.getAddress2(), actual.getAddress2());
        assertEquals(input.getEmail(), actual.getEmail());
        assertEquals(input.getStateProvince(), actual.getStateProvince());
    }

    @Test
    public void testUpdateWhenNotFoundThenFail() throws Exception {
        Integer id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(CustomerControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("managers")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateNewWhenIdIsDifferentThenFail() throws Exception {
        Customer input = customerBuilder().build();
        input.setId(EntityFactory.getFaker().number().numberBetween(1001l, 2000l));
        Long randomId = EntityFactory.getFaker().number().numberBetween(1l, 1000l);
        mockMvc.perform(put(CustomerControllerIT.ENDPOINT + "/" + randomId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("managers"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors.id", is("must be equals id from url")));
    }

    @Test
    public void testUpdateWhenInvalidInputThenFail() throws Exception {
        Customer input = new Customer();
        input.setId(EntityFactory.getFaker().number().numberBetween(1001l, 2000l));
        mockMvc.perform(put(CustomerControllerIT.ENDPOINT + "/" + input.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("managers"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateWhenForbiddenThenFail() throws Exception {
        Customer input = customerBuilder().build();
        input.setId(EntityFactory.getFaker().number().numberBetween(1001l, 2000l));
        mockMvc.perform(put(CustomerControllerIT.ENDPOINT + "/" + input.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("operators"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteWhenAuthorized() throws Exception {
        Customer entity = customerBuilder().build(this.emTest);
        mockMvc.perform(delete(CustomerControllerIT.ENDPOINT + "/" + entity.getId())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("managers"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(entity.getId().intValue())))
                .andExpect(jsonPath("firstName", is(entity.getFirstName())))
                .andExpect(jsonPath("lastName", is(entity.getLastName())))
                .andExpect(jsonPath("email", is(entity.getEmail())))
                .andExpect(jsonPath("address", is(entity.getAddress())))
                .andExpect(jsonPath("postalCode", is(entity.getPostalCode())))
                .andExpect(jsonPath("stateProvince.id", is(entity.getStateProvince().getId())));
    }

    @Test
    public void testDeleteWhenNotFoundThenFail() throws Exception {
        Integer id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(delete(CustomerControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("managers")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteWhenForbiddenThenFail() throws Exception {
        Integer id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(delete(CustomerControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("operators"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void testListAllWhenAuthenticated() throws Exception {
        List<Customer> expectedCustomers = IntStream
                .range(1, EntityFactory.getFaker().number().numberBetween(1, 10))
                .mapToObj(n -> customerBuilder().build(this.emTest)
                )
                .sorted(Comparator
                        .comparing(Customer::getLastName)
                        .thenComparing(Customer::getFirstName)
                )
                .collect(Collectors.toList());

        mockMvc.perform(get(CustomerControllerIT.ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(expectedCustomers.size())))
                .andExpect(jsonPath("$[*].stateProvince", is(empty())))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void testListAllWhenForbiddenThenFail() throws Exception {
        mockMvc.perform(get(CustomerControllerIT.ENDPOINT))
                .andExpect(status().isUnauthorized());
    }
}
