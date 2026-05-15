package boardgames_shop.service;

import boardgames_shop.dto.admin.*;
import boardgames_shop.entity.Order;
import boardgames_shop.entity.OrderItem;
import boardgames_shop.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReportsService {

    private final OrderRepository orderRepository;

    public ReportsService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    // 1. График по месяцу
    //    GET /api/admin/reports/sales/month?year=YYYY&month=MM
    //    Возвращает массив из N дней (N = кол-во дней в месяце),
    //    отсутствующие дни -> totalAmount = 0
    public List<SalesDayDto> getMonthlySales(int year, int month) {

        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime from = ym.atDay(1).atStartOfDay();
        LocalDateTime to   = ym.atEndOfMonth().plusDays(1).atStartOfDay();

        List<Order> orders = orderRepository.findCompletedBetween(from, to);

        // Группируем сумму по дате
        Map<LocalDate, BigDecimal> sumByDay = new LinkedHashMap<>();
        for (Order o : orders) {
            LocalDate day = o.getOrderDate().toLocalDate();
            sumByDay.merge(day, o.getTotalPrice(), BigDecimal::add);
        }

        // Заполняем все дни месяца, пропущенные → 0
        List<SalesDayDto> result = new ArrayList<>();
        for (int d = 1; d <= ym.lengthOfMonth(); d++) {
            LocalDate date = ym.atDay(d);
            result.add(new SalesDayDto(date, sumByDay.getOrDefault(date, BigDecimal.ZERO)));
        }

        return result;
    }

    // 2. Детализация дня
    //    GET /api/admin/reports/sales/day?date=YYYY-MM-DD
    //    Возвращает все COMPLETED заказы за конкретный день
    public List<SalesOrderDto> getDaySales(LocalDate date) {

        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to   = date.plusDays(1).atStartOfDay();

        List<Order> orders = orderRepository.findCompletedBetween(from, to);

        return orders.stream()
                .sorted(Comparator.comparing(Order::getId))
                .map(o -> {
                    SalesOrderDto dto = new SalesOrderDto();
                    dto.setId(o.getId());
                    dto.setTotal(o.getTotalPrice());

                    List<ItemDto> items = new ArrayList<>();
                    if (o.getItems() != null) {
                        o.getItems().stream()
                                .sorted(Comparator.comparing(OrderItem::getId))
                                .forEach(item -> {
                                    BigDecimal linePrice = item.getPriceAtPurchase()
                                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                                    items.add(new ItemDto(
                                            item.getGame().getName(),
                                            item.getQuantity(),
                                            linePrice
                                    ));
                                });
                    }
                    dto.setItems(items);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // 3. Отчёт за произвольный период
    //    GET /api/admin/reports/sales/period?from=YYYY-MM-DD&to=YYYY-MM-DD
    public PeriodStatsDto getPeriodStats(LocalDate from, LocalDate to) {

        List<Order> orders = orderRepository.findCompletedBetween(
                from.atStartOfDay(),
                to.atTime(23, 59, 59)
        );

        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int ordersCount = orders.size();

        BigDecimal averageCheck = ordersCount > 0
                ? totalRevenue.divide(BigDecimal.valueOf(ordersCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        PeriodStatsDto dto = new PeriodStatsDto();
        dto.setTotalRevenue(totalRevenue);
        dto.setOrdersCount(ordersCount);
        dto.setAverageCheck(averageCheck);
        return dto;
    }

    // 4. Сравнение месяца с предыдущим
    //    GET /api/admin/reports/sales/compare?year=YYYY&month=MM
    //    Считает текущий месяц vs предыдущий
    public CompareDto compareMonths(int year, int month) {

        YearMonth current  = YearMonth.of(year, month);
        YearMonth previous = current.minusMonths(1);

        PeriodStatsDto curr = getPeriodStats(current.atDay(1), current.atEndOfMonth());
        PeriodStatsDto prev = getPeriodStats(previous.atDay(1), previous.atEndOfMonth());

        CompareDto dto = new CompareDto();
        dto.setRevenuePercent(calcPercent(curr.getTotalRevenue(), prev.getTotalRevenue()));
        dto.setOrdersPercent(calcPercent(
                BigDecimal.valueOf(curr.getOrdersCount()),
                BigDecimal.valueOf(prev.getOrdersCount())
        ));
        dto.setAverageCheckPercent(calcPercent(curr.getAverageCheck(), prev.getAverageCheck()));
        return dto;
    }

    // 5. Топ игра месяца по количеству проданных единиц
    //    GET /api/admin/reports/sales/top-game?year=YYYY&month=MM
    public TopGameDto getTopGame(int year, int month) {

        YearMonth ym = YearMonth.of(year, month);
        List<Order> orders = orderRepository.findCompletedBetween(
                ym.atDay(1).atStartOfDay(),
                ym.atEndOfMonth().atTime(23, 59, 59)
        );

        Map<String, Integer> soldByGame = new HashMap<>();

        for (Order o : orders) {
            if (o.getItems() == null) continue;
            for (OrderItem item : o.getItems()) {
                String gameName = item.getGame().getName();
                soldByGame.merge(gameName, item.getQuantity(), Integer::sum);
            }
        }

        if (soldByGame.isEmpty()) {
            return new TopGameDto("—", 0);
        }

        Map.Entry<String, Integer> top = soldByGame.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElseThrow();

        return new TopGameDto(top.getKey(), top.getValue());
    }

    // 6. Средний чек за месяц
    //    GET /api/admin/reports/sales/average-check?year=YYYY&month=MM
    public AverageCheckDto getAverageCheck(int year, int month) {

        YearMonth ym = YearMonth.of(year, month);
        PeriodStatsDto stats = getPeriodStats(ym.atDay(1), ym.atEndOfMonth());
        return new AverageCheckDto(stats.getAverageCheck());
    }

    // Вспомогательный метод: ((current - previous) / previous) * 100
    // При previous = 0 → возвращает 0
    private double calcPercent(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        return current.subtract(previous)
                .divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}