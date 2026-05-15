// src/main/java/boardgames_shop/dto/admin/OrderItemDto.java
package boardgames_shop.dto.admin;

import java.math.BigDecimal;

public class OrderItemDto {

    private String name;
    private Integer quantity;
    private BigDecimal price;  // было Integer — ломалось на дробных ценах

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}