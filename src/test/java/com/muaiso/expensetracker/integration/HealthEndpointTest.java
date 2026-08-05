package com.muaiso.expensetracker.integration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class HealthEndpointTest {

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void healthEndpointIsUp() {
        String body = rest.getForObject("http://localhost:" + port + "/actuator/health", String.class);
        assertThat(body).contains("\"status\":\"UP\"");
    }
}
