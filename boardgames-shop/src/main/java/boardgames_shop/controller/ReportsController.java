package boardgames_shop.controller;

import boardgames_shop.dto.admin.*;
import boardgames_shop.service.ReportsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/reports/sales")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    /**
     * График за месяц
     * GET /api/admin/reports/sales/month?year=2026&month=3
     */
    @GetMapping("/month")
    public List<SalesDayDto> getMonthlySales(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return reportsService.getMonthlySales(year, month);
    }

    /**
     * Детализация дня (клик по точке на графике)
     * GET /api/admin/reports/sales/day?date=2026-03-15
     */
    @GetMapping("/day")
    public List<SalesOrderDto> getDaySales(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return reportsService.getDaySales(date);
    }

    /**
     * Статистика за произвольный период
     * GET /api/admin/reports/sales/period?from=2026-01-01&to=2026-03-31
     */
    @GetMapping("/period")
    public PeriodStatsDto getPeriodStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return reportsService.getPeriodStats(from, to);
    }

    /**
     * Сравнение текущего месяца с предыдущим
     * GET /api/admin/reports/sales/compare?year=2026&month=3
     */
    @GetMapping("/compare")
    public CompareDto compareMonths(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return reportsService.compareMonths(year, month);
    }

    /**
     * Топ игра за месяц
     * GET /api/admin/reports/sales/top-game?year=2026&month=3
     */
    @GetMapping("/top-game")
    public TopGameDto getTopGame(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return reportsService.getTopGame(year, month);
    }

    /**
     * Средний чек за месяц
     * GET /api/admin/reports/sales/average-check?year=2026&month=3
     */
    @GetMapping("/average-check")
    public AverageCheckDto getAverageCheck(
            @RequestParam int year,
            @RequestParam int month
    ) {
        return reportsService.getAverageCheck(year, month);
    }
}