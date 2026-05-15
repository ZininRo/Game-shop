package boardgames_shop.dto.game;

import java.math.BigDecimal;

public class GameDto {

    private Long id;
    private String name;
    private BigDecimal price;
    private String description;
    private boolean active;

    public GameDto() {}

    public GameDto(Long id, String name, BigDecimal price, String description, boolean active) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}