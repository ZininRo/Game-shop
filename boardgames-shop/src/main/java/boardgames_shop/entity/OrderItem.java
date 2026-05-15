package boardgames_shop.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    private Integer quantity;

    private BigDecimal priceAtPurchase;

    public OrderItem() {
    }

    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public Game getGame() {
        return game;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public void setPriceAtPurchase(BigDecimal priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }
}