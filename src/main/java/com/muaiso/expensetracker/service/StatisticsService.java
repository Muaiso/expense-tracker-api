package com.muaiso.expensetracker.service;

import com.muaiso.expensetracker.dto.ExpenseSummary;
import com.muaiso.expensetracker.dto.MonthlyStat;
import com.muaiso.expensetracker.model.Expense;
import com.muaiso.expensetracker.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Reine Aggregations-Logik, damit Statistiken auch ohne HTTP-Schicht
 * (z.B. in Batch-Jobs) wiederverwendbar sind.
 */
@Service
public class StatisticsService {

    private final ExpenseRepository repository;

    public StatisticsService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public List<MonthlyStat> monthlyStats() {
        Map<YearMonth, List<Expense>> byMonth = repository.findAll().stream()
                .collect(Collectors.groupingBy(
                        e -> YearMonth.from(e.getDate())));

        return byMonth.entrySet().stream()
                .map(entry -> {
                    BigDecimal total = entry.getValue().stream()
                            .map(Expense::getAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new MonthlyStat(entry.getKey(), entry.getValue().size(), total);
                })
                .sorted((a, b) -> a.month().compareTo(b.month()))
                .toList();
    }

    public ExpenseSummary currentMonthSummary() {
        YearMonth current = YearMonth.now();
        BigDecimal total = repository.findAll().stream()
                .filter(e -> YearMonth.from(e.getDate()).equals(current))
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ExpenseSummary(total, Map.of(), 0);
    }
}
