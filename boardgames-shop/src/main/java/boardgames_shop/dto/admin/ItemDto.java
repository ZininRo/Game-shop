package boardgames_shop.dto.admin;

import java.math.BigDecimal;

public class ItemDto {

    private String name;
    private int quantity;
    private BigDecimal price; // quantity × priceAtPurchase

    public ItemDto() {}

    public ItemDto(String name, int quantity, BigDecimal price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    public String getName()                { return name; }
    public void setName(String name)       { this.name = name; }

    public int getQuantity()               { return quantity; }
    public void setQuantity(int quantity)  { this.quantity = quantity; }

    public BigDecimal getPrice()           { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}