package boardgames_shop.repository;

import boardgames_shop.entity.Order;
import boardgames_shop.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);

}