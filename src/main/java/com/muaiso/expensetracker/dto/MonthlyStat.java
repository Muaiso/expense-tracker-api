package com.muaiso.expensetracker.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Ausgaben eines Kalendermonats.
 */
public record MonthlyStat(YearMonth month, int count, BigDecimal total) {
}
