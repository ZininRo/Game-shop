// src/main/java/boardgames_shop/dto/admin/CreateGameRequest.java
package boardgames_shop.dto.admin;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CreateGameRequest {

    @NotBlank(message = "Название обязательно")
    private String name;

    @NotNull @DecimalMin(value = "0.01", message = "Цена должна быть больше 0")
    private BigDecimal price;

    @Size(max = 2000)
    private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}