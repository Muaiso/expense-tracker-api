package com.muaiso.expensetracker.service;

import com.muaiso.expensetracker.dto.ExpenseRequest;
import com.muaiso.expensetracker.dto.ExpenseResponse;
import com.muaiso.expensetracker.dto.ExpenseSummary;
import com.muaiso.expensetracker.model.Expense;
import com.muaiso.expensetracker.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public List<ExpenseResponse> findAll() {
        return repository.findAll().stream()
                .map(ExpenseResponse::from)
                .toList();
    }

    public ExpenseResponse findById(Long id) {
        return ExpenseResponse.from(findEntity(id));
    }

    public ExpenseResponse create(ExpenseRequest request) {
        Expense entity = toEntity(request);
        return ExpenseResponse.from(repository.save(entity));
    }

    public ExpenseResponse update(Long id, ExpenseRequest request) {
        Expense existing = findEntity(id);
        existing.setDescription(request.description());
        existing.setAmount(request.amount());
        existing.setCategory(request.category());
        existing.setDate(request.date());
        return ExpenseResponse.from(repository.save(existing));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ExpenseNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /**
     * Aggregiert alle Ausgaben zu Gesamtsumme, Anzahl und Summe pro Kategorie.
     * Die Berechnung erfolgt serverseitig in Java (kein SQL), um die Logik
     * leicht unit-testbar zu halten.
     */
    public ExpenseSummary summarize() {
        List<Expense> all = repository.findAll();
        BigDecimal total = all.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<String, BigDecimal> byCategory = all.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Expense::getAmount,
                                BigDecimal::add)));
        return new ExpenseSummary(total, byCategory, all.size());
    }

    private Expense findEntity(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    private Expense toEntity(ExpenseRequest request) {
        return new Expense(
                request.description(), request.amount(), request.category(), request.date());
    }
}
