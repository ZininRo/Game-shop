package boardgames_shop.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private LocalDateTime orderDate;

    private LocalDateTime deliveryDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private BigDecimal totalPrice;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> items;

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}