package boardgames_shop.dto.manager;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UpdateOrderRequest {

    private BigDecimal total;
    private LocalDateTime deliveryDate;
    private String status;

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}