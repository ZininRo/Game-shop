// src/main/java/boardgames_shop/service/AdminService.java
package boardgames_shop.service;

import boardgames_shop.dto.admin.*;
import boardgames_shop.entity.*;
import boardgames_shop.exception.ResourceNotFoundException;
import boardgames_shop.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional
public class AdminService {

    // Зарезервированные id заглушек — вынесены в константы
    private static final Long PLACEHOLDER_EMPLOYEE_ID = 4L;
    private static final Long PLACEHOLDER_CLIENT_ID   = 5L;

    private final EmployeeRepository employeeRepository;
    private final ClientRepository   clientRepository;
    private final GameRepository     gameRepository;
    private final OrderRepository    orderRepository;
    private final RoleRepository     roleRepository;
    private final UserRepository     userRepository;
    private final PasswordEncoder    passwordEncoder;

    public AdminService(EmployeeRepository employeeRepository,
                        ClientRepository clientRepository,
                        GameRepository gameRepository,
                        OrderRepository orderRepository,
                        RoleRepository roleRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.clientRepository   = clientRepository;
        this.gameRepository     = gameRepository;
        this.orderRepository    = orderRepository;
        this.roleRepository     = roleRepository;
        this.userRepository     = userRepository;
        this.passwordEncoder    = passwordEncoder;
    }

    // ===== Сотрудники =====

    /**
     * Возвращает список всех активных сотрудников с количеством активных заказов.
     */
    public List<EmployeeResponse> getEmployees() {
        List<Employee> employees = employeeRepository.findAllActiveEmployees();
        List<OrderStatus> activeStatuses = List.of(
                OrderStatus.CREATED, OrderStatus.IN_TRANSIT, OrderStatus.PICKUP_POINT);

        List<EmployeeResponse> result = new ArrayList<>();
        for (Employee e : employees) {
            // FIX: считаем заказы через специализированный запрос, не findAll()
            long activeCount = orderRepository
                    .countByEmployeeAndStatusIn(e, activeStatuses);

            EmployeeResponse dto = new EmployeeResponse();
            dto.setId(e.getId());
            dto.setName(e.getFullName());
            dto.setEmail(e.getUser().getEmail());
            dto.setPhone(e.getPhone());
            dto.setPosition(e.getPosition());
            dto.setRole(e.getUser().getRole().getName());
            dto.setActiveOrdersCount((int) activeCount);
            result.add(dto);
        }
        return result;
    }

    public void createEmployee(CreateEmployeeRequest request) {
        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Роль не найдена: " + request.getRole()));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        userRepository.save(user);

        Employee employee = new Employee();
        employee.setFullName(request.getName());
        employee.setPhone(request.getPhone());
        employee.setPosition(request.getPosition());
        employee.setUser(user);
        employeeRepository.save(employee);
    }

    public void updateEmployee(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Сотрудник не найден: " + id));

        employee.setFullName(request.getName());
        employee.setPhone(request.getPhone());
        employee.setPosition(request.getPosition());

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Роль не найдена: " + request.getRole()));
        employee.getUser().setRole(role);

        employeeRepository.save(employee);
    }

    public void deleteEmployee(Long employeeId) {
        Employee placeholder = employeeRepository.findById(PLACEHOLDER_EMPLOYEE_ID)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Сотрудник-заглушка не найден (id=" + PLACEHOLDER_EMPLOYEE_ID + ")"));

        Employee target = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Сотрудник не найден: " + employeeId));

        // Переназначаем заказы на заглушку
        List<Order> orders = orderRepository.findByEmployee(target);
        orders.forEach(o -> o.setEmployee(placeholder));
        if (!orders.isEmpty()) orderRepository.saveAll(orders);

        User userToDelete = target.getUser();
        if (userToDelete != null) {
            target.setUser(null);
            employeeRepository.save(target);
            userRepository.delete(userToDelete);
        }
        employeeRepository.delete(target);
    }

    // ===== Покупатели =====

    /**
     * Возвращает список всех активных покупателей.
     */
    public List<ClientResponse> getClients() {
        List<Client> clients = clientRepository.findAllActiveClients();
        List<ClientResponse> result = new ArrayList<>();
        for (Client c : clients) {
            ClientResponse dto = new ClientResponse();
            dto.setId(c.getId());
            dto.setName(c.getFullName());
            // FIX: User может быть null (удалённый пользователь без очистки)
            dto.setEmail(c.getUser() != null ? c.getUser().getEmail() : "—");
            dto.setPhone(c.getPhone());
            dto.setBirthDate(c.getBirthDate());
            result.add(dto);
        }
        return result;
    }

    public void updateClient(Long id, UpdateClientRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Покупатель не найден: " + id));
        client.setFullName(request.getName());
        client.setPhone(request.getPhone());
        client.setBirthDate(request.getBirthDate());
        clientRepository.save(client);
    }

