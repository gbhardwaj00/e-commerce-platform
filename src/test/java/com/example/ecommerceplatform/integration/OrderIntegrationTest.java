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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class OrderIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void userBCannotAccessUserAOrder() throws Exception {
        UUID productId = UUID.randomUUID();

        Product product = new Product();
        product.setId(productId);
        product.setTitle("Mechanical Keyboard");
        product.setDescription("Compact keyboard");
        product.setPriceCents(8950);
        product.setCurrency("USD");
        product.setQuantityAvailable(10);
        product.setCreatedAt(OffsetDateTime.now());
        product.setUpdatedAt(OffsetDateTime.now());
        productRepository.save(product);

        registerUser("usera@example.com", "Password123");
        String tokenA = loginAndGetToken("usera@example.com", "Password123");

        String addItemBody = """
                {
                  "productId": "%s",
                  "quantity": 1
                }
                """.formatted(productId);

        mockMvc.perform(post("/api/v1/carts/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenA)
                        .content(addItemBody))
                .andExpect(status().isOk());

        MvcResult checkoutResult = mockMvc.perform(post("/api/v1/orders/checkout")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isCreated())
                .andReturn();

        String checkoutBody = checkoutResult.getResponse().getContentAsString();
        String orderId = JsonPath.read(checkoutBody, "$.orderId");

        registerUser("userb@example.com", "Password123");
        String tokenB = loginAndGetToken("userb@example.com", "Password123");

        mockMvc.perform(get("/api/v1/orders/" + orderId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isNotFound());


    }

    @Test
    void checkoutDecrementsStockAndClearsCart() throws Exception {
        UUID prodId = UUID.randomUUID();

        Product p = new Product();
        p.setId(prodId);
        p.setTitle("27-inch Monitor");
        p.setDescription("1440p IPS Monitor");
        p.setPriceCents(24900);
        p.setQuantityAvailable(10);
        p.setCurrency("CAD");
        p.setCreatedAt(OffsetDateTime.now());
        p.setUpdatedAt(OffsetDateTime.now());
        productRepository.save(p);

        registerUser("checkoutuser@example.com", "Password@1234");
        String token = loginAndGetToken("checkoutuser@example.com", "Password@1234");

        String addProdBody = """
                {
                  "productId": "%s",
                  "quantity": 2
                }
                """.formatted(prodId);

        mockMvc.perform(post("/api/v1/carts/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(addProdBody)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.totalCents").value(49800))
                .andExpect(jsonPath("$.currency").value("CAD"));

        mockMvc.perform(post("/api/v1/orders/checkout")
                .header("Authorization", "Bearer " + token))
                .andExpect(jsonPath("$.currency").value("CAD"))
                .andExpect(jsonPath("$.totalCents").value(49800));

        Product updatedProduct = productRepository.findById(prodId).orElseThrow();
        assertThat(updatedProduct.getQuantityAvailable()).isEqualTo(8);

        mockMvc.perform(get("/api/v1/carts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.totalCents").value(0))
                .andExpect(jsonPath("$.currency").isEmpty());

    }

    @Test
    void checkoutWithoutExistingCartReturns404() throws Exception {
        registerUser("emptyCheckoutUser@example.com", "Password@1234");
        String token = loginAndGetToken("emptyCheckoutUser@example.com", "Password@1234");

        mockMvc.perform(post("/api/v1/orders/checkout")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Cart not found"));

    }

    @Test
    void cannotCheckoutExistingEmptyCart() throws Exception {
        registerUser("emptyCheckoutUser2@example.com", "Password@1234");
        String token = loginAndGetToken("emptyCheckoutUser2@example.com", "Password@1234");

        mockMvc.perform(get("/api/v1/carts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/orders/checkout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cannot checkout an empty cart"));

    }



    private String loginAndGetToken(String email, String password) throws Exception{
        String body = """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.token");
    }

    private void registerUser(String email, String password) throws Exception {
        String registerReqBody = """
                {
                    "email" : "%s",
                    "password" : "%s"
                }
                """.formatted(email, password);

        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerReqBody))
                .andExpect(status().isCreated());
    }
}
