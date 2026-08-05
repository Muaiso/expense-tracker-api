package com.muaiso.expensetracker.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * API-Darstellung einer Ausgabe. Bewusst vom JPA-{@code Entity} getrennt,
 * damit die Persistenzschicht nicht ins Web-Layer durchsickert.
 */
public record ExpenseResponse(
        Long id,
        String description,
        BigDecimal amount,
        String category,
        LocalDate date) {

    public static ExpenseResponse from(com.muaiso.expensetracker.model.Expense e) {
        return new ExpenseResponse(
                e.getId(), e.getDescription(), e.getAmount(), e.getCategory(), e.getDate());
    }
}
