package com.muaiso.expensetracker.controller;

import com.muaiso.expensetracker.dto.ExpenseSummary;
import com.muaiso.expensetracker.dto.MonthlyStat;
import com.muaiso.expensetracker.service.StatisticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@Tag(name = "Statistics", description = "Aggregierte Auswertungen")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/monthly")
    public List<MonthlyStat> monthly() {
        return statisticsService.monthlyStats();
    }

    @GetMapping("/current-month")
    public ExpenseSummary currentMonth() {
        return statisticsService.currentMonthSummary();
    }
}
