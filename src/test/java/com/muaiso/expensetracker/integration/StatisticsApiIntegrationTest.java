package com.muaiso.expensetracker.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.muaiso.expensetracker.dto.ExpenseRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StatisticsApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper mapper;

    private void create(String amount, int y, int m, int d, String category) {
        ExpenseRequest req = new ExpenseRequest(
                "item", new BigDecimal(amount), category, LocalDate.of(y, m, d));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        rest.postForEntity("http://localhost:" + port + "/api/expenses",
                new HttpEntity<>(req, headers), Void.class);
    }

    @Test
    void monthlyStatsEndpointReturnsGroupedTotals() throws Exception {
        create("10.00", 2026, 8, 1, "Food");
        create("5.00", 2026, 8, 15, "Food");
        create("20.00", 2026, 7, 20, "Transport");

        ResponseEntity<String> response = rest.getForEntity(
                "http://localhost:" + port + "/api/stats/monthly", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        JsonNode root = mapper.readTree(response.getBody());
        assertThat(root.isArray()).isTrue();
        assertThat(root).hasSize(2);
        // aeltesten Monat zuerst (aufsteigend sortiert)
        assertThat(root.get(0).get("month").asText()).isEqualTo("2026-07");
        assertThat(root.get(0).get("total").asText()).isEqualTo("20.00");
        assertThat(root.get(1).get("total").asText()).isEqualTo("15.00");
    }
}
