package com.muaiso.expensetracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload zum Anlegen/Aendern einer Ausgabe. Validiert Eingaben, bevor sie
 * als Entity persisted werden.
 */
public record ExpenseRequest(
        @NotBlank String description,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount,
        @NotBlank String category,
        @NotNull LocalDate date) {
}
