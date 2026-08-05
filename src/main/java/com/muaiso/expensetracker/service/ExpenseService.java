package com.muaiso.expensetracker.service;

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

    public List<Expense> findAll() {
        return repository.findAll();
    }

    public Expense findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    public Expense create(Expense expense) {
        return repository.save(expense);
    }

    public Expense update(Long id, Expense updated) {
        Expense existing = findById(id);
        existing.setDescription(updated.getDescription());
        existing.setAmount(updated.getAmount());
        existing.setCategory(updated.getCategory());
        existing.setDate(updated.getDate());
        return repository.save(existing);
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
}
