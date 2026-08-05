package com.muaiso.expensetracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.muaiso.expensetracker.dto.MonthlyStat;
import com.muaiso.expensetracker.model.Expense;
import com.muaiso.expensetracker.repository.ExpenseRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private ExpenseRepository repository;

    @InjectMocks
    private StatisticsService service;

    private Expense e(String amount, int year, int month, int day) {
        Expense expense = new Expense("x", new BigDecimal(amount), "Cat", LocalDate.of(year, month, day));
        return expense;
    }

    @Test
    void monthlyStatsGroupsByYearMonthAndSums() {
        when(repository.findAll()).thenReturn(List.of(
                e("10.00", 2026, 8, 1),
                e("5.00", 2026, 8, 15),
                e("20.00", 2026, 7, 20)));

        List<MonthlyStat> stats = service.monthlyStats();

        assertThat(stats).hasSize(2);
        MonthlyStat july = stats.get(0);
        MonthlyStat august = stats.get(1);
        assertThat(july.month().toString()).isEqualTo("2026-07");
        assertThat(july.total()).isEqualByComparingTo("20.00");
        assertThat(august.month().toString()).isEqualTo("2026-08");
        assertThat(august.total()).isEqualByComparingTo("15.00");
        assertThat(august.count()).isEqualTo(2);
    }
}