    public void deleteClient(Long clientId) {
        Client placeholder = clientRepository.findById(PLACEHOLDER_CLIENT_ID)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Клиент-заглушка не найден (id=" + PLACEHOLDER_CLIENT_ID + ")"));

        Client target = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Покупатель не найден: " + clientId));

        List<Order> orders = orderRepository.findByClient(target);
        orders.forEach(o -> o.setClient(placeholder));
        if (!orders.isEmpty()) orderRepository.saveAll(orders);

        User userToDelete = target.getUser();
        if (userToDelete != null) {
            target.setUser(null);
            clientRepository.save(target);
            userRepository.delete(userToDelete);
        }
        clientRepository.delete(target);
    }

    // ===== Игры =====

    public List<GameResponse> getGames() {
        return gameRepository.findAllByOrderByIdAsc().stream()
                .map(this::mapGameToResponse)
                .toList();
    }

    public void createGame(CreateGameRequest request) {
        Game game = new Game();
        game.setName(request.getName());
        game.setPrice(request.getPrice());
        game.setDescription(request.getDescription());
        game.setActive(true);
        gameRepository.save(game);
    }

    public void updateGame(Long id, UpdateGameRequest request) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Игра не найдена: " + id));
        if (request.getName() != null)        game.setName(request.getName());
        if (request.getPrice() != null)       game.setPrice(request.getPrice());
        if (request.getDescription() != null) game.setDescription(request.getDescription());
        if (request.getActive() != null)      game.setActive(request.getActive());
        gameRepository.save(game);
    }

    public void deleteGame(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Игра не найдена: " + id));
        game.setActive(false);
        gameRepository.save(game);
    }

    // ===== Заказы =====

    public List<AdminOrderResponse> getOrders() {
        return mapOrdersToResponse(
                orderRepository.findByStatusNotOrderByIdAsc(OrderStatus.NOT_CREATED));
    }

    public List<AdminOrderResponse> getLastOrders(int limit) {
        return mapOrdersToResponse(orderRepository.findLastNOrders(limit));
    }

    public List<AdminOrderResponse> getOrdersWithMultipleItems() {
        return mapOrdersToResponse(orderRepository.findOrdersWithMultipleItems());
    }

    /**
     * Обновляет заказ.
     * FIX: deliveryDate и total обновляются только если переданы в запросе.
     */
    public void updateOrder(Long id, UpdateOrderAdminRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заказ не найден: " + id));

        if (request.getManagerId() != null) {
            Employee employee = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Менеджер не найден: " + request.getManagerId()));
            order.setEmployee(employee);
        }
        if (request.getDeliveryDate() != null) {
            order.setDeliveryDate(request.getDeliveryDate());
        }
        if (request.getStatus() != null) {
            order.setStatus(OrderStatus.valueOf(request.getStatus()));
        }
        if (request.getTotal() != null) {
            order.setTotalPrice(request.getTotal());
        }
        orderRepository.save(order);
    }

    // ===== Приватные маппинг-методы =====

    private List<AdminOrderResponse> mapOrdersToResponse(List<Order> orders) {
        List<AdminOrderResponse> result = new ArrayList<>();
        for (Order o : orders) {
            AdminOrderResponse dto = new AdminOrderResponse();
            dto.setId(o.getId());
            dto.setClient(o.getClient() != null ? o.getClient().getFullName() : "Удалённый пользователь");
            dto.setManager(o.getEmployee() != null ? o.getEmployee().getFullName() : null);
            dto.setStatus(o.getStatus().name());
            dto.setOrderDate(o.getOrderDate());
            dto.setDeliveryDate(o.getDeliveryDate());
            dto.setTotal(o.getTotalPrice());

            List<OrderItemDto> items = new ArrayList<>();
            if (o.getItems() != null) {
                o.getItems().stream()
                        .sorted(Comparator.comparing(OrderItem::getId))
                        .forEach(item -> {
                            OrderItemDto itemDto = new OrderItemDto();
                            itemDto.setName(item.getGame().getName());
                            itemDto.setQuantity(item.getQuantity());
                            // FIX: используем BigDecimal, не intValueExact()
                            itemDto.setPrice(item.getPriceAtPurchase());
                            items.add(itemDto);
                        });
            }
            dto.setItems(items);
            result.add(dto);
        }
        return result;
    }

    private GameResponse mapGameToResponse(Game g) {
        GameResponse dto = new GameResponse();
        dto.setId(g.getId());
        dto.setName(g.getName());
        dto.setPrice(g.getPrice());
        dto.setDescription(g.getDescription());
        dto.setActive(g.isActive());
        return dto;
    }
}