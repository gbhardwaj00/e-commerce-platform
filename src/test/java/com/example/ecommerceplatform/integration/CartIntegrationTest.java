package com.example.ecommerceplatform.integration;

import com.example.ecommerceplatform.catalog.product.Product;
import com.example.ecommerceplatform.catalog.product.ProductRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CartIntegrationTest extends AbstractIntegrationTest {
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

    @Test
    void cannotAddQuantityMoreThanAvailable() throws Exception {
        registerUser("cartUser3@example.com", "R@nd0mPa$$w0rd");
        String token = loginAndGetToken("cartUser3@example.com", "R@nd0mPa$$w0rd");

        UUID prodId = UUID.randomUUID();
        Product p = new Product();
        p.setId(prodId);
        p.setTitle("29-inch Monitor");
        p.setDescription("1440p IPS Monitor");
        p.setPriceCents(34900);
        p.setQuantityAvailable(5);
        p.setCurrency("CAD");
        p.setCreatedAt(OffsetDateTime.now());
        p.setUpdatedAt(OffsetDateTime.now());
        productRepository.save(p);

        String addProdBoyd = """
                {
                    "productId" : "%s",
                    "quantity" : 7
                }
                """.formatted(prodId);

        mockMvc.perform(post("/api/v1/carts/items")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(addProdBoyd))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Requested quantity exceeds available stock"));

    }
}
