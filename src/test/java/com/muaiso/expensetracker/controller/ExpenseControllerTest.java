package com.muaiso.expensetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muaiso.expensetracker.dto.ExpenseRequest;
import com.muaiso.expensetracker.dto.ExpenseResponse;
import com.muaiso.expensetracker.service.ExpenseNotFoundException;
import com.muaiso.expensetracker.service.ExpenseService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExpenseService service;

    private ExpenseRequest sampleRequest() {
        return new ExpenseRequest("Lunch", new BigDecimal("10.00"), "Food", LocalDate.of(2026, 8, 1));
    }

    @Test
    void createReturns201AndBody() throws Exception {
        when(service.create(any(ExpenseRequest.class)))
                .thenReturn(new ExpenseResponse(1L, "Lunch", new BigDecimal("10.00"), "Food", LocalDate.of(2026, 8, 1)));

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Lunch"))
                .andExpect(jsonPath("$.amount").value(10.00));
    }

    @Test
    void getMissingReturns404() throws Exception {
        when(service.findById(7L)).thenThrow(new ExpenseNotFoundException(7L));

        mockMvc.perform(get("/api/expenses/7"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createInvalidPayloadReturns400() throws Exception {
        String invalid = """
                {
                  "description": "",
                  "amount": 0,
                  "category": "",
                  "date": null
                }
                """;

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalid))
                .andExpect(status().isBadRequest());
    }
}
