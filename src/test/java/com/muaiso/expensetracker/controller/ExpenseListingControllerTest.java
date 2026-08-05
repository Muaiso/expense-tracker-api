package com.muaiso.expensetracker.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.muaiso.expensetracker.dto.ExpenseResponse;
import com.muaiso.expensetracker.dto.PageResponse;
import com.muaiso.expensetracker.service.ExpenseService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(com.muaiso.expensetracker.controller.ExpenseController.class)
class ExpenseListingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService service;

    private ExpenseResponse r(Long id, String desc, String amount, String cat) {
        return new ExpenseResponse(id, desc, new BigDecimal(amount), cat, LocalDate.of(2026, 8, 5));
    }

    @Test
    void listingReturnsPagedEnvelope() throws Exception {
        PageResponse<ExpenseResponse> page = new PageResponse<>(
                List.of(r(1L, "Lunch", "10.00", "Food")), 0, 20, 1L, 1);
        when(service.findPaged(0, 20, "DESC")).thenReturn(page);

        mockMvc.perform(get("/api/expenses").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].description").value("Lunch"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void categoryFilterDelegatesToService() throws Exception {
        when(service.findByCategory("Food")).thenReturn(List.of(r(1L, "Lunch", "10.00", "Food")));

        mockMvc.perform(get("/api/expenses").param("category", "Food"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].category").value("Food"));
    }

    @Test
    void listingAcceptsSortParameter() throws Exception {
        PageResponse<ExpenseResponse> page = new PageResponse<>(List.of(), 0, 20, 0L, 0);
        when(service.findPaged(0, 20, "ASC")).thenReturn(page);

        mockMvc.perform(get("/api/expenses").param("sort", "ASC"))
                .andExpect(status().isOk());
    }
}
