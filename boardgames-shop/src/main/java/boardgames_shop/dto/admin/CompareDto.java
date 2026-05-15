package boardgames_shop.dto.admin;

public class CompareDto {

    private double revenuePercent;
    private double ordersPercent;
    private double averageCheckPercent;

    public CompareDto() {}

    public double getRevenuePercent()                        { return revenuePercent; }
    public void setRevenuePercent(double revenuePercent)     { this.revenuePercent = revenuePercent; }

    public double getOrdersPercent()                         { return ordersPercent; }
    public void setOrdersPercent(double ordersPercent)       { this.ordersPercent = ordersPercent; }

    public double getAverageCheckPercent()                           { return averageCheckPercent; }
    public void setAverageCheckPercent(double averageCheckPercent)   { this.averageCheckPercent = averageCheckPercent; }
}