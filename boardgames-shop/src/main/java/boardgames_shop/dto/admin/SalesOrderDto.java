package boardgames_shop.dto.admin;

import java.math.BigDecimal;
import java.util.List;

public class SalesOrderDto {

    private Long id;
    private BigDecimal total;
    private List<ItemDto> items;

    public SalesOrderDto() {}

    public Long getId()                    { return id; }
    public void setId(Long id)             { this.id = id; }

    public BigDecimal getTotal()           { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public List<ItemDto> getItems()               { return items; }
    public void setItems(List<ItemDto> items)      { this.items = items; }
}