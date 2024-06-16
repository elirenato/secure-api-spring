package com.company.secureapispring.customer.controllers;

import com.company.secureapispring.common.factory.EntityBuilder;
import com.company.secureapispring.common.utils.TestJWTUtils;
import com.company.secureapispring.customer.entities.Country;
import com.company.secureapispring.customer.entities.Customer;
import com.company.secureapispring.customer.entities.StateProvince;
import com.company.secureapispring.customer.factory.EntityFactory;
import com.company.secureapispring.customer.repositories.CountryRepository;
import com.company.secureapispring.customer.repositories.CustomerRepository;
import com.company.secureapispring.customer.repositories.StateProvinceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerControllerIT extends AbstractIT {

    private static final String ENDPOINT = "/customers";

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateProvinceRepository stateProvinceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private EntityBuilder<Customer> customerBuilder() {
        Country country = EntityFactory
                .country()
                .persit(countryRepository);
        StateProvince stateProvince = EntityFactory
                .stateProvince()
                .with(StateProvince::setCountry, country)
                .persit(stateProvinceRepository);
        return EntityFactory
                .customer()
                .with(Customer::setStateProvince, stateProvince);
    }

    @Test
    public void testCreateWhenAuthorized() throws Exception {
        Customer input = customerBuilder().make();
        MvcResult result = mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ADMIN"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isCreated())
                .andReturn();
        Long newId = objectMapper.readValue(result.getResponse().getContentAsString(), Customer.class).getId();
        Customer actual = customerRepository.getReferenceById(newId);
        assertEquals(input.getPostalCode(), actual.getPostalCode());
        assertEquals(input.getFirstName(), actual.getFirstName());
        assertEquals(input.getLastName(), actual.getLastName());
        assertEquals(input.getAddress(), actual.getAddress());
        assertEquals(input.getAddress2(), actual.getAddress2());
        assertEquals(input.getEmail(), actual.getEmail());
        assertEquals(input.getStateProvince(), actual.getStateProvince());
    }

    @Test
    public void testCreateWhenEmailAlreadyExists() throws Exception {
        // create and persist a customer
        Customer input = customerBuilder().persit(customerRepository);

        // set the id to null to try to create another customer with the same email
        input.setId(null);

        String jsonResponse = mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ADMIN"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProblemDetail problemDetail = objectMapper.readValue(jsonResponse, ProblemDetail.class);
        assertEquals("There is already a customer registered with this email address. Please use a different email.", problemDetail.getDetail());
    }

    @Test
    public void testCreateWhenInvalidStateProvince() throws Exception {
        Customer input = customerBuilder().make();

        StateProvince stateProvince = new StateProvince();
        stateProvince.setId(EntityFactory.getFaker().random().nextInt(100));
        input.setStateProvince(stateProvince);

        String jsonResponse = mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ADMIN"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProblemDetail problemDetail = objectMapper.readValue(jsonResponse, ProblemDetail.class);
        assertEquals("Please enter a valid State/Province.", problemDetail.getDetail());
    }

    @Test
    public void testCreateWhenIdIsNotNullThenFail() throws Exception {
        Customer input = customerBuilder().make();
        input.setId(EntityFactory.getFaker().number().randomNumber());
        mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ADMIN"))
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
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ADMIN"))
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
        Customer input = customerBuilder().make();
        input.setEmail("email");
        mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ADMIN"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors.email", is("must be a well-formed email address")));
    }

    @Test
    public void testCreateWhenForbiddenThenFail() throws Exception {
        Customer input = customerBuilder().make();
        mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ANALYST"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetWhenRegularUserAuthenticated() throws Exception {
        Customer expected = customerBuilder().persit(this.customerRepository);
        mockMvc.perform(get(CustomerControllerIT.ENDPOINT + "/" + expected.getId())
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ANALYST")))
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
        int id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(CustomerControllerIT.ENDPOINT + "/" + id))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testGetWhenNotFoundThenFail() throws Exception {
        int id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(CustomerControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ANALYST")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateWhenAuthorized() throws Exception {
        Customer existent = customerBuilder().persit(this.customerRepository);
        Customer input = customerBuilder().make();
        input.setId(existent.getId());
        MvcResult result = mockMvc.perform(put(CustomerControllerIT.ENDPOINT + "/" + existent.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ADMIN"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andReturn();
        Long newId = objectMapper.readValue(result.getResponse().getContentAsString(), Customer.class).getId();
        Customer actual = this.customerRepository.getReferenceById(newId);
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
        int id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(CustomerControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateNewWhenIdIsDifferentThenFail() throws Exception {
        Customer input = customerBuilder().make();
        input.setId(EntityFactory.getFaker().number().numberBetween(1001L, 2000L));
        long randomId = EntityFactory.getFaker().number().numberBetween(1L, 1000L);
        mockMvc.perform(put(CustomerControllerIT.ENDPOINT + "/" + randomId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ADMIN"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors.id", is("must be equals id from url")));
    }

    @Test
    public void testUpdateWhenInvalidInputThenFail() throws Exception {
        Customer input = new Customer();
        input.setId(EntityFactory.getFaker().number().numberBetween(1001L, 2000L));
        mockMvc.perform(put(CustomerControllerIT.ENDPOINT + "/" + input.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ADMIN"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testUpdateWhenForbiddenThenFail() throws Exception {
        Customer input = customerBuilder().make();
        input.setId(EntityFactory.getFaker().number().numberBetween(1001L, 2000L));
        mockMvc.perform(put(CustomerControllerIT.ENDPOINT + "/" + input.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ANALYST"))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteWhenAuthorized() throws Exception {
        Customer entity = customerBuilder().persit(customerRepository);
        mockMvc.perform(delete(CustomerControllerIT.ENDPOINT + "/" + entity.getId())
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ADMIN"))
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
        int id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(delete(CustomerControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteWhenForbiddenThenFail() throws Exception {
        int id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(delete(CustomerControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ANALYST"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void testListAllWhenAuthenticated() throws Exception {
        List<Customer> expectedCustomers = IntStream
                .range(1, EntityFactory.getFaker().number().numberBetween(1, 10))
                .mapToObj(n -> customerBuilder().persit(this.customerRepository)
                )
                .sorted(Comparator
                        .comparing(Customer::getLastName)
                        .thenComparing(Customer::getFirstName)
                )
                .toList();

        mockMvc.perform(get(CustomerControllerIT.ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader("ROLE_ANALYST")))
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
