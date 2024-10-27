package com.company.secureapispring.customer.controllers;

import com.company.secureapispring.auth.entities.Organization;
import com.company.secureapispring.auth.entities.User;
import com.company.secureapispring.auth.repositories.OrganizationRepository;
import com.company.secureapispring.customer.AbstractIT;
import com.company.secureapispring.customer.CustomerSvcSpringBootAppTest;
import com.company.secureapispring.customer.TestJWTUtils;
import com.company.secureapispring.customer.entities.Country;
import com.company.secureapispring.customer.entities.Customer;
import com.company.secureapispring.customer.entities.StateProvince;
import com.company.secureapispring.customer.factory.EntityBuilder;
import com.company.secureapispring.customer.factory.EntityFactory;
import com.company.secureapispring.customer.repositories.CountryRepository;
import com.company.secureapispring.customer.repositories.CustomerRepository;
import com.company.secureapispring.customer.repositories.StateProvinceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@CustomerSvcSpringBootAppTest
@Transactional
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

    @Autowired
    private OrganizationRepository organizationRepository;

    private StateProvince persistStateProvince() {
        Country country = EntityFactory
                .country()
                .persit(countryRepository);
        return EntityFactory
                .stateProvince()
                .with(StateProvince::setCountry, country)
                .persit(stateProvinceRepository);
    }

    private EntityBuilder<Customer> customerBuilder(StateProvince stateProvince) {
        return EntityFactory
                .customer()
                .with(Customer::setStateProvince, stateProvince);
    }

    private Customer persistCustomer(Organization organization, StateProvince stateProvince) {
        return customerBuilder(stateProvince)
                .with(Customer::setOrganization, organization)
                .with(Customer::setCreatedBy, this.getDechatedUser().getUsername())
                .persit(this.customerRepository);
    }

    private Organization persistOrganization() {
        return EntityFactory.organization().persit(this.organizationRepository);
    }

    @Test
    public void testCreateWhenAuthorized() throws Exception {
        User user = EntityFactory.user().make();
        Organization organization = EntityFactory.organization().make();
        Customer input = customerBuilder(persistStateProvince()).make();

        MvcResult result = mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                user,
                                organization,
                                "ROLE_ADMIN"
                        ))
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
        assertEquals(user.getUsername(), actual.getCreatedBy());
        assertEquals(organization, actual.getOrganization());
    }

    @Test
    public void testCreateWhenEmailAlreadyExists() throws Exception {
        StateProvince stateProvince = persistStateProvince();
        Customer existentCustomer = persistCustomer(persistOrganization(), stateProvince);

        // set the id to null to try to create another customer with the same email
        Customer input = EntityFactory.customer()
                .with(Customer::setEmail, existentCustomer.getEmail())
                .with(Customer::setStateProvince, stateProvince)
                .make();

        String jsonResponse = mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ADMIN"
                        ))
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
    public void testCreateWhenInvalidStateProvinceIdThenFailWithFK() throws Exception {
        Customer input = customerBuilder(persistStateProvince()).make();

        StateProvince stateProvince = new StateProvince();
        stateProvince.setId(EntityFactory.getFaker().random().nextInt(100));
        input.setStateProvince(stateProvince);

        String jsonResponse = mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ADMIN"
                        ))
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
        Customer input = customerBuilder(persistStateProvince()).make();
        input.setId(EntityFactory.getFaker().number().randomNumber());
        mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ADMIN"
                        ))
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
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ADMIN"
                        ))
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
        Customer input = customerBuilder(persistStateProvince()).make();
        input.setEmail("email");
        mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ADMIN"
                        ))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors.email", is("must be a well-formed email address")));
    }

    @Test
    public void testCreateWhenForbiddenThenFail() throws Exception {
        Customer input = customerBuilder(persistStateProvince()).make();
        mockMvc.perform(post(CustomerControllerIT.ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ANALYST"
                        ))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetWhenAnalystIsAuthenticated() throws Exception {
        Organization organization = persistOrganization();
        Customer expected = persistCustomer(organization, persistStateProvince());
        mockMvc.perform(get(CustomerControllerIT.ENDPOINT + "/" + expected.getId())
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                organization,
                                "ROLE_ANALYST"
                        )))
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
    public void testGetCustomerWhenBelongsToAnotherOrganization() throws Exception {
        Organization organization = persistOrganization();
        Customer expected = persistCustomer(organization, persistStateProvince());

        Organization organization2 = persistOrganization();

        mockMvc.perform(get(CustomerControllerIT.ENDPOINT + "/" + expected.getId())
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                organization2,
                                "ROLE_ADMIN"
                        )))
                .andExpect(status().isForbidden());
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
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ANALYST"
                        )))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateWhenAuthorized() throws Exception {
        Organization organization = persistOrganization();
        StateProvince stateProvince = persistStateProvince();
        Customer existent = persistCustomer(organization, stateProvince);
        Customer input = customerBuilder(stateProvince).make();
        input.setId(existent.getId());

        MvcResult result = mockMvc.perform(put(CustomerControllerIT.ENDPOINT + "/" + existent.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                organization,
                                "ROLE_ADMIN"
                        ))
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
    public void testUpdateWhenTheCustomerBelongsToAnotherOrganizationThenFail() throws Exception {
        Organization organization = persistOrganization();
        StateProvince stateProvince = persistStateProvince();
        Customer existent = persistCustomer(organization, stateProvince);

        Customer input = customerBuilder(stateProvince).make();
        input.setId(existent.getId());

        Organization organization2 = persistOrganization();

        mockMvc.perform(put(CustomerControllerIT.ENDPOINT + "/" + existent.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                organization2,
                                "ROLE_ADMIN"
                        ))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateWhenNotFoundThenFail() throws Exception {
        int id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(get(CustomerControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ADMIN"
                        )))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateNewWhenIdIsDifferentThenFail() throws Exception {
        Customer input = customerBuilder(persistStateProvince()).make();
        input.setId(EntityFactory.getFaker().number().numberBetween(1001L, 2000L));
        long randomId = EntityFactory.getFaker().number().numberBetween(1L, 1000L);
        mockMvc.perform(put(CustomerControllerIT.ENDPOINT + "/" + randomId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ADMIN"
                        ))
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
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ADMIN"
                        ))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isBadRequest());
    }

    // only admin can update
    @Test
    public void testUpdateWhenAnalystTryToUpdateThenFail() throws Exception {
        Customer input = customerBuilder(persistStateProvince()).make();
        input.setId(EntityFactory.getFaker().number().numberBetween(1001L, 2000L));
        mockMvc.perform(put(CustomerControllerIT.ENDPOINT + "/" + input.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ANALYST"
                        ))
                        .content(objectMapper.writeValueAsString(input))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteWhenAuthorized() throws Exception {
        Organization organization = persistOrganization();
        StateProvince stateProvince = persistStateProvince();
        Customer entity = persistCustomer(organization, stateProvince);
        mockMvc.perform(delete(CustomerControllerIT.ENDPOINT + "/" + entity.getId())
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                organization,
                                "ROLE_ADMIN"
                        ))
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
    public void testDeleteWhenCustomerBelongsToAnotherOrganizationThenFail() throws Exception {
        Organization organization = persistOrganization();
        StateProvince stateProvince = persistStateProvince();
        Customer entity = persistCustomer(organization, stateProvince);

        Organization organization2 = persistOrganization();

        mockMvc.perform(delete(CustomerControllerIT.ENDPOINT + "/" + entity.getId())
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                organization2,
                                "ROLE_ADMIN"
                        ))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    public void testDeleteWhenNotFoundThenFail() throws Exception {
        int id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(delete(CustomerControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ADMIN"
                        )))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteWhenAnalystTryToDeleteThenFail() throws Exception {
        int id = EntityFactory.getFaker().number().randomDigitNotZero();
        mockMvc.perform(delete(CustomerControllerIT.ENDPOINT + "/" + id)
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "ROLE_ANALYST"
                        ))
                )
                .andExpect(status().isForbidden());
    }

    private List<Customer> persistCustomers(Organization organization, StateProvince stateProvince) {
        return IntStream
                .range(1, EntityFactory.getFaker().number().numberBetween(1, 5))
                .mapToObj(n -> persistCustomer(organization, stateProvince))
                .sorted(Comparator
                        .comparing(Customer::getLastName)
                        .thenComparing(Customer::getFirstName)
                )
                .toList();
    }

    @Test
    public void testListAllWhenAuthenticated() throws Exception {
        Organization organization1 = persistOrganization();
        StateProvince stateProvince = persistStateProvince();
        List<Customer> customersOfOrganization1 = persistCustomers(organization1, stateProvince);

        // the customer of organization2 should not be listed
        Organization organization2 = persistOrganization();
        persistCustomers(organization2, stateProvince);

        mockMvc.perform(get(CustomerControllerIT.ENDPOINT)
                .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                        this.getDechatedUser(),
                        organization1,
                        "ROLE_ANALYST"
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(customersOfOrganization1.size())))
                .andExpect(jsonPath("$[*].stateProvince", is(empty())))
                .andExpect(jsonPath("$[*].organization.id", everyItem(is(organization1.getId()))))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    public void testListAllWhenForbiddenThenFail() throws Exception {
        mockMvc.perform(get(CustomerControllerIT.ENDPOINT))
                .andExpect(status().isUnauthorized());
    }
}
