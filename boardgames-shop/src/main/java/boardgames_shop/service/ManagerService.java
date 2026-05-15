package boardgames_shop.service;


import boardgames_shop.entity.*;
import boardgames_shop.dto.manager.*;
import boardgames_shop.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ManagerService {

    private final OrderRepository orderRepository;
    private final EmployeeRepository employeeRepository;

    public ManagerService(
            OrderRepository orderRepository,
            EmployeeRepository employeeRepository
    ) {
        this.orderRepository = orderRepository;
        this.employeeRepository = employeeRepository;
    }

    public void assignOrder(Long orderId) {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Employee employee = employeeRepository
                .findByUserId(user.getId())
                .orElseThrow();

        Order order = orderRepository
                .findById(orderId)
                .orElseThrow();

        order.setEmployee(employee);
        order.setStatus(OrderStatus.IN_TRANSIT);

        if (order.getTotalPrice() == null) {
            order.setTotalPrice(calculateTotal(order));
        }

        orderRepository.save(order);
    }

    private java.math.BigDecimal calculateTotal(Order order) {
        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            total = total.add(
                    item.getPriceAtPurchase()
                            .multiply(java.math.BigDecimal.valueOf(item.getQuantity()))
            );
        }
        return total;
    }



    public void updateOrder(Long id, UpdateOrderRequest request) {

        Order order = orderRepository.findById(id).orElseThrow();

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Employee employee = employeeRepository
                .findByUserId(user.getId())
                .orElseThrow();

        if (order.getEmployee() == null ||
                !order.getEmployee().getId().equals(employee.getId())) {
            throw new RuntimeException("Нет доступа к заказу");
        }

        order.setStatus(OrderStatus.valueOf(request.getStatus()));
        order.setDeliveryDate(request.getDeliveryDate());

        if (request.getTotal() != null) {
            order.setTotalPrice(request.getTotal());
        }

        orderRepository.save(order);
    }
    private ManagerOrderResponse mapOrder(Order order) {

        ManagerOrderResponse dto = new ManagerOrderResponse();

        dto.setId(order.getId());
        dto.setClientName(order.getClient().getFullName());

        if (order.getEmployee() != null) {
            dto.setManagerName(order.getEmployee().getFullName());
        } else {
            dto.setManagerName(null);
        }

        dto.setOrderDate(order.getOrderDate());
        dto.setDeliveryDate(order.getDeliveryDate());
        dto.setStatus(order.getStatus().name());
        dto.setTotal(order.getTotalPrice());

        List<ManagerOrderResponse.Item> items = new ArrayList<>();
        for (OrderItem item : order.getItems()) {
            ManagerOrderResponse.Item i = new ManagerOrderResponse.Item();
            i.setName(item.getGame().getName());
            i.setQuantity(item.getQuantity());
            i.setPrice(item.getPriceAtPurchase());
            items.add(i);
        }
        dto.setItems(items);

        return dto;
    }

    public ManagerOrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow();
        return mapOrder(order);
    }

    private List<ManagerOrderResponse> mapOrders(List<Order> orders) {
        List<ManagerOrderResponse> list = new ArrayList<>();
        for (Order order : orders) {
            list.add(mapOrder(order));
        }
        return list;
    }

    public List<ManagerOrderResponse> getNewOrders() {
        List<Order> orders = orderRepository
                .findByStatusAndEmployeeIsNullOrderByIdAsc(OrderStatus.CREATED);
        return mapOrders(orders);
    }

    public List<ManagerOrderResponse> getMyOrders() {

        User user = (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        Employee employee = employeeRepository
                .findByUserId(user.getId())
                .orElseThrow();

        List<Order> orders = orderRepository
                .findByEmployeeOrderByIdAsc(employee);

        return mapOrders(orders);
    }

    public List<ManagerOrderResponse> getAllOrders() {

        List<Order> orders = orderRepository
                .findByStatusNot(OrderStatus.NOT_CREATED);

        return mapOrders(orders);
    }


}