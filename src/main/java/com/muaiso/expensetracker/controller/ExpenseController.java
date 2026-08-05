package com.muaiso.expensetracker.controller;

import com.muaiso.expensetracker.dto.ExpenseRequest;
import com.muaiso.expensetracker.dto.ExpenseResponse;
import com.muaiso.expensetracker.dto.ExpenseSummary;
import com.muaiso.expensetracker.dto.PageResponse;
import com.muaiso.expensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expenses", description = "CRUD und Aggregation von Ausgaben")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<ExpenseResponse> all(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        if (category != null) {
            return new PageResponse<>(service.findByCategory(category), 0, size, service.findByCategory(category).size(), 1);
        }
        if (from != null && to != null) {
            var range = service.findByDateBetween(from, to);
            return new PageResponse<>(range, 0, size, range.size(), 1);
        }
        return service.findPaged(page, size);
    }

    @GetMapping("/{id}")
    public ExpenseResponse one(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpenseResponse create(@Valid @RequestBody ExpenseRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ExpenseResponse update(@PathVariable Long id, @Valid @RequestBody ExpenseRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/summary")
    public ExpenseSummary summary() {
        return service.summarize();
    }
}
