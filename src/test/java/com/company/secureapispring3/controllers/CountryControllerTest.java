package com.company.secureapispring3.controllers;


import com.company.secureapispring3.SecureApiSpring3ApplicationIT;
import com.company.secureapispring3.utils.TestJWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SecureApiSpring3ApplicationIT
@AutoConfigureMockMvc
public class CountryControllerTest {

    private static String COUNTRY_ENDPOINT = "/api/countries";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testListCountries() throws Exception {
        this.mockMvc.perform(
                    get(CountryControllerTest.COUNTRY_ENDPOINT)
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("managers") )
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(
                        content().string(
                                containsString("[{\"id\":1,\"abbreviation\":\"BR\",\"name\":\"Brazil\"}]")
                        )
                );
    }

    @Test
    public void testListCountriesWithoutAuthorization() throws Exception {
        this.mockMvc.perform(
                        get(CountryControllerTest.COUNTRY_ENDPOINT)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("operators") )
                )
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void testListCountriesWithoutAuthentication() throws Exception {
        this.mockMvc.perform(
                        get(CountryControllerTest.COUNTRY_ENDPOINT)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
