package com.example.ecommerceplatform.integration;

import com.example.ecommerceplatform.catalog.product.Product;
import com.example.ecommerceplatform.catalog.product.ProductRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class CartIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void loginThenGetCartReturns200() throws Exception {
        String registerBody = """
                {
                  "email": "cartuser@example.com",
                  "password": "Password123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerBody))
                .andExpect(status().isCreated());

        String loginBody = """
                {
                    "email": "cartuser@example.com",
                  "password": "Password123"
                }
                """;

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        String token = JsonPath.read(responseBody, "$.token");

        mockMvc.perform(get("/api/v1/carts")
                .header("Authorization", "Bearer " + token ))
                .andExpect(status().isOk());
    }

    @Test
    void addItemToCartReturnsCartWithExpectedItem() throws Exception {
        UUID productId = UUID.randomUUID();

        Product product = new Product();
        product.setId(productId);
        product.setTitle("Wireless Mouse");
        product.setDescription("Ergonomic wireless mouse");
        product.setPriceCents(2999);
        product.setCurrency("USD");
        product.setQuantityAvailable(10);
        product.setCreatedAt(OffsetDateTime.now());
        product.setUpdatedAt(OffsetDateTime.now());

        productRepository.save(product);

        String registerBody = """
                {
                  "email": "cartuser2@example.com",
                  "password": "Password123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        String loginBody = """
                {
                  "email": "cartuser2@example.com",
                  "password": "Password123"
                }
                """;

        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andReturn();

        String token = JsonPath.read(
                loginResult.getResponse().getContentAsString(),
                "$.token"
        );

        String addItemBody = """
                {
                  "productId": "%s",
                  "quantity": 2
                }
                """.formatted(productId);

        mockMvc.perform(post("/api/v1/carts/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(addItemBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(productId.toString()))
                .andExpect(jsonPath("$.items[0].title").value("Wireless Mouse"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].priceCents").value(2999))
                .andExpect(jsonPath("$.items[0].lineTotalCents").value(5998))
                .andExpect(jsonPath("$.totalCents").value(5998))
                .andExpect(jsonPath("$.currency").value("USD"));
    }
}
