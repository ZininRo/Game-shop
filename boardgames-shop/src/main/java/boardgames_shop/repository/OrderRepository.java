// src/main/java/boardgames_shop/repository/OrderRepository.java
package boardgames_shop.repository;

import boardgames_shop.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByClient(Client client);

    Optional<Order> findByClientAndStatus(Client client, OrderStatus status);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByClientAndStatusNotOrderByOrderDateDesc(Client client, OrderStatus status);

    List<Order> findByEmployee(Employee employee);

    List<Order> findByStatusAndEmployeeIsNullOrderByIdAsc(OrderStatus status);

    List<Order> findByEmployeeOrderByIdAsc(Employee employee);

    List<Order> findByStatusNot(OrderStatus status);

    List<Order> findByStatusNotOrderByIdAsc(OrderStatus status);

    /**
     * FIX: добавлен метод для подсчёта активных заказов сотрудника.
     * Заменяет findAll() + фильтрацию в Java в AdminService.getEmployees().
     */
    @Query("SELECT COUNT(o) FROM Order o WHERE o.employee = :employee AND o.status IN :statuses")
    long countByEmployeeAndStatusIn(@Param("employee") Employee employee,
                                    @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT o FROM Order o WHERE o.status = 'COMPLETED' " +
            "AND o.orderDate >= :from AND o.orderDate < :to " +
            "ORDER BY o.orderDate ASC")
    List<Order> findCompletedBetween(@Param("from") LocalDateTime from,
                                     @Param("to")   LocalDateTime to);

    @Query("""
        SELECT DISTINCT o FROM Order o
        WHERE o.status <> boardgames_shop.entity.OrderStatus.NOT_CREATED
          AND o.id IN (
              SELECT o2.id FROM Order o2
              WHERE o2.status <> boardgames_shop.entity.OrderStatus.NOT_CREATED
              ORDER BY o2.orderDate DESC
              LIMIT :limit
          )
        ORDER BY o.orderDate DESC
        """)
    List<Order> findLastNOrders(@Param("limit") int limit);

    @Query("""
        SELECT o FROM Order o
        WHERE o.status <> boardgames_shop.entity.OrderStatus.NOT_CREATED
          AND o.id IN (
              SELECT DISTINCT oi.order.id FROM OrderItem oi
              GROUP BY oi.order.id
              HAVING COUNT(DISTINCT oi.game.id) > 1
          )
        ORDER BY o.id ASC
        """)
    List<Order> findOrdersWithMultipleItems();
}