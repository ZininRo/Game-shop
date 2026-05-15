package boardgames_shop.service;

import boardgames_shop.entity.*;
import boardgames_shop.dto.history.OrderHistoryResponse;
import boardgames_shop.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class HistoryService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;

    public HistoryService(
            OrderRepository orderRepository,
            ClientRepository clientRepository
    ) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
    }

    public List<OrderHistoryResponse> getHistory() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Client client = clientRepository
                .findByUserId(user.getId())
                .orElseThrow();

        List<Order> orders = orderRepository
                .findByClientAndStatusNotOrderByOrderDateDesc(
                        client,
                        OrderStatus.NOT_CREATED
                );

        List<OrderHistoryResponse> response = new ArrayList<>();

        for (Order order : orders) {

            OrderHistoryResponse dto =
                    new OrderHistoryResponse();

            dto.setOrderId(order.getId());
            dto.setDate(order.getOrderDate());
            dto.setStatus(order.getStatus().name());

            dto.setTotal(
                    order.getItems()
                            .stream()
                            .map(i -> i.getPriceAtPurchase()
                                    .multiply(BigDecimal.valueOf(i.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );

            List<OrderHistoryResponse.Item> items =
                    new ArrayList<>();

            for (OrderItem item : order.getItems()) {

                OrderHistoryResponse.Item i =
                        new OrderHistoryResponse.Item();

                i.setName(item.getGame().getName());
                i.setQuantity(item.getQuantity());
                i.setPrice(item.getPriceAtPurchase());

                items.add(i);
            }

            dto.setItems(items);

            response.add(dto);
        }

        return response;
    }
}