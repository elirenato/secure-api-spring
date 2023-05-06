package com.company.secureapispring.customer.controllers;

import com.company.secureapispring.common.utils.TestJWTUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AppControllerIT extends AbstractIT {
    private static String ENDPOINT = "/app";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAppBuild() throws Exception {
        mockMvc.perform(get(AppControllerIT.ENDPOINT + "/build")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + TestJWTUtils.encode("any")))
                .andExpect(status().isOk());
    }
}
