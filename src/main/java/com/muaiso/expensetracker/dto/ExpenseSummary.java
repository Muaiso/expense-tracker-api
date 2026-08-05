package com.muaiso.expensetracker.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Read-only Aggregations-Ergebnis fuer {@code GET /api/expenses/summary}.
 * Als {@code record} deklariert (immutable, automatische Accessoren).
 */
public record ExpenseSummary(BigDecimal total, Map<String, BigDecimal> byCategory, int count) {
}
