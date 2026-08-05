package com.muaiso.expensetracker.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muaiso.expensetracker.dto.ExpenseRequest;
import com.muaiso.expensetracker.dto.ExpenseResponse;
import com.muaiso.expensetracker.dto.ExpenseSummary;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * Vollstaendiger Durchlauf ueber die echte HTTP-Pipeline (Controller -> Service
 * -> Repository -> H2). Ergaenzt die isolierten MockMvc-Unit-Tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExpenseApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/expenses";
    }

    private ExpenseResponse create(String description, String amount, String category) {
        ExpenseRequest req = new ExpenseRequest(
                description, new BigDecimal(amount), category, LocalDate.of(2026, 8, 5));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<ExpenseResponse> response = rest.postForEntity(
                baseUrl(), new HttpEntity<>(req, headers), ExpenseResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody();
    }

    @Test
    void fullCrudAndSummaryFlow() {
        ExpenseResponse a = create("Lunch", "10.50", "Food");
        ExpenseResponse b = create("Bus", "4.00", "Transport");

        // GET all (paginated)
        ResponseEntity<Map> all = rest.getForEntity(baseUrl(), Map.class);
        assertThat(all.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat((List<?>) all.getBody().get("content")).hasSize(2);
        assertThat(all.getBody().get("totalElements")).isEqualTo(2);

        // GET one
        ResponseEntity<ExpenseResponse> one = rest.getForEntity(
                baseUrl() + "/" + a.id(), ExpenseResponse.class);
        assertThat(one.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(one.getBody().description()).isEqualTo("Lunch");

        // PUT update
        ExpenseRequest updated = new ExpenseRequest(
                "Lunch Deluxe", new BigDecimal("15.00"), "Food", LocalDate.of(2026, 8, 5));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<ExpenseResponse> put = rest.exchange(
                baseUrl() + "/" + a.id(), HttpMethod.PUT,
                new HttpEntity<>(updated, headers), ExpenseResponse.class);
        assertThat(put.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(put.getBody().description()).isEqualTo("Lunch Deluxe");

        // GET summary
        ResponseEntity<ExpenseSummary> summary = rest.getForEntity(
                baseUrl() + "/summary", ExpenseSummary.class);
        assertThat(summary.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(summary.getBody().total()).isEqualByComparingTo("19.00");

        // DELETE
        rest.delete(baseUrl() + "/" + a.id());
        ResponseEntity<ExpenseResponse> afterDelete = rest.getForEntity(
                baseUrl() + "/" + a.id(), ExpenseResponse.class);
        assertThat(afterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getMissingReturns404() {
        ResponseEntity<ExpenseResponse> response = rest.getForEntity(
                baseUrl() + "/999999", ExpenseResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
