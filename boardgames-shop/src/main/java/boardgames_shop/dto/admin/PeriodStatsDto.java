package boardgames_shop.dto.admin;

import java.math.BigDecimal;

public class PeriodStatsDto {

    private BigDecimal totalRevenue;
    private int ordersCount;
    private BigDecimal averageCheck;

    public PeriodStatsDto() {}

    public BigDecimal getTotalRevenue()                  { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

    public int getOrdersCount()                          { return ordersCount; }
    public void setOrdersCount(int ordersCount)          { this.ordersCount = ordersCount; }

    public BigDecimal getAverageCheck()                  { return averageCheck; }
    public void setAverageCheck(BigDecimal averageCheck) { this.averageCheck = averageCheck; }
}