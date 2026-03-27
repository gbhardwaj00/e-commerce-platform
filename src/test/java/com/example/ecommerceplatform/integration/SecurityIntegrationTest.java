package com.example.ecommerceplatform.integration;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SecurityIntegrationTest extends AbstractIntegrationTest {

    @Test
    void cartEndpointWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/api/v1/carts"))
                .andExpect(status().isUnauthorized());
    }
}
