package com.muaiso.expensetracker.dto;

import java.util.List;

/**
 * Gekapselte Seitendarstellung: Inhalt plus Metadaten (Seite, Groesse,
 * Gesamtanzahl, Gesamtseiten). Vermeidet das direkte Ausliefern von
 * Spring-Daten-{@code Page}-Objekten ueber die API.
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages) {

    public static <T> PageResponse<T> from(org.springframework.data.domain.Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
