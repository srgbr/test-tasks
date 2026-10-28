package com.bank.api;

import com.bank.dao.AccountRepository;
import com.bank.dao.entity.AccountEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
class TransferApiTest {

    static {
        // Docker 29 rejects API < 1.44; docker-java/testcontainers still default to 1.32
        System.setProperty("api.version", "1.44");
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:13.7-alpine").asCompatibleSubstituteFor("postgres"))
            .withDatabaseName("bank")
            .withUsername("bank")
            .withPassword("bank");

    @DynamicPropertySource
    static void datasourceProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AccountRepository accountRepository;

    @Test
    void loginAndTransferViaApi() throws Exception {
        BigDecimal fromBefore = balanceOf(1L);
        BigDecimal toBefore = balanceOf(2L);

        MvcResult login = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ivan@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String token = objectMapper.readTree(login.getResponse().getContentAsString()).get("token").asText();

        mockMvc.perform(post("/transfer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"toUserId\":2,\"value\":10.00}"))
                .andExpect(status().isNoContent());

        assertEquals(0, fromBefore.subtract(new BigDecimal("10.00")).compareTo(balanceOf(1L)));
        assertEquals(0, toBefore.add(new BigDecimal("10.00")).compareTo(balanceOf(2L)));
    }

    @Test
    void transferWithoutTokenIsForbidden() throws Exception {
        mockMvc.perform(post("/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"toUserId\":2,\"value\":1.00}"))
                .andExpect(status().isForbidden());
    }

    private BigDecimal balanceOf(Long userId) {
        return accountRepository.findByUserId(userId)
                .map(AccountEntity::getBalance)
                .orElseThrow();
    }
}
