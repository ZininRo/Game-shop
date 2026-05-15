package boardgames_shop.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "games")
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private BigDecimal price;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private boolean active = true;

    public Game() {
    }

    public Game(String name, BigDecimal price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.active = true;
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
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}