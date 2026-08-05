package com.muaiso.expensetracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    private Expense sample(String description, String amount, String category) {
        return new Expense(description, new BigDecimal(amount), category, LocalDate.of(2026, 8, 1));
    }

    @Test
    void createDelegatesToRepository() {
        Expense input = sample("Lunch", "10.00", "Food");
        when(repository.save(any(Expense.class))).thenReturn(input);

        Expense result = service.create(input);

        assertThat(result.getDescription()).isEqualTo("Lunch");
        verify(repository).save(input);
    }

    @Test
    void findByIdReturnsEntityWhenPresent() {
        Expense e = sample("Bus", "5.00", "Transport");
        when(repository.findById(1L)).thenReturn(Optional.of(e));

        assertThat(service.findById(1L)).isEqualTo(e);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(ExpenseNotFoundException.class);
    }

    @Test
    void updateAppliesChangesToExistingEntity() {
        Expense existing = sample("Alt", "1.00", "Sonstiges");
        Expense update = sample("Neu", "9.99", "Food");
        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));

        Expense result = service.update(1L, update);

        assertThat(result.getDescription()).isEqualTo("Neu");
        assertThat(result.getAmount()).isEqualByComparingTo("9.99");
        assertThat(result.getCategory()).isEqualTo("Food");
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
    void summarizeComputesTotalAndGroupsByCategory() {
        Expense food = sample("Lunch", "10.00", "Food");
        Expense food2 = sample("Dinner", "5.00", "Food");
        Expense transport = sample("Bus", "5.00", "Transport");
        when(repository.findAll()).thenReturn(List.of(food, food2, transport));

        ExpenseSummary summary = service.summarize();

        assertThat(summary.count()).isEqualTo(3);
        assertThat(summary.total()).isEqualByComparingTo("20.00");
        assertThat(summary.byCategory()).containsEntry("Food", new BigDecimal("15.00"));
        assertThat(summary.byCategory()).containsEntry("Transport", new BigDecimal("5.00"));
    }
}
