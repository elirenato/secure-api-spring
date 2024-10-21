package com.company.secureapispring.customer.controllers;

import com.company.secureapispring.auth.utils.TestJWTUtils;
import com.company.secureapispring.customer.AbstractIT;
import com.company.secureapispring.customer.CustomerSvcAppIT;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@CustomerSvcAppIT
public class AppControllerIT extends AbstractIT {
    private static final String ENDPOINT = "/app";

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAppBuild() throws Exception {
        mockMvc.perform(get(AppControllerIT.ENDPOINT + "/build")
                        .header(HttpHeaders.AUTHORIZATION, TestJWTUtils.getAuthHeader(
                                this.getDechatedUser(),
                                this.getDetachedOrganization(),
                                "any"
                        )))
                .andExpect(status().isOk());
    }
}
