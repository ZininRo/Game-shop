// src/main/java/boardgames_shop/controller/AdminController.java
package boardgames_shop.controller;

import boardgames_shop.dto.admin.*;
import boardgames_shop.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ===== Сотрудники =====

    @GetMapping("/employees")
    public List<EmployeeResponse> getEmployees() {
        return adminService.getEmployees();
    }

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)      // FIX: 201 при создании
    public void createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        adminService.createEmployee(request);
    }

    @PutMapping("/employees/{id}")
    public void updateEmployee(@PathVariable Long id,
                               @Valid @RequestBody UpdateEmployeeRequest request) {
        adminService.updateEmployee(id, request);
    }

    @DeleteMapping("/employees/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)   // FIX: 204 при удалении
    public void deleteEmployee(@PathVariable Long id) {
        adminService.deleteEmployee(id);
    }

    // ===== Покупатели =====

    @GetMapping("/clients")
    public List<ClientResponse> getClients() {
        return adminService.getClients();
    }

    @PutMapping("/clients/{id}")
    public void updateClient(@PathVariable Long id,
                             @Valid @RequestBody UpdateClientRequest request) {
        adminService.updateClient(id, request);
    }

    @DeleteMapping("/clients/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteClient(@PathVariable Long id) {
        adminService.deleteClient(id);
    }

    // ===== Игры =====

    @GetMapping("/games")
    public List<GameResponse> getGames() {
        return adminService.getGames();
    }

    @PostMapping("/games")
    @ResponseStatus(HttpStatus.CREATED)
    public void createGame(@Valid @RequestBody CreateGameRequest request) {
        adminService.createGame(request);
    }

    @PutMapping("/games/{id}")
    public void updateGame(@PathVariable Long id,
                           @RequestBody UpdateGameRequest request) {
        adminService.updateGame(id, request);
    }

    @DeleteMapping("/games/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable Long id) {
        adminService.deleteGame(id);
    }

    // ===== Заказы =====

    @GetMapping("/orders")
    public List<AdminOrderResponse> getOrders() {
        return adminService.getOrders();
    }

    @PutMapping("/orders/{id}")
    public void updateOrder(@PathVariable Long id,
                            @RequestBody UpdateOrderAdminRequest request) {
        adminService.updateOrder(id, request);
    }

    @GetMapping("/orders/last")
    public List<AdminOrderResponse> getLastOrders(
            @RequestParam(defaultValue = "10") int limit) {
        int safeLimit = Math.max(1, Math.min(50, limit));
        return adminService.getLastOrders(safeLimit);
    }

    @GetMapping("/orders/multi-item")
    public List<AdminOrderResponse> getOrdersWithMultipleItems() {
        return adminService.getOrdersWithMultipleItems();
    }
}