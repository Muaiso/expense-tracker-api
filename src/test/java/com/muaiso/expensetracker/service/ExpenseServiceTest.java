package com.muaiso.expensetracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.muaiso.expensetracker.dto.ExpenseRequest;
import com.muaiso.expensetracker.dto.ExpenseResponse;
import com.muaiso.expensetracker.dto.ExpenseSummary;
import com.muaiso.expensetracker.model.Expense;
import com.muaiso.expensetracker.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    @InjectMocks
    private ExpenseService service;

    private ExpenseRequest request(String description, String amount, String category) {
        return new ExpenseRequest(description, new BigDecimal(amount), category, LocalDate.of(2026, 8, 1));
    }

    private Expense entity(Long id, String description, String amount, String category) {
        Expense e = new Expense(description, new BigDecimal(amount), category, LocalDate.of(2026, 8, 1));
        e.setId(id);
        return e;
    }

    @Test
    void createReturnsResponseFromSavedEntity() {
        Expense saved = entity(1L, "Lunch", "10.00", "Food");
        when(repository.save(any(Expense.class))).thenReturn(saved);

        ExpenseResponse result = service.create(request("Lunch", "10.00", "Food"));

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.description()).isEqualTo("Lunch");
        verify(repository).save(any(Expense.class));
    }

    @Test
    void findByIdReturnsResponseWhenPresent() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity(1L, "Bus", "5.00", "Transport")));

        assertThat(service.findById(1L).description()).isEqualTo("Bus");
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ExpenseNotFoundException.class);
    }

    @Test
    void updateAppliesChangesToExistingEntity() {
        Expense existing = entity(1L, "Alt", "1.00", "Sonstiges");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));

        ExpenseResponse result = service.update(1L, request("Neu", "9.99", "Food"));

        assertThat(result.description()).isEqualTo("Neu");
        assertThat(result.amount()).isEqualByComparingTo("9.99");
        assertThat(result.category()).isEqualTo("Food");
    }

    @Test
    void deleteRemovesExistingEntity() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void deleteThrowsWhenMissing() {
        when(repository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(42L))
                .isInstanceOf(ExpenseNotFoundException.class);
        verify(repository, never()).deleteById(42L);
    }

    @Test
    void findByCategoryFiltersEntities() {
        when(repository.findByCategory("Food")).thenReturn(
                List.of(entity(1L, "Lunch", "10.00", "Food"), entity(2L, "Dinner", "5.00", "Food")));

        var result = service.findByCategory("Food");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(r -> r.category().equals("Food"));
    }

    @Test
    void findByDateBetweenFiltersEntities() {
        LocalDate start = LocalDate.of(2026, 8, 1);
        LocalDate end = LocalDate.of(2026, 8, 31);
        when(repository.findByDateBetween(start, end)).thenReturn(
                List.of(entity(1L, "Lunch", "10.00", "Food")));

        var result = service.findByDateBetween(start, end);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).description()).isEqualTo("Lunch");
    }

    @Test
    void summarizeComputesTotalAndGroupsByCategory() {
        Expense food = entity(1L, "Lunch", "10.00", "Food");
        Expense food2 = entity(2L, "Dinner", "5.00", "Food");
        Expense transport = entity(3L, "Bus", "5.00", "Transport");
        when(repository.findAll()).thenReturn(List.of(food, food2, transport));

        ExpenseSummary summary = service.summarize();

        assertThat(summary.count()).isEqualTo(3);
        assertThat(summary.total()).isEqualByComparingTo("20.00");
        assertThat(summary.byCategory()).containsEntry("Food", new BigDecimal("15.00"));
        assertThat(summary.byCategory()).containsEntry("Transport", new BigDecimal("5.00"));
    }
}
